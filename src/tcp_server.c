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

int main()
{
    fprintf(stderr, "Creating Socket...\n");
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd == -1) {
        fprintf(stderr, "Socket Error!\n");
        return 0;
    }

    struct sockaddr_in addr;

    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(8081);

    fprintf(stderr, "Binding...\n");
    if (bind(fd, (struct sockaddr *) &addr, sizeof(addr)) == -1) {
        fprintf(stderr, "Bind Error!\n");
        return 0;
    }

    int backlog = 10;

    fprintf(stderr, "Listening...\n");
    if (listen(fd, backlog) == -1) {
        fprintf(stderr, "Listen Error!\n");
        return 0;
    }

    struct sockaddr_in cliaddr;
    socklen_t cliaddr_len = sizeof(cliaddr);
    int connfd;

    ssize_t rcount;
    char buf[BUFLEN];
    char *message;
    message = "Friend Message from Friendly Server\n";

    fprintf(stderr, "Waiting for Connection...\n");
    while((connfd = accept(fd, (struct sockaddr *) &cliaddr, &cliaddr_len)))
    {
        write(connfd, message, strlen(message));

        fprintf(stderr, "Grabbing input...\n");
        rcount = read(connfd, buf, BUFLEN);
        if (rcount == -1) {
            fprintf(stderr, "Read Error!\n");
        }

        fprintf(stderr, strcat(buf, "\n"));

        fprintf(stderr, "Waiting for Connection...\n");

    }

    return -1;

}
