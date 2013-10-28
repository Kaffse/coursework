CC=gcc
CFLAGS=-w -Wall -g
LDFLAGS=
EXECUTABLE=tldmonitor

all: tldmonitor
	
tldmonitor: tldmonitor.c date.c tldlist.c
	$(CC) $(CFLAGS) -o $(EXECUTABLE) tldmonitor.c date.c tldlist.c
