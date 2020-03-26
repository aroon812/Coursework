straightline.sable  A sample input file containing a grammar
ParserTest.java     A driver routine that exercises the parser
ParserDot.java      Traverses a parse tree, generating output for dot
program.txt         A sample straightline program
program_tree.txt    The parse tree generated from the sample program

The input file describes a slightly simplified version of the
straightline language from the first chapter.  (It only supports +,
and the sequenced expressions have been removed.)  

The steps for running SableCC and then compiling the resulting code
from the second assignment will work here again. (Run Sable on 
straightline.sable, then compile ParserTest.java.) ParserTest will 
take a string as a command-line arg when you run it, or will use
"a := 1+2" as a sample program if no string is given. 

    java ParserTest

Produces the following output:

    AAssignStm
       a
       :=
       ANumExp
          1
          APlusExps
             +
             ANumExp
                2
                ANoExps
       ANoStms
