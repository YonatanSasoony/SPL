package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {

    private Squad squad;

    @BeforeEach
    public void setUp() {
        squad = Squad.getInstance();
    }

    @Test
    public void testGetAgentsNames() {

        Agent[] agentsArr = createAgents();

        squad.load(agentsArr);

        List<String> serialsList = new LinkedList<String>();
        serialsList.add("001");
        serialsList.add("002");
        serialsList.add("004");

        List<String> agentsNamesList = squad.getAgentsNames(serialsList);

        assertTrue(agentsNamesList.contains("yoni"));
        assertTrue(agentsNamesList.contains("yossy"));
        assertFalse(agentsNamesList.contains("003"));
        assertFalse(agentsNamesList.contains("yoda"));
        assertFalse(agentsNamesList.contains(null));

    }

    @Test
    public void testReleaseAgents() {

        Agent[] agentsArr = createAgents();

        agentsArr[0].acquire();
        agentsArr[1].acquire();
        agentsArr[2].acquire();
        squad.load(agentsArr);

        List<String> serialsList = new LinkedList<String>();
        serialsList.add("001");

        squad.releaseAgents(serialsList);

        assertTrue(agentsArr[0].isAvailable());
        assertFalse(agentsArr[1].isAvailable());
        assertTrue(agentsArr[3].isAvailable());

    }

    @Test
    public void testGetAgents() {

        Agent[] agentsArr = createAgents();

        squad.load(agentsArr);

        List<String> serialsList = new LinkedList<String>();
        serialsList.add("001");
        serialsList.add("002");
        serialsList.add("003");
        serialsList.add("004");

        assertTrue(squad.getAgents(serialsList));
        agentsArr[2].acquire();
        assertFalse(squad.getAgents(serialsList));

    }

    private Agent[] createAgents() {
//
      Agent[] agentsArr = new Agent [4];
//        agentsArr[0] = new Agent();
//        agentsArr[0].setName("yossy");
//        agentsArr[0].setSerialNumber("001");
//        agentsArr[1] = new Agent();
//        agentsArr[1].setName("yoni");
//        agentsArr[1].setSerialNumber("002");
//        agentsArr[2] = new Agent();
//        agentsArr[2].setName("yoda");
//        agentsArr[2].setSerialNumber("003");
//        agentsArr[3] = new Agent();
//        agentsArr[3].setName("barack");
//        agentsArr[3].setSerialNumber("004");
     return agentsArr;
    }

    @Test
    public void testSendAgents() {
        Agent[] agentsArr = createAgents();
        squad.load(agentsArr);

        List<String> serialsList = new LinkedList<String>();
        serialsList.add("001");
        serialsList.add("002");
        serialsList.add("003");

        squad.sendAgents(serialsList, 10000);
        agentsArr[1].isAvailable();
        List<String> serialsList2 = new LinkedList<String>();
        serialsList.add("001");

        List<String> serialsList3 = new LinkedList<String>();
        serialsList.add("004");

        List<String> serialsList4 = new LinkedList<String>();

        assertTrue(squad.getAgents(serialsList3));
        assertTrue(squad.getAgents(serialsList4));
        assertFalse(squad.getAgents(serialsList2));

    }

}

