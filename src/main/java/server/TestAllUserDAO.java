package server;

public class TestAllUserDAO {
    public static void main(String[] args) {
        AllUserDAO allUserDAO = new AllUserDAO();

        // Test adding users
        System.out.println("Adding users...");
        boolean user1Added = allUserDAO.addUser("Friend5");
        boolean user2Added = allUserDAO.addUser("Friend6");
        boolean user3Added = allUserDAO.addUser("Friend7");
        boolean user4Added = allUserDAO.addUser("Friend8");

        System.out.println("User user1 added: " + user1Added);
        System.out.println("User user2 added: " + user2Added);
        System.out.println("User user3 added: " + user3Added);
        System.out.println("User user4 added: " + user4Added);

        // Test if users exist
        System.out.println("Checking if users exist...");
        boolean user1Exists = allUserDAO.userExists("user1");
        boolean user2Exists = allUserDAO.userExists("user2");
        boolean user3Exists = allUserDAO.userExists("user3");
        boolean user4Exists = allUserDAO.userExists("user4");

        System.out.println("User user1 exists: " + user1Exists);
        System.out.println("User user2 exists: " + user2Exists);
        System.out.println("User user3 exists: " + user3Exists);
        System.out.println("User user4 exists: " + user4Exists);
    }
}
