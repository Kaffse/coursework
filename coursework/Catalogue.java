import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Date;

public interface Catalogue extends java.rmi.Remote {
    public String[] getListing() throws java.rmi.RemoteException;
    public UUID addAuction(UUID uid, String item_name, int min_price, Date end_time) throws java.rmi.RemoteException;
    public Auction getAuction(UUID uid) throws java.rmi.RemoteException;
    public void resolveAuction(UUID auctionid) throws java.rmi.RemoteException;
    public Message poll() throws java.rmi.RemoteException;
    public Message peek() throws java.rmi.RemoteException;
    public void add(Message m) throws java.rmi.RemoteException;
    public boolean isEmpty() throws java.rmi.RemoteException;
}
