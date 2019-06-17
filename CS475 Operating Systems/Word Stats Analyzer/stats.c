/**
 * Author: Aaron Thompson
 * Version: 1/29/2019
 * Contains all of the methods which update all of the stats tracked by the program.
 */

#include <stdio.h>
#include <string.h>
#include "stats.h"

/**
 * Updates the frequency of each letter for the histogram
 */
void updateHisto(char string[]){
    
    int length = strlen(string);
    int i;
    for (i = 0; i < length; i++){
        if (string[i] >= 'a' && string[i] <= 'z'){
            stats.histo[string[i]-'a']++;
        }
        else if (string[i] >= 'A' && string[i] <= 'Z'){
            stats.histo[string[i]-'A']++;
        }
    }
}

/**
 * Helper function to check if a character is a letter.
 * returns: 1 if the char parameter is a letter, 0 if otherwise
 */
int isLetter(char letter){
    if ((letter >= 'a' && letter <= 'z') || (letter >= 'A' && letter <= 'Z')){
        return 1;
    }
    else{
        return 0;
    }
}

/**
 * Helper function to check if a character is a vowel.
 * returns: 1 if the char parameter is a vowel, 0 if otherwise.
 */
int isVowel(char c){
    char vowels[] = {'a','e','i','o','u','A','E','I','O','U'};
    int length = strlen(vowels);

    int i;
    for (i = 0; i < length; i++){
        if (c == vowels[i]){
            return 1;
        }
    }
    return 0;
}

/**
 * Updates the word count in the WordStats struct by tokenizing the entire string based on white space
 */
void updateWordCount(char string[]){
    char* word = strtok(string, " ");

    while (word != NULL){
        if (isLetter(word[0])){
            stats.wordCount++;
        }
        word = strtok(NULL, " "); //jump to the next "word"
    }
}

/**
 * Updates the vowel and consonant counts in the WordStats struct
 */ 
void countLetterTypes(char string[]){
    int length = strlen(string);
    
    int i;
    for (i = 0; i < length; i++){
        if (isLetter(string[i])){
            if (isVowel(string[i])){
                stats.vowelCount++;
            }
            else{
                stats.consonantCount++;
            }
        }   
    }
}


