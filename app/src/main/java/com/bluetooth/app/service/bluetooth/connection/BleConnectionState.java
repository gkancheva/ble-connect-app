package com.bluetooth.app.service.bluetooth.connection;

import java.util.Arrays;

public enum BleConnectionState {
    STATE_DISCONNECTED (0),
    STATE_CONNECTING (1),
    STATE_CONNECTED (2);

    private final int id;

    BleConnectionState(int index) {
        this.id = index;
    }

    public static BleConnectionState getById(int newStateId) {
        return Arrays.stream(values())
                .filter(s -> s.id == newStateId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown state"));
    }

}
