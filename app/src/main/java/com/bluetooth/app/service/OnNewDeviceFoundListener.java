package com.bluetooth.app.service;

import android.bluetooth.BluetoothDevice;

public interface OnNewDeviceFoundListener {

    void onNewDevice(BluetoothDevice device);

}
