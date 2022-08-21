package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

import com.bluetooth.app.model.CharacteristicType;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class RPCMessageCommandHandler extends RPCCommandHandler {

    private static final String TAG = RPCMessageCommandHandler.class.getSimpleName();

    @Override
    public boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap, String message, BluetoothGatt gatt) {
        BluetoothGattCharacteristic rwCharacteristic = characteristicsMap
                .get(CharacteristicType.READ_WRITE.getUuid());
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        rwCharacteristic.setValue(messageBytes);
        Log.i(TAG, "Sending json object to: READ_WRITE, value: " + printByteArray(messageBytes));
        return gatt.writeCharacteristic(rwCharacteristic);
    }

    @Override
    public RPCCommandType getCommandType() {
        return RPCCommandType.SEND_MESSAGE;
    }
}
