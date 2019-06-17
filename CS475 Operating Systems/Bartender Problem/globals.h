/*
 * globals.h
 *
 *  Created on: Dec 26, 2015
 *      Author: dchiu
 */

#ifndef GLOBALS_H_
#define GLOBALS_H_

int num_threads;	// number of customer threads
int now_serving;	// customer's ID who is being served

sem_t* bartenderWait;
sem_t* waitForPayment;
sem_t* waitOutsideBar;
sem_t* waitForDrink;
sem_t* confirmPayment;

#endif /* GLOBALS_H_ */
