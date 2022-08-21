package com.bluetooth.app.service;

import android.bluetooth.BluetoothDevice;

import com.bluetooth.app.views.BluetoothDeviceRVAdapter;

public class OnNewDeviceFound implements OnNewDeviceFoundListener {

    private final BluetoothDeviceRVAdapter rvAdapter;

    public OnNewDeviceFound(BluetoothDeviceRVAdapter rvAdapter) {
        this.rvAdapter = rvAdapter;
    }

    @Override
    public void onNewDevice(BluetoothDevice device) {
        if (device.getName() != null) {
            rvAdapter.addDevice(device);
        }
    }

}
