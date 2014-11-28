import java.util.UUID;
import java.io.Serializable;

public class Message implements java.io.Serializable{
    private UUID client;
    private String message;

    public Message(UUID id, String msg) {
        client = id;
        message = msg;
    }

    public UUID getId() {
        return client;
    }

    public String getMessage() {
        return message;
    }
}
