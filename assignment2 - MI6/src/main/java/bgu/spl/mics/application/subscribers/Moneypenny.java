package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Squad;
import bgu.spl.mics.Pair;

import java.util.LinkedList;


/**
 * Only this type of Subscriber can access the squad.
 * There are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

    private Squad squad;
    private static int serialCount = 0;
    private int serialNumber;
    private MessageBroker msg = MessageBrokerImpl.getInstance();

    public Moneypenny() {
        super("MP " + serialCount);
        squad = Squad.getInstance();
        this.serialNumber = serialCount;
        serialCount++;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    @Override
    protected void initialize() {

        msg.register(this);

        Callback<SendAgentsEvent> callbackSEE = (SendAgentsEvent e) -> {
            System.out.println(getName() + " sent agents for " + e.getMissionInfo().getMissionName() + " " + e.getM());
            squad.sendAgents(e.getSerialNumbers(), e.getTime());
        };
        if (getSerialNumber() % 2 == 1)
            subscribeEvent(SendAgentsEvent.class, callbackSEE);


        Callback<AgentsAvailableEvent> callbackAAE = (AgentsAvailableEvent e) -> {
            System.out.println(getName() + " checks for agents for mission " + e.getMissionInfo().getMissionName() + " for: " + e.getM());
            if (squad.getAgents(e.getSerialNumbers())) {
                complete(e, new Pair(getSerialNumber(), squad.getAgentsNames(e.getSerialNumbers())));
                System.out.println(getName()+ " completes checking agents");
            }
            else
                complete(e, new Pair(-1, null));
        };
        if (getSerialNumber() % 2 == 0)
            subscribeEvent(AgentsAvailableEvent.class, callbackAAE);


        Callback<ReleaseAgentsEvent> callbackREE = (ReleaseAgentsEvent e) -> {

            System.out.println(getName() + " releases agents for " + e.getMissionInfo().getMissionName() + " " + e.getM());
            squad.releaseAgents(e.getSerialNumbers());
        };
        if (getSerialNumber() % 2 == 1)
            subscribeEvent(ReleaseAgentsEvent.class, callbackREE); //


        Callback<BurnThemAllBroadcast> callbackBTA = (BurnThemAllBroadcast b) -> {
            System.out.println(getName() + " got last tick");
            if (getSerialNumber() % 2 == 1) {
                LinkedList<String> burnList = new LinkedList<>();
                burnList.add("BURN");
                System.out.println(getName() + " releases ALL@@@@@@@@@@@");
                squad.releaseAgents(burnList);
            }
            terminate();
        };
        subscribeBroadcast(BurnThemAllBroadcast.class, callbackBTA);

        Callback<TickBroadcast> callbackTB = (TickBroadcast b) -> {

            System.out.println(getName() + " got tick " + b.getTick());
        };
        subscribeBroadcast(TickBroadcast.class, callbackTB);


    }

}
