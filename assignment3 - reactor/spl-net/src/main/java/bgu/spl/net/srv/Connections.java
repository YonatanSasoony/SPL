package bgu.spl.net.srv;

import bgu.spl.net.api.MessageFrame;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, MessageFrame msg);

    void send(String channel, MessageFrame msg);

    void disconnect(int connectionId);
}
