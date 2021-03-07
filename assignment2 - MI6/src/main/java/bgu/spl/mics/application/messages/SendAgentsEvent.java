package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import java.util.List;

public class SendAgentsEvent implements Event<Integer> {

    private List<String> serialNumbers;
    private int time;
    private MissionInfo missionInfo;
    String m;

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public String getM() {
        return m;
    }

    public SendAgentsEvent(List<String> serialNumbers, int time, MissionInfo missionInfo, String m) {
        this.serialNumbers = serialNumbers;
        this.time = time;
        this.missionInfo = missionInfo;
        this.m = m;
    }

    public List<String> getSerialNumbers() {
        return serialNumbers;
    }

    public int getTime() {
        return time;
    }
}
