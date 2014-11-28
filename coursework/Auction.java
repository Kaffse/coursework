import java.rmi.RemoteException;
import java.util.ArrayList;
import java.rmi.Remote;

public interface Auction extends java.rmi.Remote {
    public boolean placeBid(Bid newBid) throws java.rmi.RemoteException;
    public ArrayList<Object> resolveAuction() throws java.rmi.RemoteException;
}
