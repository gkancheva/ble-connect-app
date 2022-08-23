package com.bluetooth.app.model;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CharacteristicType {
    READ_WRITE ("5f6d4f53-5f52-5043-5f64-6174615f5f5f", "Read/write attribute. Used for reading/writing messages"),
    WRITE_LENGTH ("5f6d4f53-5f52-5043-5f74-785f63746c5f", "Write only attribute. Used for sending information for the initial length of the message"),
    READ_LENGTH ("5f6d4f53-5f52-5043-5f72-785f63746c5f", "Read/notify attribute. Used for reading the length of the message");

    private final String uuid;
    private final String description;

    CharacteristicType(String uuid, String description) {
        this.uuid = uuid;
        this.description = description;
    }

    public UUID getUuid() {
        return UUID.fromString(uuid);
    }

    public static final Map<UUID, CharacteristicType> mapByUUID = Arrays.stream(values())
            .collect(Collectors.toMap(x -> UUID.fromString(x.uuid), Function.identity()));

    public static CharacteristicType getByUUID(UUID uuid) {
        return mapByUUID.get(uuid);
    }

}
