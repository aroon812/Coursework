#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include <string.h>
#include "dsh.h"
#include "builtIns.h"

/**
 * Removes the newline character at the end of an fgets input
 * @param str The input
 */ 
void removeNewline(char *str){
    str[strlen(str)-1] = '\0';
}

/**
 * Runs the David Shell
 */ 
int main(){
    char* path = copyStr(getenv("HOME"));
    char cwd[MAX_INPUT_LEN];
    getcwd(cwd, MAX_INPUT_LEN);
    char* newCwd;
    char* newPath;
    philosophicalMessage(path);
    resetPath(path);

    List* list = (List*) malloc(sizeof(List));
    initializeList(list);

    char input[MAX_INPUT_LEN+1];
    char* inputTokens[MAX_INPUT_LEN];
    int inputLooping;
    
    while(1){
        inputLooping = 1;
        while(inputLooping){
            printf("dsh>");
            fgets(input, MAX_INPUT_LEN, stdin);
            removeNewline(input);

            if(strlen(input) != 0){
                inputLooping = 0;
            }
        }

        char* storedInput = copyStr(input);
        push(storedInput, list);

        tokenizeInput(input, inputTokens);
        
        if (inputTokens[0][0] == '/'){
            if (checkPath(input) == TRUE){
                printf("%s: is a directory\n", inputTokens[0]);
            }
            else{
                printf("Error: %s: No such file or directory\n", inputTokens[0]);
            }
        }
        else{
            if (!isBuiltIn(inputTokens, cwd, list)){
                newCwd = copyStr(cwd);
                newPath = concatPath(newCwd, input);

                if (checkPath(newPath) == TRUE){
                    runProgram(newPath, inputTokens);
                }
                else{ 
                    char* checkedPath = checkPaths(inputTokens[0]);

                    if(checkedPath[0] == '\0'){
                        printf("ERROR: %s not found!\n", input);
                    }
                    else{
                        runProgram(checkedPath, inputTokens);
                    }
                }
            }
            //free(newCwd);
            //free(newPath);
            
        }
        printf("\n");
    }    
}