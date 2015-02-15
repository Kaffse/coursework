import sys

filename = raw_input("Filename of network file: ")

try:
    file = open(filename, "r")
except:
    print "Error opening file"
    sys.exit(0)

cost_matrix = []

for line in file:
    #remove the newline
    line = line[:-1]

    #pack information into a datastruture
    cost_matrix += [line.split()]

print cost_matrix
