package irt;

import arch.Label;
import minijava.analysis.DepthFirstAdapter;
import mips.MipsArch;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import tree.BINOP;
import tree.CONST;
import tree.Exp;
import tree.MEM;
import tree.REG;
import tree.SEQ;
import tree.Stm;

/** 
 * This class traverses a MiniJava Abstract Syntax Tree and generates
 * corresponding IRT code.
 * 
 * @author Brad Richards
 */

public class IRTVisitor extends DepthFirstAdapter {
	ClassTable table; 			// The symbol table
	MethodInfo currentMethod; 	// The method whose code we're currently checking
	ClassInfo currentClass; 	// The class in which we're currently checking
	int line; 					// Line number of the most recent terminal
	MipsArch alloc; 			// Use to allocate registers as needed
	Stm stm; 					// The return value for the statement cases
	Stm prog; 					// Holds the IRT tree while we construct it
	Exp exp; 					// The return value for expression subtrees

	Label print = new Label("print");
	Label malloc = new Label("malloc");
	REG SP = new REG(mips.MipsArch.SP);
	REG SL = new REG(mips.MipsArch.SL);
	Exp SLfromStack = new MEM(new BINOP(BINOP.PLUS, SP, new CONST(4)));

	/**
	 * The constructor takes the symbol table for the program we're about to
	 * translate.
	 * @param symtab The symbol table
	 */
	public IRTVisitor(ClassTable symtab) {
		table = symtab;
		currentMethod = null;
		currentClass = null;
	}

	/**
	 * Tacks a new statement node onto the tree we're building. 
	 * @param piece A new IRT tree fragment
	 */
	private void extendProg(Stm piece) {
		// If this is the first piece we've created, it becomes the program,
		// otherwise we glue it to the existing program with a SEQ node.
		if (prog == null) {
			prog = piece;
		} else {
			prog = new SEQ(piece, prog);
		}
	}

	/**
	 * This accessor returns the root of the current IRT tree. Once the visitor
	 * is done, this method will return the entire IRT representation of our program.
	 * @return  Current IRT tree
	 */
	public Stm getProgram() {
		return prog;
	}

	/**
	 * Creates an IRT fragment for accessing a variable. The variable is either
	 * a local in the current method, a field in the current class, or an
	 * instance variable in one of the superclasses.
	 * @param name  The name of the variable we're after
	 * @return      IRT for accessing the specified variable
	 */
	private Exp lookupVar(String name) {

		// TODO: You need to add some guts here

		return null;	// so things will compile for now
	}

	// ------------- visitor methods from here down ---------------

	// TODO: Override necessary visitor methods

}
