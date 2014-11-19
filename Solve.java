import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;

public class Solve {
    Model model;  
    Solver solver; 
    int meetings;
    int agents;
    int timeslots;

    Solve (String fname) throws IOException {
	Scanner sc = new Scanner(new File(fname));
	meetings = sc.nextInt(); 
    agents = sc.nextInt();
	timeslots = sc.nextInt();

	model      = new CPModel();
	solver     = new CPSolver();
	
	// create constrained integer variables
	
    for (int i = 0; i < timeslots; i++) {
	    String token  = sc.next();

        for (int j = 0; j < meetings; j++) {
            //do constraints
        }
	}

    for (int i = 0; i < meetings; i++) {
        string token = sc.next();
        for (int j = 0; j < meetings; j++) {
            //do more constraints
        }
    }

	sc.close();
    // maybe more
	solver.read(model);
    } 

    boolean solve(){return solver.solve();}

    void result(){
	System.out.println("produce verifiable results from the solver");
    }

    void stats(){
	System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
    }
    
    public static void main(String[] args)  throws IOException {
	Solve s = new Solve (args[0]);
	if (s.solve()) s.result();
	else System.out.println(false);
	//s.stats(); // optional
    }
}
