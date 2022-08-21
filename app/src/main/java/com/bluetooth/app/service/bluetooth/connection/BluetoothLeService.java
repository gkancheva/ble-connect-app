package com.bluetooth.app.service.bluetooth.connection;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.bluetooth.app.service.bluetooth.gatt.response.BluetoothGattResponseHandler;

import androidx.annotation.Nullable;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;

public class BluetoothLeService extends Service {
    private static final String  TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;

    private final Binder binder = new LocalBinder();

    public boolean initialize() {
        Log.i(TAG, "Initializing...");
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(getClass().getSimpleName(), "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final BluetoothDevice bleDevice) {
        if (bluetoothAdapter == null || bleDevice == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        Log.i(TAG, "Will try to connect to: " + bleDevice.getName() + ", on address: " + bleDevice.getAddress());

        BluetoothDevice device;
        try {
            device = bluetoothAdapter.getRemoteDevice(bleDevice.getAddress());
            if (device == null) {
                Log.e(TAG, "Device not found. Unable to connect.");
                return false;
            }
            BluetoothGattCallback gattCallBackHandler = new BluetoothGattResponseHandler(this);
            bluetoothGatt = device.connectGatt(this, false, gattCallBackHandler, TRANSPORT_LE);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Exception while trying to connect to device with address: " + bleDevice);
            return false;
        }

        Log.d(TAG, "Trying to create a new connection.");
        return true;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    private void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
        bluetoothGatt = null;
    }

}
