/*
 * matrixOperations.c
 *
 * @version March 20, 2019
 * @author Aaron Thompson
 */

#include <stdio.h>
#include <stdlib.h>
#include <time.h> 
#include "matrixOperations.h"

/**
 * Multiplies two matricies without threads
 * @param size The length/height of the matricies
 * @param matrix The matrix where the result will be stored
 */ 
void sequentialMultiply(int size, int** matrix){
    int sum;
    for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
            sum = 0;
            for (int k = 0; k < size; k++){
                sum += matrix1[i][k] * matrix2[k][j];
            }
            matrix[i][j] = sum;
        }
    }
}

/**
 * Multiplies two matricies using threads
 * @param parallelArgs an array containing the length of the matrix and the number of threads
 */ 
void parallelMultiply(int parallelArgs[]){

    int threadStart = start++;
    int sum;

    for (int i = threadStart * (parallelArgs[1]/parallelArgs[0]); i < (threadStart+1) * (parallelArgs[1]/parallelArgs[0]); i++){
        for (int j = 0; j < parallelArgs[1]; j++){
            sum = 0;
            for (int k = 0; k < parallelArgs[1]; k++){
                sum += matrix1[i][k] * matrix2[k][j];
            }
            multipliedMatrix[i][j] = sum;
        }
    }
}

/**
 * Resets the two multiplied matricies to 0
 */ 
void resetMatricies(int size){
    for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
            multipliedMatrix[i][j] = 0;
            multipliedMatrix2[i][j] = 0;
        }
    }
}

/**
 * Generates two matricies with random numbers from 0 to 99
 */ 
void randomizeMatricies(int size){
    srand(time(0));
    for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
            matrix1[i][j] = (rand() % (99 + 1)); 
            matrix2[i][j] = (rand() % (99 + 1)); 
        }
    }
}

/**
 * Compare two matricies
 * @return the largest difference between the same element of the two matricies
 */ 
double compareMatricies(int size){
    double largestError = 0;
    for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
            if (abs(multipliedMatrix[i][j] - multipliedMatrix2[i][j]) > largestError){
                largestError = abs(multipliedMatrix[i][j] - multipliedMatrix2[i][j]);
            }
               
        }
    }
    return largestError;
}

/**
 * Utility function to print a matrix
 */ 
void printMatrix(int** matrix, int size){
    for (int i = 0; i < size; i++){
        for (int j = 0; j < size; j++){
            printf("%u ", matrix[i][j]);
        }
        printf("\n");
    }
}

/**
 * Allocates two matricies on the heap
 * @param the length/height of the two matricies
 */ 
void allocateArrays(int size){
    matrix1 = malloc(sizeof(int*) * size);
    matrix2 = malloc(sizeof(int*) * size);
    multipliedMatrix = malloc(sizeof(int*) * size);
    multipliedMatrix2 = malloc(sizeof(int*) * size);


    for(int i = 0; i < size; i++){
        matrix1[i] = malloc(size * sizeof(int));
        matrix2[i] = malloc(size * sizeof(int));
        multipliedMatrix[i] = malloc(size * sizeof(int));
        multipliedMatrix2[i] = malloc(size * sizeof(int));
    }	
}

