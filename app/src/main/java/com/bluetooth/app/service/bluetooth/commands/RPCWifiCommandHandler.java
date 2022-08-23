package com.bluetooth.app.service.bluetooth.commands;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import java.util.Map;
import java.util.UUID;

public class RPCWifiCommandHandler extends RPCMessageCommandHandler {

    private final String TEST_JSON = "{\"id\":0,\"method\":\"WiFi.Scan\",\"src\":\"infinno\"}";

    @Override
    public boolean handle(Map<UUID, BluetoothGattCharacteristic> map, BluetoothGatt gatt) {
        return super.handle(map, gatt, TEST_JSON);
    }

    @Override
    public long getMessageLength() {
        return TEST_JSON.length();
    }

    @Override
    public RPCCommandType getCommandType() {
        return RPCCommandType.WIFI_COMMAND;
    }
}
