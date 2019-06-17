/*
 * main.c
 *
 * @version March 20, 2019
 * @author Aaron Thompson
 */
#include <pthread.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h> 
#include "matrixOperations.h"
#include "rtclock.h"

/*
 * A matrix multiplication program that can multiply two matricies using parallel or sequential programming
 */
int main(int argc, const char * argv[]){

    char parallel;
    int threadCount;
    int size;
    int numRuns = 3;
    double sequentialStart;
    double sequentialEnd;
    double parallelStart;
    double parallelEnd;
    double sequentialTime;
    double parallelTime;
    double parallelTimeCumulative = 0;
    double sequentialTimeCumulative = 0;

    if (argv[1][0] != 'S' && argv[1][0] != 'P'){
        printf("First argument must be \"S\" or \"P\" \n");
        return -1;
    }
    else{
        parallel = argv[1][0];
    }

    if (parallel == 'S'){
        if (argc != 3){
            printf("Usage: ./mmm <mode> [num threads] <size>\n");
            return -1;
        }
    }
    else{
        if (argc != 4){
            printf("Usage: ./mmm <mode> [num threads] <size>\n");
            return -1;
        }
    }

    if (argc == 3){
        size = strtol(argv[2], NULL, 10);
        threadCount = 1;
    }
    if (argc == 4){
        threadCount = strtol(argv[2], NULL, 10);
        size = strtol(argv[3], NULL, 10);
    }

    allocateArrays(size);
    randomizeMatricies(size);

    if (parallel == 'S'){
        sequentialStart = rtclock();
        sequentialMultiply(size, multipliedMatrix2);
        sequentialEnd = rtclock();
        sequentialTime = sequentialEnd - sequentialStart;

        printf("=======\n");
        printf("mode: sequential\n");
        printf("thread count: %u\n", threadCount);
        printf("size: %u\n", size);
        printf("=======\n");
        printf("Sequential time: %f sec\n", sequentialTime);
    }
    else if (parallel == 'P'){
        
        int parallelArgs[2];
        parallelArgs[0] = threadCount;
        parallelArgs[1] = size;

        for (int i = 0; i <= numRuns; i++){
            start = 0;
    
            parallelStart = rtclock();
            
            pthread_t *threads = (pthread_t*) malloc(sizeof(pthread_t) * threadCount);
            for (int j = 0; j < threadCount; j++){
                pthread_create(&threads[j], NULL, parallelMultiply, parallelArgs);
            }

            for (int j = 0; j < threadCount; j++){
                pthread_join(threads[j], NULL);
            }
            parallelEnd = rtclock();

            sequentialStart = rtclock();
            sequentialMultiply(size, multipliedMatrix2);
            sequentialEnd = rtclock();
            
            resetMatricies(size); 
            parallelTimeCumulative += (parallelEnd - parallelStart);
            sequentialTimeCumulative += (sequentialEnd - sequentialStart);
        }
        
        parallelTime = parallelTimeCumulative/numRuns;
        sequentialTime = sequentialTimeCumulative/numRuns;

        double error = compareMatricies(size);
        double speedup = sequentialTime/parallelTime;

        printf("=======\n");
        printf("mode: parallel\n");
        printf("thread count: %u\n", threadCount);
        printf("size: %u\n", size);
        printf("=======\n");
        printf("Sequential time: %f sec\n", sequentialTime);
        printf("Parallel time: %f sec\n", parallelTime);
        printf("Speedup: %f\n", speedup);
        printf("Verifying... largest error between parallel and sequential matrix: %f\n", error);
    }
    return 0;
}