package backend;

import frontend.MessageListener;
import frontend.Message;
import server.MessageDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatClient {
    private String serverAddress;
    private int serverPort;
    private String userName;
    private PrintWriter out;
    private Socket socket;
    private List<MessageListener> messageListeners;
    private BlockingQueue<String[]> messageBuffer;
    private boolean bufferRunning;
    private boolean initialized = false;

    public ChatClient(String serverAddress, int serverPort, String userName) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.userName = userName;
        this.messageListeners = new ArrayList<>();
        this.messageBuffer = new LinkedBlockingQueue<>();
        this.bufferRunning = true;
    }

    public void start() {
        try {
            socket = new Socket(serverAddress, serverPort);
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(userName);

            System.out.println("**Connected to server as " + userName);
            new Thread(new ServerHandler(socket)).start();
            new Thread(new MessageBufferHandler()).start();

        } catch (IOException e) {
            System.err.println("**Error connecting to server: " + e.getMessage());
        }
    }

    public void sendMessage(String recipient, String message) {
        if (out != null) {
            try {
                System.out.println("**Sending message to " + recipient + ": " + message);
                out.println(recipient);
                out.println(message);
                out.flush(); // Ensure the message is sent
                System.out.println("**Message sent to " + recipient);
            } catch (Exception e) {
                System.err.println("**Error: Message sending failed, adding to buffer");
                messageBuffer.offer(new String[]{recipient, message});
                printBufferState();
            }
        } else {
            System.err.println("**Error: No connection to server");
        }
    }

    public void addMessageListener(MessageListener listener) {
        if (!messageListeners.contains(listener)) {
            System.out.println("**Adding message listener: " + listener);
            messageListeners.add(listener);
        }
    }

    public void removeMessageListener(MessageListener listener) {
        System.out.println("**Removing message listener: " + listener);
        messageListeners.remove(listener);
    }

    private class ServerHandler implements Runnable {
        private Socket socket;

        public ServerHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String sender;
                String message;
                while ((sender = in.readLine()) != null && (message = in.readLine()) != null) {
                    if (!initialized) {
                        System.out.println("**Received offline message from " + sender + ": " + message);
                    } else {
                        System.out.println("**Received message from " + sender + ": " + message);
                    }
                    printListeners();
                    for (MessageListener listener : messageListeners) {
                        if (listener instanceof frontend.MessageComponent) {
                            frontend.MessageComponent mc = (frontend.MessageComponent) listener;
                            if (mc.getFriendName().equals(sender)) {
                                System.out.println("**Dispatching message to listener: " + mc.getFriendName());
                                listener.onMessageReceived(sender, message);
                            }
                        } else {
                            System.out.println("**Dispatching message to listener: " + listener);
                            listener.onMessageReceived(sender, message);
                        }
                    }
                }
                initialized = true; // Mark client as initialized after receiving messages
            } catch (IOException e) {
                System.err.println("**Error reading message from server: " + e.getMessage());
                killBlockingThread();
            }
        }
    }

    private class MessageBufferHandler implements Runnable {
        @Override
        public void run() {
            while (bufferRunning) {
                try {
                    String[] message = messageBuffer.take();
                    System.out.println("**Retrying message from buffer: " + message[1] + " to " + message[0]);
                    sendMessage(message[0], message[1]);
                    printBufferState();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("**Message buffer handler interrupted");
                }
            }
        }
    }

    private void killBlockingThread() {
        System.err.println("**Killing blocking thread");
        bufferRunning = false;
    }

    private void printBufferState() {
        System.out.println("**Current buffer state: " + messageBuffer.size() + " messages");
        for (String[] msg : messageBuffer) {
            System.out.println("**Buffered message: " + msg[1] + " to " + msg[0]);
        }
    }

    public void printListeners() {
        System.out.println("**Current message listeners:");
        for (MessageListener listener : messageListeners) {
            System.out.println("+++" + listener);
        }
    }
}
