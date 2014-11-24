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
    int meetings;   //Number of meetings
    int agents;     //Number of agents
    int timeslots;  //Number of timeslots
    int[][] agentAttendance;    //2D array representing the meetings each agent must attend
    //int[][] distanceMatrix;     //2D array representing distances between each meeting

    IntegerVariable[] meetingAloc;  //A 2D array representing which timeslot each meeting shall be allocated to

    Solve (String fname) throws IOException {
        Scanner sc = new Scanner(new File(fname));

        //Get first 3 values and allocate them correctly
        meetings = sc.nextInt(); 
        agents = sc.nextInt();
        timeslots = sc.nextInt();

        //New model and solver
        model      = new CPModel();
        solver     = new CPSolver();

        //Setup initial Variables, agentAttendance and distanceMatrix
        meetingAloc = makeIntVarArray("Meeting Allocations", meetings, 0, timeslots - 1);
        agentAttendance = new int[agents][meetings];
        //distanceMatrix = int[meetings][meetings];

        //Get the agent meeting attendance
        //For each agent...
        for (int i = 0; i < timeslots; i++) {
            //Skip this identifer token, we don't need it
            String token  = sc.next();

            //Which meetings does this agent attend?
            for (int j = 0; j < meetings; j++) {
                //Set 1 if attending, 0 if not
                agentAttendance[i][j] = sc.nextInt();
            }

            //for each meeting...
            for (int j = 0; j < meetings; j++) {
                if (agentAttendance[i][j] == 1) {   //If the agent attends this meeting...
                    for (int k = j + 1; k < meetings; k++) {    //Sub for from j + 1 onwards
                        if (agentAttendance[i][k] == 1)     //If the agenet attends this meeting...
                            //If the agent attends this meeting, ensure they don't clash
                            model.addConstraint(neq(meetingAloc[j],meetingAloc[k]));
                        }
                    }
                }
            }

        //Get the distance values
        //For each meeting...
        for (int i = 0; i < meetings; i++) {
            //Skip this identifier, we don't need it
            String token = sc.next();

            //For each meeting...
            for (int j = i + 1; j < meetings; j++) {
                //Ensure there is time to get between meetings
                model.addConstraint(distanceGT(meetingAloc[i], meetingAloc[j], sc.nextInt()));
            }
        }

        sc.close();
        // maybe more
        solver.read(model);
    } 

    boolean solve(){return solver.solve();}

    void result(){
        //Print the domain the solver found for each meeting variable
        for (int i = 0; i < meetings; i++) {
            System.out.println(i + " " + solver.getVar(meetingAloc[i]).getVal());
        }
    }

    void stats(){
        System.out.println("nodes: "+ solver.getNodeCount() +"   cpu: "+ solver.getTimeCount());
    }
    
    public static void main(String[] args)  throws IOException {
        Solve s = new Solve (args[0]);
        if (s.solve()) s.result();
        else System.out.println(false);
        s.stats(); // optional
    }
}
