/*
 * Contains all of the methods necessary to run the heapsort
 * @author: Aaron Thompson
 */
#include <stdio.h>
#include <stdlib.h>
#include "employee.h"
#include "heap.h"

/**
 * Sorts a list of n employees in descending order
 *
 * @param	*A	Pointer to the list of employees
 * @param	n	Size of the list
 */
void heapSort(struct Employee *A, int n)
{
    buildHeap(A, n);
	
    while (n > 0){
        swap(&A[n-1], &A[0]);
        n--;
        heapify(A, 0, n);
    }
}

/**
 * Given an array A[], we want to get A[] into min-heap property
 * We only need to run heapify() on internal nodes (there are floor(n/2) of them)
 * and we need to run it bottom up (top-down cannot build a heap)
 *
 * @param	*A	Pointer to the list of employees
 * @param	n	Size of the list
 */
void buildHeap(struct Employee *A, int n)
{
	for (int i = n/2; i >= 0; i--){
        heapify(A, i, n);
    }
}

/**
 * We want to start with A[i] and trickle it downward
 * if it is greater than either left or right child.
 *
 * @param	*A	Pointer to the list of employees
 * @param	i	Index of current element to heapify
 * @param	n	Size of the list
 */
void heapify(struct Employee *A, int i, int n)
{

    int smaller = i;
    int left = (i*2)+1;
    int right = (i*2)+2;

     if (left < n && A[i].salary > A[left].salary){
        smaller = left;
    }

    if (right < n && A[smaller].salary > A[right].salary){
        smaller = right;
    }

    if (A[i].salary > A[smaller].salary){
        swap(&A[smaller], &A[i]);
        heapify(A, smaller, n);
    }
}

/**
 * Swaps the locations of two Employees
 * @param *e1 An Employee
 * @param *e2 Another Employee to swap with
 */
void swap(struct Employee *e1, struct Employee *e2)
{
	Employee temp = *e1; 
    *e1 = *e2;
    *e2 = temp; 
}

/**
 * Gets the minimally paid employee. Assumes the given list is a min-heap.
 *
 * @param	*A	Pointer to the list of employees
 * @param	n	Size of the list
 */
struct Employee *getMinPaidEmployee(struct Employee *A, int n)
{
	return &A[n-1];
}

/**
 * Outputs an array of Employees
 * @param	*A	Pointer to the list of employees
 * @param	n	Size of the list
 */
void printList(struct Employee *A, int n)
{
    int i;
	for (i = 0; i < n-1; i++){
        printf("[id=%s, sal=%u], ", A[i].name ,A[i].salary);
    }
    printf("[id=%s, sal=%u] ", A[i].name ,A[i].salary);
    printf("\n");
}