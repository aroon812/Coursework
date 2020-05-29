package typecheck;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator; 

import minijava.analysis.DepthFirstAdapter;
import minijava.node.AAllocExp;
import minijava.node.AAndExp;
import minijava.node.AArrayAsmtStmt;
import minijava.node.AAsmtStmt;
import minijava.node.ABaseClassDecl;
import minijava.node.AFalseExp;
import minijava.node.AFormal;
import minijava.node.AIdExp;
import minijava.node.AIfStmt;
import minijava.node.ALengthExp;
import minijava.node.ALtExp;
import minijava.node.AMainClassDecl;
import minijava.node.AMethod;
import minijava.node.AMethodExp;
import minijava.node.AMinusExp;
import minijava.node.ANewExp;
import minijava.node.ANotExp;
import minijava.node.ANumExp;
import minijava.node.APlusExp;
import minijava.node.APrintStmt;
import minijava.node.ARefExp;
import minijava.node.AReturnStmt;
import minijava.node.ASubClassDecl;
import minijava.node.AThisExp;
import minijava.node.ATimesExp;
import minijava.node.ATrueExp;
import minijava.node.AUserType;
import minijava.node.AVarDecl;
import minijava.node.AWhileStmt;
import minijava.node.PExp;
import minijava.node.PFormal;
import minijava.node.PMethod;
import minijava.node.PStmt;
import minijava.node.PType;
import minijava.node.PVarDecl;
import minijava.node.TId;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import symtable.Types;

/** 
 * This class traverses a MiniJava Abstract Syntax Tree and typechecks
 * the program it represents.  It reports standard "type mismatch" sorts
 * of errors, and flags undefined variables and types.  It understands
 * inheritance, to the extent that a subclass can be passed where a 
 * superclass is expected.  It makes no effort to detect overloading (which
 * is currently handled in a separate pass by CheckOverload), but implements
 * overriding properly on vars and methods.  (Overridden methods must have
 * identical signatures.)
 */
public class TypecheckVisitor extends DepthFirstAdapter {
	ClassTable table; 			// The symbol table, created in previous pass
	String typeof; 				// Used as a "return value" when typing a subtree
	MethodInfo currentMethod; 	// The method whose code we're currently checking
	ClassInfo currentClass; 	// The class in which we're currently checking
	int line; 					// Line number of the most recent terminal

	private void reportError(String err) {
		System.err.println(err);
		System.exit(-1);
	}

	/** true if t1 is the SAME as t2, or if t1 is a subclass of t2 */
	private boolean compatible(String t1, String t2) {
		if (t2.equals(t1)) {
			return true;
		}

		ClassInfo t1Class = table.get(t1); 	// Look for a class named t1
		if (t1Class == null) {
			return false;
		}
		TId superClass = t1Class.getSuper();// Get t1's superclass record
		while (superClass != null) { 		// if one exists...
			if (t2.equals(superClass.getText())) {
				return true; 				// Else super's super and try again
			}
			superClass = (table.get(superClass.getText())).getSuper();
		}
		return false;
	}

	/**
	 * Need to type the args, and match them to the types of the formals. If all
	 * goes well, return the method's return type as a string
	 */
	private String checkMethodInvocation(int line, MethodInfo mInfo,
			LinkedList<PExp> args) {
		LinkedList<PFormal> formals = mInfo.getFormals();
		// Make sure number of args is right before proceeding
		if (formals.size() != args.size()) {
			reportError("Expected " + formals.size() + " arguments to "
					+ mInfo.getName().getText() + " at line " + line);
		}

		// Now we'll loop through the args and make sure the actuals match the
		// type of the formals.
		ListIterator<PFormal> iter = formals.listIterator(0);
		int count = 0;
		for (PExp e : args) {
			count++;
			String formalType = Types
					.toString(((AFormal) (iter.next())).getType());
			e.apply(this);
			if (!compatible(typeof, formalType)) {
				reportError("Type mismatch at arg " + count + " of "
						+ mInfo.getName().getText() + ", line " + line + " ("
						+ typeof + " instead of " + formalType + ")");
			}
		}
		return Types.toString(mInfo.getRetType());
	}

	/**
	 * Returns a variable's type as a string.
	 * @param name  The variable's name.
	 * @return  Its type, as a string.
	 */
	private String lookupVar(String name) {
		PType type;
		ClassInfo info;
		// Check in current method
		if (currentMethod == null) {
			reportError("Varible " + name + " undefined on line " + line);
		} else if ((type = currentMethod.getLocals().get(name)) != null) {
			return Types.toString(type);
		}
		// Check current class
		if (currentClass == null) {
			reportError("Varible " + name + " undefined on line " + line);
		} else if ((type = currentClass.getVarTable().get(name)) != null) {
			return Types.toString(type);
		} else {
			// Now it's either defined in a superclass, or not at all...
			TId superClass = currentClass.getSuper(); // TId of parent class
			while (superClass != null) { // if superclass exists...
				info = table.get(superClass.getText()); // Dig up super's info
				assert (info != null); // shouldn't be, after previous tests
				if ((type = info.getVarTable().get(name)) != null) {
					break; // Found it
				}
				superClass = info.getSuper(); // find super's parent
			}
			// We either left the loop because we ran out of superclasses without
			// finding the var, or we left the loop early 'cause we found it.
			if (superClass == null) {
				reportError("Varible " + name + " undefined on line " + line);
			}
			return Types.toString(type);
		}
		return "---"; // Won't ever get here -- need to keep compiler happy
	}

	public TypecheckVisitor(ClassTable symtab) {
		table = symtab;
		currentMethod = null;
		currentClass = null;
	}

	@Override
	public void caseAMainClassDecl(AMainClassDecl node) {
		inAMainClassDecl(node);
		currentMethod = null; // This should be the case anyway, since we do the
		currentClass = null; // main class before the others...
		if (node.getStmt() != null) {
			node.getStmt().apply(this);
		}
		outAMainClassDecl(node);
	}

	/**
	 * When we process a class, we first look up its name in the symbol table
	 * and keep a reference to its ClassInfo structure. We don't need to
	 * traverse the var decls, as there's nothing to learn from them that's not
	 * already in the symbol table. As we visit each method, we set a reference
	 * to its info record as well.
	 */
	@Override
	public void caseABaseClassDecl(ABaseClassDecl node) {
		inABaseClassDecl(node);
		if (node.getId() != null) {
			currentClass = table.get(node.getId().getText()); // Lookup
			// ClassInfo
		}

		// Have to visit var decls so we can watch for undefined types
		List<PVarDecl> vars = new ArrayList<PVarDecl>(node.getVarDecl());
		for (PVarDecl e : vars) {
			e.apply(this);
		}

		List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
		for (PMethod e : copy) {
			AMethod m = (AMethod) e;
			currentMethod = currentClass.getMethodTable().get(
					m.getId().getText());
			line = currentMethod.getName().getLine();
			e.apply(this);
		}
		outABaseClassDecl(node);
	}

	/**
	 * This is just like typechecking a BaseClass, except that we also need to
	 * make sure that the superclass really exists.
	 */
	@Override
	public void caseASubClassDecl(ASubClassDecl node) {
		inASubClassDecl(node);
		assert ((node.getId() != null) && (node.getExtends() != null));
		currentClass = table.get(node.getId().getText()); // Lookup ClassInfo
		if (table.get(node.getExtends().getText()) == null) {
			reportError("Superclass " + node.getExtends()
			+ "doesn't exist on line " + node.getExtends().getLine());
		}

		// Have to visit var decls so we can watch for undefined types
		List<PVarDecl> vars = new ArrayList<PVarDecl>(node.getVarDecl());
		for (PVarDecl e : vars) {
			e.apply(this);
		}

		List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
		for (PMethod e : copy) {
			AMethod m = (AMethod) e;
			currentMethod = currentClass.getMethodTable().get(
					m.getId().getText());
			line = currentMethod.getName().getLine();
			e.apply(this);
		}
		outASubClassDecl(node);
	}

	/**
	 * Visit the formal parameters and var declarations to look for undefined
	 * user types, then typecheck the stmts in the body.
	 */
	@Override
	public void caseAMethod(AMethod node) {
		inAMethod(node);
		List<PFormal> args = new ArrayList<PFormal>(node.getFormal());
		for (PFormal e : args) {
			e.apply(this);
		}

		List<PVarDecl> vars = new ArrayList<PVarDecl>(node.getVarDecl());
		for (PVarDecl e : vars) {
			e.apply(this);
		}

		List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
		for (PStmt e : copy) {
			e.apply(this);
		}
		outAMethod(node);
	}

	/** Make sure types in formal parameters are found in table */
	@Override
	public void caseAFormal(AFormal node) {
		inAFormal(node);
		// System.err.println("Checking formal "+node.getId().getText());
		assert ((node.getType() != null) && (node.getId() != null));
		line = node.getId().getLine();
		if ((node.getType() instanceof AUserType)
				&& table.get(Types.toString(node.getType())) == null) {
			reportError("Unknown type " + Types.toString(node.getType())
			+ " at line " + line);
		}
		outAFormal(node);
	}

	/** Make sure types in var decls are found in table */
	@Override
	public void caseAVarDecl(AVarDecl node) {
		inAVarDecl(node);
		assert ((node.getType() != null) && (node.getId() != null));
		line = node.getId().getLine();
		if ((node.getType() instanceof AUserType)
				&& table.get(Types.toString(node.getType())) == null) {
			reportError("Unknown type " + Types.toString(node.getType())
			+ " at line " + line);
		}
		outAVarDecl(node);
	}

	/** Make sure type of exp matches return type of method. */
	@Override
	public void caseAReturnStmt(AReturnStmt node) {
		inAReturnStmt(node);
		assert (node.getExp() != null);
		String retType = Types.toString(currentMethod.getRetType());
		node.getExp().apply(this);
		if (!compatible(typeof, retType)) {
			reportError("Type of returned expr must be " + retType + ", not "
					+ typeof + ", on line " + line);
		}
		typeof = null;
		outAReturnStmt(node);
	}

	/** Type of test must be boolean. Visit both branches to typecheck. */
	@Override
	public void caseAIfStmt(AIfStmt node) {
		inAIfStmt(node);
		assert ((node.getExp() != null) && (node.getYes() != null) && (node
				.getNo() != null));
		node.getExp().apply(this);
		if (!compatible(typeof, Types.BOOL)) {
			reportError("Test in if stmt must be boolean, not " + typeof
					+ ", on line " + line);
		}
		node.getYes().apply(this);
		node.getNo().apply(this);
		typeof = null;
		outAIfStmt(node);
	}

	/** Type of test must be boolean. Visit body to typecheck. */
	@Override
	public void caseAWhileStmt(AWhileStmt node) {
		inAWhileStmt(node);
		assert ((node.getExp() != null) && (node.getStmt() != null));
		node.getExp().apply(this);
		if (!compatible(typeof, Types.BOOL)) {
			reportError("Test in while stmt must be boolean, not " + typeof
					+ ", on line " + line);
		}
		node.getStmt().apply(this);
		typeof = null;
		outAWhileStmt(node);
	}

	/** Printed value must be int (can't skip arg either) */
	@Override
	public void caseAPrintStmt(APrintStmt node) {
		inAPrintStmt(node);
		assert (node.getExp() != null);
		node.getExp().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Argument to println must be int, not " + typeof
					+ ", on line " + line);
		}
		typeof = null;
		outAPrintStmt(node);
	}

	/** LHS and RHS must have same type. */
	@Override
	public void caseAAsmtStmt(AAsmtStmt node) {
		String lhsType;
		inAAsmtStmt(node);
		assert ((node.getId() != null) && (node.getExp() != null));
		line = node.getId().getLine();
		lhsType = lookupVar(node.getId().getText());
		node.getExp().apply(this);
		if (!compatible(lhsType, typeof)) {
			reportError("Type mismatch in assignment on line " + line);
		}
		typeof = null;
		outAAsmtStmt(node);
	}

	/** Id must be int[], Idx and Val must be int. */
	@Override
	public void caseAArrayAsmtStmt(AArrayAsmtStmt node) {
		inAArrayAsmtStmt(node);
		assert ((node.getId() != null) && (node.getIdx() != null) && (node
				.getVal() != null));
		node.getId().apply(this);
		line = node.getId().getLine();
		typeof = lookupVar(node.getId().getText());

		if (!compatible(typeof, Types.INTARRAY)) {
			reportError("Indexing into non-array type on LHS, line " + line);
		}
		node.getIdx().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Index on LHS is of type " + typeof
					+ ", not int on line " + line);
		}
		node.getVal().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Expected int, not " + typeof
					+ ", on RHS of assignment, line " + line);
		}
		typeof = null;
		outAArrayAsmtStmt(node);
	}

	/** Args must be boolean, as is return type */
	@Override
	public void caseAAndExp(AAndExp node) {
		inAAndExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		if (!compatible(typeof, Types.BOOL)) {
			reportError("Attempt to apply && to argument of type " + typeof
					+ " on line " + line);
		}
		node.getRight().apply(this);
		if (!compatible(typeof, Types.BOOL)) {
			reportError("Attempt to apply && to argument of type " + typeof
					+ " on line " + line);
		}
		typeof = Types.BOOL;
		outAAndExp(node);
	}

	/** Args must be int, return type is boolean */
	@Override
	public void caseALtExp(ALtExp node) {
		inALtExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply < to argument of type " + typeof
					+ " on line " + line);
		}
		node.getRight().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply < to argument of type " + typeof
					+ " on line " + line);
		}
		typeof = Types.BOOL;
		outALtExp(node);
	}

	/** Args must be int, as is return type */
	@Override
	public void caseAPlusExp(APlusExp node) {
		inAPlusExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply + to argument of type " + typeof
					+ " on line " + line);
		}
		node.getRight().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply + to argument of type " + typeof
					+ " on line " + line);
		}
		typeof = Types.INT;
		outAPlusExp(node);
	}

	/** Args must be int, as is return type */
	@Override
	public void caseAMinusExp(AMinusExp node) {
		inAMinusExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply - to argument of type " + typeof
					+ " on line " + line);
		}
		node.getRight().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply - to argument of type " + typeof
					+ " on line " + line);
		}
		typeof = Types.INT;
		outAMinusExp(node);
	}

	/** Args must be int, as is return type */
	@Override
	public void caseATimesExp(ATimesExp node) {
		inATimesExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply * to argument of type " + typeof
					+ " on line " + line);
		}
		node.getRight().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Attempt to apply * to argument of type " + typeof
					+ " on line " + line);
		}
		typeof = Types.INT;
		outATimesExp(node);
	}

	/** Ensure the expression is boolean; return type is boolean */
	@Override
	public void caseANotExp(ANotExp node) {
		inANotExp(node);
		assert (node.getExp() != null);
		node.getExp().apply(this);
		if (!compatible(typeof, Types.BOOL)) {
			reportError("Argument of ! is of type " + typeof
					+ ", not boolean, on line " + line);
		} else {
			typeof = Types.BOOL;
		}
		outANotExp(node);
	}

	/** Make sure the type of name is int[], and that idx is an int */
	@Override
	public void caseARefExp(ARefExp node) {
		inARefExp(node);
		assert ((node.getName() != null) && (node.getIdx() != null));
		node.getName().apply(this);
		if (!compatible(typeof, Types.INTARRAY)) {
			reportError("Attempt to index an expression of type " + typeof
					+ "on line " + line);
		}
		node.getIdx().apply(this);
		if (!compatible(typeof, Types.INT)) {
			reportError("Index expression is of type " + typeof
					+ ", not int, on line " + line);
		}
		typeof = Types.INT;
		outARefExp(node);
	}

	/**
	 * The length field only applies to int[] types, so we need to type the
	 * expression on which length is applied and ensure it's int[]. If so, the
	 * return type is int.
	 */
	@Override
	public void caseALengthExp(ALengthExp node) {
		inALengthExp(node);
		assert (node.getExp() != null);
		node.getExp().apply(this); // Type the expression
		if (!compatible(typeof, Types.INTARRAY)) {
			reportError("length applied to inappropriate type (" + typeof
					+ ") at line " + line);
		} else {
			typeof = Types.INT;
		}
		outALengthExp(node);
	}

	/**
	 * The object reference in obj.method() cannot be a class name, since we
	 * don't allow static methods in minijava. It must be a variable, "this", or
	 * some more complex expression whose value is a user-defined type (E.g.
	 * Foo.instVar.do()). We can visit it to determine its type.
	 */
	@Override
	public void caseAMethodExp(AMethodExp node) {
		String objType, methodName;
		ClassInfo cInfo;
		inAMethodExp(node);
		assert ((node.getObj() != null) && (node.getId() != null));
		// Type the object reference to get the name of the class containing the
		// method, then go looking for the method info record, which could be in
		// a superclass.
		node.getObj().apply(this); // Find type of object reference
		objType = typeof;
		line = node.getId().getLine(); // Update the file position

		methodName = node.getId().getText(); // Retrieve name for convenience
		cInfo = table.get(objType); // Find info on reference's class
		if (cInfo == null) {
			reportError("Bad object reference on line " + line + ".");
		}
		MethodInfo mInfo = cInfo.getMethodTable().get(methodName);
		if (mInfo != null) {
			typeof = checkMethodInvocation(line, mInfo, node.getArgs());
		} else {
			TId superClass = cInfo.getSuper(); 		// TId of parent class
			while (superClass != null) { 			// if superclass exists...
				cInfo = table.get(superClass.getText()); // Dig up super's info
				assert (cInfo != null); 			// shouldn't be, after previous tests
				if ((mInfo = cInfo.getMethodTable().get(methodName)) != null) {
					break; 							// Found it
				}
				superClass = cInfo.getSuper(); 		// find super's parent
			}
			// We either left the loop because we ran out of superclasses without
			// finding the var, or we left the loop early 'cause we found it.
			if (superClass == null) {
				reportError("Method " + methodName + " undefined on line "
						+ line);
			} else {
				typeof = checkMethodInvocation(line, mInfo, node.getArgs());
				// mInfo.enclosing = cInfo;
				objType = superClass.getText();
			}
		}
		// This code was added during IRT-generation to create the appropriate
		// method name while we've got all of the necessary info. We set the
		// text of the method identifier to "ClassName.method". Note that the
		// name stored in the MethodInfo record is still set to the original,
		// and that table lookups on the method will still need to use the
		// original name.
		node.getId().setText(objType + "." + methodName);
		// System.err.println("Set text to "+objType+"$"+methodName);
		outAMethodExp(node);
	}

	/** A number is always an int */
	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		if (node.getNum() != null) {
			typeof = Types.INT;
			line = node.getNum().getLine();
		}
		outANumExp(node);
	}

	/**
	 * It's assumed if we get here that the Id in question was embedded in an
	 * expression, and must therefore be a variable. (If it was a method name,
	 * it would've been handled in the MethodExp case.) We look in the current
	 * method and class, respectively, for defintions. If we strike out, we look
	 * up the class hierarchy for a definition.
	 */
	@Override
	public void caseAIdExp(AIdExp node) {
		inAIdExp(node);
		assert (node.getId() != null);
		line = node.getId().getLine();
		typeof = lookupVar(node.getId().getText());
		outAIdExp(node);
	}

	/** These next three are "type literals", essentially. */
	@Override
	public void caseATrueExp(ATrueExp node) {
		inATrueExp(node);
		typeof = Types.BOOL;
		outATrueExp(node);
	}

	@Override
	public void caseAFalseExp(AFalseExp node) {
		inAFalseExp(node);
		typeof = Types.BOOL;
		outAFalseExp(node);
	}

	@Override
	public void caseAThisExp(AThisExp node) {
		inAThisExp(node);
		typeof = currentClass.getName().getText();
		outAThisExp(node);
	}

	/** Need to make sure the exp is an int; return int[] */
	@Override
	public void caseAAllocExp(AAllocExp node) {
		inAAllocExp(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (compatible(typeof, "int")) {
				typeof = Types.INTARRAY;
			} else {
				reportError("Expected int expression instead of " + typeof
						+ " in array allocation near line " + line);
			}
		}
		outAAllocExp(node);
	}

	/** Need to make sure the Id is a type. If so, set typeof to its text. */
	@Override
	public void caseANewExp(ANewExp node) {
		inANewExp(node);
		if (node.getId() != null) {
			if (table.get(node.getId().getText()) == null) {
				reportError("Type " + node.getId() + "undefined on line "
						+ node.getId().getLine());
			} else {
				typeof = node.getId().getText();
				line = node.getId().getLine();
			}
		}
		outANewExp(node);
	}
}
