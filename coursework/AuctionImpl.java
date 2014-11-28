import java.rmi.RemoteException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class AuctionImpl extends java.rmi.server.UnicastRemoteObject implements Auction {
    private UUID auction_id;
    private int owner_id;
    private String item_name;
    private int min_bid;
    private Date end_time;
    private CopyOnWriteArrayList<Bid> bid_list;

    public AuctionImpl(int owner, String name, int min, Date end) throws java.rmi.RemoteException{
        super();
        auction_id = UUID.randomUUID();
        owner_id = owner;
        item_name = name;
        min_bid = min;
        end_time = end;
        bid_list = new CopyOnWriteArrayList<Bid>();
        bid_list.add(new Bid(0, 0));
    }

    public UUID getId() throws java.rmi.RemoteException{
        return auction_id;
    }

    public int getOwner() throws java.rmi.RemoteException{
        return owner_id;
    }

    public String getItemName() throws java.rmi.RemoteException{
        return item_name;
    }

    public Date getEndTime() throws java.rmi.RemoteException{
        return end_time;
    }

    public CopyOnWriteArrayList getBidList() throws java.rmi.RemoteException{
        return bid_list;
    }

    public synchronized Bid getHighestBid() throws java.rmi.RemoteException{
        return bid_list.get(0);
    }

    public synchronized boolean placeBid(Bid newBid) throws java.rmi.RemoteException{
        if (bid_list.get(0).getBid() < newBid.getBid()) {
            bid_list.add(0, newBid);
            return true;
        }
        else {
            return false;
        }
    }
    
    public String toString(){
        try{
            return item_name + " currently at " + getHighestBid().getPrettyBid() + ". Ends at " + end_time.toString();
        } catch(java.rmi.RemoteException e){
            System.out.println("fuck");
            return "fuck";
        }
    }
}
