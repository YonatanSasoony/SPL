package bgu.spl.mics.application.passiveObjects;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing information about a mission.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class MissionInfo {
    private String missionName;
    private List<String> serialAgentsNumbers;
    private String gadget;
    private Integer timeIssued;
    private Integer timeExpired;
    private Integer duration;

    public MissionInfo(String missionName, List<String> serialAgentsNumbers, String gadget, int timeIssued, int timeExpired, int duration) {
        this.missionName = missionName;
        this.serialAgentsNumbers = new LinkedList<>(serialAgentsNumbers);
        this.gadget = gadget;
        this.timeIssued = timeIssued;
        this.timeExpired = timeExpired;
        this.duration = duration;
    }

    /**
     * Sets the name of the mission.
     */
    public void setMissionName(String missionName) {
        synchronized (this.missionName) {
            this.missionName = missionName;
        }
    }

    /**
     * Retrieves the name of the mission.
     */
    public String getMissionName() {
        synchronized (missionName) {
            return missionName;
        }
    }

    /**
     * Sets the serial agent number.
     */
    public void setSerialAgentsNumbers(List<String> serialAgentsNumbers) {
        synchronized (this.serialAgentsNumbers) {
            this.serialAgentsNumbers.clear();
            for (String serial : serialAgentsNumbers)
                this.serialAgentsNumbers.add(serial);
        }
    }

    /**
     * Retrieves the serial agent number.
     */
    public List<String> getSerialAgentsNumbers() {
        synchronized (serialAgentsNumbers) {
            return serialAgentsNumbers;
        }
    }

    /**
     * Sets the gadget name.
     */
    public void setGadget(String gadget) {
        synchronized (this.gadget) {
            this.gadget = gadget;
        }
    }

    /**
     * Retrieves the gadget name.
     */
    public String getGadget() {
        synchronized (gadget) {
            return gadget;
        }
    }

    /**
     * Sets the time the mission was issued in milliseconds.
     */
    public void setTimeIssued(int timeIssued) {
        synchronized (this.timeIssued) {
            this.timeIssued = timeIssued;
        }
    }

    /**
     * Retrieves the time the mission was issued in milliseconds.
     */
    public int getTimeIssued() {
        synchronized (timeIssued) {
            return timeIssued;
        }
    }

    /**
     * Sets the time that if it that time passed the mission should be aborted.
     */
    public void setTimeExpired(int timeExpired) {
        synchronized (this.timeExpired) {
            this.timeExpired = timeExpired;
        }
    }

    /**
     * Retrieves the time that if it that time passed the mission should be aborted.
     */
    public int getTimeExpired() {
        synchronized (timeExpired) {
            return timeExpired;
        }
    }

    /**
     * Sets the duration of the mission in time-ticks.
     */
    public void setDuration(int duration) {
        synchronized (this.duration) {
            this.duration = duration;
        }
    }

    /**
     * Retrieves the duration of the mission in time-ticks.
     */
    public int getDuration() {
        synchronized (duration) {
            return duration;
        }
    }
}
