package frontend;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class Message {
    private static final AtomicInteger idCounter = new AtomicInteger(0);
    private String sender;
    private String text;
    private String timeStamp;
    private int message_id;
    private String receiver;
    private String message_type;


    public Message(String sender,String receiver, String text,String message_type) {
        this.message_id = generateId();
        this.sender = sender;
        this.text = text;
        this.receiver=receiver;
        this.timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()); // Add timestamp
        this.message_type = message_type;
    }

    public Message(int message_id, String sender,String receiver, String text,String message_type,String timeStamp) {
        this.sender = sender;
        this.text = text;
        this.receiver=receiver;
        this.timeStamp = timeStamp;
        this.message_id = message_id;
        this.message_type = message_type;
    }

    private int generateId() {
        return idCounter.incrementAndGet();
    }

    public String getSender(){
        return sender;
    }
    

    public String getText() {
        return text;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "Message ID: " + message_id +
                "\nSender: " + sender +
                "\nText: " + text +
                "\nTimestamp: " + timeStamp +
                "\n";
    }

    public String getMessageType() {
        return message_type;
    }

    public int getMessageId() {
        return message_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public Timestamp getTimestamp_exact() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parsedDate = dateFormat.parse(timeStamp);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null; // Handle the error gracefully
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Message message = (Message) obj;
        return message_id == message.message_id &&
                Objects.equals(sender, message.sender) &&
                Objects.equals(text, message.text) &&
                Objects.equals(timeStamp, message.timeStamp) &&
                Objects.equals(receiver, message.receiver) &&
                Objects.equals(message_type, message.message_type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message_id, sender, text, timeStamp, receiver, message_type);
    }
}
