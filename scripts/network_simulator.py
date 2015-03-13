import sys
import string

class Node:
    def __init__(self, node_num, cost_list):
        self.id = node_num
        self.cost_map = {}
        self.adj_nodes = []

        self.cost_map[self.id] = (self.id, 0)
        for i in range(len(cost_list)):
            if cost_list[i] != 'x':
                self.cost_map[i] = (i, int(cost_list[i]))
                self.adj_nodes += [i]

    def __str__(self):
        return str(self.id)

    def update(self, updated_matrix, recieved_from):
        #print self.id, recieved_from
        #print updated_matrix
        #print ""
        for item in updated_matrix.keys():
            dest = item
            distance = updated_matrix[item][1]
            recieved_from_dist = self.cost_map[recieved_from][1]
            #print dest, distance, recieved_from_dist

            if dest not in self.cost_map.keys():
                self.cost_map[dest] = (recieved_from, distance + recieved_from_dist)
            elif self.cost_map[dest][1] > (distance + recieved_from_dist):
                #print self.cost_map[dest]
                self.cost_map[dest] = (recieved_from, distance + recieved_from_dist)
                #print self.cost_map[dest]

    def update_adj(self, adj_list):
        self.adj_nodes = adj_list

    def step_routing(self, split_horizon):
        for node in self.adj_nodes:
            temp_map = self.cost_map
            if split_horizon:
                for dest in temp_map.keys():
                    if temp_map[dest][0] == node:
                        del(temp_map[dest])
            node.update(temp_map, self.id)

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

node_matrix = []
split_horizon = False
while com != "quit":

    if com == "load":
        node_matrix = load_file()
        for node in node_matrix:
            adj_list = []
            this_adj = node.adj_nodes
            for adj in this_adj:
                adj_list += [node_matrix[adj]]
            node.update_adj(adj_list)

    elif com == "tables":
        cycles = int(raw_input("Number of cycles: "))
        for i in range(cycles):
            for node in node_matrix:
                node.step_routing(split_horizon)
        for node in node_matrix:
            node.pretty_print()
    elif com == "preset":
        print "preset"
    elif com == "route":
        source = int(raw_input("Source Node: "))
        dest = int(raw_input("Destination Node: "))
        cycles = int(raw_input("Cycles: "))
        for i in range(cycles):
            for node in node_matrix:
                node.step_routing(split_horizon)
        source = node_matrix[source]
        if dest in source.cost_map.keys():
            cur_node = source
            while cur_node.id != dest:
                print str(cur_node.id) + " -> " + str(cur_node.cost_map[dest][0])
                cur_node = node_matrix[cur_node.cost_map[dest][0]]
        else:
            print "No route avalaible"
    elif com == "trace":
        nodes = raw_input("Nodes to trace: ")
        cycles = int(raw_input("Number of cycles: "))
        nodes = string.split(nodes)
        for i in range(cycles):
            for node in node_matrix:
                if str(node.id) in nodes:
                    node.pretty_print()
                node.step_routing(split_horizon)
            print "-" * 8

    elif com == "split":
        if split_horizon:
            print "Disabling Split Horizon"
            split_horizon = False
        else:
            print "Enabling Split Horizon"
            split_horizon = True
    com = string.lower(raw_input(">"))
