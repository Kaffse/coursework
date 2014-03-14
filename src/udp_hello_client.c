/*
* Light(est) Weight TCP server
* Author: Keir Alexander Smith
*
* This application is designed and implemented simply to accept and process HTTP GET requests and respond with data correctly
*
*/

#define BUFLEN 10000

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <signal.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <errno.h>
#include <sys/time.h>
#include <stdlib.h>
#include <memory.h>
#include <ifaddrs.h>
#include <net/if.h>
#include <stdarg.h>
#include <time.h>
#include <pthread.h>

int main()
{
    //declare socket vars
    int fd = socket(AF_INET, SOCK_DGRAM, 0);
    int backlog = 10;
    int rlen;
    struct sockaddr_in addr;
    struct sockaddr_in cliaddr;

    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(5008);

    fprintf(stderr, "Creating Socket...\n");
    if (fd == -1) {
        fprintf(stderr, "Socket Error!\n");
        return 0;
    }

    fprintf(stderr, "Accepting Input...\n");
    if ((rlen = recvfrom(fd, buffer, buflen, 0, %cliaddr, sizeof(cliaddr))) < 0)
    {
        fprintf(stderr, "Error Accepting Input!i\n");
        return 0;
    }

    fprintf(stderr, "%s\n", (char *)(buffer + '\0'));

    close(fd);

    return -1;

}
