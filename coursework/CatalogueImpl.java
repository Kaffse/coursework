import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

public class CatalogueImpl extends java.rmi.server.UnicastRemoteObject implements Catalogue {

    private ConcurrentHashMap<UUID, AuctionImpl> auctionListing;

    public CatalogueImpl() throws java.rmi.RemoteException {
        auctionListing = new ConcurrentHashMap<UUID, AuctionImpl>();
    }

    public synchronized String[] getListing() throws java.rmi.RemoteException {
        int listSize = auctionListing.size();
        if (listSize <= 0) {
            return new String[1];
        } else {
            String[] listing = new String[listSize];
            Enumeration<AuctionImpl> auctions = auctionListing.elements();
            int i = 0;
            while (auctions.hasMoreElements()) {
                listing[i] = auctions.nextElement().toString();
            }
            return listing;
        }
    }

    public synchronized void addAuction(String item_name, int min_price, Date end_time) throws java.rmi.RemoteException {
        AuctionImpl auction = new AuctionImpl(1, item_name, min_price, end_time);
        auctionListing.put(auction.getId(), auction);
    }
}
