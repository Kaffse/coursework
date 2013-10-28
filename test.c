#include <string.h>
#include <stdio.h>
#include <stdlib.h>

int main()
{
	char *url = "www.test.net";
	char *last;
	char *tok;
	while (url = strpbrk((const char *)(url + 1), "."))
		tok = url + 1;
                                                                                                                                                   
	//for(tok = strpbrk (url, dlim); tok; last = tok + 1, tok = strpbrk (tok, dlim)) {} 

	printf ("%s\n", tok); 
	return 0;
}
