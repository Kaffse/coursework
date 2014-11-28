import java.rmi.RemoteException;
import java.rmi.Remote;
import java.util.Date;

public interface Catalogue extends java.rmi.Remote {
    public String[] getListing() throws java.rmi.RemoteException;
    public void addAuction(String item_name, int min_price, Date end_time) throws java.rmi.RemoteException;
}
