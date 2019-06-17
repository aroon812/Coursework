#define HISTORY_LEN 2

typedef struct List{
    int length;
    struct ListEntry* head;
    struct ListEntry* tail;
}List;

 typedef struct ListEntry{
    char* value;
    struct ListEntry* prev;
    struct ListEntry* next;
}ListEntry;


void exit();
void pwd();
void cd();
void history();
int isBuiltIn();
void push();
void initializeList();