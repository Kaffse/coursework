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

void *prepare_header(char header[BUFLEN], char* response, char* conlen, char data[BUFLEN])
{
    time_t rawtime;
    struct tm * timeinfo;
    char buffer[10000];
    char timebuf[128];

    time (&rawtime);
    timeinfo = localtime (&rawtime);
    strftime (timebuf, sizeof(timebuf), "Date: %a, %d %b %G %T %Z",timeinfo);
    buffer[0] = 'H';
    strcat(buffer, "TTP/1.1 ");
    strcat(buffer, response);
    strcat(buffer, "\r\n");
    strcat(buffer, timebuf);
    strcat(buffer, "\r\nAccept-Ranges: bytes\r\nContent-Length: ");
    strcat(buffer, conlen);
    strcat(buffer, "\r\nContent-Type: text/html\r\nConnection: close\r\n\r\n");
    strcat(buffer, data);
    strcpy(header, buffer);

    return NULL;
}
void *connection_worker(void *socketpt)
{
    int socket = *(int *)socketpt;

    ssize_t rcount;
    char buf[BUFLEN];
    char *request;
    char * temp;
    char filename[64];
    char hostname[64];
    char ourhost[64];
    char line[1024];
    char messagebuf[BUFLEN];
    char header[BUFLEN];
    char *pagenotfound = "<h1>404 - Page Not Found</h1>";
    char bodysizebuf[1024];
    int bodysize;
    int i = 0;

    FILE * req;

    fprintf(stderr, "Grabbing input...\n");
    while((rcount = read(socket, buf, BUFLEN)) > 0)
    {
        request = strcat(buf, "\0");

        if ((temp = strstr(request, "GET")) != NULL) 
        {  
            temp = temp + 5;
            while (*temp != ' ' && *temp != '\n' && *temp != '\r') 
            {
                filename[i] = *temp;
                temp++;
                i++;
            }
        }

        fprintf(stderr, "Grabbing Hostname...\n");

        i = 0;
        if ((temp = strstr(request, "Host:")) != NULL) 
        {  
            temp = temp + 6;
            while (*temp != ' ' && *temp != '\n' && *temp != '\r') 
            {
                hostname[i] = *temp;
                temp++;
                i++;
            }
        }

        fprintf(stderr, "Checking our Hostname...\n");

        gethostname(ourhost, 64);

        if (strcmp(hostname, ourhost) != 0)
        {
            fprintf(stderr, "Warning! Hostname sent was: %s Expected: %s\n", hostname, ourhost);
        }

        fprintf(stderr, "Trying to open %s...\n", filename);
        if ((req = fopen(filename, "r")) != NULL)
        {
            *messagebuf = '\0';

            fprintf(stderr, "Attempting to send data...\n");
            while (fgets(line, 1024, req) != NULL)
            {
                strcat(messagebuf, line);
            }

            bodysize = strlen(messagebuf) * sizeof(char);

            sprintf(bodysizebuf, "%d", bodysize);

            prepare_header(header, "200 OK", (char *)bodysizebuf, (char *)messagebuf);

            write(socket, header, strlen(header));
            fprintf(stderr, "Closing file...\n");
            fclose(req);
        }
        else
        {
            bodysize = strlen(pagenotfound) * sizeof(char);
            sprintf(bodysizebuf, "%d", bodysize);

            fprintf(stderr, "File Read Error!\n");
            prepare_header(header, "404 Not Found", (char *)bodysizebuf, pagenotfound); 
            write(socket, header, strlen(header));
        }
        fprintf(stderr, "Grabbing input...\n");
    }

    if (rcount == -1) {
        fprintf(stderr, "Read Error!\n");
    }

    close(socket);
    return NULL;
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
    int *cur_sock;


    fprintf(stderr, "Waiting for Connection...\n");
    while((connfd = accept(fd, (struct sockaddr *) &cliaddr, &cliaddr_len)))
    {
        cur_sock = malloc(1);
        *cur_sock = connfd;

        connection_worker((void *)cur_sock);

        fprintf(stderr, "Waiting for Connection...\n");

    }

    return -1;

}

