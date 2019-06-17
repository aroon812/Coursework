/**
 * Obtains all of the user input, runs the heapsort, and prints out all of the employees
 * @author Aaron Thompson
 */ 

#include <stdio.h>
#include "employee.h"
#include "heap.h"

#define MAX_EMPLOYEES 5
#define TRUE 1
#define FALSE 0

void assignName(char *A, char *name){
    for (int i = 0; i < MAX_NAME_LEN; i++){
        A[i] = name[i];
    }
}

int main()
{
    int numEmployees = 0;
    int looping = TRUE;
    char name[MAX_NAME_LEN];
    int salary;
    char choice[2];

    Employee newEmployee;
    Employee employees[MAX_EMPLOYEES];

    while (looping == TRUE && numEmployees < MAX_EMPLOYEES){
        printf("Name: ");
        scanf("%s", &name);
        printf("Salary: ");
        scanf("%u", &salary);
        assignName(newEmployee.name, name);
        newEmployee.salary = salary;
        employees[numEmployees] = newEmployee;

        numEmployees++;

        if(numEmployees != MAX_EMPLOYEES){
            printf("Enter another user (y/n)? ");
            getchar();
            scanf("%s", &choice);
            printf("\n");
        
            while (choice[0] != 'n' && choice[0] != 'y'){
                printf("Error: please enter y or n: ");
                scanf("%s", &choice);
                printf("\n");
            }

            if (choice[0] == 'n'){
                looping = FALSE;
            }   
        }
    }

    heapSort(employees, numEmployees);
    printList(employees, numEmployees);
	
	return 0;
}
