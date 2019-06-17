/**
 * Author: Aaron Thompson
 * Version: 1/29/2019
 * Defines the an int for the size of the alphabet,as well as all of the prototype functions in stats.c that will be used in other files.
 */

#define ALPHABET_SIZE 26

typedef struct WordStats {
     int histo[ALPHABET_SIZE];
     int wordCount;
     int vowelCount;
     int consonantCount;
 } WordStats;

WordStats stats;

void updateHisto();
void countLetterTypes();
void updateWordCount();


