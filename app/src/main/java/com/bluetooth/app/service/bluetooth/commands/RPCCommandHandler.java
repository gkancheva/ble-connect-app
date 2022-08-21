package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public abstract class RPCCommandHandler {

    abstract public boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap,
                                   String message, BluetoothGatt gatt);

    abstract public RPCCommandType getCommandType();

    protected String printByteArray(byte[] arr) {
        StringBuilder sb = new StringBuilder();
        for (byte b : arr) {
            sb.append(b).append(" ");
        }
        return sb.toString();
    }

//    protected byte[] intToByteArray(int value){
//        ByteBuffer buffer = ByteBuffer.allocate(4);
//        buffer.order(ByteOrder.BIG_ENDIAN);
//        buffer.putInt(value);
//        buffer.flip();
//        return buffer.array();
//    }

    protected byte[] intToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);

        return Arrays.copyOfRange(bytes, 4, 8);
    }

}
