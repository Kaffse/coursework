import sys
import string

class Node:
    def __init__(self, node_num, cost_list):
        self.id = node_num
        self.cost_map = {}
        self.adj_nodes = []

        for i in range(len(cost_list)):
            if cost_list[i] != 'x':
                self.cost_map[i] = (i, int(cost_list[i]))
                self.adj_nodes += [i]

    def __str__(self):
        return str(self.id)

    def update(self, updated_matrix):
        for item in updated_matrix.keys():
            dest = item
            recieved_from = updated_matrix[item][0]
            distance = updated_matrix[item][0]

            if dest not in self.cost_map.keys():
                self.cost_map[dest] = (recieved_from, distance)
            elif self.cost_map[dest] > distance:
                self.cost_map[dest] = (recieved_from, distance)

    def update_adj(self, adj_list):
        self.adj_nodes = adj_list

    def step_routing():
        for node in self.adj_nodes:
            node.update(self.cost_map)

    def pretty_print(self):
        print str(self.id)
        for node in self.cost_map.keys():
            print str(node) + ":" + str(self.cost_map[node])

def load_file():
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

    return node_matrix

com = string.lower(raw_input(">"))

while com != "quit":
    node_matrix = []
    split_horizon = True

    if com == "load":
        node_matrix = load_file()
        for node in node_matrix:
            adj_list = []
            this_adj = node.adj_nodes
            for adj in this_adj:
                adj_list += [node_matrix[adj]]
            node.update_adj(adj_list)

    elif com == "tables":
        print node_matrix
        for i in range(3):
            for node in node_matrix:
                node.step()
        for node in node_matrix:
            node.pretty_print()
    elif com == "preset":
        print "preset"
    elif com == "route":
        print "route"
    elif com == "trace":
        print "trace"
    elif com == "split":
        if split_horizon:
            print "Disabling Split Horizon"
            split_horizon = False
        else:
            print "Enabling Split Horizon"
            split_horizon = True
    com = string.lower(raw_input(">"))
