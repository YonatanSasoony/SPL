package bgu.spl.mics.application.passiveObjects;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Agent {

    private String serialNumber;
    private String name;
    private Boolean available;


    public Agent(String serialNumber, String name) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.available = true;
    }

    /**
     * Sets the serial number of an agent.
     */
    public void setSerialNumber(String serialNumber) {
        synchronized (serialNumber) {
            this.serialNumber = serialNumber;
        }
    }

    /**
     * Retrieves the serial number of an agent.
     * <p>
     *
     * @return The serial number of an agent.
     */
    public String getSerialNumber() {
        synchronized (serialNumber) {
            return serialNumber;
        }
    }

    /**
     * Sets the name of the agent.
     */
    public void setName(String name) {
        synchronized (name) {
            this.name = name;
        }
    }

    /**
     * Retrieves the name of the agent.
     * <p>
     *
     * @return the name of the agent.
     */
    public String getName() {
        synchronized (name) {
            return name;
        }
    }

    /**
     * Retrieves if the agent is available.
     * <p>
     *
     * @return if the agent is available.
     */
    public boolean isAvailable() {
        synchronized (available) {
            return available;
        }
    }

    /**
     * Acquires an agent.
     */
    public void acquire() {
        synchronized (available) {
            this.available = false;
        }
    }

    /**
     * Releases an agent.
     */
    public void release() {
        synchronized (available) {
            this.available = true;
        }
    }
}
