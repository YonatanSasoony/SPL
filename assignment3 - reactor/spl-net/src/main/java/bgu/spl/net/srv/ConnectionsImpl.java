package bgu.spl.net.srv;

import bgu.spl.net.api.MessageFrame;
import bgu.spl.net.api.User;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {


    private Map<String, Map<Integer, String>> topicMap = new ConcurrentHashMap<>(); //topic - Map of Connection id and subscribe id
    private Map<Integer, ConnectionHandler> handlerMap;// id - handler of the client
    private Map<String, User> userMap = new ConcurrentHashMap<>(); // user name - User
    private AtomicInteger messageId = new AtomicInteger(1);


    public ConnectionsImpl(BaseServer server) {
        this.handlerMap = server.getHandlerMap();
    }

    public ConnectionsImpl(Reactor server) {
        this.handlerMap = server.getHandlerMap();
    }

    @Override
    public boolean send(int connectionId, MessageFrame msg) { // send a msg to client with specific id
        ConnectionHandler connectionHandler = handlerMap.get(connectionId);
        if (connectionHandler != null) {
            connectionHandler.send(msg);
            return true;
        }
        return false;

    }

    @Override
    public void send(String topic, MessageFrame msg) { // send to each user subscribed to topic
        Map<Integer, String> subscribedMap = topicMap.get(topic);
        if (subscribedMap != null) {
            for (Integer connectionId : subscribedMap.keySet()) {
                msg.setSubIdHeader(subscribedMap.get(connectionId));
                send(connectionId, msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) { //disconnects client with specific id
        handlerMap.remove(connectionId);
    }

    public String getAndIncrementMsgId() {
        return String.valueOf(messageId.getAndIncrement());
    }

    public Map<String, Map<Integer, String>> getTopicMap() {
        return topicMap;
    }

    public Map<Integer, ConnectionHandler> getHandlerMap() {
        return handlerMap;
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }


}


