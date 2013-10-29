#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include "tldlist.h"
#include "date.h"

#define max( a, b ) ( ((a) > (b)) ? (a) : (b) )

/*********************************************************************************
 * Keir Alexander Smith
 * 1102028
 * 29/10/2013
 *
 * Advanced Programming 3 - Assessed Exercise 1
 *
 * This is my own work as defined in the Academic Ethics agreement I have signed.
 *********************************************************************************/


//Define Structs for each 'object' in the tree
struct tldnode
{
	//Left child
	TLDNode *left;
	//Right child
	TLDNode *right;
	//Parent node
	TLDNode *parent;
	//TLD
	char *content;
	//How many of this TLD?
	long long count;
	//height of node
	long height;                                                                                                      
};


struct tldlist
{
	//Start limit
	Date *begin;
	//End limit
	Date *end;
	//How many successful additions to the tree?
	long count;
	//Head of the tree
	TLDNode *head;		
};

struct tlditerator
{
	//Array of pointers to nodes, so we can easily iterate back over the whole tree
	TLDNode **array;
	//Pointer to the current end of the array
	long cur;
};

//Constructor method, inits values and returns a pointer to new tldlist
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

//This method is for internal uses only. It's a recursive method for iterating over the whole tree
static void iterate(TLDNode *tldnode, TLDIterator *iter)
{
	if (tldnode->left)
		iterate(tldnode->left, iter);

	if (tldnode->right)
		iterate(tldnode->right, iter);

	iter->array[iter->cur] = tldnode;
	iter->cur++;
}

static int get_height(TLDNode *node) 
{
	if (!node)
		return -1;

	return max(get_height(node->left), get_height(node->right)) + 1; 

}

static TLDNode *find_inbalance(TLDNode *node)
{
	//printf("Finding an Inbalance...\n");
	if (!node)
		return NULL;
	else if ((get_height(node->left) - get_height(node->right)) > 1)
		return node;
	else
		return find_inbalance(node->parent);
}

static int case1(TLDNode *node)
{
	return get_height(node->left) > get_height(node->right) &&
		get_height(node->left->left) >= get_height(node->left->right);
}

static int case2(TLDNode *node)
{
	return get_height(node->left) > get_height(node->right) &&
		get_height(node->left->right) > get_height(node->left->left);
}

static int case3(TLDNode *node)
{
	return get_height(node->right) > get_height(node->left) &&
		get_height(node->right->left) > get_height(node->right->right);
}

static int case4(TLDNode *node)
{
	return get_height(node->right) > get_height(node->left) &&
		get_height(node->right->right) >= get_height(node->right->left);
}

static void balance(TLDNode *node)
{
	TLDNode *inNode = find_inbalance(node);

	printf("The node with the TLD %s has a height of %d\n", node->content, get_height(node));

	//printf("Running Tests...\n");
	if (inNode) {
		//printf("There is an inbalance!\n");
		if (case1(node)) 
			printf("one\n");
		else if (case2(node))
			printf("two\n");
		else if (case3(node))
			printf("three\n");
		else if (case4(node))
			printf("four\n");
	}
}


//De-constructor, frees up all memory used by the whole tree using an iterator
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

//Add a node to the tree, checking if it's within the input parameters
int tldlist_add(TLDList *tld, char *hostname, Date *d)
{
	
	//Compare the dates, is the date of this node within the limits?
	if (date_compare(d, tld->begin) < 0 && date_compare(d, tld->end) > 0)
		return 0;

	//Malloc a new node
	TLDNode *node = (TLDNode *)malloc(sizeof(TLDNode));

	//Malloc successful? Great!
	if (node) {
		
		//Let's get a new string to hold our TLD using strrchr
		char* url = strrchr(hostname, '.') + 1;


		//Not got a head of the tree? Let's add one!	
		if (!tld->head){
			//This line makes sure to copy a new instance of url so we don't go overwriting memory locations
			node->content = strdup(url);
			node->count = 1;
			node->left = NULL;
			node->right = NULL;
			node->parent = NULL;
			node->height = -1;
			tld->head = node;
			tld->count++;
			balance(node);
			return 1;
		}

		//Right, we've got a head, let's start from the head and move down the tree
		TLDNode *cur = tld->head;
		TLDNode *last;
		long height = -1;

		//While there are nodes to check...
		while(cur){
			//Do we put the new node to the right?
			if (strcmp(url, cur->content) > 0) {
				//Yep! But is there a node already there?
				if (cur->right != NULL){ 
					//Yes! Let's go down the tree futher
					last = cur;
					cur = cur->right;
					height++;
				}
				else {
					//Nope! Let's add this node to the tree...
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					node->parent = last; 
					node->height = height;
					cur->right = node;
					tld->count++;
					balance(node);
					return 1;
				}
			}
			//Does it go to the left?...
			else if (strcmp(url, cur->content) < 0) {
				//Yep! Same as above
				if (cur->left != NULL){
					last = cur;	
					cur = cur->left;
					height++;
				}
				else {
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					node->parent = last;
					node->height = height;
					cur->left = node;
					tld->count++;
					return 1;
				}
			}
			//It doesn't go to either side, so it must match this node!
			else if (strcmp(url, cur->content) == 0) {
				//Increment count on the node, and list and free the node we didn't use
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

//How many successful additions have we made?
long tldlist_count(TLDList *tld)
{
	return tld->count;
}

//Let's make a new iterator!
TLDIterator *tldlist_iter_create(TLDList *tld)
{
	//Malloc a new iterator...
	TLDIterator *iter = (TLDIterator *)malloc(sizeof(TLDIterator));

	//Malloc a new array of pointers...
	TLDNode **array = malloc(tld->count * sizeof(TLDNode *));

	//point the array inside the iterator to our new array
	iter->array = array;

	//init the current node to be the zeroth node
	iter->cur = 0;

	//fun begins! if we malloc'd right, iterate over the whole tree
	if (iter) 
		iterate(tld->head, iter);

	return iter;
}

//We've got an iterator, return to me the next node, otherwise NULL
TLDNode *tldlist_iter_next(TLDIterator *iter)
{
	if (!iter) 
	    return NULL;

	//Decrement current node
	iter->cur = iter->cur - 1;

	//if we are at the end of our iterator list, return NULL
	if (iter->cur < 0) return NULL;


	return (iter->array[iter->cur]);
}

//Deconstructor for the iterator, simply free the array then the iterator
void tldlist_iter_destroy(TLDIterator *iter)
{
	free(iter->array);
	free(iter);
}

//What is the TLD of this current node?
char *tldnode_tldname(TLDNode *node)
{
	if (node)
		return (node->content);
	return NULL;
}

//What is the current number of times this TLD has been added?
long tldnode_count(TLDNode *node)
{
	return node->count;
}
