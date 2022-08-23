package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Map;
import java.util.UUID;

public class RPCSwitchToggleCommandHandler extends RPCMessageCommandHandler {
    private static final String TAG = RPCSwitchToggleCommandHandler.class.getSimpleName();

    private final String TEST_SWITCH_TOGGLE = "{\"id\":1,\"method\":\"switch.toggle\",\"params\":{\"id\":0},\"src\":\"infinno\"}";

    @Override
    public boolean handle(Map<UUID, BluetoothGattCharacteristic> characteristicsMap, BluetoothGatt gatt) {
        return super.handle(characteristicsMap, gatt, TEST_SWITCH_TOGGLE);
    }

    @Override
    public long getMessageLength() {
        return TEST_SWITCH_TOGGLE.length();
    }

    @Override
    public RPCCommandType getCommandType() {
        return RPCCommandType.SWITCH_TOGGLE_COMMAND;
    }
}
