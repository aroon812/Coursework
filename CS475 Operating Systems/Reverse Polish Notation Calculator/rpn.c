/**
 * Provides functionality for the rpn calculator
 * @author Aaron Thompson
 * @version 2/13/2019
 */
#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include <math.h>
#include "stack.h"
#include "stdlib.h"

/**
 * Implements the rpn algorithm
 */ 
double rpn(char input[]){
    char* token = strtok(input, " ");
    double num1;
    double num2;
    double result;

    while (token != NULL){

        if (isdigit(token[0])){
            float num = atof(token);
            push(num);
        }
        else{
            if (size() < 2){
                printf("Error: insufficient operands\n");
                clearStack();
                return NAN;
            }

            if (token[0] == '+'){
                num2 = pop();
                num1 = pop();

                result = num1 + num2;
                push(result);
            }
            else if (token[0] == '-'){
                num2 = pop();
                num1 = pop();

                result = num1 - num2;
                push(result);
            }
            else if (token[0] == '/'){
                num2 = pop();
                num1 = pop();

                if (num2 == 0){
                    printf("Error: divide-by-zero\n");
                    clearStack();
                    return NAN;
                }
                else{
                    result = num1 / num2;
                    push(result);
                }
            }
            else if (token[0] == '*'){
                num2 = pop();
                num1 = pop();

                result = num1 * num2;
                push(result);
            }

            else{
                printf("Error: operator %c unrecognized\n", token[0]);
                return NAN;
            }
        }

        token = strtok(NULL, " "); 
    }

    if (size() > 1){
        printf("Error: too many operands entered\n");
        clearStack();
        return NAN;
    }
    else{
        return pop();
    }
}

