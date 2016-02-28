package com.scanners.ccsnaptv;

import com.toortools.tor.TorHelper;

public class Main
{
    public static void main(String args[])
    {
        TorHelper.torifySimple(true);
        new CcSnapTv();
    }
}
