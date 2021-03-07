package bgu.spl.mics.application.publishers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.BurnThemAllBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 * <p>
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

    private Timer timer;
    private int duration;

    public TimeService(int duration) {
        super("TimeService");
        timer = new Timer();
        this.duration = duration * 100;
    }


    @Override
    protected void initialize() {
        for (int i = 0; i <= duration; i += 100) {
            final int j = i;
            timer.schedule(new TimerTask() {
                public void run() {
                    getSimplePublisher().sendBroadcast(new TickBroadcast(j / 100));
                }
            }, j);
        }
    }


    @Override
    public void run() {

        initialize();
        System.out.println("TS init");
        try {
            sleep(duration);
        } catch (InterruptedException ex) {
        }

        getSimplePublisher().sendBroadcast(new BurnThemAllBroadcast());
        timer.cancel();

    }

}
