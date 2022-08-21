package com.bluetooth.app.service.bluetooth.gatt.response;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;
import com.bluetooth.app.model.GattActionType;
import com.bluetooth.app.service.bluetooth.commands.RPCCommandFactory;
import com.bluetooth.app.service.bluetooth.commands.RPCCommandType;
import com.bluetooth.app.service.bluetooth.connection.BleConnectionState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BluetoothGattResponseHandler extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattResponseHandler.class.getSimpleName();
    private static final String CUSTOM_SERVICE_UUID = "5f6d4f53-5f52-5043-5f53-56435f49445f";

    private Map<UUID, BluetoothGattCharacteristic> characteristicsByUUID = new HashMap<>();

    private final String TEST_JSON = "{“id”:0,“method”:“WiFi.scan”,“src”:“infinno”}";

    private final Service service;

    private final RPCCommandFactory commandFactory = new RPCCommandFactory();

    public BluetoothGattResponseHandler(Service service) {
        this.service = service;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newStateId) {
        BleConnectionState newState = BleConnectionState.getById(newStateId);
        switch (newState) {
            case STATE_CONNECTED:
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Service discovery started: " + gatt.discoverServices());
                break;
            case STATE_DISCONNECTED:
                Log.i(TAG, "Disconnected from GATT server.");
                final Intent intent = new Intent(GattActionType.ACTION_GATT_DISCONNECTED.getName());
                service.sendBroadcast(intent);
                gatt.close();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        if (status == BluetoothGatt.GATT_SUCCESS) {
            Log.i(TAG, "onServicesDiscovered");
            for (BluetoothGattService gattService : gatt.getServices()) {
                if (!gattService.getUuid().toString().equals(CUSTOM_SERVICE_UUID)) {
                    continue;
                }

                initializeCharacteristicMap(gattService.getCharacteristics());

                if (characteristicsByUUID.size() != CharacteristicType.values().length) {
                    throw new IllegalStateException("Unexpected size of characteristics");
                }

                commandFactory
                        .get(RPCCommandType.SEND_MESSAGE_LENGTH)
                        .handle(characteristicsByUUID, TEST_JSON, gatt);
            }
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }


    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        CharacteristicType characteristicType = CharacteristicType.getByUUID(characteristic.getUuid());
        Log.i(TAG, "onCharacteristicWrite: " + characteristicType);
        switch (characteristicType) {
            case WRITE_LENGTH:
                Log.i(TAG, "Sending write characteristic to: " + CharacteristicType.READ_WRITE);
                commandFactory
                        .get(RPCCommandType.SEND_MESSAGE)
                        .handle(characteristicsByUUID, TEST_JSON, gatt);
                break;
            case READ_WRITE:
                Log.i(TAG, "Requesting read from: " + CharacteristicType.READ_LENGTH);
                gatt.readCharacteristic(characteristicsByUUID.get(CharacteristicType.READ_LENGTH.getUuid()));
                break;
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "onCharacteristicRead, status: " + status);
        String valueAsString = getValueAsString(characteristic.getValue());
        CharacteristicType characteristicType = CharacteristicType.getByUUID(characteristic.getUuid());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (characteristicType) {
                case READ_WRITE:
                    Log.i(TAG, "Received characteristic read event form uuid: READ_WRITE, value: '" + valueAsString + "'");
                    break;
                case READ_LENGTH:
                    Log.i(TAG, "Received characteristic read event form uuid: READ_LENGTH, value: '" + valueAsString + "'");
                    gatt.readCharacteristic(characteristicsByUUID.get(CharacteristicType.READ_WRITE.getUuid()));
                    break;
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "onCharacteristicChanged");
    }

    private void initializeCharacteristicMap(List<BluetoothGattCharacteristic> characteristics) {
        characteristicsByUUID = characteristics.stream()
                .filter(x -> CharacteristicType.mapByUUID.containsKey(x.getUuid()))
                .collect(Collectors.toMap(
                        BluetoothGattCharacteristic::getUuid, Function.identity()));
        Log.i(TAG, "Characteristics size: " + characteristicsByUUID.size());
    }

    private String getValueAsString(byte[] data) {
        if (data != null && data.length > 0) {
            Log.i(TAG, "Length: " + data.length);
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for(byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            Log.i(TAG, "stringBuilder: " + stringBuilder);
            return new String(data) + "\n" + stringBuilder;
        }
        return "";
    }

}
