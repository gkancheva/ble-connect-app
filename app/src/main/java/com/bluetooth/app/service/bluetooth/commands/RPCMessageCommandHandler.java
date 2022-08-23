package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public abstract class RPCMessageCommandHandler implements RPCCommandHandler {

    private static final String TAG = RPCMessageCommandHandler.class.getSimpleName();

    protected boolean handle (Map<UUID, BluetoothGattCharacteristic> map, BluetoothGatt gatt, String message) {
        BluetoothGattCharacteristic characteristic = map.get(CharacteristicType.READ_WRITE.getUuid());
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        characteristic.setValue(messageBytes);
        Log.i(TAG, "Sending json object to: READ_WRITE, value: " + printByteArray(messageBytes));
        return gatt.writeCharacteristic(characteristic);
    }

    public abstract long getMessageLength();

    @Override
    public abstract RPCCommandType getCommandType();
}
