package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Map;
import java.util.UUID;

public interface RPCCommandHandler {

    boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap, BluetoothGatt gatt);

    RPCCommandType getCommandType();

    default String printByteArray(byte[] arr) {
        return new String(arr);
    }

}
