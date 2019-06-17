/*
 * customer.c
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
#include <time.h>
#include "globals.h"
#include "customer.h"

/**
 * This is what the thread will call.
 * Do not touch.
 */
void* customer(void* args)
{
	unsigned int *custID = (unsigned int*) args;
	custTravelToBar(*custID);
	custArriveAtBar(*custID);
	custPlaceOrder();
	custBrowseArt();
	custAtRegister();
	custLeaveBar();
	return NULL;
}


/**
 * Each customer takes a random amount of time between 20 ms and 5000 ms to travel to the bar.
 */
void custTravelToBar(unsigned int custID)
{
	srand(time(0));
	int randomMS = (rand() % (5000 - 20 + 1)) + 5000; 
	usleep(randomMS * 1000);
	printf("Cust %u\t\t\t\t\t\t\t\t\t\t\t|\n", custID);
}


/**
 * If there is already another customer in the bar the current customer has
 * to wait until bar is empty before entering.
 */
void custArriveAtBar(unsigned int custID)
{
	printf("\t\tCust %u\t\t\t\t\t\t\t\t\t|\n", custID);
	sem_wait(waitOutsideBar);
	now_serving = custID;
}


/**
 * The customer in the bar places an order
 */
void custPlaceOrder()
{
	printf("\t\t\t\tCust %u\t\t\t\t\t\t\t|\n", now_serving);
	sem_post(bartenderWait);
}


/**
 * The customer in the bar can browse the wall art for a random amount of time between 3ms and 4000ms.
 */
void custBrowseArt()
{
	printf("\t\t\t\t\t\tCust %u\t\t\t\t\t|\n", now_serving);
	srand(time(0));
	int randomMS = (rand() % (4000 - 3 + 1)) + 4000; 
	usleep(randomMS * 1000);
}


/**
 * If their drink is not ready by the time they are done admiring the art they must wait
 * until the bartender has finished. When the bartender is finished, the customer pays.
 *
 */
void custAtRegister()
{
	printf("\t\t\t\t\t\t\t\tCust %u\t\t\t|\n", now_serving);
	sem_wait(waitForDrink);
	sem_post(waitForPayment);
	sem_wait(confirmPayment);
}


/**
 * The customer in the bar leaves the bar.
 */
void custLeaveBar()
{
	printf("\t\t\t\t\t\t\t\t\t\tCust %u\t|\n", now_serving);
	sem_post(waitOutsideBar);
}
