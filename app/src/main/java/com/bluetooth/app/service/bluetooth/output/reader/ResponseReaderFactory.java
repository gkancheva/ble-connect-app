package com.bluetooth.app.service.bluetooth.output.reader;

public class ResponseReaderFactory {

    public static ResponseReader getReader (ReaderType readerType) {
        switch (readerType) {
            case WIFI_READER: return new WifiResponseReader();
            case SWITCH_TOGGLE: return new SwitchToggleReader();
        }
        throw new IllegalStateException("Unknown reader");
    }

}
