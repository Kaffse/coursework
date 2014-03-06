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

int prepare_header(char header[BUFLEN])
{
    time_t rawtime;
    struct tm * timeinfo;
    char buffer [10000];

    time (&rawtime);
    timeinfo = localtime (&rawtime);
    strftime (buffer,sizeof(buffer),"HTTP/1.1 200 OK\r\nDate: %a, %d %b %G %T %Z\r\nAccept-Ranges: bytes\r\nContent-Length: ",timeinfo);

    strcpy(header, buffer);
    return 1;
}

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
    addr.sin_port = htons(8080);

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
    char *request;
    char * temp;
    char *filename;
    //char *hostname;
    //char ourhost[1024];
    char cwd[1024];
    char line[1024];
    char messagebuf[BUFLEN];
    char bodysizebuf[1024];
    int bodysize;

    FILE * req;

    fprintf(stderr, "Waiting for Connection...\n");
    while((connfd = accept(fd, (struct sockaddr *) &cliaddr, &cliaddr_len)))
    {

        fprintf(stderr, "Grabbing input...\n");
        rcount = read(connfd, buf, BUFLEN);
        if (rcount == -1) {
            fprintf(stderr, "Read Error!\n");
        }

        request = strcat(buf, "\0");

        if ((temp = strstr(request, "GET")) != NULL) 
        {  
            temp = temp + 5;
            while (*temp != ' ') 
            {
                char temps[2];
                temps[0] = *temp;
                temps[1] = '\0';
                filename = strcat(filename, temps);
                temp++;
            }
        }

        /*if ((temp = strstr(request, "Host:")) != NULL) 
        {  
            temp = temp + 6;
            while (*temp != ' ') 
            {
                char temps[2];
                temps[0] = *temp;
                temps[1] = '\0';
                hostname = strcat(hostname, temps);
                temp++;
            }
        }

        fprintf(stderr, "Checking Hostname...\n");
        
        gethostname(ourhost, 1024);

        if (strcmp(hostname, ourhost) != 0)
        {
            fprintf(stderr, "Warning! Hostname sent was: %s\nExpected: %s\n", hostname, ourhost);
        }*/

        getcwd(cwd, sizeof(cwd));
        strcat(cwd, filename);

        fprintf(stderr, "Trying to open %s...\n", filename);
        if ((req = fopen("index.html", "r")) == NULL)
        {
            fprintf(stderr, "File Error!\n");
            continue;
        }

        char header[BUFLEN];
        prepare_header(header);

        fprintf(stderr, "Attempting to send data...\n");
        while (fgets(line, 1024, req) != NULL)
        {
            strcat(messagebuf, line);
        }

        bodysize = strlen(messagebuf) * sizeof(char);

        sprintf(bodysizebuf, "%d", bodysize);

        strcat(header, bodysizebuf);

        temp = "\r\nContent-Type: text/html\r\n\r\n";

        strcat(header, temp);

        strcat(header, messagebuf);

        write(connfd, header, strlen(header));
        fprintf(stderr, "Closing file...\n");

        fclose(req);

        close(connfd);

        fprintf(stderr, "Waiting for Connection...\n");

    }

    return -1;

}

