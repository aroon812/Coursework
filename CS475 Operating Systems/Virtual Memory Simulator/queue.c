/**
 * Basically the queue from xinu cut down for this assignment
 */ 
#include <stdio.h>
#include <stdlib.h>
#include "queue.h"

/**
 * Add an element to the tail of the queue.
 * @param index the frame number to add
 * @param q the queue
 * @param priority the priority of the queue
 */ 
void  enqueue(int index, struct queue *q, int priority)
{
	struct qentry *newEntry = (struct qentry*) malloc(sizeof(struct qentry));
	newEntry->index = index;
	newEntry->priority = priority;
	newEntry->next = NULL;
	newEntry->prev = NULL;

	struct qentry *currEntry;	
	struct qentry *prevEntry;	

	currEntry = q->head;
	while (currEntry != NULL && currEntry->priority >= priority){
		currEntry = currEntry->next;
	}

	if (currEntry == NULL){		
		prevEntry = q->tail;
	}
	else{
		prevEntry = currEntry->prev;
	}

	newEntry->prev = prevEntry;
	newEntry->next = currEntry;

	if (prevEntry != NULL){
		prevEntry->next = newEntry;
	}
	if (currEntry != NULL){
		currEntry->prev = newEntry;
	}

	if (NULL == prevEntry){
		q->head = newEntry;
	}
	if (NULL == currEntry){
		q->tail = newEntry;
	}
	q->size++;			
}

/**
 * Removes an element at the head of the queue
 * @param q the queue
 * @return the frame number that was just dequeued
 */ 
int dequeue(struct queue *q)
{
	int index;	
	struct qentry *head = q->head;
	struct qentry *newHead = head->next;
	index = head->index;

	if (newHead != NULL){
		newHead->prev = NULL;
	}
	else{
		q->tail = NULL;
	}

	q->head = newHead;
	q->size--;
	free(head);
	return index;
}

/**
 * Removes a frame with a specified index
 * @param index the index (frame number)
 * @param q the queue
 * @return the frame number that was removed
 */ 
int removeFrame(int index, struct queue *q)
{
	struct qentry *currEntry = q->head;
	while (currEntry != NULL)
	{
		if (currEntry->index == index)
		{
			struct qentry *next = currEntry->next;
			struct qentry *prev = currEntry->prev;
			if (next != NULL){
				next->prev = prev;
			}
			if (prev != NULL){
				prev->next = next;
			}

			if (currEntry == q->head){
				q->head = next;
			}
			if (currEntry == q->tail){
				q->tail = prev;
			}
			q->size--;

			free(currEntry);
			return index;
		}
		currEntry = currEntry->next;
	}
	return -1;
}

