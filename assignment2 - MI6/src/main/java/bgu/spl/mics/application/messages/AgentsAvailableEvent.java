package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Pair;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

import java.util.LinkedList;
import java.util.List;

public class AgentsAvailableEvent implements Event<Pair> {

    private List<String> serialAgentsNumbers;
    private int duration;
    MissionInfo missionInfo;
    String m;

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public String getM() {
        return m;
    }

    public AgentsAvailableEvent(List<String> serialAgentsNumbers, int duration, MissionInfo missionInfo, String m) {
        this.serialAgentsNumbers = new LinkedList<>(serialAgentsNumbers);
        this.duration = duration;
        this.missionInfo = missionInfo;
        this.m = m;
    }

    public List<String> getSerialNumbers() {
        return serialAgentsNumbers;
    }


    public int getDuration() {
        return duration;
    }
}
