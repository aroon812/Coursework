/**
 * Author: Aaron Thompson
 * Version: 1/29/2019
 * Connects everthing together, takes most of the user unput for the program
 */

#include <stdio.h>
#include <string.h>
#include "stats.h"
#include "menu.h"

int main(int argc, char *argv[]){
    char choice[MAX_INPUT_LEN];
    int looping = 1;

    inputStrings();

    while (looping == 1){
        printMenu();
        scanf("%s", &choice);

        //making sure no one tries to input any strings
        if(strlen(choice) > 1){
            printf("Error: unknown option \"%s\". Try again.\n", choice);
            printf("\n");
        }
        else{
            int intChoice = choice[0]-'0';

            if (intChoice == 1){
                printFrequencies();
            }
            else if (intChoice == 2){
                printWordCount();
            }
            else if (intChoice == 3){
                printHistogram();
            }
            else if (intChoice == 4){
                inputStrings();
            }
            else if (intChoice == 5){
                looping = 0;
                printf("Exiting...\n");
            }
            else{
                printf("Error: unknown option \"%u\". Try again.\n", (char) intChoice);
                printf("\n");
            }
        }  
    }
}