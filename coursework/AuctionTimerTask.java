import java.util.TimerTask;
import java.util.UUID;

public class AuctionTimerTask extends TimerTask {
    private UUID auction_id;
    private Catalogue cata;

    public AuctionTimerTask(UUID id, Catalogue c) {
        auction_id = id;
        cata = c;
    }

    public void run() {
        cata.getAuction(auction_id).resolveAuction();
    }
}
