SOURCES= main.c dsh.c builtIns.c
CFLAGS=
PROGRAM= dsh

OBJECTS= $(SOURCES:.c=.o)

debug := CFLAGS= -Wall -g

all debug: $(PROGRAM)

$(PROGRAM): $(OBJECTS) 
		$(LINK.c) -o $@ $(OBJECTS) 

clean: 
	rm -f $(PROGRAM) $(OBJECTS)