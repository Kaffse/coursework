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

//helper function to get content header
char *get_content_header(char *ext)
{
    if ((strcmp(ext, "html")) == 0 || (strcmp(ext, "htm")) == 0) {return "text/html";}
    else if ((strcmp(ext, "txt")) == 0) {return "text/plain";}
    else if ((strcmp(ext, "jpg") == 0) || (strcmp(ext, "jpeg")) == 0) {return "image/jpeg";}
    else if ((strcmp(ext, "gif") == 0)) {return "image/gif";}
    else {return "application/octet-stream";}
}

//helper function to build the header
void *prepare_header(char header[BUFLEN], char* response, char* conlen, char *ext, char data[BUFLEN])
{
    time_t rawtime;
    struct tm * timeinfo;
    char buffer[10000];
    char timebuf[128];

    //grabbing time now
    time (&rawtime);
    timeinfo = localtime (&rawtime);

    //building formatted string
    strftime (timebuf, sizeof(timebuf), "Date: %a, %d %b %G %T %Z",timeinfo);

    //building the header
    buffer[0] = 'H';
    strcat(buffer, "TTP/1.1 ");
    strcat(buffer, response);
    strcat(buffer, "\r\n");
    strcat(buffer, timebuf);
    strcat(buffer, "\r\nAccept-Ranges: bytes\r\nContent-Length: ");
    strcat(buffer, conlen);
    strcat(buffer, "\r\nContent-Type: ");
    strcat(buffer, get_content_header(ext));
    strcat(buffer, "\r\n\r\n");
    strcat(buffer, data);
    strcpy(header, buffer);

    return NULL;
}

//body function for threads
void *connection_worker(void *socketpt)
{
    //build socket from data in pointer
    int socket = *(int *)socketpt;

    //define wall of buffers and vars
    ssize_t rcount;
    char request[BUFLEN];
    char * temp;
    char filename[64];
    char hostname[64];
    char ourhost[64];
    char line[1024];
    char messagebuf[BUFLEN];
    char * cur;
    char * ext;
    char header[BUFLEN];
    char *pagenotfound = "<h1>404 - Page Not Found</h1>";
    char bodysizebuf[1024];
    int bodysize;
    int i = 0;

    FILE * req;

    fprintf(stderr, "Grabbing input...\n");

    //while the client is sending stuff, keep looping
    while((rcount = read(socket, request, BUFLEN)) > 0)
    {

        //rip out the filename the server is looking for
        i = 0;
        if ((temp = strstr(request, "GET")) != NULL) 
        {  
            temp = temp + 5;
            while (*temp != ' ' && *temp != '\n' && *temp != '\r') 
            {
                filename[i] = *temp;
                temp++;
                i++;
            }
            filename[i + 1] = '\0';
        }
        else
        {
            fprintf(stderr, "No Get Request Detected in Header!");
            prepare_header(header, "400 Bad Request", 0, "html", ""); 
            write(socket, header, strlen(header));
        }

        //if no file requested, send index
        if (filename[0] == '\0') 
        {
            filename[0] = 'i';
            filename[1] = '\0';
            strcat(filename, "ndex.html");
        }

        //get the extension of the requested file
        ext = strrchr(filename, '.') + 1;

        fprintf(stderr, "Grabbing Hostname...\n");

        //rip out the host name
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
        else
        {
            fprintf(stderr, "No Host Detected in Header!");
            prepare_header(header, "400 Bad Request", 0, "html", ""); 
            write(socket, header, strlen(header));
        }

        fprintf(stderr, "Checking our Hostname...\n");

        //check out hostname
        gethostname(ourhost, 64);

        //compare them, warn if different
        if (strcmp(hostname, ourhost) != 0)
        {
            fprintf(stderr, "Warning! Hostname sent was: %s Expected: %s\n", hostname, ourhost);
        }

        fprintf(stderr, "Trying to open %s...\n", filename);

        //try and opeen the file
        if ((req = fopen(filename, "r")) != NULL)
        {
            *messagebuf = '\0';

            fprintf(stderr, "Attempting to read data...\n");
            cur = messagebuf;
            while (fgets(line, 1024, req) != NULL)
            {
                memcpy(cur, line, sizeof(line));
                cur = strrchr(messagebuf, '\0');
            }

            //grab the body size
            bodysize = strlen(messagebuf) * sizeof(char);

            //stick it into a string
            sprintf(bodysizebuf, "%d", bodysize);

            prepare_header(header, "200 OK", (char *)bodysizebuf, (char *)ext, (char *)messagebuf);

            fprintf(stderr, "Attempting to send data...\n");

            write(socket, header, strlen(header));
            fprintf(stderr, "Closing file...\n");
            fclose(req);
        }
        else //need to 404 the file 
        {
            bodysize = strlen(pagenotfound) * sizeof(char);
            sprintf(bodysizebuf, "%d", bodysize);

            fprintf(stderr, "File Read Error!\n");

            //if it's a 404 on a html file, then send a generic 404 page
            if ((((strcmp((char *)ext, "html")) == 0) || (strcmp((char *)ext, "htm")) == 0))
            {
                prepare_header(header, "404 Not Found", (char *)bodysizebuf, (char *)ext, pagenotfound); 
            }
            //else send no content
            else
            {
                prepare_header(header, "404 Not Found", 0, (char *)ext, "");
            }
            write(socket, header, strlen(header));
        }
        request[0] = '\0';
        fprintf(stderr, "Grabbing input...\n");
    }

    if (rcount == -1) {
        fprintf(stderr, "Read Error!\n");
    }

    //tidy up
    close(socket);
    free(socketpt);
    return NULL;
}

int main()
{
    //declare socket vars
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    int backlog = 10;
    int connfd;
    int *cur_sock;
    struct sockaddr_in addr;
    struct sockaddr_in cliaddr;

    addr.sin_addr.s_addr = INADDR_ANY;
    addr.sin_family = AF_INET;
    addr.sin_port = htons(8080);

    fprintf(stderr, "Creating Socket...\n");
    if (fd == -1) {
        fprintf(stderr, "Socket Error!\n");
        return 0;
    }

    fprintf(stderr, "Binding...\n");
    if (bind(fd, (struct sockaddr *) &addr, sizeof(addr)) == -1) {
        fprintf(stderr, "Bind Error!\n");
        return 0;
    }

    fprintf(stderr, "Listening...\n");
    if (listen(fd, backlog) == -1) {
        fprintf(stderr, "Listen Error!\n");
        return 0;
    }

    socklen_t cliaddr_len = sizeof(cliaddr);

    //loop to wait on new connections then pass them to threads
    fprintf(stderr, "Waiting for Connection...\n");
    while((connfd = accept(fd, (struct sockaddr *) &cliaddr, &cliaddr_len)))
    {
        cur_sock = malloc(1);
        *cur_sock = connfd;

        pthread_t worker;
        
        if ((pthread_create(&worker, NULL, connection_worker, (void *)cur_sock)) < 0)
        {
            fprintf(stderr, "Error Creating Thread!");
            return 0;
        }

        fprintf(stderr, "Waiting for Connection...\n");

    }

    close(fd);

    return -1;

}

