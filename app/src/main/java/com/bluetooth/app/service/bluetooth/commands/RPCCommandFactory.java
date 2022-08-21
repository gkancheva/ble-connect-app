package com.bluetooth.app.service.bluetooth.commands;

public class RPCCommandFactory {

    public RPCCommandHandler get(RPCCommandType commandType) {
        switch (commandType) {
            case SEND_MESSAGE_LENGTH: return new RPCMessageLengthCommandHandler();
            case SEND_MESSAGE: return new RPCMessageCommandHandler();
        }
        throw new IllegalStateException("Unknown command handler: " + commandType);
    }

}
