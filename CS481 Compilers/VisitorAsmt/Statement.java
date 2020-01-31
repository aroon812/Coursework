/**
 * The collection of classes defined here are virtually identical to
 * the sample code in Program 1.5 from the book.  There's an interface
 * for each of the three construct categories (Statement, Expression,
 * ExpressionList), and specific classes that implement each of these
 * interfaces.  (E.g. AssignmentStatement, PrintStatement, etc.)  
 *
 * Instances of these classes are used to build tree structures like the
 * one shown on the assignment page.  The resulting trees represent the
 * structure of a program, but don't implement any other functionality.
 * The functionality will come from a Visitor class that you'll write.
 * 
 * @author Brad Richards
 */

public interface Statement {
    public void apply(Visitor v);
}


class CompoundStatement implements Statement
{
    Statement statement1;
    Statement statement2;
    public CompoundStatement( Statement s1, Statement s2 )
    {
        statement1 = s1;
        statement2 = s2;
    }

    public void apply(Visitor v) {v.caseCompoundStatement(this);}
}

class AssignmentStatement implements Statement
{
    String id;
    Expression exp;
    public AssignmentStatement( String i, Expression e )
    {
        id = i;
        exp = e;
    }

    public void apply(Visitor v) {v.caseAssignmentStatement(this);}
}

class PrintStatement implements Statement
{
    ExpressionList exps;
    public PrintStatement( ExpressionList e )
    {
        exps = e;
    }

    public void apply(Visitor v) {v.casePrintStatement(this);}
}

/* Now the Expressions: */
interface Expression {
    public void apply(Visitor v);
}

class IdentifierExp implements Expression
{
    String id;
    public IdentifierExp( String i )
    {
        id = i;
    }

    public void apply(Visitor v) {v.caseIdentifierExp(this);}
}

class NumberExp implements Expression
{
    int num;
    public NumberExp( int n )
    {
        num = n;
    }

    public void apply(Visitor v) {v.caseNumberExp(this);}
    
}

class OperationExp implements Expression
{
    Expression left;
    Expression right;
    int operation;
    public final static int Plus=1,Minus=2,Times=3,Div=4;
    public OperationExp( Expression l, int o, Expression r )
    {
        left      = l;
        operation = o;
        right     = r;
    }

    public void apply(Visitor v) {v.caseOperationExp(this);}
}

class ExpressionSequence implements Expression
{
    Statement stm;
    Expression exp;
    public ExpressionSequence( Statement s, Expression e )
    {
        stm = s;
        exp = e;
    }

    public void apply(Visitor v) {v.caseExpressionSequence(this);}
}

/**
 * We can have lists of expressions as well.  They're stored in a
 * structure built out of the following node structures. 
 */

interface ExpressionList {
    public void apply(Visitor v);
}

class PairExpressionList implements ExpressionList
{
    Expression head;
    ExpressionList tail;
    public PairExpressionList( Expression h, ExpressionList t)
    {
        head = h;
        tail = t;
    }

    public void apply(Visitor v) {v.casePairExpressionList(this);}
}

class LastExpressionList implements ExpressionList
{
    Expression head; 
    public LastExpressionList( Expression h )
    {
        head = h;
    }

    public void apply(Visitor v) {v.caseLastExpressionList(this);}
}