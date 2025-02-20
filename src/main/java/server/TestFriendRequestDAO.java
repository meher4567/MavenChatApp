package server;

public class TestFriendRequestDAO {
    public static void main(String[] args) {
        // Test inserting data
        FriendRequestDAO friendRequestDAO = new FriendRequestDAO();
        System.out.println("Inserting friend request...");
        boolean insertResult = friendRequestDAO.sendFriendRequest("user1", "receiver_username");
        System.out.println("Friend request inserted: " + insertResult);

        /*// Test getting pending requests
        System.out.println("\nGetting pending friend requests...");
        String receiverUsername = "receiver_username";
        System.out.println("Pending requests for " + receiverUsername + ":");
        for (String sender : friendRequestDAO.getPendingRequests(receiverUsername)) {
            System.out.println(sender);
        }

        // Test accepting friend request
        System.out.println("\nAccepting friend request...");
        String senderUsername = "sender_username";
        boolean acceptResult = friendRequestDAO.acceptFriendRequest(senderUsername, receiverUsername);
        System.out.println("Friend request accepted: " + acceptResult);

        // Test getting pending requests after accepting
        System.out.println("\nGetting pending friend requests after accepting...");
        System.out.println("Pending requests for " + receiverUsername + ":");
        for (String sender : friendRequestDAO.getPendingRequests(receiverUsername)) {
            System.out.println(sender);
        }*/
    }
}
