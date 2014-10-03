import java.util.*;
import java.io.*;

public class Verify {

    public static void main(String[] args) throws IOException {
	Scanner sc     = new Scanner(new File(args[0]));
	int n          = sc.nextInt(); // number of people
	int m          = sc.nextInt(); // number of teams 
	int [][] A     = new int[n][n]; // A[i][j] = 1 <-> together(i,j); A[i][j] = -1 <-> apart(i,j)
	int [][] alloc = new int[m][n]; // alloc[i][j] = 1 <-> player i is in team j
	int [] player  = new int[n];    // player[i] = k <-> player[i] i team k
	int size       = n/m; // size of a team
	while (sc.hasNext()){
	    String s = sc.next();
	    int i    = sc.nextInt();
	    int j    = sc.nextInt();
	    if (s.equals("together")) A[i][j] = A[j][i] = 1;
	    if (s.equals("apart")) A[i][j] = A[j][i] = -1;
	}
	sc.close();
	sc = new Scanner(new File(args[1]));
	for (int i=0;i<m;i++){ // read a team
	    int k = sc.nextInt(); // team number is k
	    for (int j=0;j<size;j++){
		int p = sc.nextInt();
		alloc[k][p] = 1;
		player[p] = k;
	    }
	}
	sc.close();
	System.out.println("players: "+ n +"  teams: "+ m +"  team size: "+ size);
	for (int i=0;i<n;i++)
	    for (int j=0;j<n;j++){
		if (player[i] != player[j] && A[i][j] == 1) System.out.println("player["+ i +"] and player["+ j +"] should be together");
		if (player[i] == player[j] && A[i][j] == -1) System.out.println("player["+ i +"] and player["+ j +"] should be apart");
	    }
	for (int i=0;i<m;i++){
	    int count = 0;
	    for (int j=0;j<n;j++) count = count + alloc[i][j];
	    if (count != size) System.out.println("team["+ i +"] is wrong size "+ count);
	}
	for (int j=0;j<n;j++){
	    int count = 0;
	    for (int i=0;i<m;i++) count = count + alloc[i][j];
	    if (count != 1) System.out.println("player["+ j +"] must be in exactly 1 team");
	}
    }
}