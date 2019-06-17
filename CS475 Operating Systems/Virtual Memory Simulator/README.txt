Aaron Thompson 
5/7/2019
Hwk7 - Virtual Memory Management Unit Simulator
This program takes in a file and various arguments pertaining to the block size (-b), the number of frames (-f), the number of pages (-p), and the page 
replacement policy (-r). The sum of log2(blockSize) and log2(numPages) must be below 32 becuase this program assumes that it's working with 32 bit 
instructions. The program takes in a list of instructions with corresponding operations to perform. The program will then use the specified page replacement policy
to translate the given virtual address to a physical address. At the end, the program will print out the total number of instructions, read percentage, write percentage
number of page faults and number of page replacements.