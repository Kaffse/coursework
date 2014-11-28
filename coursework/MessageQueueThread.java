import java.lang.Thread;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageQueueThread implements Runnable {
    private ConcurrentLinkedQueue<Message> messageQ;
    private UUID my_id;

    public MessageQueueThread(ConcurrentLinkedQueue<Message> q, UUID id) {
        messageQ = q;
        my_id = id;
    }

    public void run() {
        UUID current_uuid;
        int counter = 0;
        UUID last_uuid = UUID.randomUUID();

        while(true) {
            while (messageQ.isEmpty()) {
                wait();
            }

            current_uuid = messageQ.peek().getId();
            if (current_uuid.equals(my_id)) {
                System.out.println(messageQ.poll().getMessage());
            } 
            else if (current_uuid.equals(last_uuid) && counter >= 2) {
                messageQ.poll();
                signal();
                try {
                    Thread.sleep(200);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else if (current_uuid.equals(last_uuid && counter < 2)) {
                signal();
                counter++;
                try {
                    Thread.sleep(200);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            else {
                last_uuid = current_uuid;
                signal();
                try {
                    Thread.sleep(200);
                } catch(InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }      
        }
    }
}
