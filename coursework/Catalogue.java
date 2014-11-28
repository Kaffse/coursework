import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.UUID;
import java.util.Date;

public interface Catalogue extends java.rmi.Remote {
    public String[] getListing() throws java.rmi.RemoteException;
    public UUID addAuction(UUID uid, String item_name, int min_price, Date end_time) throws java.rmi.RemoteException;
    public Auction getAuction(UUID uid) throws java.rmi.RemoteException;
    public void resolveAuction(UUID auctionid) throws java.rmi.RemoteException;
    public ConcurrentLinkedQueue<Message> getMessageQ() throws java.rmi.RemoteException;
}
