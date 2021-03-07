package bgu.spl.mics;

import java.util.List;

public class Pair {

    private Integer serialNumber;
    private List<String> agentNames;

    public Pair(Integer serialNumber, List<String> agentSerialNumbers) {
        this.serialNumber = serialNumber;
        this.agentNames = agentSerialNumbers;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public List<String> getAgentNames() {
        return agentNames;
    }
}
