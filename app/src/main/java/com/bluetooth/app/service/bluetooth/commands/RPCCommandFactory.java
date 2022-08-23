package com.bluetooth.app.service.bluetooth.commands;

public class RPCCommandFactory {

    public RPCMessageCommandHandler get(RPCCommandType commandType) {
        switch (commandType) {
            case WIFI_COMMAND: return new RPCWifiCommandHandler();
            case SWITCH_TOGGLE_COMMAND: return new RPCSwitchToggleCommandHandler();
        }
        throw new IllegalStateException("Unknown command handler: " + commandType);
    }

    public RPCCommandHandler getMessageLengthHandler(long messageLength) {
        return new RPCMessageLengthCommandHandler(messageLength);
    }

}
