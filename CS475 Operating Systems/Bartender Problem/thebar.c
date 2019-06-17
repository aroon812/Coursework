/*
 * TheBar.c
 *
 *  Created on: Dec 26, 2015
 *      Author: dchiu, modified by Aaron Thompson
 */


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>
#include <fcntl.h>
#include <time.h>
#include "globals.h"
#include "customer.h"
#include "bartender.h"

void printBanner();
void init();
void cleanup();

/**
 * Main function
 */
int main(int argc, char** argv){

	int numCustomers = strtol(argv[1], NULL, 10);
	num_threads = numCustomers;
	printBanner();
	init();		//initialize semaphores

	pthread_t *customerThreads = (pthread_t*) malloc(sizeof(pthread_t) * numCustomers);
	pthread_t bartenderThread = (pthread_t) malloc(sizeof(pthread_t));
	int **arg = malloc(sizeof(**arg));
	
	//fire off customer thread
	int i;
	for (i = 0; i < numCustomers; i++){
		int *arg = malloc(sizeof(int*));
		*arg = i;
		pthread_create(&customerThreads[i], NULL, customer, arg);
	}
	
	//fire off bartender thread
	int bartenderNum = numCustomers;
	pthread_create(&bartenderThread, NULL, bartender, &bartenderNum);

	//wait for all threads to finish
	for (i = 0; i < numCustomers; i++){
		pthread_join(customerThreads[i], NULL);
	}
	pthread_join(bartenderThread, NULL);

	cleanup();	//cleanup and destroy semaphores
	return 0;
	
}


/**
 * Prints the activity banner.
 * Do not touch.
 */
void printBanner() {
	printf("Customer:\t\t\t\t\t\t\t\t\t\t| Employee:\n");
	printf("Traveling\tArrived\t\tOrdering\tBrowsing\tAt Register\tLeaving");
	printf("\t| Waiting\tMixing Drinks\tAt Register\tPayment Recv\n");
	printf("----------------------------------------------------------------------------------------+");
	printf("------------------------------------------------------------\n");
}


/**
 * Create and initialize semaphores
 */
void init()
{
	//unlink semaphores
	sem_unlink("/waitForPayment");
	sem_unlink("/bartenderWait");  //remove the semaphore if it exists
	sem_unlink("/waitOutsideBar");
	sem_unlink("/waitForDrink");
	sem_unlink("/confirmPayment");


	//create semaphores
	bartenderWait = sem_open("/bartenderWait", O_CREAT, 0600, 0);
	waitForPayment = sem_open("/waitForPayment", O_CREAT, 0600, 0);
	waitOutsideBar = sem_open("/waitOutsideBar", O_CREAT, 0600, 1);
	waitForDrink = sem_open("/waitForDrink", O_CREAT, 0600, 0);
	confirmPayment = sem_open("/confirmPayment", O_CREAT, 0600, 0);
}


/**
 * Cleanup and destroy semaphores
 */
void cleanup()
{
	//close semaphores
	sem_close(waitForPayment);
	sem_close(bartenderWait);
	sem_close(waitOutsideBar);
	sem_close(waitForDrink);
	sem_close(confirmPayment);
	sem_unlink("/waitForPayment");
	sem_unlink("/bartenderWait");
	sem_unlink("/waitOutsideBar");
	sem_unlink("/waitForDrink");
	sem_unlink("/confirmPayment");
}
