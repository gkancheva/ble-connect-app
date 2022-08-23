package com.bluetooth.app.service.bluetooth.output.reader;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class WifiResponseReader implements ResponseReader {
    private static final String TAG = WifiResponseReader.class.getSimpleName();

    @Override
    public void read(String rawResponse) {
        Log.i(TAG, "Raw response: " + rawResponse);
        try {
            JSONObject jsonObject = new JSONObject(rawResponse);
            Log.i(TAG, "Parsed successfully json");
        } catch (JSONException e) {
            Log.e(TAG, "Exception while parsing json object");
        }
    }

}
