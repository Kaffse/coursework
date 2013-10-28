#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "tldlist.h"
#include "date.h"

struct tldnode
{
	TLDNode *left;
	TLDNode *right;
	char *content;
	long long count;                                                                                                      
};


struct tldlist
{
	Date *begin;
	Date *end;
	long count;
	TLDNode *head;		
};

struct tlditerator
{
	TLDNode **array;
	long cur;
};

TLDList *tldlist_create(Date *begin, Date *end)
{
	TLDList *new_tldlist = (TLDList *)malloc(sizeof(TLDList));

	if (new_tldlist) {
		new_tldlist->begin = begin;
		new_tldlist->end = end;
		new_tldlist->count = 0;
		new_tldlist->head = NULL;
	}

	return new_tldlist;
}

static void iterate(TLDNode *tldnode, TLDIterator *iter)
{
	if (tldnode->left)
		iterate(tldnode->left, iter);

	if (tldnode->right)
		iterate(tldnode->right, iter);

	iter->array[iter->cur] = tldnode;
	iter->cur++;
}

void tldlist_destroy(TLDList *tld)
{
	TLDNode *node;

	TLDIterator *iterator = tldlist_iter_create(tld);

	while ((node = tldlist_iter_next(iterator))){
		free(node->content);
		free(node);
	}

	tldlist_iter_destroy(iterator);
	free(tld);
}

int tldlist_add(TLDList *tld, char *hostname, Date *d)
{
	if (date_compare(d, tld->begin) < 0 && date_compare(d, tld->end) > 0)
		return 0;

	TLDNode *node = (TLDNode *)malloc(sizeof(TLDNode));
	if (node) {
		char* url = strrchr(hostname, '.') + 1;

		if (!tld->head){
			node->content = strdup(url);
			node->count = 1;
			node->left = NULL;
			node->right = NULL;
			tld->head = node;
			tld->count++;
			return 1;
		}

		TLDNode *cur = tld->head;

		while(cur){
			if (strcmp(url, cur->content) > 0) {
				if (cur->right != NULL) 
					cur = cur->right;
				else {
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					cur->right = node;
					tld->count++;
					return 1;
				}
			}
			else if (strcmp(url, cur->content) < 0) {
				if (cur->left != NULL) 
					cur = cur->left;
				else {
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					cur->left = node;
					tld->count++;
					return 1;
				}
			}
			else if (strcmp(url, cur->content) == 0) {
				cur->count++;
				tld->count++;
				free(node);
				return 1;
			}
			else
				return 0;
		}
	}
	return 0;
}

long tldlist_count(TLDList *tld)
{
	return tld->count;
}

TLDIterator *tldlist_iter_create(TLDList *tld)
{
	TLDIterator *iter = (TLDIterator *)malloc(sizeof(TLDIterator));

	TLDNode **array = malloc(tld->count * sizeof(TLDNode *));

	iter->array = array;

	iter->cur = 0;

	if (iter) {
		iterate(tld->head, iter);
	}
	return iter;
}

TLDNode *tldlist_iter_next(TLDIterator *iter)
{
	iter->cur = iter->cur - 1;
	if (iter->cur < 0) return NULL;
	return (iter->array[iter->cur]);
}

void tldlist_iter_destroy(TLDIterator *iter)
{
	free(iter->array);
	free(iter);
}

char *tldnode_tldname(TLDNode *node)
{
	if (node)
		return (node->content);
	return NULL;
}

long tldnode_count(TLDNode *node)
{
	return node->count;
}
