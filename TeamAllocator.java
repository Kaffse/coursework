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
    int teamSize;

    TeamAllocator (String fname) throws IOException {
	Scanner sc = new Scanner(new File(fname));
	n          = sc.nextInt(); // number of players
	k          = sc.nextInt(); // number of teams 
	model      = new CPModel();
	solver     = new CPSolver();
    teamSize = k / n;

	// create constrained integer variables
    IntegerVariable[] teamAlloc = new IntegerVariable[n];

	while (sc.hasNext()){
	    String s = sc.next();
	    int i = sc.nextInt();
	    int j = sc.nextInt();

	    // add constraints to model
        if (s.equals("together")) {
            model.addConstraint(eq(teamAlloc[i], teamAlloc[j]));
        } else if (s.equals("apart")) {
            model.addConstraint(neq(teamAlloc[i], teamAlloc[j]));
        } else {
            System.out.println("Error reading line");
        }
    }
	sc.close();
	//
	// maybe add more constraints to model
	//
    for (int i = 0; i < n; i++) {
        teamAlloc[i] = makeIntVar("Player " + Integer.toString(n), 0, k - 1);
    }
    for (int i = 0; i < k; i++) {
        model.addConstraint(occurrence(teamSize, teamAlloc, i));
    }
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
