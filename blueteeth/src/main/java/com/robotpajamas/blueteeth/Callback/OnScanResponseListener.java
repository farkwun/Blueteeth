package com.robotpajamas.blueteeth.Callback;

import com.robotpajamas.blueteeth.BlueteethDevice;

import java.util.List;

public interface OnScanResponseListener {
    void onScanResponse(List<BlueteethDevice> blueteethDevices);
}
