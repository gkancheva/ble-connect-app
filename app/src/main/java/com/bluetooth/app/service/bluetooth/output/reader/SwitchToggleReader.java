package com.bluetooth.app.service.bluetooth.output.reader;

import android.util.Log;

public class SwitchToggleReader implements ResponseReader {
    private static final String TAG = SwitchToggleReader.class.getSimpleName();

    @Override
    public void read(String rawResponse) {
        Log.i(TAG, "Raw response: " + rawResponse);
    }

}
