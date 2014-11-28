import java.rmi.RemoteException;
import java.until.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Timer;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.Arrays;

public class CatalogueImpl extends java.rmi.server.UnicastRemoteObject implements Catalogue {

    private ConcurrentHashMap<UUID, AuctionImpl> auctionListing;
    private ConcurrentLinkedQueue<Message> messageQ;
    private HashMap<UUID, Timer> timermap;

    public CatalogueImpl() throws java.rmi.RemoteException {
        auctionListing = new ConcurrentHashMap<UUID, AuctionImpl>();
        messageQ = new ConcurrentLinkedQueue<Message>();
        timermap = new HashMap<UUID, Timer>();
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

    public synchronized UUID addAuction(UUID uid, String item_name, int min_price, Date end_time) throws java.rmi.RemoteException {
        AuctionImpl auction = new AuctionImpl(uid, item_name, min_price, end_time);
        auctionListing.put(auction.getId(), auction);
        timermap.add(uid, new Timer().schdual(new AuctionTimerTask(uid, cata), end_time));
        return auction.getId();
    }

    public Auction getAuction(UUID uid) throws java.rmi.RemoteException {
        return auctionListing.get(uid);
    }

    public ConcurrentLinkedQueue getMessageQ() {
        return messageQ;
    }

    public void resolveAuction(UUID auctionid) throws java.rmi.RemoteException {
        Auction auction = getAuction(auctionid);
        ArrayList<E> results = auction.resolve();
        cata.remove(auctionid);
        timermap.remove(auctionid);
        String message = results.get(0);
        Iterator<E> res_it = results.iterator();
        res_it.next();

        while (res_it.hasNext()){
            messageQ.add(new Message(res_it.next(), message));
            signal();
        }
    }
}
