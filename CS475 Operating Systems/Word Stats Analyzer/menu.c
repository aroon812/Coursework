/**
 * Author: Aaron Thompson
 * Version: 1/29/2019
 * Displays the menu and has functions for all of its options.
 */

#include <stdio.h>
#include <string.h>
#include "menu.h"
#include "stats.h"

/**
 * Prints the menu
 */ 
void printMenu(){
    printf("*** WORD STATS MENU ***\n");
    printf("Enter 1 to print vowel and consonant frequency.\n");
    printf("Enter 2 to print word count.\n");
    printf("Enter 3 to print histogram.\n");
    printf("Enter 4 to return to inputting more strings.\n");
    printf("Enter 5 to quit.\n");
}

/**
 * Calculates the total amount of letters in the WordStats struct so far,
 * also calculates the percentage of vowels and consonants in the entered strings,
 * and prints everthing out.
 */ 
void printFrequencies(){
    int total = stats.vowelCount + stats.consonantCount;
    float vowelPercentage = (float)stats.vowelCount/total*100;
    float consonantPercentage = (float)stats.consonantCount/total*100;
    printf("\n");
    printf("Vowels = %u (%0.2f%), Consonants = %u (%0.2f%), Total = %u\n", stats.vowelCount, vowelPercentage, stats.consonantCount, consonantPercentage, total);
    printf("\n");
}

/**
 * Prints the total amount of words from the strings that have been inputted by the user
 */ 
void printWordCount(){
    printf("\n");
    printf("Words: %u\n", stats.wordCount);
    printf("\n");
}

/**
 * Helper function for the printHistogram function which returns the maximum value in an array
 * returns: The maxumum value in an array
 */ 
int maxValue(int a[]){
    int value = 0;
    int i;
    for (i = 0; i < ALPHABET_SIZE; i++){
        if(a[i] > value){
            value = a[i];
        }
    }
    return value;
}

/**
 * Prints the histogram that displays the frequency off all of the inputted letters.
 */ 
void printHistogram(){
    int max = maxValue(stats.histo);

    int i;
    int j;
    for (i = max; 0 < i; i--){
        for (j = 0; j < ALPHABET_SIZE; j++){
            if (stats.histo[j] >= i){
                printf("* ");
            }
            else{
                printf("  ");
            }
        }
        printf("\n");
    }

    for (i = 0; i < ALPHABET_SIZE; i++){
        printf("%c ", i+97);
    }
    printf("\n");
    for (i = 0; i < ALPHABET_SIZE; i++){
        printf("%u ", stats.histo[i]);
    }
    printf("\n\n");
}

/**
 * Askes the user to input strings to be used by the other functions and then displayed.
 */ 
void inputStrings(){
    int inputLooping = 1;
    char input[MAX_INPUT_LEN];
    printf("Enter strings (# to stop): \n");
    
    while (inputLooping == 1){
        fgets(input, MAX_INPUT_LEN, stdin);

        if (input[0] == '#'){
            inputLooping = 0;
        }
        else{
            updateHisto(input);
            countLetterTypes(input);
            updateWordCount(input);
        }  
    }
}
