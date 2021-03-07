package bgu.spl.mics.application.passiveObjects;

import java.util.*;

import static java.lang.Thread.sleep;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Squad {

    private Map<String, Agent> agents;
    private boolean isTerminated;

    private static class SquadHolder {
        private static Squad instance = new Squad();
    }

    private Squad() {
        agents = new HashMap<>();
        isTerminated = false;
    }


    /**
     * Retrieves the single instance of this class.
     */
    public static Squad getInstance() {
        return SquadHolder.instance;
    }

    /**
     * Initializes the squad. This method adds all the agents to the squad.
     * <p>
     *
     * @param agents Data structure containing all data necessary for initialization
     *               of the squad.
     */
    public void load(Agent[] agents) {

        this.agents.clear();
        for (Agent agent : agents)
            this.agents.put(agent.getSerialNumber(), agent);
    }

    /**
     * Releases agents.
     */
    public synchronized void releaseAgents(List<String> serials) {

        if (serials != null && serials.size() != 0)
            if (serials.get(0).equals("BURN")) {
                isTerminated = true;
                notifyAll();
                return;
            }

        for (String serial : serials)
            agents.get(serial).release();
        notifyAll();

        System.out.println("agents released");
    }

    /**
     * simulates executing a mission by calling sleep.
     *
     * @param time milliseconds to sleep
     */
    public void sendAgents(List<String> serials, int time) {

        try {
            sleep(time * 100);
        } catch (InterruptedException ex) {
        }
        releaseAgents(serials);

    }


    /**
     * acquires an agent, i.e. holds the agent until the caller is done with it
     *
     * @param serials the serial numbers of the agents
     * @return ‘false’ if an agent of serialNumber ‘serial’ is missing, and ‘true’ otherwise
     */
    public synchronized boolean getAgents(List<String> serials) {

        for (String serial : serials)
            if (agents.containsKey(serial) == false)
                return false;

        for (String serial : serials) {
            Agent agent = agents.get(serial);
            while (agent.isAvailable() == false) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                }
                if (isTerminated)
                    return false;
            }
            agent.acquire();
        }

        return true;
    }

    /**
     * gets the agents names
     *
     * @param serials the serial numbers of the agents
     * @return a list of the names of the agents with the specified serials.
     */
    public List<String> getAgentsNames(List<String> serials) {

        List<String> agentsNames = new LinkedList<>();
        for (String serial : serials)
            agentsNames.add(agents.get(serial).getName());
        return agentsNames;
    }

}
