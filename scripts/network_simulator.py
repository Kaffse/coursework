import sys

class Node:
    def __init__(self, node_num, cost_list):
        self.id = node_num
        self.cost_map = {}

        for i in range(len(cost_list)):
            if cost_list[i] != 'x':
                self.cost_map[i] = (i, int(cost_list[i]))

    def __str__(self):
        return str(self.id)

    def update(self, dest, recived_from, distance):
        if dest not in self.cost_map.keys():
            self.cost_map[dest] = (recieved_from, distance)
        elif self.cost_map[dest] > distance:
            self.cost_map[dest] = (recieved_from, distance)

    def pretty_print(self):
        print str(self.id)
        for node in self.cost_map.keys():
            print str(node) + ":" + str(self.cost_map[node])

filename = raw_input("Filename of network file: ")

try:
    file = open(filename, "r")
except:
    print "Error opening file"
    sys.exit(0)

node_matrix = []
i = 0

for line in file:
    #remove the newline
    line = line[:-1]

    #pack information into a datastruture
    node_matrix += [Node(i, line.split())]
    i += 1

#set n to be total number of nodes in the network
n = len(node_matrix)

for node in node_matrix:
    node.pretty_print()

#for node in range(n):
    #routing stuff
