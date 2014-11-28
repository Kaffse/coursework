import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Timer;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;
import java.util.Arrays;
import java.util.Iterator;
import java.lang.Thread;

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
            String[] s = new String[1];
            s[0] = "No Auctions Avaliable";
            return s;
        } else {
            String[] listing = new String[listSize];
            Enumeration<AuctionImpl> auctions = auctionListing.elements();
            int i = 0;
            while (auctions.hasMoreElements()) {
                listing[i] = auctions.nextElement().toString();
                i++;
            }
            return listing;
        }
    }

    public synchronized UUID addAuction(UUID uid, String item_name, int min_price, Date end_time) throws java.rmi.RemoteException {
        AuctionImpl auction = new AuctionImpl(uid, item_name, min_price, end_time);
        auctionListing.put(auction.getId(), auction);
        Timer t = new Timer();
        t.schedule(new AuctionTimerTask(auction.getId(), this), end_time);
        timermap.put(uid, t);
        return auction.getId();
    }

    public Auction getAuction(UUID uid) throws java.rmi.RemoteException {
        return auctionListing.get(uid);
    }

    public synchronized Message poll() throws java.rmi.RemoteException {
        return messageQ.poll();
    }

    public synchronized Message peek() throws java.rmi.RemoteException {
        return messageQ.peek();
    }
    
    public synchronized void add(Message m) throws java.rmi.RemoteException {
        messageQ.add(m);
    }

    public synchronized boolean isEmpty() throws java.rmi.RemoteException {
        return messageQ.isEmpty();
    }

    public void resolveAuction(UUID auctionid) throws java.rmi.RemoteException {
        Auction auction = getAuction(auctionid);
        ArrayList<Object> results = auction.resolveAuction();
        auctionListing.remove(auctionid);
        timermap.remove(auctionid);
        String message = (String)results.get(0);
        Iterator<Object> res_it = results.iterator();
        res_it.next();

        synchronized(messageQ) {
            while (res_it.hasNext()){
                messageQ.add(new Message((UUID)res_it.next(), message));
                //notify();
            }
        }
    }
}
