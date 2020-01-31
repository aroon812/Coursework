/**
 * Interprets an execution tree of a program
 * 
 * @author Aaron Thompson
 * @version 1/27/2020
 */
public class Interpreter
{
    /**
     * Print the code for a statement
     * @param s The statement to print code for
     */
    public void prettyPrint( Statement s )
    {  
        PrintVisitor v = new PrintVisitor();
        s.apply(v);
    }

    /**
     * Execute a statement
     * @param s The statement to be executed
     */
    public void interpret( Statement s )
    {
        InterpVisitor v = new InterpVisitor();
        s.apply(v);
    }

    /**
     * Get the maximum number of arguments passed to a print statement
     * @param s The statement to be evaluated
     * @return The maximum number of arguments passed to a print statement
     */
    public int maxArgs( Statement s )
    {
        ArgCountVisitor v = new ArgCountVisitor();
        s.apply(v);
        return v.getMaxArgs();
    }
}
