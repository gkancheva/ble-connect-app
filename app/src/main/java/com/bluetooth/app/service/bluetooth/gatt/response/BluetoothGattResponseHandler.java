package com.bluetooth.app.service.bluetooth.gatt.response;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;
import com.bluetooth.app.model.GattActionType;
import com.bluetooth.app.service.bluetooth.commands.RPCCommandFactory;
import com.bluetooth.app.service.bluetooth.commands.RPCCommandType;
import com.bluetooth.app.service.bluetooth.connection.BleConnectionState;
import com.bluetooth.app.service.bluetooth.output.reader.ReaderType;
import com.bluetooth.app.service.bluetooth.output.reader.ResponseReaderFactory;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.bluetooth.app.model.CharacteristicType.READ_WRITE;

public class BluetoothGattResponseHandler extends BluetoothGattCallback {

    private static final String TAG = BluetoothGattResponseHandler.class.getSimpleName();
    private static final String CUSTOM_SERVICE_UUID = "5f6d4f53-5f52-5043-5f53-56435f49445f";
    private static final String NOTIFY_DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    private static final RPCCommandType RPC_COMMAND_TYPE = RPCCommandType.WIFI_COMMAND;

    private Map<UUID, BluetoothGattCharacteristic> characteristicsByUUID = new ConcurrentHashMap<>();

    private final Service service;

    private final StringBuilder output = new StringBuilder();

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

                BluetoothGattCharacteristic characteristic = characteristicsByUUID.get(CharacteristicType.READ_LENGTH.getUuid());
                subscribeForNotification(characteristic, gatt);

                if (characteristicsByUUID.size() != CharacteristicType.values().length) {
                    throw new IllegalStateException("Unexpected size of characteristics");
                }
            }
        } else {
            Log.w(TAG, "onServicesDiscovered received: " + status);
        }
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        CharacteristicType characteristicType = CharacteristicType.getByUUID(characteristic.getUuid());
        Log.i(TAG, "onCharacteristicWrite: " + characteristicType);
        if (characteristicType == CharacteristicType.WRITE_LENGTH) {
            Log.i(TAG, "Sending write characteristic to: " + READ_WRITE);
            commandFactory
                    .get(RPC_COMMAND_TYPE)
                    .handle(characteristicsByUUID, gatt);
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "onDescriptorWrite");
        gatt.readDescriptor(descriptor);
    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        Log.i(TAG, "onDescriptorRead");
        Log.i(TAG, "descriptor value: " + getValueAsString(descriptor.getValue()));
        long messageLength = commandFactory.get(RPC_COMMAND_TYPE).getMessageLength();
        commandFactory
                .getMessageLengthHandler(messageLength)
                .handle(characteristicsByUUID, gatt);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        Log.i(TAG, "onCharacteristicRead, status: " + status);
        String valueAsString = getValueAsString(characteristic.getValue());
        CharacteristicType characteristicType = CharacteristicType.getByUUID(characteristic.getUuid());
        if (status == BluetoothGatt.GATT_SUCCESS) {
            switch (characteristicType) {
                case READ_WRITE:
                    Log.i(TAG, "Received characteristic read event from uuid: READ_WRITE");
                    output.append(new String(characteristic.getValue()));
                    if (characteristic.getValue().length > 0) {
                        gatt.readCharacteristic(characteristicsByUUID.get(READ_WRITE.getUuid()));
                    } else {
                        Objects.requireNonNull(ResponseReaderFactory
                                .getReader(ReaderType.WIFI_READER)).read(output.toString());
                    }
                    break;
                case READ_LENGTH:
                    Log.i(TAG, "Received characteristic read event form uuid: READ_LENGTH, value: '" + valueAsString + "'");
                    long len = new BigInteger(characteristic.getValue()).longValue();
                    Log.i(TAG, "Length: " + len);
                    gatt.readCharacteristic(characteristicsByUUID.get(READ_WRITE.getUuid()));
                    break;
            }
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "onCharacteristicChanged, value: " + getValueAsString(characteristic.getValue()));
        gatt.readCharacteristic(characteristic);
    }

    private void subscribeForNotification(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        gatt.setCharacteristicNotification(characteristic, true);
        enableDescriptorNotification(characteristic, gatt);
    }

    private void enableDescriptorNotification(BluetoothGattCharacteristic characteristic, BluetoothGatt gatt) {
        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        if (descriptors == null) {
            return;
        }
        descriptors.forEach(d -> {
            if (d.getUuid().toString().equals(NOTIFY_DESCRIPTOR_UUID)) {
                d.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(d);
            }
        });
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
            return new String(data);
        }
        return "";
    }

}
