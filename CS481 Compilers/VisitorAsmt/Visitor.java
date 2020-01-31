import java.util.HashMap;
import java.util.Stack;

/**
 * Runs through the execution tree of a program. Doesn't do anything special.
 * 
 * @author Aaron Thompson
 * @version 1/25/2020
 */
class Visitor{
    /**
     * Visits a compound statement
     * @param stmt The CompoundStatement to be visited
     */
    public void caseCompoundStatement(CompoundStatement stmt){
        stmt.statement1.apply(this);
        stmt.statement2.apply(this);
    }

    /**
     * Visits an assignment statement
     * @param stmt The AssignmentStatement to be visited
     */
    public void caseAssignmentStatement(AssignmentStatement stmt){
        stmt.exp.apply(this);
    }

    /**
     * Visits a print statement
     * @param stmt The PrintStatement to be visited
     */
    public void casePrintStatement(PrintStatement stmt){
        stmt.exps.apply(this);
    }

    /**
     * Visits an identifier expression
     * @param exp The IdentifierExp to be visited
     */
    public void caseIdentifierExp(IdentifierExp exp){}

    /**
     * Visits a number expression
     * @param exp The NumberExp to be visited
     */
    public void caseNumberExp(NumberExp exp) {}

    /**
     * Visits a binary operation expression
     * @param exp The OperationExp to be visited
     */
    public void caseOperationExp(OperationExp exp){
        exp.left.apply(this);
        exp.right.apply(this);
    }

    /**
     * Visits an expression sequence
     * @param exp The ExpressionSequence to be visited
     */
    public void caseExpressionSequence(ExpressionSequence exp){
        exp.stm.apply(this);
        exp.exp.apply(this);
    }

    /**
     * Visits a pair in a list
     * @param expList The PairExpressionList to be visited
     */
    public void casePairExpressionList(PairExpressionList expList){
        expList.head.apply(this);
        expList.tail.apply(this);
    }

    /**
     * Visits the last element in a list
     * @param exp The LastExpressionList to be visited
     */
    public void caseLastExpressionList(LastExpressionList exp){
        exp.head.apply(this);
    }
}

/**
 * Runs through the expression tree of a program, printing out the code that was probably written to make the program function.
 * 
 * @author Aaron Thompson
 * @version 1/25/2020
 */
class PrintVisitor extends Visitor{
    /**
     * Prints the code for a compound statement
     * @param stmt The CompoundStatement to be printed
     */
    @Override
    public void caseCompoundStatement(CompoundStatement stmt){
        stmt.statement1.apply(this);
        System.out.println(";");
        stmt.statement2.apply(this);
        System.out.println();
    }

    /**
     * Prints the code for an assignment statement
     * @param stmt The AssignmentStatement to be printed
     */
    @Override
    public void caseAssignmentStatement(AssignmentStatement stmt){
        System.out.print(stmt.id);
        System.out.print(" := ");
        stmt.exp.apply(this);
    }

    /**
     * Prints the code for a print statement
     * @param stmt The PrintStatement to be printed
     */
    @Override
    public void casePrintStatement(PrintStatement stmt){
        System.out.print("print(");
        stmt.exps.apply(this);
        System.out.print(")");
    }

    /**
     * Prints the code for an identifier expression
     * @param exp The IdentifierExp to be printed
     */
    @Override
    public void caseIdentifierExp(IdentifierExp exp){
        System.out.print(exp.id);
    }

    /**
     * Prints the code for a number expression
     * @param exp The NumberExp to be printed
     */
    @Override
    public void caseNumberExp(NumberExp exp) {
        System.out.print(exp.num);
    }

    /**
     * Prints the code for an operation expression
     * @param exp The OperationExp to be printed
     */
    @Override
    public void caseOperationExp(OperationExp exp){
        exp.left.apply(this);
        if (exp.operation == OperationExp.Plus){
            System.out.print(" + ");
        }
        else if (exp.operation == OperationExp.Minus){
            System.out.print(" - ");
        }
        else if (exp.operation == OperationExp.Times){
            System.out.print(" * ");
        }
        else if (exp.operation == OperationExp.Div){
            System.out.print(" / ");
        }
        exp.right.apply(this);
    }

    /**
     * Prints the code for an expression sequence
     * @param exp The ExpressionSequence to be printed
     */
    @Override
    public void caseExpressionSequence(ExpressionSequence exp){
        System.out.print("(");
        exp.stm.apply(this);
        System.out.print(", ");
        exp.exp.apply(this);
        System.out.print(")");
    }

    /**
     * Prints a pair in a list
     * @param expList The PairExpressionList to be printed
     */
    @Override
    public void casePairExpressionList(PairExpressionList expList){
        expList.head.apply(this);
        System.out.print(", ");
        expList.tail.apply(this);
    }

    /**
     * Prints the last element in a list
     * @param exp The LastExpressionList to be printed
     */
    @Override
    public void caseLastExpressionList(LastExpressionList exp){
        exp.head.apply(this);
    }
}

/**
 * Executes a program given an expression tree.
 * 
 * @author Aaron Thompson
 * @version 1/26/2020
 */
class InterpVisitor extends Visitor{
    private int results;
    private HashMap<String, Integer> variables = new HashMap<String, Integer>();
    int printNum = 0;
 
    /**
     * Executes a compound statement
     * @param stmt The CompoundStatement to be executed
     */
    @Override
    public void caseCompoundStatement(CompoundStatement stmt){
        stmt.statement1.apply(this);
        stmt.statement2.apply(this);
    }

    /**
     * Executes an assignment statement
     * @param stmt The AssignmentStatement to be executed
     */
    @Override
    public void caseAssignmentStatement(AssignmentStatement stmt){
        stmt.exp.apply(this);
        variables.put(stmt.id, results);
    }

    /**
     * Executes a print statement
     * @param stmt The PrintStatement to be executed
     */
    @Override
    public void casePrintStatement(PrintStatement stmt){
        printNum++;
        stmt.exps.apply(this);
        printNum--;

        if (printNum == 0){
            System.out.println();
        }  
    }

    /**
     * Executes an identifier expression
     * @param exp The IdentifierExp to be executed
     */
    @Override
    public void caseIdentifierExp(IdentifierExp exp){
        results = variables.get(exp.id);
    }

    /**
     * Executes a number expression
     * @param exp The NumberExp to be executed
     */
    @Override
    public void caseNumberExp(NumberExp exp) {
        results = exp.num;
    }

    /**
     * Executes an operation expression
     * @param exp The OperationExp to be executed
     */
    @Override
    public void caseOperationExp(OperationExp exp){
        exp.left.apply(this);
        int left = results;
        exp.right.apply(this);
        int right = results;
        if (exp.operation == OperationExp.Plus){
            results = left + right;
        }
        else if (exp.operation == OperationExp.Minus){
            results = left - right;
        }
        else if (exp.operation == OperationExp.Times){
            results = left * right;
        }
        else if (exp.operation == OperationExp.Div){
            results = left / right;
        }
    }

    /**
     * Executes an expression sequence
     * @param exps The ExpressionSequence to be executed
     */
    @Override
    public void caseExpressionSequence(ExpressionSequence exps){
        exps.stm.apply(this);
        exps.exp.apply(this);
    }

    /**
     * Executes a pair in a list
     * @param expList The PairExpressionList to be executed
     */
    @Override
    public void casePairExpressionList(PairExpressionList expList){
        expList.head.apply(this);
        if (printNum != 0){
            System.out.print(results + " ");
        }
        expList.tail.apply(this);
    }

    /**
     * Executes the last expression in a list
     * @param exp The LastExpressionList to be executed
     */
    @Override
    public void caseLastExpressionList(LastExpressionList exp){
        exp.head.apply(this); 
        if (printNum != 0){
            System.out.print(results + " ");
        }
    }
}

/**
 * Calculates the max number of arguments in a print statement within an execution tree of a program
 * 
 * @author Aaron Thompson
 * @version 1/27/2020
 */
class ArgCountVisitor extends Visitor{
    private Stack<Integer> argsStack = new Stack<Integer>();
    private int maxArgs = 0;

    /**
     * Returns the maximum arguments passed to a print statement
     * @return the maximum number of arguments passed to a print statement
     */
    public int getMaxArgs(){
        return maxArgs;
    }

    /**
     * Find the maximum number of arguments in a print statement
     * @param stmt The PrintStatement to be visited
     */
    @Override
    public void casePrintStatement(PrintStatement stmt){
        argsStack.push(0);
        stmt.exps.apply(this);
        int numArgs = argsStack.pop();
        if (numArgs > maxArgs){
            maxArgs = numArgs;
        }
    }

    /**
     * step through a list in a print statement 
     * @param expList a pair in a list
     */
    @Override
    public void casePairExpressionList(PairExpressionList expList){
        expList.head.apply(this);
        int newVal = argsStack.pop();
        newVal++;
        argsStack.push(newVal);
        expList.tail.apply(this);
    }

    /**
     * Visit the last element in a list and update arguments
     * @param exp The last element in a list
     */
    @Override
    public void caseLastExpressionList(LastExpressionList exp){
        exp.head.apply(this);
        int newVal = argsStack.pop();
        newVal++;
        argsStack.push(newVal);
    }
}


