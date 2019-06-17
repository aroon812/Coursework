#include <stdio.h> 
#include <stdlib.h> 
#include <unistd.h>
#include <string.h>
#include <sys/wait.h>
#include "dsh.h"

/**
 * Prints the message of the day when dsh opens
 * @param the users home path
 */ 
void philosophicalMessage(char *path){
    
    char *program = concatPath(path, ".dsh_motd");

    if(!checkPath(program)){
        return;
    }

    int pid = fork();
    char* args[3];
    char *catPath = "/bin/cat";
    args[0] = "cat";
    args[1] = program;
    args[2] = NULL;

    if (pid == 0){
        execv(catPath, args);
        printf("\n");
    }
    else{
        wait(NULL);
    }
}

/**
 * Causes dsh to run an executable
 * @param path The path to the executable
 * @param tokens An array of strings as arguments to the executable
 */ 
void runProgram(char* path, char** tokens){
    int concurrent;
    int pid;

    if (tokens[tokensLength(tokens)-1][0] == '&'){
        concurrent = TRUE;
        tokens[tokensLength(tokens)-1] = NULL;
    }
    else{
        concurrent = FALSE;
    }

    pid = fork();
    
    if (pid == 0){
        execv(path, tokens);
    }
    else{
        if (concurrent == FALSE){
            waitpid(pid, NULL, WNOHANG);
        }
    }
}

/**
 * Places all words in the input into an array of strings
 * @param input The input that is entered by the user
 * @param tokens The array of strings that will be populated
 */ 
void tokenizeInput(char* input, char** tokens){
    char* token = strtok(input, " ");
    int i = 0;
    while (token != NULL){
        tokens[i] = token;
        i++;
        token = strtok(NULL, " ");
    }
}

/**
 * Return the number of tokens in the input
 * @param tokens The array of strings
 * @return the number of tokens in the string
 */ 
int tokensLength(char** tokens){
    int i = 0; 
    char* token;
    for(token = tokens[0]; token != NULL; i++){
        token = tokens[i];
    }
    return i-1;
}

/**
 * Checks to see if a path is valid
 * @param path The path in question
 * @return an int stating whether the file is found and can be accessed
 */ 
int checkPath(char *path){
    int status;
    status = access(path, F_OK);

    if(status == 0){
        return TRUE;
    }
    else{
        return FALSE;
    }
}

/**
 * Check if a program is located in any paths on the PATH variable
 * @param input The program entered by the user
 * @return the path to the executable, or a null character if the executable isn't found
 */ 
char* checkPaths(char* input){
    char* path = getenv("PATH");
    char* actualPath = copyStr(path);
    char* returnPath = (char*) malloc(2);
    char* token;

    token = strtok(actualPath, ":");

    while (token != NULL){ 
        char* newPath = concatPath(token, input);
        
        if(checkPath(newPath) == TRUE){
            returnPath = (char*) realloc(returnPath, strlen(returnPath));
            returnPath = newPath;
            return returnPath;
        }
        token = strtok(NULL, ":");
    }

    returnPath = realloc(returnPath, 2);
    returnPath[0] = '\0';

    return returnPath;
}

/**
 * Creates a brand new copy of a path
 * @param path The path to be copied
 * @return a pointer to the new copy of the path
 */ 
char* copyStr(char *path){
    int length = strlen(path);
    char* pathCopy = (char*) malloc(length);
    strcpy(pathCopy, path);
    return pathCopy;
}

/**
 * Sets the current path back to the home path
 * @param path The user's current path
 */ 
void resetPath(char *path){
    free(path);
    char* home = getenv("HOME");
    path = copyStr(home);
}

/**
 * Concatonates a path with another path or program
 * @param path The existing path
 * @param add What is to be added to the path
 * @return a pointer to the new path
 */ 
char* concatPath(char *path, char *add){
    int length = strlen(path);
    int addlength = strlen(add);

    char* pathCopy = (char*) malloc(length+addlength+1);
    strcpy(pathCopy, path);
    strcat(pathCopy, "/");
    strcat(pathCopy, add);

    return pathCopy;
}


