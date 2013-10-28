#include <stdlib.h>
#include <stdio.h>
#include "date.h"

struct date 
{
	int day;
	int month;
	int year;
};

Date *date_create(char *datestr)
{
	Date *new_date = (Date *)malloc(sizeof(Date));
	if (new_date)
		sscanf(datestr, "%d/%d/%d", &new_date->day, &new_date->month, &new_date->year);
	return new_date;
}

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

int date_compare(Date *date1, Date *date2)
{
	if (date1->year >= date2->year)
		if (date1->month >= date2->month)
			if (date1->day >= date2->day)
				return 1;
			else if (date1->day == date2->day && 
					date1->month == date2->month && 
					date1->year == date2->year)
				return 0;
			else
				return -1;
		else
			return -1;
	else 
		return -1;
}

void date_destroy(Date *d)
{
	free(d);
}
