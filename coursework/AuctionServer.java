import java.rmi.Naming;

public class AuctionServer {

    public AuctionServer() {
        try {
            Catalogue cata = new CatalogueImpl();
            Naming.rebind("rmi://localhost/Auction", cata);
        } catch (Exception e) {
            System.out.println("An Error has Occured: " + e);
        }
    }

    public static void main(String args[]) {
        new AuctionServer();
    }
}
