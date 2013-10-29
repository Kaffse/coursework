#include <stdlib.h>
#include <stdio.h>
#include "date.h"

/*********************************************************************************
 * Keir Alexander Smith
 * 1102028
 * 29/10/2013
 *
 * This is my own work as defined in the Academic Ethics agreement I have signed.
 *********************************************************************************/

//Let's define our date struct...
struct date 
{
	int day;
	int month;
	int year;
};

//Date constructor
Date *date_create(char *datestr)
{
	//Malloc a new date....
	Date *new_date = (Date *)malloc(sizeof(Date));
	
	//If it malloc'd, use sscanf to return the three ints in the formatted string
	if (new_date)
		sscanf(datestr, "%d/%d/%d", &new_date->day, &new_date->month, &new_date->year);

	return new_date;
}

//Copy constructor for date, super simple
Date *date_duplicate(Date *d)
{
	Date *new_date = (Date *)malloc(sizeof(Date));

	if (new_date) {
		new_date->day = d->day;
		new_date->month = d->month;
		new_date->year = d->year;
	}
	return new_date;
}

//Compare two dates, if date1>date1 return >1, d1==d2 r 0, d1<d2 r <0 and look how pretty this is!
int date_compare(Date *date1, Date *date2)
{
	return ( ( date1->year * 10000000 + date1->month * 1000 + date1->day ) - 
	( date2->year * 10000000 + date2->month * 10000 + date2->day ) );
}

//Deconstruct date
void date_destroy(Date *d)
{
	free(d);
}
