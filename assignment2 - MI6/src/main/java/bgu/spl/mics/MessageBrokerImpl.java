package bgu.spl.mics;


import bgu.spl.mics.application.messages.*;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

    private static class MessageBrokerImplHolder {
        private static MessageBrokerImpl instance = new MessageBrokerImpl();
    }

    private Map<Class, LinkedList<Subscriber>> subscribedMap; // links between message type to its subscribers
    private Map<Class, LinkedBlockingQueue<Subscriber>> roundRobinMap; // links between message type to the round robin queue
    private Map<Subscriber, LinkedBlockingQueue<Message>> queueMap; //queue of messages of each subscriber
    private Map<Message, Future> futureMap; // links between each message to its future


    private MessageBrokerImpl() {
        subscribedMap = new ConcurrentHashMap<>();
        roundRobinMap = new ConcurrentHashMap<>();
        queueMap = new ConcurrentHashMap<>();
        futureMap = new ConcurrentHashMap<>();


    }

    /**
     * Retrieves the single instance of this class.
     */
    public static MessageBroker getInstance() {
        return MessageBrokerImplHolder.instance;
    }

    @Override
    public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber s) {

        synchronized (subscribedMap) {

            if (subscribedMap.containsKey(type) == false)
                subscribedMap.put(type, new LinkedList<>());
            subscribedMap.get(type).add(s);

            if (roundRobinMap.containsKey(type) == false)
                roundRobinMap.put(type, new LinkedBlockingQueue<>());
            roundRobinMap.get(type).add(s);
        }
    }

    @Override
    public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber s) {

        synchronized (subscribedMap) {

            if (subscribedMap.containsKey(type) == false)
                subscribedMap.put(type, new LinkedList<>());
            subscribedMap.get(type).add(s);
        }
    }

    @Override
    public <T> void complete(Event<T> e, T result) {
        if (futureMap.get(e) != null)
            futureMap.get(e).resolve(result);
    }

    @Override
    public void sendBroadcast(Broadcast b) {

        synchronized (queueMap) {
            synchronized (subscribedMap) {
                if (b instanceof BurnThemAllBroadcast) {
                    
                    for (Subscriber sub : subscribedMap.get(b.getClass())) {

                        LinkedBlockingQueue<Message> queueToBurn = queueMap.get(sub);
                        while (queueToBurn.isEmpty() == false) {
                            Message message = queueToBurn.poll();
                            if (message instanceof Event)
                                complete((Event) message, null);

                        }
                        queueToBurn.add(b);
                    }
                    return;
                }

                LinkedList<Subscriber> listOfSubscribers = subscribedMap.get(b.getClass());
                if (listOfSubscribers != null && listOfSubscribers.isEmpty() == false) {// there are no subscribers who subscribed
                    for (Subscriber sub : listOfSubscribers) {
                        queueMap.get(sub).add(b);
                    }
                }
            }
        }
    }

    @Override
    public <T> Future<T> sendEvent(Event<T> e) {

        Future<T> future = new Future<>();
        futureMap.put(e, future);
        LinkedBlockingQueue<Subscriber> queueOfRoundRobin = roundRobinMap.get(e.getClass());

        synchronized (roundRobinMap) {
            if (queueOfRoundRobin.isEmpty()) {
               // future.resolve(null); // there arent subscriber to handle this event
                 complete(e,null);
                System.out.println(e.toString() + " no subscribers for this event, future result = null");
                return null;
            } else {
                Subscriber sub = queueOfRoundRobin.poll();
                queueMap.get(sub).add(e);
                queueOfRoundRobin.add(sub);
                System.out.println(e.toString() + " message added to " + sub.getName());
            }

            System.out.println("event: " + e.toString() + " added to future map******* resolve?: " + future.isDone());
            return future;
        }
    }

    @Override
    public void register(Subscriber s) {
        queueMap.put(s, new LinkedBlockingQueue<>());
    }

    @Override
    public void unregister(Subscriber s) {

        synchronized (queueMap) {
            synchronized (subscribedMap) {
                synchronized (roundRobinMap) {

                    for (LinkedList subscribedList : subscribedMap.values())
                        subscribedList.remove(s);

                    for (LinkedBlockingQueue roundRobinQueue : roundRobinMap.values())
                        roundRobinQueue.remove(s);

                    queueMap.remove(s);

                }
            }
        }
    }

    @Override
    public Message awaitMessage(Subscriber s) throws InterruptedException {

        if (!queueMap.containsKey(s))
            throw new IllegalStateException();

        LinkedBlockingQueue<Message> q = queueMap.get(s);
        return q.take();

    }
}
