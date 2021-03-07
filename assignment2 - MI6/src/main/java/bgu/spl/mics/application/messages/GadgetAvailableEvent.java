package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.MissionInfo;

public class GadgetAvailableEvent implements Event<Integer> {

    private String gadget;
    MissionInfo missionInfo;
    String m;

    public MissionInfo getMissionInfo() {
        return missionInfo;
    }

    public String getM() {
        return m;
    }

    public GadgetAvailableEvent(String gadget, MissionInfo missionInfo, String m) {
        this.gadget = gadget;
        this.missionInfo = missionInfo;
        this.m = m;
    }

    public String getGadget() {
        return gadget;
    }



}
