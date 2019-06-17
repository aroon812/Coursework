/**
 * Implements the stack 
 * @author Aaron Thompson
 * @version 2/13/2019
 */ 
#include <stdio.h>
#include <stdlib.h>
#include "stack.h"

int numNodes;
Node *topNode;

/**
 * Pops every node off of the stack
 */ 
void clearStack(){
    while (topNode != NULL){
        pop();
    }
}

/**
 * Returns the number of values on the stack
 */ 
int size(){
    return numNodes; 
}

/**
 * Puts a new value on to the stack
 */ 
void push(double value){
    Node *newNode = (Node*) malloc(sizeof(Node));
    newNode->value = value;
    newNode->next = NULL;

    if (numNodes == 0){
        topNode = newNode;
    }
    else{
        newNode->next = topNode;
        topNode = newNode;
    }
    numNodes++;
}

/**
 * Removes and returns the value at the top of the stack
 */ 
double pop(){
    double returnValue = topNode->value;
    free(topNode);
    topNode = topNode->next;
    numNodes--;
    return returnValue;
}