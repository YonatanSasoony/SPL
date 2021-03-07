package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.ConnectionsImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StompMessagingProtocolImpl implements StompMessagingProtocol {

    private boolean shouldTerminate = false;
    int connectionId;
    ConnectionsImpl<MessageFrame> connections;

    private Map< String, Map<Integer,String> > topicMap;
    private Map<Integer, ConnectionHandler> handlerMap;
    private Map< String, User> userMap ;
    private String userName = "";
    @Override
    public void start(int connectionId, ConnectionsImpl<MessageFrame> connections) {

        this.connectionId = connectionId;
        this.connections = connections;
        this.topicMap = connections.getTopicMap();
        this.handlerMap = connections.getHandlerMap();
        this.userMap =  connections.getUserMap();

    }

    @Override
    public void process(MessageFrame frame) { //assume each frame contains receipt id

        String command = frame.getCommand();

        if (command.equals("SEND")) send(frame);
        else if (command.equals("SUBSCRIBE")) subscribe(frame);
        else if (command.equals("UNSUBSCRIBE")) unsubscribe(frame);
        else if (command.equals("CONNECT")) connect(frame);
        else if (command.equals("DISCONNECT")) disconnect(frame);


    }



    private MessageFrame createMessage(MessageFrame frame){

        String topic = getKeyValue(frame, "destination");
        List<Pair<String,String>> headers = new LinkedList<>();
        headers.add(new Pair<>("Message-id" ,connections.getAndIncrementMsgId()));
        headers.add(new Pair<>("destination" , topic));
        return new MessageFrame("MESSAGE" , headers, frame.getFrameBody());
    }

    private void send(MessageFrame frame) {
        String topic = getKeyValue(frame, "destination");
        connections.send(topic, createMessage(frame));
    }



    private void subscribe(MessageFrame frame) {

        String topic = getKeyValue(frame,"destination");
        String subId = getKeyValue(frame,"id");

        if(topicMap.containsKey(topic) == false)
            topicMap.put(topic,new ConcurrentHashMap<>());

        topicMap.get(topic).put(connectionId,subId);

        connections.send(connectionId, receiptFrame(getKeyValue(frame,"receipt")));
    }

    private MessageFrame receiptFrame(String receipt) {
        List<Pair<String,String>> headersList = new LinkedList<>();
        headersList.add(new Pair<>("receipt-id", receipt));
        return new MessageFrame("RECEIPT", headersList, "");
    }

    private String getKeyValue(MessageFrame frame,String key) {
        String value = null;
        for (Pair<String, String> p : frame.getHeadersList())
            if (p.getFirst().equals(key))
                value = p.getSecond();
        return value;
    }


    private void unsubscribe(MessageFrame frame) {

        String subId = getKeyValue(frame,"id");
        for (Map<Integer,String> subscribersMap : topicMap.values() )
                if (subscribersMap.containsKey(connectionId) && subscribersMap.get(connectionId).equals(subId))
                    subscribersMap.remove(connectionId);

        connections.send(connectionId, receiptFrame(getKeyValue(frame,"receipt")));

    }


    private void connect(MessageFrame frame) {

        String userName = getKeyValue(frame,"login");
        String passcode = getKeyValue(frame,"passcode");

        if (userMap.containsKey(userName) == false) { //new user
            User newUser = new User(userName, passcode);
            userMap.put(userName, newUser);
            connections.send(connectionId, connectedFrame(frame));
            this.userName = userName;

        }
        else { // existing user
            User user = userMap.get(userName);

            if (user.getPasscode().equals(passcode) == false)
                connections.send(connectionId, errorFrame("Wrong password", frame));
            else if (user.isLogged())
                connections.send(connectionId, errorFrame("User already logged in", frame));
            else {
                user.setLogged(true);
                connections.send(connectionId, connectedFrame(frame));
                this.userName = userName;
            }
        }
    }


    private MessageFrame connectedFrame(MessageFrame frame) {
        String version = null;
        for (Pair<String, String> p : frame.getHeadersList())
            if (p.getFirst().equals("accept-version"))
                version = p.getSecond();


        List<Pair<String, String>> headersList = new LinkedList<>();
        headersList.add(new Pair<>("version", version));

        return new MessageFrame("CONNECTED", headersList, "");

    }


    private MessageFrame errorFrame(String error, MessageFrame frame) {
        String receipt = getKeyValue(frame, "receipt");

        List<Pair<String, String>> headersList = new LinkedList<>();
        headersList.add(new Pair<>("receipt-id", receipt));
        headersList.add(new Pair<>("message", error));
        String body = "The message:\n -----\n" + frame.toString() + "\n-----";
        shouldTerminate = true;
        return new MessageFrame("ERROR", headersList, body);
    }

    private void disconnect(MessageFrame frame) {
        userMap.get(userName).setLogged(false);
        connections.send(connectionId, receiptFrame(getKeyValue(frame,"receipt")));
        connections.disconnect(connectionId);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
