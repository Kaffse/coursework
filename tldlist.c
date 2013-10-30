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
static void balance(TLDNode *node, TLDList *tld, int side);


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
	if (node)
		return 1 + max(get_height(node->left), get_height(node->right));
	return -1;
}

static int is_left(TLDNode *node)
{
	if(node->parent->left)
		return strcmp(node->parent->left->content, node->content) == 0;
	return 0;
}

static int get_balance(TLDNode *node)
{
	return get_height(node->left) - get_height(node->right);
}

static TLDNode *find_inbalance(TLDNode *node)
{
	if (!node){
		return NULL;
	}
	else if (abs(get_balance(node)) > 1){
		return node;
	}
	else{
		return find_inbalance(node->parent);
	}
}

static void right_rotation(TLDNode *node, TLDList *tld)
{
	printf("Preforming Right Rotation on %s...\n", node->content);
	TLDNode *q = node;
	TLDNode *p = node->left;
	TLDNode *pa = node->parent;

	if (!pa) {
		tld->head = p;
		p->parent = NULL;
	}
	else{
		p->parent = pa;
		if (is_left(q))
			pa->left = p;
		else
			pa->right = p;
		printf("ng\n");
	}

	q->left = p->right;
	if (p->right)
		p->right->parent = q;
	p->right = q;
	q->parent = p;

	printf("Done!\n\n");
}

static void left_rotation(TLDNode *node, TLDList *tld)
{
	printf("Preforming Left Rotation on %s...\n", node->content);
	TLDNode *p = node;
	TLDNode *q = node->right;
	TLDNode *pa = node->parent;

	if (pa == NULL) {
		tld->head = q;
		q->parent = NULL;
	}
	else{ 
		q->parent = pa;
		if (is_left(p))
			pa->left = q;
		else
			pa->right = q;
	}
	printf("two\n");
	p->right = q->left;
	if (q->left)
		q->left->parent = p;
	q->left = p;
	p->parent = q;
	printf("Done!\n\n");
}

static void leftright_rotation(TLDNode *node, TLDList *tld)
{
	left_rotation(node->left, tld);
	right_rotation(node, tld);
}

static void rightleft_rotation(TLDNode *node, TLDList *tld)
{
	right_rotation(node->right, tld);
	left_rotation(node, tld);
}

static void balance(TLDNode *node, TLDList *tld, int side)
{
	TLDNode *inNode = find_inbalance(node);

	if (inNode) {
		if (get_balance(inNode) > 0 && side == 0)
			right_rotation(inNode, tld);
		if (get_balance(inNode) < 0 && side == 1)
			left_rotation(inNode, tld);
		if (get_balance(inNode) > 0 && side == 1)
			leftright_rotation(inNode, tld);
		if (get_balance(inNode) < 0 && side == 0)
			rightleft_rotation(inNode, tld);
		printf("After Rotations!\n\n");
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
			tld->head = node;
			tld->count++;
			return 1;
		}

		//Right, we've got a head, let's start from the head and move down the tree
		TLDNode *cur = tld->head;

		//While there are nodes to check...
		while(cur){
			//Do we put the new node to the right?
			if (strcmp(url, cur->content) > 0) {
				//Yep! But is there a node already there?
				if (cur->right != NULL){ 
					//Yes! Let's go down the tree futher
					cur = cur->right;
				}
				else {
					//Nope! Let's add this node to the tree...
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					node->parent = cur; 
					cur->right = node;
					tld->count++;
					balance(node, tld, 1);
					return 1;
				}
			}
			//Does it go to the left?...
			else if (strcmp(url, cur->content) < 0) {
				//Yep! Same as above
				if (cur->left != NULL){
					cur = cur->left;
				}
				else {
					node->content = strdup(url);
					node->count = 1;
					node->left = NULL;
					node->right = NULL;
					node->parent = cur;
					cur->left = node;
					tld->count++;
					balance(node, tld, 0);
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
