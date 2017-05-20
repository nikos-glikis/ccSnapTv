ccSnapTv
========

Security POC - ccSpanTv searches the internet for cctv systems using the vulnerable JAWS/1.0 firmware and downloads screenshots for all cameras connected to them.

The vulnerabilities are described here: 

[https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/](https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/)

[https://www.pentestpartners.com/blog/pwning-cctv-cameras/](https://www.pentestpartners.com/blog/pwning-cctv-cameras/)

This was created (and published) for 2 reasons:

- As a proof of concept of my stance on "smart" devices: You should never install anything "smart" in you home - It can be, and will, hacked at some point.
- To show in action some programming principles I believe in and I want to demonstrate.


This software uses 3 of my libraries - Uploaded in different repositories and only shared with the public .

[TorRange](https://github.com/nikos-glikis/TorRange) - MultiThreaded processing of tasks using tor or proxies.

[ToorTools](https://github.com/nikos-glikis/toortools) - Various Utilities and small libraries to make my life easier.

[ShodanScanner](#) - A library makes multiple requests to the shodan Website and returns the found ips for a specific query.

The actual code in this repo is ~200 lines. The rest are libraries that I have developed in the past.

Install:
========

Ubuntu 14.04:

1) Install dependencies 
    
    sudo apt-get update
        
    #remove maven2
    sudo apt-get remove maven2
    
    sudo apt-get install tor maven git openjdk-7-jdk openjdk-7-jre
    
    
2) Start tor:
    
    sudo service tor start
    
    
3) Install and run ccSnapTv

    git clone https://github.com/nikos-glikis/ccSnapTv.git
    
    cd ccSnapTv
    
    #./build.sh
    
    mvn clean compile assembly:single
    
    
Take a look at ccSnapTv.ini and adjust the params. Defaults work. Increasing threads makes the process faster, but more bandwidth is required.
Also you can use tor and make everything stealth with useTor=true. If you do that make sure that tor is accessible. Login over TOR might not work.
    
    Also a shodan account is required for the software to run properly. Registration is free and the credentials of one account are already there
    #./start.sh
    java -jar target/ccSnapTv-1.0.0-jar-with-dependencies.jar ccSnapTv.ini
    
If everything works, you should see a list of IPs in your screen. The images will be downloaded in the output/ directory.
