import java.rmi.RemoteException;
import java.rmi.Remote;

public interface Auction extends java.rmi.Remote {
    public boolean placeBid(Bid newBid) throws java.rmi.RemoteException;
}
