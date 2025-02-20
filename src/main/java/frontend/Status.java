package frontend;

import java.util.HashMap;
import java.util.Map;

public class Status {

    private static Map<String, String> statusMap = new HashMap<>();

    // Simulate backend data
    static {
        statusMap.put("Friend 1", "Online");
        statusMap.put("Friend 2", "Offline");
        statusMap.put("Friend 3", "Busy");
        statusMap.put("Friend 4", "Away");
        statusMap.put("Friend 5", "Do Not Disturb");
        statusMap.put("Friend 6", "Online");
        statusMap.put("Friend 7", "Offline");
        statusMap.put("Friend 8", "Busy");
        statusMap.put("Friend 9", "Away");
        statusMap.put("Friend 10", "Do Not Disturb");
        statusMap.put("Friend 11", "Online");
        statusMap.put("Friend 12", "Offline");
        statusMap.put("Friend 13", "Busy");
        statusMap.put("Friend 14", "Away");
        statusMap.put("Friend 15", "Do Not Disturb");
        // Add more friends as needed
    }

    // Method to get the status of a friend
    public static String getStatus(String friendName) {
        return statusMap.getOrDefault(friendName, "Unknown");
    }

    // Method to set the status of a friend (for simulation purposes)
    public static void setStatus(String friendName, String status) {
        statusMap.put(friendName, status);
    }
}
