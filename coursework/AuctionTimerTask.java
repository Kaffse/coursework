import java.util.TimerTask;
import java.rmi.RemoteException;
import java.util.UUID;

public class AuctionTimerTask extends TimerTask {
    private UUID auction_id;
    private Catalogue cata;

    public AuctionTimerTask(UUID id, Catalogue c) {
        auction_id = id;
        cata = c;
    }

    public void run(){
        try{
            cata.resolveAuction(auction_id);
        } catch(java.rmi.RemoteException e){
            System.out.print(e);
        }
    }
}
