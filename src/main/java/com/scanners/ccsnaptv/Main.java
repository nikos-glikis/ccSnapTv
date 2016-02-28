package com.scanners.ccsnaptv;

import com.object0r.toortools.tor.TorHelper;

public class Main
{
    public static void main(String args[])
    {
        TorHelper.torifySimple(true);
        new CcSnapTv();
    }
}
