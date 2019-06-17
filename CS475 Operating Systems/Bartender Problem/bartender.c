/*
 * bartender.c
 *
 *  Created on: Dec 26, 2015
 *      Author: dchiu, modified by Aaron Thompson
 */

#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <semaphore.h>
#include <fcntl.h>
#include "globals.h"
#include "bartender.h"

/**
 * Code for bartender thread.
 * Do not touch.
 */
void* bartender(void* args)
{
	int i;
	for (i = 0; i<num_threads; i++)
	{
		waitForCustomer();
		makeDrink();
		receivePayment();
	}
	return NULL;
}

/**
 * Waits in a closet until a customer enters.
 */
void waitForCustomer()
{
	printf("\t\t\t\t\t\t\t\t\t\t\t| Bartender\n");
	sem_wait(bartenderWait);
}


/**
 * When a customer places an order it takes the Bartender
 * a random amount of time between 5 ms and 1000 ms to make the drink.
 */
void makeDrink()
{
	printf("\t\t\t\t\t\t\t\t\t\t\t| \t\tBartender\n");
	srand(time(0));
	int randomMS = (rand() % (1000 - 5 + 1)) + 5000; 
	usleep(randomMS * 1000);
	sem_post(waitForDrink);
}


/**
 * Gets payment from the correct customer
 */
void receivePayment()
{
	//at the register waiting for customer to pay
	printf("\t\t\t\t\t\t\t\t\t\t\t| \t\t\t\tBartender\n");
	sem_wait(waitForPayment);

	//got the payment from the right customer!
	printf("\t\t\t\t\t\t\t\t\t\t\t| \t\t\t\t\t\tBartender\n");
	sem_post(confirmPayment);	
}
