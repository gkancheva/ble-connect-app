package com.bluetooth.app.model;

import java.util.Arrays;

public enum GattActionType {
    ACTION_GATT_CONNECTED ("ACTION_GATT_CONNECTED"),
    ACTION_GATT_DISCONNECTED ("ACTION_GATT_DISCONNECTED"),
    ACTION_GATT_SERVICES_DISCOVERED ("ACTION_GATT_SERVICES_DISCOVERED"),
    ACTION_DATA_AVAILABLE ("ACTION_DATA_AVAILABLE");

    private final String name;

    GattActionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static GattActionType getByName(String name) {
        return Arrays.stream(values())
                .filter(x -> x.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknow action type: " + name));
    }
}
