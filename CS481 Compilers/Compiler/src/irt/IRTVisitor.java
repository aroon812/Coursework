//Aaron Thompson & Lukas Jimenez-Smith

package irt;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import minijava.analysis.DepthFirstAdapter;
import minijava.node.AAllocExp;
import minijava.node.AAndExp;
import minijava.node.AArrayAsmtStmt;
import minijava.node.AAsmtStmt;
import minijava.node.ABaseClassDecl;
import minijava.node.ABlockStmt;
import minijava.node.AFalseExp;
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
import minijava.node.AWhileStmt;
import minijava.node.PExp;
import minijava.node.PMethod;
import minijava.node.PStmt;
import minijava.node.TId;
import mips.MipsArch;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import tree.JUMP;
import tree.CJUMP;
import tree.BINOP;
import tree.CALL;
import tree.COMMENT;
import tree.CONST;
import tree.ESEQ;
import tree.EXPR;
import tree.Exp;
import tree.ExpList;
import tree.LABEL;
import tree.MEM;
import tree.MOVE;
import tree.NAME;
import tree.REG;
import tree.RETURN;
import tree.SEQ;
import tree.Stm;
import arch.Label;
import arch.Reg;

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
	MipsArch alloc = new MipsArch();			// Use to allocate registers as needed
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
	 * Creates an IRT fragment for accessing a variable. The variable is either
	 * a local in the current method, a field in the current class, or an
	 * instance variable in one of the superclasses.
	 * @param name  The name of the variable we're after
	 * @return      IRT for accessing the specified variable
	 */
	private Exp lookupVar(String name) {
		if (currentMethod.getLocals().get(name) != null) {
			// It's a local in the current method
			return currentMethod.getLocals().getInfo(name).getAccess().getTree(SP);
		}
		else if (currentClass.getVarTable().get(name) != null) {
			// It's a field in the current class
			return currentClass.getVarTable().getInfo(name).getAccess().getTree(SLfromStack);
		} 
		else {
			// Now it's either defined in a superclass, or not at all...
			ClassInfo info = currentClass;
			TId superClass = currentClass.getSuper(); 	// TId of parent class
			while (superClass != null) { 				// if superclass exists...
				info = table.get(superClass.getText()); // Dig up super's info
				assert (info != null); 		// shouldn't be, after previous tests
				if (info.getVarTable().get(name) != null) {
					return info.getVarTable().getInfo(name).getAccess().getTree(SLfromStack);
				}
				superClass = info.getSuper(); 			// find super's parent
			}
			// If we get here, we tried the entire inheritance chain without
			// finding the variable.
			throw new Error("Didn't find variable "+name+" in "+currentMethod.getName());
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

	// ------------- visitor methods from here down ---------------


	/**
	 * Build IRT for the main method: Translate the body, then add the
	 * appropriate label.
	 */
	@Override
	public void caseAMainClassDecl(AMainClassDecl node) {
		inAMainClassDecl(node);
		currentMethod = null; 	// This should be the case anyway, since we do the
		currentClass = null; 	// main class before the others...
		if (node.getStmt() != null) {
			node.getStmt().apply(this);
			extendProg(new SEQ(new LABEL(new Label("main")), stm));
		}
		outAMainClassDecl(node);
	}

	/**
	 * Build IRT for a class that doesn't inherit from another. 
	 */
	@Override
	public void caseABaseClassDecl(ABaseClassDecl node) {
		inABaseClassDecl(node);
		assert(node.getId() != null);
		currentClass = table.get(node.getId().getText()); // Lookup ClassInfo

		// No need to visit instance var decls -- we already know how to
		// create new instances after the last pass. Just visit methods.

		List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
		for (PMethod e : copy) {
			AMethod m = (AMethod) e;
			currentMethod = currentClass.getMethodTable().get(m.getId().getText());
			line = currentMethod.getName().getLine();
			e.apply(this);
			//extendProg(stm);	
		}
		outABaseClassDecl(node);
	}

	/**
	 * This is just like processing a BaseClass.
	 */
	@Override
	public void caseASubClassDecl(ASubClassDecl node) {
		inASubClassDecl(node);
		assert(node.getId() != null);
		currentClass = table.get(node.getId().getText()); // Lookup ClassInfo

		// No need to visit instance var decls -- we already know how to *
		// create new instances after the last pass. Just visit methods. *
		
		List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
		for (PMethod e : copy) {
			AMethod m = (AMethod) e;
			currentMethod = currentClass.getMethodTable().get(m.getId().getText());
			line = currentMethod.getName().getLine();
			e.apply(this);
			//extendProg(stm);
		}
		
		outASubClassDecl(node);
	}

	/**
	 * Build IRT for a method. No need to visit the formal parameters or local
	 * variables -- we already know their offsets, and we'll allocate arrays when 
	 * new() is invoked. Just visit each of the statements and collect the IRT from
	 * each into a larger IRT tree.
	 */
	@Override
	public void caseAMethod(AMethod node) {
		inAMethod(node);
		List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
		
		copy.get(0).apply(this);
		Stm temp = stm;
		if (copy.size() > 1){
			for (int i = 1; i < copy.size(); i++){
				copy.get(i).apply(this);
				temp = new SEQ(temp, stm);
			}
		}
		
		extendProg(new SEQ(new LABEL(new Label(currentClass.getName().getText() + "." + node.getId().getText())), temp));
		
		outAMethod(node);
	}

	/** 
	 * Build IRT for a return statement: Visit the Exp to generate IRT code, 
	 * then build a RETURN node around it. 
	 */
	@Override
	public void caseAReturnStmt(AReturnStmt node) {
		inAReturnStmt(node);
		assert (node.getExp() != null);
		Reg.reset(); //in alloc exp, reset between expressions
		node.getExp().apply(this);
		stm = new RETURN(exp);
		outAReturnStmt(node);
	}

	/**
	 * Translate a block of statements into IRT: Build the IRT for each
	 * statement, tack them together with SEQ nodes, and return the 
	 * resulting tree.
	 */
	@Override
	public void caseABlockStmt(ABlockStmt node) {
		inABlockStmt(node);
		List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
		copy.get(0).apply(this);

		Stm newStm = stm;

		if (copy.size() > 1){
			for(int i = 1; i < copy.size(); i++) {
				copy.get(i).apply(this);
				newStm = new SEQ(newStm, stm);
			}
		}
		stm = newStm;

		outABlockStmt(node);
	}


	/** 
	 * Build IRT for an IF statement. Translate the boolean expression as
	 * well as both the true and false branches, then build a larger IRT
	 * expression out of those pieces. The condition will use a CJUMP, but
	 * we'll need labels for each of the branches and jumps to get the
	 * control flow right.
	 */
	@Override
	public void caseAIfStmt(AIfStmt node) {
		inAIfStmt(node);
		Label yes = new Label();
		Label no = new Label();
		Label other = new Label();

		
		node.getYes().apply(this);
		Stm yesBranch = stm;
		node.getNo().apply(this);
		Stm noBranch = stm;
		node.getExp().apply(this);
		
		
		Stm joomperguy7 = new SEQ(new CJUMP(CJUMP.EQ, exp, new CONST(1), yes, no), new SEQ(new SEQ(new LABEL(no), new SEQ(noBranch, new JUMP(other))), new SEQ(new SEQ(new LABEL(yes),new SEQ(yesBranch, new JUMP(other))), new LABEL(other))));
		stm = joomperguy7;


		outAIfStmt(node);
	}

	/** 
	 * Build IRT for a while loop. The approach is similar to that used in
	 * translating an IF statement. 
	 */
	@Override
	public void caseAWhileStmt(AWhileStmt node) {
		inAWhileStmt(node);
		node.getExp().apply(this);
		Exp whileExp = exp;
		node.getStmt().apply(this);
		Label boolCheck = new Label();
		Label inLoop = new Label();
		Label exitLoop = new Label();

		stm = new SEQ(
					new SEQ(
						new COMMENT("while loop near line " + line), 
						new SEQ(
							new LABEL(boolCheck),
							new CJUMP(
								CJUMP.EQ,
								whileExp,
								new CONST(0),
								exitLoop,
								inLoop
							)
						)),
					new SEQ(
						new SEQ(
							new LABEL(inLoop), 
							new SEQ(
								stm,
								new JUMP(boolCheck)
							)),
						new LABEL(exitLoop)
					)
					);

		outAWhileStmt(node);
	}

	/**
	 * Built the IRT for a print statement: Visit arg to get IRT for the value being
	 * printed and insert it into a call to the built-in print procedure. Wrap an
	 * EXPR around the CALL to make a statement out of it.
	 */
	@Override
	public void caseAPrintStmt(APrintStmt node) {
		inAPrintStmt(node);
		assert (node.getExp() != null);
		Reg.reset();
		node.getExp().apply(this);
		stm = new EXPR(new CALL(new NAME(print), new ExpList(exp, null)));
		outAPrintStmt(node);
	}

	/**
	 * Build IRT for an assignment statement: Visit the RHS to get IRT for the
	 * value being assigned, then build a MOVE node around result and the accessor
	 * fragment for the variable on the LHS.
	 */
	@Override
	public void caseAAsmtStmt(AAsmtStmt node) {
		inAAsmtStmt(node);
		node.getExp().apply(this);
		stm = new SEQ(new COMMENT(node.getId().getText() + " = ... [" + "line " + line + "]"), new MOVE(lookupVar(node.getId().getText()), exp));
		outAAsmtStmt(node);
	}

	/**
	 * Build IRT for an assignment to an array. This is similar to a normal assignment
	 * statement, but we need to create IRT for computing the index expression and
	 * then build an expression around the index value that calculates the offset from
	 * the base address of the array. (The length is stored at offset 0, so we need to
	 * add one to the index before multiplying it by 4 to get the byte offset.)
	 */
	@Override
	public void caseAArrayAsmtStmt(AArrayAsmtStmt node) {
		inAArrayAsmtStmt(node);
		node.getIdx().apply(this);
		Exp idxExp = exp;

		node.getVal().apply(this);
		stm = new SEQ(
				new COMMENT(node.getId().getText() + "[...] = ...[" + "line " + line + "]"), 
				new MOVE(
					new MEM(
						new BINOP(BINOP.PLUS, 	
							lookupVar(node.getId().getText()), 
							new BINOP(BINOP.MUL,
								new BINOP(BINOP.PLUS,
									idxExp,
									new CONST(1)),
								new CONST(4)))), 
					exp));
		outAArrayAsmtStmt(node);
	}

	/**
	 * Build IRT for a boolean && expression. My code doesn't do short-circuiting.
	 * It translates the left and right expressions into IRT and then builds a
	 * BINOP.AND out of them.
	 */
	@Override
	public void caseAAndExp(AAndExp node) {
		inAAndExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		Exp left = exp;
		node.getRight().apply(this);
		Exp right = exp;
		exp = new BINOP(BINOP.AND, left, right);
		outAAndExp(node);
	}

	/** 
	 * Built IRT for a < expression. Create IRT for the left and right expressions,
	 * and build a structure that uses a CJUMP.LT to jump to code that puts a 1 (or
	 * code that puts a 0) in a result register. 
	 */
	@Override
	public void caseALtExp(ALtExp node) {
		inALtExp(node);
		node.getLeft().apply(this);
		Exp leftExp = exp;
		node.getRight().apply(this);
		exp = new BINOP(BINOP.SLT, leftExp, exp);
		
		outALtExp(node);
	}

	/** 
	 * Built IRT for a + expression. Create IRT for the left and right expressions,
	 * and build a BINOP.PLUS node out of them.
	 */
	@Override
	public void caseAPlusExp(APlusExp node) {
		inAPlusExp(node);
		assert ((node.getLeft() != null) && (node.getRight() != null));
		node.getLeft().apply(this);
		Exp left = exp;
		node.getRight().apply(this);
		Exp right = exp;
		exp = new BINOP(BINOP.PLUS, left, right);
		outAPlusExp(node);
	}

	/** 
	 * Built IRT for a - expression. Create IRT for the left and right expressions,
	 * and build a BINOP.MINUS node out of them.
	 */
	@Override
	public void caseAMinusExp(AMinusExp node) {
		inAMinusExp(node);
		node.getLeft().apply(this);
		Exp left = exp;
		node.getRight().apply(this);
		Exp right = exp;
		exp = new BINOP(BINOP.MINUS, left, right);
		outAMinusExp(node);
	}

	/** 
	 * Built IRT for a * expression. Create IRT for the left and right expressions,
	 * and build a BINOP.MUL node out of them.
	 */
	@Override
	public void caseATimesExp(ATimesExp node) {
		inATimesExp(node);
		node.getLeft().apply(this);
		Exp left = exp;
		node.getRight().apply(this);
		Exp right = exp;
		exp = new BINOP(BINOP.MUL, left, right);
		outATimesExp(node);
	}

	/**
	 * Built IRT for a boolean ! expression. Since booleans are represented with
	 * 0 and 1, we get the right result if we just calculate 1-b where b is the
	 * boolean value we're negating.
	 */
	@Override
	public void caseANotExp(ANotExp node) {
		inANotExp(node);

		node.getExp().apply(this);
		Exp expr = exp;
		exp = new BINOP(BINOP.MINUS, new CONST(1), expr);
		
		outANotExp(node);
	}

	/**
	 * Build IRT for an expression that's indexing into an array. Build the
	 * IRT for the index expression, then add one and multiply by 4 to get the
	 * offset in bytes. Build a MEM node that retrieves from the sum of the 
	 * array's base address and the offset.
	 */
	@Override
	public void caseARefExp(ARefExp node) {
		inARefExp(node);
		node.getIdx().apply(this);
		
		Exp idxExp = exp;
		exp = new MEM(
				new BINOP(
					BINOP.PLUS,
					lookupVar(node.getName().toString().replaceAll("\\s+", "")),
					new BINOP(
						BINOP.MUL,
						new CONST(4),
						new BINOP(
							BINOP.PLUS,
							new CONST(1),
							idxExp
						)
					)
				)
		);
		
		outARefExp(node);
	}

	/**
	 * Build IRT for an expression taking the length of an array. We store
	 * the length at an offset of 0 from the base address. so just create a
	 * MEM node that reads from the base address.
	 */
	@Override
	public void caseALengthExp(ALengthExp node) {
		inALengthExp(node);
		node.getExp().apply(this);

		exp = new MEM(
				lookupVar(node.toString().replaceAll("\\s+", ""))
			);
		
		outALengthExp(node);
	}

	/**
	 * Build the IRT for an expression that's invoking a method. We have to build
	 * IRT for the object reference expression and for each of the argument
	 * expressions, then build a CALL node including the correct method NAME and
	 * all of the args. (Thankfully, the typecheck pass converted method names to 
	 * their fully qualified equivalents -- e.g. foo to SomeClass.foo, so we can
	 * just use the method name as the label.)
	 */
	@Override
	public void caseAMethodExp(AMethodExp node) {
		inAMethodExp(node);

		LinkedList<PExp> args = node.getArgs();
		ArrayList<Exp> cArgs = new ArrayList<Exp>();

		node.getObj().apply(this);
		cArgs.add(exp);

		for (PExp pexp : args){
			pexp.apply(this);
			cArgs.add(exp);
		}
		
		ExpList args2 = null;
		for (int i = cArgs.size() - 1; i >= 0; i--) {
			args2 = new ExpList(cArgs.get(i), args2);
		}

		exp = new CALL(new NAME(new Label(node.getId().getText())), args2);
		
		outAMethodExp(node);
	}

	/** Build IRT for a numeric literal. */
	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		if (node.getNum() != null) {
			line = node.getNum().getLine();
			exp = new CONST(Integer.parseInt(node.getNum().getText()));
		}
		outANumExp(node);
	}

	/**
	 * Build IRT for a variable reference. Assignment statements already deal
	 * with variables on the LHS, so here we only worry about variables on the
	 * RHS: Look up the variable accessor from the symbot table and return it.
	 */
	@Override
	public void caseAIdExp(AIdExp node) {
		inAIdExp(node);
		assert (node.getId() != null);
		line = node.getId().getLine(); // Keep line number current
		exp = lookupVar(node.getId().getText()); // Lookup returns the IRT
		// fragment
		outAIdExp(node);
	}

	/**
	 * Build the IRT for the boolean true literal. This is really just the
	 * numeric literal 1.
	 */
	@Override
	public void caseATrueExp(ATrueExp node) {
		inATrueExp(node);
		exp = new CONST(1);
		outATrueExp(node);
	}

	/**
	 * Build the IRT for the boolean false literal. This is really just the
	 * numeric literal 0.
	 */
	@Override
	public void caseAFalseExp(AFalseExp node) {
		inAFalseExp(node);
		exp = new CONST(0);
		outAFalseExp(node);
	}

	/**
	 * Build the IRT for extracting the object reference ("this") from the stack.
	 * It's a memory reference to an address that's at an offset of 4 from the
	 * top of the stack.
	 */
	@Override
	public void caseAThisExp(AThisExp node) {
		inAThisExp(node);
		exp = new MEM(new BINOP(BINOP.PLUS, this.SP,new CONST(4)));
		outAThisExp(node);
	}

	/**
	 * Build IRT for array allocation. This involves building IRT for the size
	 * expression, and a call to malloc to get the appropriate number of bytes.
	 * Don't forget that we need one extra word for the length.
	 */
	@Override
	public void caseAAllocExp(AAllocExp node) {
		inAAllocExp(node);
		assert (node.getExp() != null);
		node.getExp().apply(this);
		//line = node.getExp().getLine();
		
		Reg reg = alloc.getReg();
		exp = new ESEQ(
				new SEQ(
					new COMMENT("Allocating array, near line " + line), 
					new SEQ(
						new MOVE(
							new REG(reg), 
							new CALL(
								new NAME(
									new Label("malloc")), 
								new ExpList(
									new BINOP(BINOP.MUL, 
										new BINOP(BINOP.PLUS,
											new CONST(Integer.parseInt(node.toString().replaceAll("\\s+", ""))), 
											new CONST(1)), 
										new CONST(4)), 
										null))), 
						new MOVE(
							new MEM(new REG(reg)), 
							new CONST(Integer.parseInt(node.toString().replaceAll("\\s+", "")))))), 
				new REG(reg));
		outAAllocExp(node);
	}

	/** 
	 * Build IRT for creating an instance of a class. We've already created these
	 * IRT fragments during the symbol table pass, so we just need to retrieve it
	 * here.
	 */
	@Override
	public void caseANewExp(ANewExp node) {
		inANewExp(node);
		if (node.getId() != null) {
			line = node.getId().getLine();
			// Find corresponding class in symbol table
			exp = table.get(node.getId().getText()).getIRTinfo().getInit(mips.MipsArch.SL);
		}
		outANewExp(node);
	}
}
