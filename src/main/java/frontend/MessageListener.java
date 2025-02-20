package frontend;

public interface MessageListener {
    void onMessageReceived(String sender, String message);
    void onNewClientConnected(String client);
}

