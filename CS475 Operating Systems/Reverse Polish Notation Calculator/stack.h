/**
 * Desfines the Node struct and the methods in stack.c
 * @author Aaron Thompson
 * @version 2/13/2019
 */

int size();
void push();
double pop();
void clearStack();

typedef struct Node{
    double value;
    struct Node *next;
} Node;