import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Date;
import java.util.UUID;
import java.util.Scanner;
import java.util.Arrays;

public class AuctionClient {

    public static void main(String args[]) {
        System.out.println("Auction Client - Connecting to Server...");
        try{
            UUID my_id = UUID.randomUUID();
            Catalogue cata = (Catalogue) Naming.lookup("rmi://localhost/Auction");
            System.out.println("Connection Successful!");

            ConcurrentLinkedQueue<Message> messageQ = cata.getMessageQ();
            MessageQueueThread msgT = new MessageQueueThread(messageQ, my_id);
            msgT.run();

            System.out.println("The help Comamnd is avaliable");
            System.out.println("Please End Input with ^C");
            Scanner input = new Scanner (System.in);

            System.out.println("Please Enter a Command: ");
            String command = input.next().toLowerCase();
            while(true) {
                switch (command) {
                    case "help": printHelp(); break;
                    case "list": printList(cata); break;
                    case "bid": bid(cata, my_id, input); break;
                    case "add": add(cata, my_id, input); break;
                }
                System.out.println("Please Enter a Command: ");
                command = input.next().toLowerCase();
            }

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

    private static void printHelp() {
        System.out.println("Here is the list of commands:");
        System.out.println("list, bid, add, help");
    }

    private static void printList(Catalogue cata) throws java.rmi.RemoteException{
        String[] list = cata.getListing();
        for (int i = 0; i < list.length; i++) {
            System.out.println(list[i]);
        }
    }

    private static void bid(Catalogue cata, UUID my_id, Scanner input) throws java.rmi.RemoteException {
        Auction auction;
        System.out.println("Please enter an auction id: ");
        try {
            UUID id = UUID.fromString(input.next());
            auction = cata.getAuction(id);
        } catch (java.lang.IllegalArgumentException e) {
            System.out.println("Invalid UUID!");
            return;
        } catch (java.rmi.RemoteException e) {
            System.out.println("Wrong UUID or Server connection Faulty");
            return;
        }

        System.out.println("Note, Bid foramt is in pennies. The last 2 numbers are not pounds!");
        System.out.println("Please enter a bid ammount: ");
        int bid = Integer.parseInt(input.next());

        auction.placeBid(new Bid(my_id, bid));
    }

    private static void add(Catalogue cata, UUID uid, Scanner input) throws java.rmi.RemoteException {
        System.out.println("Please enter item name: ");
        String name = input.next();

        System.out.println("Please enter minimum price: ");
        int mini = Integer.parseInt(input.next());

        System.out.println("Please enter end year: ");
        int year = Integer.parseInt(input.next());

        System.out.println("Please enter end month: ");
        int month = Integer.parseInt(input.next());

        System.out.println("Please enter end day: ");
        int day = Integer.parseInt(input.next());

        System.out.println("Please enter end hour: ");
        int hour = Integer.parseInt(input.next());

        System.out.println("Please enter end minute: ");
        int min = Integer.parseInt(input.next());

        Date end = new Date(year, month, day, hour, min);
        UUID id = cata.addAuction(uid, name, mini, end);
        System.out.println("Your Auction ID is: " + id.toString());
    } 
}
