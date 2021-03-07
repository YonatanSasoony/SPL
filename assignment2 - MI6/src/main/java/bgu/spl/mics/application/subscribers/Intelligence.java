package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BurnThemAllBroadcast;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;


import java.util.HashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Publisher only. FALSE!!!!!!!
 * Holds a list of Info objects and sends them
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

    private MessageBroker msg = MessageBrokerImpl.getInstance();
    private Map<Integer, List<MissionInfo> > missionsMap;

    public Intelligence(List<MissionInfo> missions) {
        super("Intelligence");
        missionsMap = new HashMap<>();
        for (MissionInfo mission : missions)
            if (missionsMap.containsKey(mission.getTimeIssued()))
                missionsMap.get(mission.getTimeIssued()).add(mission);
            else {
                missionsMap.put(mission.getTimeIssued(), new LinkedList<MissionInfo>());
                missionsMap.get(mission.getTimeIssued()).add(mission);
            }
    }

    @Override
    protected void initialize() {

        msg.register(this);

        Callback<BurnThemAllBroadcast> callbackTRB = (BurnThemAllBroadcast b) -> {
            System.out.println(getName() + " got last tick");
            terminate();
        };
        subscribeBroadcast(BurnThemAllBroadcast.class, callbackTRB);

        Callback<TickBroadcast> callbackTB = (TickBroadcast b) -> {

            System.out.println(getName() + " got tick " + b.getTick());
            if (missionsMap.containsKey(b.getTick())) {
                for (MissionInfo mission : missionsMap.get(b.getTick())) {
                    System.out.println(getName() + " sent mission");
                    msg.sendEvent(new MissionReceivedEvent(mission));
                }
            }

        };
        subscribeBroadcast(TickBroadcast.class, callbackTB);
    }

}
