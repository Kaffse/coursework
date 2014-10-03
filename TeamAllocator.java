import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;

public class TeamAllocator {
    Model model;  
    Solver solver; 
    int n;
    int k;

    TeamAllocator (String fname) throws IOException {
	Scanner sc = new Scanner(new File(fname));
	n          = sc.nextInt(); // number of players
	k          = sc.nextInt(); // number of teams 
	model      = new CPModel();
	solver     = new CPSolver();
	//
	// create constrained integer variables
	//
	while (sc.hasNext()){
	    String s = sc.next();
	    int i = sc.nextInt();
	    int j = sc.nextInt();
	    //
	    // add constraints to model
	    //
	}
	sc.close();
	//
	// maybe add more constraints to model
	//
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
	TeamAllocator ta = new TeamAllocator(args[0]);
	if (ta.solve()) ta.result();
	else System.out.println(false);
	//ta.stats(); // optional
    }
}