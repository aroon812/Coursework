SOURCES= main.c heap.c
CFLAGS=
PROGRAM= main

OBJECTS= $(SOURCES:.c=.o)

debug := CFLAGS= -Wall -g

all debug: $(PROGRAM)

$(PROGRAM): $(OBJECTS)
		$(LINK.c) -o $@ $(OBJECTS)

clean:
	rm -f $(PROGRAM) $(OBJECTS)