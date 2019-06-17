#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include "dsh.h"
#include "builtIns.h"

/**
 * Initialize the new list
 * @param l A pointer to the list
 */ 
void initializeList(List *l){
    l->length = 0;
    l->head = NULL;
    l->tail = NULL;
}

/**
 * Push a new string onto the list
 * @param inpit The string to be inserted into the list
 * @param l A pointer to the list
 */
void push(char* input, List *l){

    ListEntry *newentry = (ListEntry*) malloc(sizeof(ListEntry));
        newentry->value = input;
        newentry->prev = l->tail;
        newentry->next = NULL;
        
        if(l->tail == NULL && l->head == NULL){
            l->head = newentry;
        }
        else{
          l->tail->next = newentry;
        }

        if(l->length == MAX_INPUT_LEN){
            l->head = l->head->next;
            //free(l->head->prev);
            l->head->prev = NULL;
            l->length--;
        }
        
        l->tail = newentry;
        l->length++;
}

/**
 * exits dsh
 */ 
void dshExit(){
    exit(0);
}

/**
 * prints the present working directory
 */ 
void pwd(char* cwd){
    printf("%s\n", cwd);
}

void cd(char* destination, char* directory){
    if(destination == NULL){
        directory = copyStr(getenv("HOME"));
    }
    else if(checkPath(destination) == TRUE){
        directory = destination;
    }
    else{
        printf("ERROR: cd: %s: No such file or directory\n", destination);
    }  

    printf("directory: %s\n", directory);
    chdir(directory);
}

void history(List *l){
    for (ListEntry *entry = l->head; entry != NULL; entry = entry->next){
        printf("%s\n", entry->value);
    }
}

/**
 * Checks to see if input is a built in command. If so, the built in command is executed
 * @param commands The tokenized user input
 * @param cwd The user's current working directory
 * @param l a pointer to the list of previous commands
 * @return an int stating whether the user's command is a built in
 */
int isBuiltIn(char** commands, char* cwd, List *l){
    if (strcmp(commands[0], "exit") == 0){
        dshExit();
        return TRUE;
    }
    if (strcmp(commands[0], "pwd") == 0){
        pwd(cwd);
        return TRUE;
    }
    if (strcmp(commands[0], "cd") == 0){
        cd(commands[1], cwd);
        return TRUE;
    }
    if (strcmp(commands[0], "history") == 0){
        history(l);
        return TRUE;
    }
    return FALSE;
}