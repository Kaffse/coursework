
Team Allocator
--------------
You are given n players to be allocated to m teams (where n % m = 0). There are constraintt of the form
together(i,j) and apart(i,j) where together(i,j) means that players i and j must be in the same team
and apart(i,j) that players i and j must be in different teams. By default, players can be in any team with 
any other player.

Problem Instances
-----------------
There is a directory of problem instances, called data. Below is instance 12-4-04-03.txt where the instance has
12 players to be split into 4 teams (of size 3) and players 3 and 9 must be in the same team, 5 and 9 in same team, 
players 2 and 8 in different teams and 6 and 8 in different teams

12 4
together 3 9
together 5 9
apart 2 8
apart 6 8

Solution Format and Verification
--------------------------------
Below is a solution (one of many) to the above problem. The first line is team 0 and it has players 3, 5 and 9. 
The next line is team 1 with players 8, 10 and 11 ... and so on. The last line is optional, and is essentially
run time statistics (8 decisions were made in 4 milliseconds)

0 3 5 9
1 8 10 11
2 2 6 7
3 0 1 4
nodes: 8   cpu: 4

Supplied is a program that will be used to verify that a solution is indeed a solution. This is run as follows

 > java Verify 12-4-04-03.txt sol-12-4-04-03.txt


What you have to do
-------------------
1. Modify the program TeamAllocator.java so that it finds a solution to instances in the data directory 
   and produces verifiable results. To do this you will have to do the following

   - create appropriate constrained integer variables
   - add appropriate constraints to the model
   - modify the method result to produce verifiable results from the solver

2. Write a short report (a .txt file will suffice) that should contain the following
     - description of your model
     - typical performance
     - possible enhancements or alternative models that might be considered

3. email TeamAllocator.java and report.txt as attachements to pat@dcs.gla.ac.uk with subject
   CP(M) ex01 including in the body of the email your name, course of study and matriculation number


NOTE 1: this is 5% of the course mark and should take no more than 1 day of your time 
NOTE 2: you have to email me only 2 files as above
NOTE 3: your program must run on the command line as follows

        > java TeamAllocator data/200-50-02.txt

NOTE 3: failure to comply with the instructions will result in zero marks.



Patrick Prosser
