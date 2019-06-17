Aaron Thompson
Hwk4
A simple shell implementation called the David Shell. A couple of wierd things that I could not figure out. 
The first is that the dsh> prompt kept getting pushed up to a place it's not supposed to be by the output of the other programs. 
When this happens, the shell can still run further inputs from the user.
The second is that the command "gcc -Wall feelGood.c -o feelGood" produces a segmentation fault, even though it didn't do so on
every other input I tried. 