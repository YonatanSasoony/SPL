package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.List;

public class ReleaseAgentsEvent implements Event<Boolean> {
    private List<String> agentSerialNumbers;
    private MissionInfo missionInfo;

    public String getM() {
        return m;
    }

    String m;

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public ReleaseAgentsEvent(List<String> agentSerialNumbers, MissionInfo missionInfo, String m) {
        this.agentSerialNumbers = agentSerialNumbers;
        this.missionInfo = missionInfo;
        this.m = m;
    }

    public List<String> getSerialNumbers() {
        return agentSerialNumbers;
    }
}
