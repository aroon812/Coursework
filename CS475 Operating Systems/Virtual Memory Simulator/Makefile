SOURCES= vmm.c queue.c -lm
CFLAGS=
PROGRAM= vmmu

OBJECTS= $(SOURCES:.c=.o)

debug := CFLAGS= -Wall -g 

all debug: $(PROGRAM)

$(PROGRAM): $(OBJECTS)
		$(LINK.c) -o $@ $(OBJECTS)

clean:
	rm -f $(PROGRAM) $(OBJECTS)