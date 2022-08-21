package com.bluetooth.app;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.bluetooth.app.model.GattActionType;
import com.bluetooth.app.service.bluetooth.connection.BluetoothLeService;
import com.bluetooth.app.service.bluetooth.connection.BluetoothScanService;
import com.bluetooth.app.service.OnNewDeviceFound;
import com.bluetooth.app.service.OnStateChangedListener;
import com.bluetooth.app.service.bluetooth.permissions.PermissionService;
import com.bluetooth.app.views.BluetoothDeviceRVAdapter;
import com.bluetooth.app.views.OnDeviceSelectedListener;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.bluetooth.app.model.GattActionType.ACTION_DATA_AVAILABLE;
import static com.bluetooth.app.model.GattActionType.ACTION_GATT_CONNECTED;
import static com.bluetooth.app.model.GattActionType.ACTION_GATT_DISCONNECTED;
import static com.bluetooth.app.model.GattActionType.ACTION_GATT_SERVICES_DISCOVERED;

public class MainActivity extends AppCompatActivity implements OnDeviceSelectedListener, OnStateChangedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_ENABLE_BT = 2;

    private Button btnScan;

    private BluetoothScanService mBluetoothService;
    private BluetoothLeService bluetoothLeService;

    private BluetoothDevice selectedDevice;
    private PermissionService permissionService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        BluetoothDeviceRVAdapter rvAdapter = new BluetoothDeviceRVAdapter(this);
        recyclerView.setAdapter(rvAdapter);

        mBluetoothService = new BluetoothScanService(new OnNewDeviceFound(rvAdapter), this);

        permissionService = new PermissionService(this);
        permissionService.requestLocationPermissions();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupBluetoothService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, gattIntentFilter());
        if (bluetoothLeService != null && selectedDevice != null) {
            final boolean result = bluetoothLeService.connect(selectedDevice);
            Log.d(TAG, "Connect request result = " + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        mBluetoothService = null;
        bluetoothLeService = null;
    }

    private void setupBluetoothService() {
        if (!mBluetoothService.bleAdapterIsEnabled()) {
            if(!permissionService.hasBlePermissionsGranted()) {
                String error = "Missing permissions";
                Log.e(TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        btnScan = (Button) findViewById(R.id.btn_scan);
        onStateChanged(false);
        btnScan.setOnClickListener(v -> mBluetoothService.scanForDevices());
    }

    @Override
    public void onDeviceSelected(BluetoothDevice device) {
        selectedDevice = device;
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStateChanged(boolean isScanning) {
        if (isScanning) {
            btnScan.setText("Stop scanning");
        } else {
            btnScan.setText("Start scanning");
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            bluetoothLeService.connect(selectedDevice);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service disconnected");
            bluetoothLeService = null;
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final GattActionType action = GattActionType.getByName(intent.getAction());
            switch (action) {
                case ACTION_GATT_CONNECTED:
                    Log.i(TAG, "Connected");
                case ACTION_GATT_DISCONNECTED:
                    Log.i(TAG, "Disconnected");
                    unregisterReceiver(gattUpdateReceiver);
                    unbindService(serviceConnection);
                case ACTION_GATT_SERVICES_DISCOVERED:
                    Log.i(TAG, "Gatt services discovered");
                case ACTION_DATA_AVAILABLE:
                    Log.i(TAG, "Action data available");
            }
        }

    };

    private IntentFilter gattIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED.getName());
        intentFilter.addAction(ACTION_GATT_DISCONNECTED.getName());
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED.getName());
        intentFilter.addAction(ACTION_DATA_AVAILABLE.getName());
        return intentFilter;
    }
}
