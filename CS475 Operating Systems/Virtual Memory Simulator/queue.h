struct queue
{
	struct qentry *head; 
	struct qentry *tail; 
	int size;
};

struct qentry
{
	int index;
	int priority;	
	struct qentry *next;	
	struct qentry *prev;
};

void enqueue();
int dequeue();
void printqueue();
void elapseTime();
int removeFrame();



