import java.util.*;
import java.io.*;
import static choco.Choco.*;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.solver.Solver;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.cp.solver.search.integer.varselector.*;
import choco.cp.solver.search.integer.branching.AssignVar;
import choco.cp.solver.search.integer.valiterator.IncreasingDomain;

public class Optimize {
    Model model;  
    Solver solver; 
    int meetings;   //Number of meetings
    int agents;     //Number of agents
    int timeslots;  //Number of timeslots
    int[][] agentAttendance;    //2D array representing the meetings each agent must attend
    int[][] distanceMatrix;     //2D array representing distances between each meeting
    int[] attended;     //Array of meetings showing if they are being attended or not
    int[][] clash;      //2D array showing all meetings which cannot be on the same slot

    IntegerVariable[] meetingAloc;  //A 2D array representing which timeslot each meeting shall be allocated to
    IntegerVariable highSlot;       //This varible contains the highest timeslot currently

    Optimize (String fname, int timelimit) throws IOException {
        Scanner sc = new Scanner(new File(fname));

        //Get first 3 values and allocate them correctly
        meetings = sc.nextInt(); 
        agents = sc.nextInt();
        timeslots = sc.nextInt();

        //New model and solver
        model      = new CPModel();
        solver     = new CPSolver();

        //Setup initial Variables, agentAttendance distanceMatrix and attended
        meetingAloc = makeIntVarArray("Meeting Allocations", meetings, 0, timeslots - 1);
        highSlot = makeIntVar("Max Slot", 0, timeslots - 1);
        model.addConstraint(max(meetingAloc, highSlot)); //Ensure highSlot is the max current variable

        agentAttendance = new int[agents][meetings];
        distanceMatrix = new int[meetings][meetings];
        attended = new int[meetings];
        clash = new int[meetings][meetings];

        //Get the agent meeting attendance
        //For each agent...
        for (int i = 0; i < agents; i++) {
            //Skip this identifer token, we don't need it
            sc.next();

            //Which meetings does this agent attend?
            for (int j = 0; j < meetings; j++) {
                //Set 1 if attending, 0 if not
                agentAttendance[i][j] = sc.nextInt();
                if (agentAttendance[i][j] == 1) {
                    attended[j] = 1;
                }
            }

            //for each meeting...
            for (int j = 0; j < meetings; j++) {
                if (agentAttendance[i][j] == 1) {   //If the agent attends this meeting...
                    for (int k = j + 1; k < meetings; k++) {    //Sub for from j + 1 onwards
                        if (agentAttendance[i][k] == 1) {     //If the agenet attends this meeting...
                            //If the agent attends this meeting, ensure they don't clash
                            model.addConstraint(neq(meetingAloc[j], meetingAloc[k]));
                            //Add clashes
                            clash[j][k] = 1;
                            clash[k][j]= 1;
                        }
                    }
                }
            }
        }

        //Get the distance values
        //For each meeting...
        for (int i = 0; i < meetings; i++) {
            //Skip this identifier, we don't need it
            sc.next();

            //For each meeting...
            for (int j = 0; j < meetings; j++) {
                //Put distance into the distance matrix
                distanceMatrix[i][j] = sc.nextInt();
            }
        }

        //Now interate over meetings and ensure distance rule is held for all attended meetings
        for (int i = 0; i < meetings; i++) {
            if (attended[i] == 1) {
                for (int j = i + 1; j < meetings; j++) {
                    if (attended[j] == 1 && clash[i][j] == 1) {
                        model.addConstraint(distanceGT(meetingAloc[i], meetingAloc[j], distanceMatrix[i][j]));
                    }
                    else {
                        model.addVariable(meetingAloc[i]);
                    }
                }
            } else {
                //However, if the meeting isn't attended, ensure it's set to 0 and added to the model
                meetingAloc[i] = constant(0);
                model.addVariable(meetingAloc[i]);
            }
        }

        sc.close();
        solver.setTimeLimit(timelimit);
        solver.read(model);
    } 

    boolean solve(){
        return solver.minimize(solver.getVar(highSlot), false);
    }

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
        Optimize s = new Optimize (args[0], Integer.parseInt(args[1]));
        if (s.solve()) s.result();
        else System.out.println("false");
        s.stats();
    }
}
