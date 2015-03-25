from py2neo import Graph, Node, Relationship
import random as r

#packages.txt is a straight dump of dnf list  piped to a file
list_file = open("packages.txt", "r")

packagelist = []

for line in list_file:
    packagelist = packagelist + [line.split('.')[0]]

list_file.close()

#Cut the first three lines since they are junk from stdout
packagelist = packagelist[3:]

graph = Graph()

cycle = int(raw_input("Number of users: "))

for i in range(1, cycle + 1):
    print "Commiting packages for user " + str(i)
    user_list = graph.merge("User", "id", i)

    for user in user_list:
        this_pc = user

    for package in packagelist:
        if r.randint(0, 10) < 1:
            nodes = graph.merge("Package", "name", package)
            for node in nodes:
                relationship = Relationship(this_pc, "INSTALLED", node)
                graph.create_unique(relationship)
