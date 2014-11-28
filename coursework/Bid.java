public class Bid {

    private int bidder_id;
    private int bid;

    public Bid(int id, int ammount) {
        bidder_id = id;
        bid = ammount;
    }

    public int getBid() {
        return bid;
    }

    public int getId() {
        return bidder_id;
    }

    public String getPrettyBid() {
        String bidString = Integer.toString(bid);
        String pounds = bidString.substring(0, bidString.length() - 2);
        String pennies = bidString.substring(bidString.length() - 2, bidString.length());

        return "£" + pounds + "." + pennies;
    }
}
