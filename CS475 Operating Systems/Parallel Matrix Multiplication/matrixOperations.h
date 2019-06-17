/*
 * matrixOperations.h
 *
 * @version March 20, 2019
 * @author Aaron Thompson
 */

int** matrix1;
int** matrix2;
int** multipliedMatrix;
int** multipliedMatrix2;
int start;

void sequentialMultiply();
void parallelMultiply();
void randomizeMatricies();
void allocateArrays();
double compareMatricies();
void printMatrix();
void resetMatricies();