
import com.google.gson.*;
//Gavin.Delphia
//4zPWkt

//5ktvbW
/**
 * Represents a Generic Message
 *
 * @author adrian.defreitas
 */
public class Message {
    // Used to Differentiate Each Type of Message

    protected String messageType;
    // Used by the Server To Make Sure Clients are Up to Date
    protected final int version = 1;

    /**
     * Constructor
     *
     * @param messageType
     */
    public Message(String messageType) {
        this.messageType = messageType;
    }

    /**
     * Retrieves the Message Type (i.e. "FOV", "CHAT", etc)
     *
     * @return
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Provides a Simple Conversion of a Message Into a String
     *
     * @return
     */
    public String toString() {
        return String.format("MESSAGE (v%d): %s", version, messageType);
    }
}
