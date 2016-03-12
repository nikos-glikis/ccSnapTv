ccSnapTv
========

Security POC - ccSpanTv searches the internet for cctv systems using the vulnerable JAWS/1.0 firmware and downloads screenshots for all cameras connected to them.

The vulnerabilities are described here: 

[https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/](https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/)

[https://www.pentestpartners.com/blog/pwning-cctv-cameras/](https://www.pentestpartners.com/blog/pwning-cctv-cameras/)

This was created (and published) for 2 reasons:

- As a proof of concept of my stance on "smart" devices: You should never install anything "smart" in you home - It can be, and will, hacked at some point.
- To show in action some programming principles I believe in and I want to demonstrate.

The actual software

This software uses 3 of my libraries - Uploaded in different repositories and only shared with the public .

[TorRange](https://github.com/nikos-glikis/TorRange) - MultiThreaded processing of tasks using tor or proxies.

[ToorTools](https://github.com/nikos-glikis/toortools) - Various Utilities and small libraries to make my life easier.

[ShodanScanner](#) - A library makes multiple requests to the shodan Website and returns the found ips for a specific query.

The actual code in this repo is ~200 lines. The rest are libraries that I have developed.

Install:
========

Ubuntu 14.04:

1) Install dependencies 
    
    sudo apt-get update
    sudo apt-get install tor maven git openjdk-7-jdk openjdk-7-jre
    
2) Start tor:
    
    sudo service tor start
    
3) Clone and install maven dependencies (Not yet on Maven Central)

    #ToorTools
    git clone git@github.com:nikos-glikis/toortools.git
    cd toortools
    ./build.sh
    ./install.sh
     
    #TorRange
    git clone git@github.com:nikos-glikis/TorRange.git
    cd TorRange
    ./build_jar.sh
    ./install_jar.sh
   
    #ShodanScanner
    git clone git@github.com:nikos-glikis/ShodanScanner.git
    cd ShodanScanner
    ./build_jar.sh
    ./install_jar.sh

4) Install ccSnapTv

    git clone git@github.com:nikos-glikis/ccSnapTv.git
    cd ccSnapTv
    ./build.sh
    ./start.sh
    