package bgu.spl.mics.application.passiveObjects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

    private static class DiaryHolder {
        private static Diary instance = new Diary();
    }

    private List<Report> reports;
    Integer totalReports;


    private Diary() {
        reports = new LinkedList<>();
        totalReports = 0;
    }

    /**
     * Retrieves the single instance of this class.
     */
    public static Diary getInstance() {
        return DiaryHolder.instance;
    }

    public List<Report> getReports() {
        synchronized (reports) {
            return reports;
        }
    }

    /**
     * adds a report to the diary
     *
     * @param reportToAdd - the report to add
     */
    public void addReport(Report reportToAdd) {
        synchronized (reports) {
            reports.add(reportToAdd);
        }
    }

    /**
     * <p>
     * Prints to a file name @filename a serialized object List<Report> which is a
     * List of all the reports in the diary.
     * This method is called by the main method in order to generate the output.
     */
    public void printToFile(String filename) {
        synchronized (totalReports) {
            synchronized (reports) {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                try {
                    String output = gson.toJson(this);
                    Files.write(Paths.get(filename), output.getBytes());
                } catch (IOException ex) {   }
            }
        }
    }

    /**
     * Gets the total number of received missions (executed / aborted) be all the M-instances.
     *
     * @return the total number of received missions (executed / aborted) be all the M-instances.
     */
    public int getTotal() {
        synchronized (totalReports) {
            return totalReports;
        }
    }


    /**
     * Increments the total number of received missions by 1
     */
    public void incrementTotal() {
        synchronized (totalReports) {
            totalReports++;
        }
    }


}


