package com.bluetooth.app.service.bluetooth.permissions;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionService {

    private static final String TAG = PermissionService.class.getSimpleName();

    private static final String ACCESS_COARSE_LOCATION = "android.permission.ACCESS_COARSE_LOCATION";
    private static final String ACCESS_FINE_LOCATION = "android.permission.ACCESS_FINE_LOCATION";
    private static final String BLUETOOTH_SCAN = "android.permission.BLUETOOTH_SCAN";
    private static final String BLUETOOTH_CONNECT = "android.permission.BLUETOOTH_CONNECT";

    private final AppCompatActivity context;

    public PermissionService(AppCompatActivity context) {
        this.context = context;
    }

    private static boolean locationPermissionsGranted = false;
    private static boolean bluetoothPermissionGranted = false;

    public void requestLocationPermissions() {
        String[] permissions = {ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION};

        if(!hasLocationPermissions()) {
            Log.i(TAG, "Asking for location permissions" );
            ActivityCompat.requestPermissions(context, permissions, 0);
        } else {
            locationPermissionsGranted = true;
            Log.i(TAG, "Location permissions already granted." );
            requestBluetoothPermission();
        }
    }

    public boolean hasBlePermissionsGranted() {
        Log.i(TAG, "Ble permissions grated: " + bluetoothPermissionGranted);
        return bluetoothPermissionGranted;
    }

    public void requestBluetoothPermission() {
        String[] permissions = new String[] { BLUETOOTH_SCAN, BLUETOOTH_CONNECT };

        if (!hasBluetoothPermissions()) {
            Log.i(TAG, "Asking for bluetooth permissions..." );
            ActivityCompat.requestPermissions(context, permissions, 1 );
        } else {
            bluetoothPermissionGranted = true;
            Log.i(TAG, "Bluetooth permissions already granted." );
        }
    }

    private boolean hasBluetoothPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            int scanPermissionCode = ContextCompat
                    .checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN);
            int connectPermissionCode = ContextCompat
                    .checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
            bluetoothPermissionGranted = scanPermissionCode == PackageManager.PERMISSION_GRANTED &&
                    connectPermissionCode == PackageManager.PERMISSION_GRANTED;
        }
        return bluetoothPermissionGranted;
    }

    private boolean hasLocationPermissions() {
        int accessCoarseCode = ContextCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        int accessFineCode = ContextCompat
                .checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);

        locationPermissionsGranted = accessCoarseCode == PackageManager.PERMISSION_GRANTED
                && accessFineCode == PackageManager.PERMISSION_GRANTED;
        return locationPermissionsGranted;
    }

}
