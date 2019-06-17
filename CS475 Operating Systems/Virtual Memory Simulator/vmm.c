#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <math.h>
#include <time.h>
#include "queue.h"

int num_pages;
int num_frames;
int block_size;
int rep_policy;
double instCount;
double numReads;
double numWrites;
double numPageFaults;
double numReplacements;
unsigned int *pageTable;
unsigned int *frameTable;
struct queue *q;
int numFrameBits;
int numPageBits;
int clockHand;

/**
 * Returns a frame to evict using clock
 * @return the frame to be evicted 
 */ 
int scanClock(){
    unsigned int mask1;
    unsigned int mask2;
    unsigned int result;
    int foundCold = 0;
    while (foundCold != 1){
        if (clockHand >= num_frames){
            clockHand = 0;
        }
        int pageNumber = (frameTable[clockHand] << (32-numPageBits)) >> (32-numPageBits);
        int reference = (pageTable[pageNumber] << 2) >> 31;

        if (reference == 1){
            mask1 = (pageTable[pageNumber] >> 30) << 30;
            mask2 = (pageTable[pageNumber] << 3) >> 3;
            result = mask1 | mask2;
            pageTable[pageNumber] = result;
        }
        else{
            return clockHand;
        }
        clockHand++;
    }
    return -1;
}

/**
 * when a frame is referenced, move it to the back of the queue
 * @frameNumber the frame to be moved to the back of the queue.
 */ 
void lruUtility(int frameNumber){
    removeFrame(frameNumber, q);
    enqueue(frameNumber, q, 1);
}

/**
 * Selects an index of a frame where it's page will be replaced.
 * @return The index of the frame that had it's page evicted.
 */ 
int pageReplacement(){
    int written;
    int frameIndex = -1;
    if (rep_policy == 0){
        frameIndex = (rand() % ((num_frames-1) + 1)); 
    }
    else if (rep_policy == 1 || rep_policy == 2){
        frameIndex = dequeue(q);
        enqueue(frameIndex, q, 1);
    }
    else if (rep_policy == 3){
        frameIndex = scanClock();
        clockHand++;
       
    }

    unsigned int fte = frameTable[frameIndex];
    frameTable[frameIndex] =  0x80000000;
    unsigned int pageNum = (fte << (32-numPageBits)) >> (32-numPageBits);
    unsigned int pte = pageTable[pageNum];
    pageTable[pageNum] = 0;
    
    if (pte >> 30 == 3){
        written = 1;
    }
    else{
        written = 0;
    }

    printf("Page replacement: evicted_page=%u (frame=%u), ", pageNum, frameIndex);
    if (written == 1){
        printf("writeout=true\n");
    }
    else{
        printf("writeout=false\n");
    }

    numReplacements++;
    return frameIndex;
}

/**
 * A linear search through the frame table for a free frame.
 * @return The next free frame, or -1 if no frames are free.
 */ 
unsigned int findFreeFrame(){
    int i;
    for (i = 0; i < num_frames; i++){
        if (frameTable[i] >> 31 == 1){
            return i;
        }
    }
    return -1;
}

/**
 * Prints the frame table
 */  
void printFrameTable(){
    unsigned int i;
    unsigned int f;
    unsigned int page;

    printf("\n");
    printf("Frame\t| F, Page\n");
    for (int i = 0; i < num_frames; i++){
        if (frameTable[i] != 0x80000000){
            f = frameTable[i] >> 31;
            page = frameTable[i] << (32 - numPageBits);
            page >>= (32 - numPageBits);
            printf("%d\t| %d, %d", i, f, page);
        }
        else{
            f = 1;
            printf("%d\t| %d, -", i, f);
        }
        printf("\n");
    }
}

/**
 * Prints the page table.
 */ 
void printPageTable(){
    unsigned int i;
    unsigned int v;
    unsigned int m;
    unsigned int r;
    unsigned int frame;

    printf("\n");
    printf("Page\t| V, M, R, Frame\n");
    for (int i = 0; i < num_pages; i++){
        if (pageTable[i] != 0){
            v = pageTable[i] >> 31;
            m = (pageTable[i] << 1) >> 31;
            r = (pageTable[i] << 2) >> 31;
            frame = pageTable[i] << (32-numFrameBits);
            frame >>= (32-numFrameBits);
            printf("%d\t| %d, %d, %d, %d", i, v, m, r, frame);
        }
        else{
            v = 0;
            printf("%d\t| %d, -, -, -", i, v);
        }
        printf("\n");
    }
}

/**
 * Updates the frame table and the page table and returns the resulting physical address.
 * @param pageNumber The index of the page table where the new pte will be placed.
 * @param frameNumber Index of the free frame that will be filled.
 * @param numOffsetBits the number of offset bits for the physical address.
 * @param numOffsetBits The offset for the virtual and physical address'
 * @param instruction The type of the current instruction.
 */ 
unsigned int updateTables(unsigned int pageNumber, int frameNumber, int numOffsetBits, unsigned int offset, char instruction){
    unsigned int newPte;
    unsigned int newFte = 0x00000000;
    if (instruction == 'W'){
        newPte = 0xE0000000;
    }
    else{
        newPte = 0xA0000000;
    }
    newPte |= frameNumber;
    newFte |= pageNumber;

    frameTable[frameNumber] = newFte;
    pageTable[pageNumber] = newPte;  

    return (frameNumber << numOffsetBits) | offset;
}

/**
 * Translates a virtual address for a physical address given an instruction.
 * @param address The virtual address.
 * @param instruction The instruction to be executed.
 */ 
unsigned int readOrWrite(unsigned int address, char instruction){
    int pageValid;

    unsigned int offset = address & (block_size-1);
    int numOffsetBits = log(block_size) / log(2);

    unsigned int pageNumber = address >> numOffsetBits;
    unsigned int frameNumber = (pageTable[pageNumber] << (32-numFrameBits));
    unsigned int physAddress;
    frameNumber >>= (32-numFrameBits);
    
    printf("\n");
    if (instruction == 'R'){
        printf("START_READ\n");
    }
    else if (instruction == 'W'){
        printf("START_WRITE\n");
    }
    printf("virt_addr=0x%08X\n", address);

    if (!(pageNumber >= 0 && pageNumber < num_pages)){
        printf("Segmentation fault: illegal_page=%u\n", pageNumber);
        return -1;
    }
    else{
        pageValid = pageTable[pageNumber] >> 31;
        if (pageValid){
            physAddress = (frameNumber << numOffsetBits) | offset;

            if(rep_policy == 2){
                lruUtility(frameNumber);
            }   
        }
        else{
            printf("Page fault: page=%u\n", pageNumber);
            numPageFaults++;
            frameNumber = findFreeFrame();

            if (frameNumber != -1){
                frameTable[frameNumber] <<= 1;
                frameTable[frameNumber] >>= 1;
                physAddress = updateTables(pageNumber, frameNumber, numOffsetBits, offset, instruction);
            }
            else{
                frameNumber = pageReplacement();
                physAddress = updateTables(pageNumber, frameNumber, numOffsetBits, offset, instruction);
            }
            if(rep_policy == 2){
                lruUtility(frameNumber);
            }   
        }
        if (rep_policy == 3){
                pageTable[pageNumber] |= (0x20000000);
        }
        
        printf("phys_addr=0x%08X\n", physAddress);
        if (instruction == 'R'){
            numReads++;
        }
        else if (instruction == 'W'){
            numWrites++;
        }
    }

    if (instruction == 'R'){
        printf("END_READ\n");
    }
    else if (instruction == 'W'){
        printf("END_WRITE\n");
    }
    instCount++;

    return physAddress;
}

/**
 * Prints some statistics about the instructions executed so far.
 */ 
void printStats(){
    double writePercentage = (numWrites / instCount) * 100;
    double readPercentage = (numReads / instCount) * 100;
    double pageFaultPercentage = (numPageFaults / instCount) * 100;
    double replacementPercentage = (numReplacements / instCount) * 100;

    printf("inst_count = %.0f (reads=%.2f%%, writes=%.2f%%), page_faults=%.0f (%.2f%%), replacements=%.0f (%.2f%%)\n", instCount, readPercentage, writePercentage, numPageFaults, pageFaultPercentage, numReplacements, replacementPercentage);
}

/**
 * Initializes the frame table.
 */ 
void intializeFrameTable(){
    int i;
    for (i = 0; i < num_frames; i++){
        frameTable[i] = 0x80000000;
    }
}

/**
 * Initializes the page table.
 */ 
void initializePageTable(){
    int i;
    for (i = 0; i < num_pages; i++){
        pageTable[i] = 0;
    }
}

/**
 * Initially adds N frames to the queue for the fifo policy.
 */ 
void intializeQueue(){
    int i;
    for (i = 0; i < num_frames; i++){
        enqueue(i, q, 1);
    }
}

int main(int argc, char* argv[]){
    int hasArgs;
    char *fileName;
    if (argc == 2){
        hasArgs = 1;
        num_pages = 8;
        num_frames = 4;
        block_size = 1024;
        rep_policy = 0;
        fileName = argv[1];
    }
    else{
        int hasNumPages = 0;
        int hasNumFrames = 0;
        int hasBlockSize = 0;
        int hasRepPolicy = 0;
        int i;
        for (i = 1; i < argc-1; i++){
            if (strcmp(argv[i], "-p") == 0){
                i++;
                num_pages = atoi(argv[i]);
                hasNumPages = 1;
            }
            else if (strcmp(argv[i], "-f") == 0){
                i++;
                num_frames = atoi(argv[i]);
                hasNumFrames = 1;
            }
            else if (strcmp(argv[i], "-b") == 0){
                i++;
                block_size = atoi(argv[i]);
                hasBlockSize = 1;
            }
            else if (strcmp(argv[i], "-r") == 0){
                i++;
                if (strcmp(argv[i], "random") == 0){
                    rep_policy = 0;
                }
                else if (strcmp(argv[i], "fifo") == 0){
                    rep_policy = 1;
                }
                else if (strcmp(argv[i], "lru") == 0){
                    rep_policy = 2;
                }
                else if (strcmp(argv[i], "clock") == 0){
                    rep_policy = 3;
                }    
                hasRepPolicy = 1;
            }
        }
        fileName = argv[i];

        if (hasNumPages == 0){
            num_pages = 8;
        }
        if (hasNumFrames == 0){
            num_frames = 4;
        }
        if (hasBlockSize == 0){
            block_size = 1024;
        }
        if (hasRepPolicy == 0){
            rep_policy = 0;
        }
    }

    instCount = 0;
    numReads = 0;
    numWrites = 0;
    numPageFaults = 0;
    numReplacements = 0;
    clockHand = 0;

    if (num_frames % 2 == 0){
        numFrameBits = log(num_frames) / log(2);
    }
    else{
        numFrameBits = (log(num_frames) / log(2)) + 1;
    }

    if (num_pages % 2 == 0){
        numPageBits = log(num_pages) / log(2);
    }
    else{
        numPageBits = (log(num_pages) / log(2)) + 1;
    }

    if (numPageBits + log2(block_size) >= 32){
        printf("Error: number of pages and block size are too large for 32 bits. Exiting program...\n");
        return -1;
    }
    
    if (rep_policy == 0){
        srand(time(0)); 
    }
    else if (rep_policy == 1 || rep_policy == 2){
        q = malloc(sizeof(struct queue));
        intializeQueue();
    }

   pageTable = malloc(sizeof(unsigned int) * num_pages);
   frameTable = malloc(sizeof(unsigned int) * num_frames);
   initializePageTable();
   intializeFrameTable();

    char *instruction;
    unsigned int address;
    FILE *file = fopen (fileName, "r");
    if (file != NULL)
   {
        
      char line[255]; 
      char* args[2];

      while (fgets(line ,255,file) != NULL) 
      {    
        char* argument = strtok(line, " ");
        int i = 0;
        
        while(argument != NULL) {
            args[i] = argument;
            argument = strtok(NULL, " ");
            i++;
        }

        instruction = args[0];
        address = (int)strtol(args[1], NULL, 16);

        if (instruction[0] == 'R' || instruction[0] == 'W'){
            readOrWrite(address, instruction[0]);
        }
        else if (instruction[1] == 'F'){
            printFrameTable();
        }
        else if (instruction[1] == 'P'){
            printPageTable();
        }
      }
      printStats();  
      fclose ( file );
   }
   else{
       printf("File not found!\n");
   }
}