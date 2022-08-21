package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class RPCMessageLengthCommandHandler extends RPCCommandHandler {

    private static final String TAG = RPCMessageLengthCommandHandler.class.getSimpleName();

    @Override
    public boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap, String command, BluetoothGatt gatt) {
        BluetoothGattCharacteristic writeLengthChar = characteristicsMap
                .get(CharacteristicType.WRITE_LENGTH.getUuid());
        byte[] message = intToByteArray(command.getBytes().length);
        writeLengthChar.setValue(message);
        Log.i(TAG, "Writing to characteristic type WRITE_LENGTH, value: " + printByteArray(message));
        return gatt.writeCharacteristic(writeLengthChar);
    }

    @Override
    public RPCCommandType getCommandType() {
        return RPCCommandType.SEND_MESSAGE_LENGTH;
    }

}
