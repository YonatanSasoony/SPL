package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.BurnThemAllBroadcast;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

    private static class qHolder {
        public static Q instance = new Q();
    }

    private Inventory inventory;
    private MessageBroker msg = MessageBrokerImpl.getInstance();
    ;

    private int tick;

    private Q() {
        super("Q");
        inventory = Inventory.getInstance();
        this.tick = 0;

    }

    public static Q getInstance() {


        return qHolder.instance;

    }

    @Override
    protected void initialize() {

        msg.register(this);
        Callback<GadgetAvailableEvent> callbackGAE = (GadgetAvailableEvent e) -> {
            System.out.println(getName() + " gets gadgets for " + e.getMissionInfo().getMissionName() + " " + e.getM());
            if (inventory.getItem(e.getGadget())) {
                System.out.println(e.getGadget() + " available");
                complete(e, getTick());

            } else {
                System.out.println(e.getGadget() + " not available");
                complete(e, -1);

            }
        };
        subscribeEvent(GadgetAvailableEvent.class, callbackGAE);

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

    }


    private void setTick(int tick) {
        this.tick = tick;
    }

    private int getTick() {
        return tick;
    }

}
