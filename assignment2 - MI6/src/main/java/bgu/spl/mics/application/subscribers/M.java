package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Report;

import java.util.List;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

    private static int serialCount = 0;
    private int serialNumber;
    private MessageBroker msg = MessageBrokerImpl.getInstance();
    private Diary diary;
    private int tick;

    public M() {
        super("M " + serialCount);
        this.serialNumber = serialCount;
        serialCount++;
        diary = Diary.getInstance();
        tick = 0;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    @Override
    protected void initialize() {

        msg.register(this);

        Callback<TickBroadcast> callbackTB = (TickBroadcast b) -> {
            System.out.println(getName() + " got tick " + b.getTick());
            setTick(b.getTick());
        };
        subscribeBroadcast(TickBroadcast.class, callbackTB);

        Callback<BurnThemAllBroadcast> callbackTRB = (BurnThemAllBroadcast b) -> {
            System.out.println(getName() + " got last tick");
            terminate();
        };
        subscribeBroadcast(BurnThemAllBroadcast.class, callbackTRB);

        Callback<MissionReceivedEvent> callbackMRE = (MissionReceivedEvent e) -> {
            System.out.println(getName() + " got mission " + e.getMissionInfo().getMissionName());
            diary.incrementTotal();

            MissionInfo missionInfo = e.getMissionInfo();
            String gadget = missionInfo.getGadget();
            List<String> agentSerialNumbers = missionInfo.getSerialAgentsNumbers();
            int duration = missionInfo.getDuration();
            int timeExpired = missionInfo.getTimeExpired();

            System.out.println(getName() +" sends AgentsAvailableEvent for mission "+missionInfo.getMissionName());
            Future<Pair> agentsAvailableFuture = getSimplePublisher().sendEvent(new AgentsAvailableEvent(agentSerialNumbers, duration, e.getMissionInfo(), getName()));
            System.out.println(getName()+" waits for AgentsAvailableEvent");
            Pair agentsAvailablePair = agentsAvailableFuture.get(); //waits
            System.out.println(getName()+" done waiting for AgentsAvailableEvent");

            if (agentsAvailablePair == null) // no subscriber to handle this event
                return;

            if (agentsAvailablePair.getSerialNumber() == -1) return; // agents dont exist

            System.out.println(getName() +" sends GadgetAvailableEvent for mission "+missionInfo.getMissionName());
            Future<Integer> gadgetAvailableFuture = msg.sendEvent(new GadgetAvailableEvent(gadget, e.getMissionInfo(), getName()));

            System.out.println(getName()+" waits for GadgetAvailableEvent");
            if (gadgetAvailableFuture == null){
                System.out.println(getName()+"           MADAFAKAAAAAAAAAA **************************8");
                return;
            }
            Integer qTime = gadgetAvailableFuture.get(); // waits

            System.out.println(getName()+" done waiting for GadgetAvailableEvent");

            if (qTime == null) // no subscriber to handle this event
                return;
            if (qTime == -1 || timeExpired < getTick()) { // cant continue - no such gadget or time expired
                System.out.println(getName()+"aborts mission "+missionInfo.getMissionName());
                getSimplePublisher().sendEvent(new ReleaseAgentsEvent(agentSerialNumbers, e.getMissionInfo(), getName())); // releases agents
                return;
            }

            getSimplePublisher().sendEvent(new SendAgentsEvent(agentSerialNumbers, duration, e.getMissionInfo(), getName())); //  executing the mission
            writeReport(agentsAvailableFuture, e,  qTime);
        };
        subscribeEvent(MissionReceivedEvent.class, callbackMRE);
    }
    private void writeReport(Future<Pair> agentsAvailableFuture,MissionReceivedEvent e, int qTime){

        int serialMP = agentsAvailableFuture.get().getSerialNumber();
        Pair agentsAvailablePair = agentsAvailableFuture.get();
        List<String> agentNames = agentsAvailablePair.getAgentNames();
        int timeCreated = getTick();
        MissionInfo missionInfo = e.getMissionInfo();
        List<String> agentSerialNumbers = missionInfo.getSerialAgentsNumbers();
        String gadget = missionInfo.getGadget();

        System.out.println(getName() + " sent report for " + e.getMissionInfo().getMissionName());
        Report report = new Report(missionInfo.getMissionName(), getSerialNumber(), serialMP, agentSerialNumbers,
                agentNames, gadget, qTime, missionInfo.getTimeIssued(), timeCreated);
        diary.addReport(report);

    }

}


