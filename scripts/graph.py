from py2neo import Graph, Node, Relationship

list_file = open("installed.txt", "r")

packagelist = []

for line in list_file:
    packagelist = packagelist + [line.split()[0]]

list_file.close()

graph = Graph()

user_list = graph.merge("User", "id", 1)

for user in user_list:
 this_pc = user

for package in packagelist:
    nodes = graph.merge("Package", "name", package)
    for node in nodes:
        relationship = Relationship(this_pc, "INSTALLED", node)
        graph.create(relationship)
