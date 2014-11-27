import os

problems_raw = os.listdir("problems")
rProblems_raw = os.listdir("rProblems")

problems = []
rProblems = []

for problem in problems_raw:
    if 'sol' not in problem:
        print("Working on problem... " + problem + "\n")
        os.system("java Optimize problems/" + problem + " 10000 > problem_sol/sol_" + problem)
        try:
            os.system("java Validate problems/" + problem + " problem_sol/sol_" + problem + " > problem_sol/result_" + problem)
        except:
            print ("Problem Validating problem " + problem + ". Maybe unsolvable?")

for problem in rProblems_raw:
    if 'sol' not in problem:
        print("Working on problem... " + problem + "\n")
        os.system("java Optimize rProblems/" + problem + " 10000 > rProblem_sol/sol_" + problem)
        try:
            os.system("java Validate rProblems/" + problem + " rProblem_sol/sol_" + problem + " > rProblem_sol/result_" + problem)
        except:
            print ("Problem Validating problem " + problem + ". Maybe unsolvable?")
