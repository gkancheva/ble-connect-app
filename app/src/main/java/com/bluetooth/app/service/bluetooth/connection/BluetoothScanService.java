package com.bluetooth.app.service.bluetooth.connection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;
import android.util.Log;

import com.bluetooth.app.service.OnNewDeviceFoundListener;
import com.bluetooth.app.service.OnStateChangedListener;

public class BluetoothScanService {
    private static final long SCAN_PERIOD_MS = 10000;

    private final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final Handler handler = new Handler();

    private final BluetoothLeScanner bleScanner;
    private boolean isScanning;

    private final OnNewDeviceFoundListener onNewDeviceFoundListener;
    private final OnStateChangedListener onStateChangedListener;

    public BluetoothScanService(OnNewDeviceFoundListener listener, OnStateChangedListener stateChangedListener) {
        bleScanner = bluetoothAdapter.getBluetoothLeScanner();
        onNewDeviceFoundListener = listener;
        onStateChangedListener = stateChangedListener;
    }

    public boolean bleAdapterIsEnabled() {
        return bluetoothAdapter.isEnabled();
    }

    public void scanForDevices() {
        if (bleScanner == null) {
            String error = "Bluetooth is not supported";
            Log.e(getClass().getSimpleName(), error);
            return;
        }
        if (!isScanning) {
            handler.postDelayed(() -> {
                setIsScanning(false);
                bleScanner.stopScan(scannerCallback);
            }, SCAN_PERIOD_MS);
            setIsScanning(true);
            bleScanner.startScan(scannerCallback);
        } else {
            setIsScanning(false);
            bleScanner.stopScan(scannerCallback);
        }
    }

    private void setIsScanning (boolean isScanning) {
        this.isScanning = isScanning;
        onStateChangedListener.onStateChanged(isScanning);
    }

    private final ScanCallback scannerCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            onNewDeviceFoundListener.onNewDevice(result.getDevice());
        }

    };

}
