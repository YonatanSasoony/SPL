package bgu.spl.mics.application;

import bgu.spl.mics.MessageBroker;
import bgu.spl.mics.MessageBrokerImpl;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;
import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {

    private static HashMap<String, Object> Read(String inputFile) {

        HashMap<String, Object> data = new HashMap<>();

        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(inputFile));
            HashMap<String, Object> map = gson.fromJson(reader, HashMap.class);


            ArrayList<String> inventoryJson = (ArrayList) map.get("inventory");
            ArrayList<LinkedTreeMap<String, String>> squadJson = (ArrayList) map.get("squad");
            LinkedTreeMap servicesJson = (LinkedTreeMap) map.get("services");


            String[] inventory = new String[inventoryJson.size()];
            Agent[] squad = new Agent[squadJson.size()];
            for (int i = 0; i < inventory.length; i++)
                inventory[i] = inventoryJson.get(i);

            for (int i = 0; i < squad.length; i++) {
                String name = squadJson.get(i).get("name");
                String serialNumber = squadJson.get(i).get("serialNumber");
                squad[i] = new Agent(serialNumber, name);
            }

            double numOfM = (double) servicesJson.get("M");
            double numOfMP = (double) servicesJson.get("Moneypenny");
            double time = (double) servicesJson.get("time");
            data.put("M", (int) numOfM);
            data.put("MP", (int) numOfMP);
            data.put("time", (int) time);
            data.put("inventory", inventory);
            data.put("squad", squad);
            ArrayList<LinkedTreeMap<String, Object>> intelligenceJson = (ArrayList) servicesJson.get("intelligence");
            List<Intelligence> intelligences = new LinkedList<>();


            for (int i = 0; i < intelligenceJson.size(); i++) {
                LinkedTreeMap intellij = intelligenceJson.get(i);
                ArrayList missions = (ArrayList) intellij.get("missions");
                List<MissionInfo> missionInfoList = new LinkedList<>();
                for (int j = 0; j < missions.size(); j++) {
                    LinkedTreeMap mission = (LinkedTreeMap) missions.get(j);
                    List<String> serialAgentNumber = (List) mission.get("serialAgentsNumbers");
                    double duration = (double) mission.get("duration");
                    String gadget = (String) mission.get("gadget");
                    String name = (String) mission.get("name"); // TODO CHECK which name need
                    double timeExpired = (double) mission.get("timeExpired");
                    double timeIssued = (double) mission.get("timeIssued");
                    MissionInfo missionInfo = new MissionInfo(name, serialAgentNumber, gadget, (int) timeIssued, (int) timeExpired, (int) duration);
                    missionInfoList.add(missionInfo);
                }
                Intelligence newIntell = new Intelligence(missionInfoList);
                intelligences.add(newIntell);
            }
            data.put("intelligence", intelligences);
            return data;

        } catch (FileNotFoundException e) {
            return null;
        }

    }

    private static List<Thread> threadLoader(HashMap<String, Object> data){

        Q q = Q.getInstance();

        int numOfMP = (int) data.get("MP");
        int terminateTime = (int) data.get("time");
        int numOfM = (int) data.get("M");
        LinkedList<Intelligence> intelligencesList = (LinkedList<Intelligence>) data.get("intelligence");
        Agent[] agentsArr = (Agent[]) data.get("squad");
        String[] gadgetArr = (String[]) data.get("inventory");

        Inventory.getInstance().load(gadgetArr);
        Squad.getInstance().load(agentsArr);


        List<Thread> threadList = new LinkedList<>();

        threadList.add(new Thread(q));

        for (int i = 0; i < numOfMP; i++)
            threadList.add(new Thread(new Moneypenny()));

        for (int i = 0; i < numOfM; i++)
            threadList.add(new Thread(new M()));

        for (Intelligence intell : intelligencesList)
            threadList.add(new Thread(intell));


        threadList.add(new Thread(new TimeService(terminateTime)));

        for (Thread t : threadList)
            t.start();

        return threadList;

    }


    public static void main(String[] args) {

        MessageBroker msg = MessageBrokerImpl.getInstance();

        String inputFile = args[0];
        String inventoryOutputFile = args[1];
        String diaryOutputFile = args[2];






        HashMap<String, Object> data = Read(inputFile);
        List<Thread> threadList = threadLoader(data);

        for (Thread t : threadList) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }

        Inventory.getInstance().printToFile(inventoryOutputFile);
        Diary.getInstance().printToFile(diaryOutputFile);
        System.out.println("DONE :)");


    }
}
