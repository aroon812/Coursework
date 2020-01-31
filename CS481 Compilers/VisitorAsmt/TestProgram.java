/**
 * This is the top-level test harness for your interpreter.  It runs
 * your Interpreter methods on a sample program tree corresponding to
 * this program:
 * 
 *   a := 5 + 3 ;
 *   b := ( print(a, a-1), 10*a ) ;
 *   print(b)
 *   
 * @author Brad Richards
 */

public class TestProgram
{
    /** This var holds a sample program to use as test input */
    private static Statement program = 
        new CompoundStatement(
            new AssignmentStatement(
                "a",
                new OperationExp( new NumberExp(5),
                    OperationExp.Plus, 
                    new NumberExp(3))),
            new CompoundStatement(
                new AssignmentStatement(
                    "b",
                    new ExpressionSequence(
                        new PrintStatement(
                            new PairExpressionList(
                                new IdentifierExp("a"),
                                new LastExpressionList(
                                    new OperationExp(
                                        new IdentifierExp("a"),
                                        OperationExp.Minus,
                                        new NumberExp(1))))),
                        new OperationExp(
                            new NumberExp(10),
                            OperationExp.Times,
                            new IdentifierExp("a")))),
                new PrintStatement(
                    new LastExpressionList(
                        new IdentifierExp("b")))));

    /** 
     * Creates a new interpreter and invokes its prettyPrint, maxArgs,
     * and interpret methods on our sample program.
     */
    public static void main( String[] args ) throws java.io.IOException
    {
        Interpreter i = new Interpreter();
        System.out.println("The following program:\n");
        i.prettyPrint(program);
        
        System.out.println("\nhas max args of "+ i.maxArgs(program) +
            " and produces the output shown below when run:");
        i.interpret( program );
        
    }
}
