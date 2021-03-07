package bgu.spl.mics.application.passiveObjects;

import com.google.gson.JsonArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * That's where Q holds his gadget (e.g. an explosive pen was used in GoldenEye, a geiger counter in Dr. No, etc).
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

    private static class InventoryHolder {
        private static Inventory instance = new Inventory();
    }

    private List<String> gadgets;

    private Inventory() {
        gadgets = new LinkedList<>();
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Inventory getInstance() {
        return InventoryHolder.instance;
    }

    /**
     * Initializes the inventory. This method adds all the items given to the gadget
     * inventory.
     * <p>
     *
     * @param inventory Data structure containing all data necessary for initialization
     *                  of the inventory.
     */
    public void load(String[] inventory) {
        for (String item : inventory)
            gadgets.add(item);
    }

    /**
     * acquires a gadget and returns 'true' if it exists.
     * <p>
     *
     * @param gadget Name of the gadget to check if available
     * @return ‘false’ if the gadget is missing, and ‘true’ otherwise
     */
    public boolean getItem(String gadget) {

        Iterator<String> iter = gadgets.iterator();

        while (iter.hasNext()) {
            String temp = iter.next();
            if (temp.equals(gadget)) {
                iter.remove();
                return true;
            }
        }
        return false;
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Gadget> which is a
     * List of all the gadgets in the inventory.
     * This method is called by the main method in order to generate the output.
     */
    public void printToFile(String filename) {

        JsonArray gadgetsToJ = new JsonArray();

        for (String gadget : gadgets)
            gadgetsToJ.add(gadget);

        try {
            Files.write(Paths.get(filename), gadgetsToJ.toString().getBytes());
        } catch (IOException ex) {
        }

    }
}
