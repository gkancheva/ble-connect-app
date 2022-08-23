package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class RPCMessageLengthCommandHandler implements RPCCommandHandler {

    private static final String TAG = RPCMessageLengthCommandHandler.class.getSimpleName();

    private final long messageLength;

    public RPCMessageLengthCommandHandler(long messageLength) {
        this.messageLength = messageLength;
    }

    @Override
    public boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap, BluetoothGatt gatt) {
        BluetoothGattCharacteristic writeLengthChar = characteristicsMap.get(CharacteristicType.WRITE_LENGTH.getUuid());
        byte[] message = longToByteArray(messageLength);
        writeLengthChar.setValue(message);
        Log.i(TAG, "Writing to characteristic type WRITE_LENGTH, value: " + printByteArray(message));
        return gatt.writeCharacteristic(writeLengthChar);
    }

    @Override
    public RPCCommandType getCommandType() {
        return RPCCommandType.MESSAGE_LENGTH;
    }

    private byte[] longToByteArray(long value) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(value);
        return Arrays.copyOfRange(bytes, 4, 8);
    }

}
