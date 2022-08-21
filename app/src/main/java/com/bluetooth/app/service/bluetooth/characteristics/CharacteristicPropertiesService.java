package com.bluetooth.app.service.bluetooth.characteristics;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PERMISSION_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE;

public class CharacteristicPropertiesService {

    private static final List<Integer> CHARACTERISTIC_PERMISSIONS = List.of(
            PERMISSION_WRITE, PERMISSION_READ);

    private static final List<Integer> CHARACTERISTIC_PROPERTIES = List.of(
            PROPERTY_READ, PROPERTY_WRITE_NO_RESPONSE, PROPERTY_WRITE,
            PROPERTY_NOTIFY, PROPERTY_INDICATE);

    public Set<String> getPermissions(int bitPermission) {
        return CHARACTERISTIC_PERMISSIONS.stream()
                .filter(p -> ((bitPermission & p) > 0))
                .map(this::getPermissionValue)
                .collect(Collectors.toSet());
    }

    public Set<String> getProperties(int bitProperty) {
        return CHARACTERISTIC_PROPERTIES.stream()
                .filter(p -> ((bitProperty & p) > 0))
                .map(this::getPropertyValue)
                .collect(Collectors.toSet());
    }

    private String getPermissionValue (int permission) {
        if ((permission & PERMISSION_READ) > 0) {
            return "PERMISSION_READ";
        } else if ((permission & PERMISSION_WRITE) > 0) {
            return "PERMISSION_WRITE";
        } else {
            return "UNDEFINED";
        }
    }

    private String getPropertyValue (int property) {
        if ((property & PROPERTY_READ) > 0) {
            return "PROPERTY_READ";
        } else if ((property & PROPERTY_WRITE_NO_RESPONSE) > 0) {
            return "PROPERTY_WRITE_NO_RESPONSE";
        } else if ((property & PROPERTY_WRITE) > 0) {
            return "PROPERTY_WRITE";
        } else if ((property & PROPERTY_NOTIFY) > 0) {
            return "PROPERTY_NOTIFY";
        } else if ((property & PROPERTY_INDICATE) > 0) {
            return "PROPERTY_INDICATE";
        } else {
            return "UNDEFINED";
        }
    }

}
