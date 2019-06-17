#include <stdio.h>
#include <string.h>
#include <math.h>
#include "stack.h"
#include "rpn.h"

#define TRUE 1
#define FALSE 0
#define MAX_CHARS 100

/**
 * Asks the user to input equations in RPN notation
 */ 
void getInput(){
    char equation[MAX_CHARS];
    char choice[100];
    int looping = TRUE;

    while (looping){
        printf("> Enter an equation (in RPN): \n> ");
        fgets(equation, MAX_CHARS, stdin);
        equation[MAX_CHARS-2] = '\0';
        double result = rpn(equation);

        if (!isnan(result)){
            printf("%.3f\n", result);
        }
        printf("\n");

        printf("> Evaluate another? (y/n): ");
        fgets(choice, MAX_CHARS, stdin);

        while (choice[0] != 'y' && choice[0] != 'n'){

            for(int i = 0; i < strlen(choice); i++){
                choice[i] = '\0';
            }
            
            printf("> Please enter y or n: ");
            fflush(stdout);
            fgets(choice, 2, stdin);
        }

        if (choice[0] == 'n'){
            looping = FALSE;
            printf("Exiting...\n");
        } 
    }
}

int main(int argc, char *argv[]){
    getInput();
}