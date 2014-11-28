import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Date;
import java.util.Arrays;

public class AuctionClient {

    public static void main(String args[]) {
        try{
            Catalogue cata = (Catalogue) Naming.lookup("rmi://localhost/Auction");
            cata.addAuction("Bike", 10000, new Date(2014, 11, 28, 02, 21));
            String[] list = cata.getListing();
            System.out.println(Arrays.toString(list));
        }
        catch (MalformedURLException murle) {
            System.out.println("MalformedURLException");
            System.out.println(murle);
        }
        catch (RemoteException re) {
            System.out.println("RemoteException");
            System.out.println(re);
        }
        catch (NotBoundException nbe) {
            System.out.println("NotBoundException");
            System.out.println(nbe);
        }
    }
}
