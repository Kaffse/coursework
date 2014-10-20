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
    teamSize = n / k;

	// create constrained integer variables
	teamAlloc = makeIntVarArray("Player", n, 0, k - 1);

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

	// maybe add more constraints to model
    for (int i = 0; i < k; i++) {
        model.addConstraint(occurrence(teamSize, teamAlloc, i));
    }
	solver.read(model);
    } 

    boolean solve(){return solver.solve();}

    void result(){
		int[][] teams = new int[k][teamSize];
		int[] lastpos = new int[k];
        int thisTeam;
        for (int i = 0; i < n; i++) {
            thisTeam = solver.getVar(teamAlloc[i]).getVal();
			teams[thisTeam][lastpos[thisTeam]] = i;
			lastpos[thisTeam]++;
        }
		for (int i = 0; i < k; i++) {
			System.out.print(Integer.toString(i));
            for (int j = 0; j < teamSize; j++) {
                System.out.print(" " + Integer.toString(teams[i][j]));
            }
            System.out.print("\n");
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
