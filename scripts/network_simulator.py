filename = raw_input("Filename of network file: ")

file = open(filename, "r")

for line in file:
    print line
