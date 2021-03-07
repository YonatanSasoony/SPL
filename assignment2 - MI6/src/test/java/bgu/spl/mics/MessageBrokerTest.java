package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


class IntEvent implements Event<Integer>{}
class SimpleBroadcast implements Broadcast{}

public class MessageBrokerTest {

    MessageBroker msg;
    Publisher intel;
    Subscriber sub;
    IntEvent e;

    @BeforeEach
    public void setUp(){

        msg = MessageBrokerImpl.getInstance();


        e = new IntEvent();
    }
    @Test
    public void testSubscribeEvent(){


        msg.subscribeEvent(e.getClass(),sub);
        msg.sendEvent(e);
        try {
            assertEquals(e,msg.awaitMessage(sub));
        }catch (InterruptedException exp){}



    }

    @Test
    public void testSubscribeBroadcast(){

        Broadcast broad = new SimpleBroadcast();
        msg.subscribeBroadcast(SimpleBroadcast.class,sub);
        msg.sendBroadcast(broad);
        try {
            assertEquals(broad,msg.awaitMessage(sub));
        }catch (InterruptedException exp){exp.printStackTrace();};
    }

    @Test
    public void testComplete(){

        msg.subscribeEvent(e.getClass(),sub);
        Future<Integer> fut = msg.sendEvent(e);
        msg.complete(e,42);
        assertEquals(42,fut.get());
        assertTrue(fut.isDone());

    }

    @Test
    public void testSendBroadcast(){
        Broadcast broad = new SimpleBroadcast();
        msg.sendBroadcast(broad);

        try {
            assertEquals(e,msg.awaitMessage(sub));
        }catch (InterruptedException exp){exp.printStackTrace();};
    }

    @Test
    public void testSendEvent(){

        msg.subscribeEvent(e.getClass(),sub);
        Future<Integer> fut = msg.sendEvent(e);
        assertFalse(fut.isDone());
    }

}
