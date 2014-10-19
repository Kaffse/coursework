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
	IntegerVariable[] teamAlloc;

    TeamAllocator (String fname) throws IOException {
	Scanner sc = new Scanner(new File(fname));
	n          = sc.nextInt(); // number of players
	k          = sc.nextInt(); // number of teams 
	model      = new CPModel();
	solver     = new CPSolver();
    teamSize = k / n;

	// create constrained integer variables
	teamAlloc = makeIntVarArray("Teams", n, 0, k - 1);

	while (sc.hasNext()){
	    String s = sc.next();
	    int i = sc.nextInt();
	    int j = sc.nextInt();

	    // add constraints to model
        if (s.equals("together")) {
            //System.out.println("i: " + Integer.toString(i) + " j: " + Integer.toString(j));
            model.addConstraint(eq(teamAlloc[i], teamAlloc[j]));
        } else if (s.equals("apart")) {
            model.addConstraint(neq(teamAlloc[i], teamAlloc[j]));
        } else {
            System.out.println("Error reading line");
        }
    }
	sc.close();

	// maybe add more constraints to model
	IntegerVariable temp;
    for (int i = 0; i < k; i++) {
		temp = new IntegerVariable("temp", i, i);
        model.addConstraint(occurrence(teamSize, temp, teamAlloc));
    }
	solver.read(model);
    } 

    boolean solve(){return solver.solve();}

    void result(){
		int[][] teams = new int[k][teamSize];
		int[] lastpos = new int[k];
        for (int i = 0; i < n; i++) {
			//System.out.println("i: " + Integer.toString(i));
			//System.out.println(solver.getVar(teamAlloc[i]));
			teams[solver.getVar(teamAlloc[i]).getVal()][lastpos[i]] = i;
			lastpos[i]++;
        }
		for (int i = 0; i < k; i++) {
			System.out.println(Integer.toString(i) + Arrays.toString(teams[i]));
		};
    }

    void stats(){
	System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
    }
    
    public static void main(String[] args)  throws IOException {
	TeamAllocator ta = new TeamAllocator(args[0]);
	if (ta.solve()) ta.result();
	else System.out.println(false);
	ta.stats(); // optional
    }
}
