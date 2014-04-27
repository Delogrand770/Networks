
/**
 * Describes a textual message from the server to the client
 *
 * @author adrian.defreitas
 */
public class TextMessage extends Message {

    private String type;
    private String sender;
    private String message;

    /**
     * Constructor
     *
     * @param messageKind - the kind of message it is
     * @param message - the actual message being transmitted
     */
    public TextMessage(String type, String sender, String message) {
        super("TEXT");
        this.type = type;
        this.sender = sender;
        this.message = message;
    }

    /**
     * Returns the TYPE of message (i.e. "ERROR", "NOTIFICATION", etc)
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Returns the sender of the message
     *
     * @return
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the message
     *
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * Converts this object into a string
     *
     * @return
     */
    @Override
    public String toString() {
        String result = String.format("TEXT MESSAGE (from %s): %s", sender, message);
        return result;
    }
}
