SOURCES= main.c matrixOperations.c rtclock.c -lpthread
CFLAGS=
PROGRAM= mmm

OBJECTS= $(SOURCES:.c=.o)

debug := CFLAGS= -Wall -g 

all debug: $(PROGRAM)

$(PROGRAM): $(OBJECTS) 
		$(LINK.c) -o $@ $(OBJECTS) 

clean: 
	rm -f $(PROGRAM) $(OBJECTS)