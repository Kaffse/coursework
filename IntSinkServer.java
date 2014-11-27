import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

class IntSinkServer {
    public static void main(String args[]) {
        String regHost = "localhost";
        Integer regPort = new Integer(1099);
        if (args.length > 0) {
            regHost = args[0];
            if (args.length > 1) regPort = Integer.decode(args[1]);
        }
        try {
            IntSinkServant iss = new IntSinkServant();
            Registry reg = LocateRegistry.getRegistry(regHost,
                                                      regPort.intValue());
            reg.rebind("intSink", iss);
	} catch (Exception e) {
            System.out.format("intSink: export error: %s\n", e.getMessage());
            e.printStackTrace();
            System.exit(1);
	}
    }
}
