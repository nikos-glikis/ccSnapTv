ccSnapTv
========

ccSpanTv searches the internet for cctv systems using the vulnerable JAWS/1.0 firmware. It then connects to them and downloads screens shots saved locally.

The vulnerabilities are described here: 

[https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/](https://www.reddit.com/r/netsec/comments/466jap/pwning_cctv_cameras/)

[https://www.pentestpartners.com/blog/pwning-cctv-cameras/](https://www.pentestpartners.com/blog/pwning-cctv-cameras/)

This was created (and published) for 2 reasons:

- As a proof of concept of my philosophy: You should never install anything "smart" in you home - It can be, and will, hacked at some point.
- To show in action some programming principles I believe in and I want to demonstrate.

This software uses 3 of my libraries - Uploaded in different repositories and only shared with the public .

[TorRange](https://github.com/nikos-glikis/TorRange) - MultiThreaded processing of tasks using tor or proxies.

[ToorTools](https://github.com/nikos-glikis/toortools) - Various Utilities and small libraries to make my life easier.

[ShodanScanner](#) - 