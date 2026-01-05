package com.donovanbeulze.wifirtt;

import com.getcapacitor.Logger;

public class WifiRtt {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
