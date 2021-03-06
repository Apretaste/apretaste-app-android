package apretaste;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**

 */
public class HistoryManager {
    private static HistoryManager singleton;
    public String currentUrl;


    public List<HistoryEntry> entries = new ArrayList<>(30);

    public interface HistoryListener {
        void onHistoryChange(HistoryEntry newUrl);
    }

    private HistoryEntry currentEntry;

    private Set<HistoryListener> listeners = new HashSet<>();

    public void addListener(HistoryListener listener) {
        listeners.add(listener);
        listener.onHistoryChange(currentEntry);
    }

    public void removeListener(HistoryListener listener) {
        listeners.remove(listener);
    }

    static {
        singleton = new HistoryManager();
    }

    public HistoryManager() {

    }

    public static HistoryManager getSingleton() {
        return singleton;
    }

    public String getEntry(String field) {
        String result = "";
        switch (field) {
            case "service":
                result = currentEntry.service;
                break;
            case "command":
                result = currentEntry.command;
                break;

        }

        return result;
    }

    public void setCurrentPage(HistoryEntry entry) {
        currentEntry = entry;
        for (HistoryListener listener :
                listeners) {
            listener.onHistoryChange(currentEntry);
        }
    }

    public void addToHistory(HistoryEntry entry) {
        entries.add(entry);
    }
}
