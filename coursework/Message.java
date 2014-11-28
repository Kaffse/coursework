import java.util.UUID;

public class Message {
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
