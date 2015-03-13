import sys
import string

class Node:
    def __init__(self, node_num, cost_list):
        self.id = node_num
        self.cost_map = {}
        self.adj_nodes = []
        self.failure_settings = {}
        self.step_count = 0

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
        self.step_count += 1
        if self.step_count in self.failure_settings.keys():
            this_failure = self.failure_settings[self.step_count]
            if this_failure[1] < 0:
                del(self.cost_map[this_failure[0]])
            else:
                self.cost_map[this_failure[0]][1] = this_failure[1]
        for node in self.adj_nodes:
            temp_map = self.cost_map
            if split_horizon:
                for dest in temp_map.keys():
                    if temp_map[dest][0] == node:
                        del(temp_map[dest])
            node.update(temp_map, self.id)

    def set_failure(self, step_num, dest_link, new_cost):
        self.failure_settings[step_num] = (dest_link, new_cost)
        if new_cost < 0:
            for node in self.adj_nodes:
                if node.id == dest_link:
                    self.adj_nodes.remove(node)
            print "Set link " + str(self.id) + " -> " + str(dest_link) + " to fail on step " + str(step_num)
        else:
            print "Set link " + str(self.id) + " -> " + str(dest_link) + " to " + str(new_cost) + "  on step " + str(step_num)

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

print "ANC4 Network Simulator"
print "Commands are as follows: load, tables, preset, route, trace, split, help, quit"
print "Please use help for speicifc help on each command"
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
        cycle = int(raw_input("Fail on cycle #: "))
        source = int(raw_input("Origin node of change: "))
        dest = int(raw_input("Destination node of change: "))
        change = int(raw_input("Change of cost: "))
        node_matrix[source].set_failure(cycle, dest, change)
        node_matrix[dest].set_failure(cycle, source, change)

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

    elif com == "help":
        com_help = raw_input("Which command do you need help with: ")
        if com_help == "load":
            print "Will prompt you for a valid network file, this is required to use the other commands. Use this to reset routing tables"
        elif com_help == "tables":
            print "Prompted for # of cycles to preform before printing out all the routing tables for all nodes. Use 0 to print current tables"
        elif com_help == "preset":
            print "Promted for the cycle the change will occur, one node in the link, the other node in the link and the cost change. A cost chance to -1 indicates failure"
        elif com_help == "route":
            print "Promted for the soruce and destination nodes of the route and how many cycle of the routing algoritm to preform. Use 0 to print current route"
        elif com_help == "trace":
            print "Prompted for the nodes of interest, seperated by a single space, then the number of cycles of the routing algoritm to preform"
        elif com_help == "split":
            print "Switches split horizon on and off in all routing algoritms. Defaulted to off"
    com = string.lower(raw_input(">"))
