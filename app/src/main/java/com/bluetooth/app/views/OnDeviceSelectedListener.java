package com.bluetooth.app.views;

import android.bluetooth.BluetoothDevice;

public interface OnDeviceSelectedListener {
    void onDeviceSelected(BluetoothDevice device);
}
