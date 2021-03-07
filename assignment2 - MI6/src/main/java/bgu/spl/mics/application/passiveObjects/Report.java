package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Report {

    private String missionName;
    private Integer m;
    private Integer moneypenny;
    private List<String> agentsSerialNumbers;
    private List<String> agentsNames;
    private String gadgetName;
    private Integer qTime;
    private Integer timeIssued;
    private Integer timeCreated;

    public Report(String missionName, int m, int moneypenny, List<String> agentsSerialNumbers, List<String> agentsNames,
                  String gadgetName, int qTime, int timeIssued, int timeCreated) {

        this.missionName = missionName;
        this.m = m;
        this.moneypenny = moneypenny;
        this.agentsSerialNumbers = new LinkedList<>(agentsSerialNumbers);
        this.agentsNames = new LinkedList<>(agentsNames);
        this.gadgetName = gadgetName;
        this.qTime = qTime;
        this.timeIssued = timeIssued;
        this.timeCreated = timeCreated;
    }


    /**
     * Retrieves the mission name.
     */
    public String getMissionName() {
        synchronized (this.missionName) {
            return missionName;
        }
    }

    /**
     * Sets the mission name.
     */
    public void setMissionName(String missionName) {
        synchronized (this.missionName) {
            this.missionName = missionName;
        }
    }

    /**
     * Retrieves the M's id.
     */
    public int getM() {
        synchronized (this.m) {
            return m;
        }
    }

    /**
     * Sets the M's id.
     */
    public void setM(int m) {
        synchronized (this.m) {
            this.m = m;
        }
    }

    /**
     * Retrieves the Moneypenny's id.
     */
    public int getMoneypenny() {
        synchronized (this.m) {
            return moneypenny;
        }
    }

    /**
     * Sets the Moneypenny's id.
     */
    public void setMoneypenny(int moneypenny) {
        synchronized (this.moneypenny) {
            this.moneypenny = moneypenny;
        }
    }

    /**
     * Retrieves the serial numbers of the agents.
     * <p>
     *
     * @return The serial numbers of the agents.
     */
    public List<String> getAgentsSerialNumbers() {
        synchronized (this.agentsSerialNumbers) {
            return agentsSerialNumbers;
        }
    }

    /**
     * Sets the serial numbers of the agents.
     */
    public void setAgentsSerialNumbers(List<String> agentsSerialNumbers) {
        synchronized (this.agentsSerialNumbers) {
            this.agentsSerialNumbers = agentsSerialNumbers;
        }
    }

    /**
     * Retrieves the agents names.
     * <p>
     *
     * @return The agents names.
     */
    public List<String> getAgentsNames() {
        synchronized (this.agentsNames) {
            return agentsNames;
        }
    }

    /**
     * Sets the agents names.
     */
    public void setAgentsNames(List<String> agentsNames) {
        synchronized (this.agentsNames) {
            this.agentsNames = agentsNames;
        }
    }

    /**
     * Retrieves the name of the gadget.
     * <p>
     *
     * @return the name of the gadget.
     */
    public String getGadgetName() {
        synchronized (this.gadgetName) {
            return gadgetName;
        }
    }

    /**
     * Sets the name of the gadget.
     */
    public void setGadgetName(String gadgetName) {
        synchronized (this.gadgetName) {
            this.gadgetName = gadgetName;
        }
    }

    /**
     * Retrieves the time-tick in which Q Received the GadgetAvailableEvent for that mission.
     */
    public int getQTime() {
        synchronized (this.qTime) {
            return qTime;
        }
    }

    /**
     * Sets the time-tick in which Q Received the GadgetAvailableEvent for that mission.
     */
    public void setQTime(int qTime) {
        synchronized (this.qTime) {
            this.qTime = qTime;
        }
    }

    /**
     * Retrieves the time when the mission was sent by an Intelligence Publisher.
     */
    public int getTimeIssued() {
        synchronized (this.timeIssued) {
            return timeIssued;
        }
    }

    /**
     * Sets the time when the mission was sent by an Intelligence Publisher.
     */
    public void setTimeIssued(int timeIssued) {
        synchronized (this.timeIssued) {
            this.timeIssued = timeIssued;
        }
    }

    /**
     * Retrieves the time-tick when the report has been created.
     */
    public int getTimeCreated() {
        synchronized (this.timeCreated) {
            return timeCreated;
        }
    }

    /**
     * Sets the time-tick when the report has been created.
     */
    public void setTimeCreated(int timeCreated) {
        synchronized (this.timeCreated) {
            this.timeCreated = timeCreated;
        }
    }
}
