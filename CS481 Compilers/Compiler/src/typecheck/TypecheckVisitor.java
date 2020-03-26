//Aaron Thompson & Lukas Jimenez-Smith
package typecheck;

import java.util.LinkedList;

import minijava.analysis.DepthFirstAdapter;
import minijava.node.PExp;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import symtable.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import symtable.VarTable;
import minijava.node.*;

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


	/**
	 * Constructor takes a symbol table so we can look up types as necessary.
	 * @param symtab
	 */
	public TypecheckVisitor(ClassTable symtab) {
		table = symtab;
		currentMethod = null;
		currentClass = null;
	}

	/** true if t1 is the SAME as t2, or if t1 is a subclass of t2 */
	private boolean compatible(String t1, String t2) {
		HashMap<String, Integer> inheritanceChain = new HashMap<String, Integer>();
		ClassInfo info;
		Integer class1Val;
		Integer class2Val;
		String name;

		if (t1.equals(t2)){
			return true;
		}

		int counter = 0;
		info = table.get(t1);
	
		while (null != info){ 
			name = info.getName().getText(); 
			inheritanceChain.put(name, counter);
			if (null != info.getSuper()){
				info = table.get(info.getSuper().getText());
			}
			else{
				info = null;
			}
			counter++;
		}
		
		class1Val = inheritanceChain.get(t1);
		class2Val = inheritanceChain.get(t2);
		if (null != class1Val && null != class2Val){
			if (class1Val < class2Val){
				return true;
			}
		}
		return false;
	}

	/**
	 * Need to type the args, and match them to the types of the formals. If all
	 * goes well, return the method's return type as a string
	 */
	private String checkMethodInvocation(int line, MethodInfo mInfo,
			LinkedList<PExp> args) {
		Boolean correctArgs = true;
		ArrayList<String> argsList = new ArrayList<String>();
		LinkedList<PFormal> mArgs = mInfo.getFormals();

		for (PExp arg : args){
			arg.apply(this);
			String addition = typeof;
			argsList.add(addition);
		}

		if (mArgs.size() != args.size()){
			ReportException(new Exception("line " + line + ": Arguments " + argsList.toString() + " not applicable for " + mInfo.getName().getText()));
		}

		for (int i = 0; i < mArgs.size(); i++){
			AFormal curFormal = (AFormal) mArgs.get(i);
			String formalType = Types.toStr(curFormal.getType());
			
			if (!compatible(argsList.get(i), formalType)){
				correctArgs = false;
			}
		}
		
		if (!correctArgs){
			ReportException(new Exception("line " + line + ": Arguments " + argsList.toString() + " not applicable for " + mInfo.getName().getText()));
		}

		return Types.toStr(mInfo.getRetType());
	}

	/**
	 * Returns a variable's type as a string. Looks first in the locals,
	 * then fields, then parent classes if there are any.
	 * @param name  The variable's name.
	 * @return  Its type, as a string.
	 */
	private String lookupVar(String name) {
		VarTable locals = currentMethod.getLocals();
		if (locals.get(name) != null){			
			return Types.toStr(locals.get(name));
		}

		VarTable globals = currentClass.getVarTable();
		if (globals.get(name) != null){
			return Types.toStr(globals.get(name));
		}

		ClassInfo curClassInfo = currentClass;
		while (curClassInfo.getSuper() != null){
			curClassInfo = table.get(curClassInfo.getSuper().getText());
			VarTable parentVars = curClassInfo.getVarTable();
			if (parentVars.get(name) != null){
				return Types.toStr(parentVars.get(name));
			}
		}
		return null;
	}

	// ----- Override visitor methods from here down

	@Override
	public void caseAMainClassDecl(AMainClassDecl node) {
		String className;
		inAMainClassDecl(node);
		if (node.getId() != null) {
			node.getId().apply(this);
			className = node.getId().getText();
			currentClass = table.get(className);
		}
		if (node.getStmt() != null) {
			node.getStmt().apply(this);
		}
		outAMainClassDecl(node);
	}

	@Override
	public void caseABaseClassDecl(ABaseClassDecl node) {
		String className;
		
		inABaseClassDecl(node);
		if (node.getId() != null) {
			node.getId().apply(this);
			className = node.getId().getText();
			currentClass = table.get(className);
			line = node.getId().getLine();
		}
		{
			List<PVarDecl> copy = new ArrayList<PVarDecl>(node.getVarDecl());
			for (PVarDecl e : copy) {
				e.apply(this);
			}
		}
		{
			List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
			for (PMethod e : copy) {
				e.apply(this);
			}
		}
		outABaseClassDecl(node);
	}

	@Override
	public void caseASubClassDecl(ASubClassDecl node) {
		String className;
		String extendsName;
		inASubClassDecl(node);
		if (node.getId() != null) {
			node.getId().apply(this);
			className = node.getId().getText();
			currentClass = table.get(className);
			line = node.getId().getLine();
		}
		if (node.getExtends() != null) {
			node.getExtends().apply(this);
			extendsName = node.getExtends().getText();
			if (null == table.get(extendsName)){
				ReportException(new Exception("line " + line + ": Cannot find symbol: " + extendsName));
			}
		}
		{
			List<PVarDecl> copy = new ArrayList<PVarDecl>(node.getVarDecl());
			for (PVarDecl e : copy) {
				e.apply(this);
			}
		}
		{
			List<PMethod> copy = new ArrayList<PMethod>(node.getMethod());
			for (PMethod e : copy) {
				e.apply(this);
			}
		}
		outASubClassDecl(node);
	}

	@Override
	public void caseAMethod(AMethod node) {
		String methodName;

		inAMethod(node);
		if (node.getType() != null) {
			node.getType().apply(this);
		}
		if (node.getId() != null) {
			node.getId().apply(this);
			methodName = node.getId().getText();
			currentMethod = currentClass.getMethodTable().get(methodName);
			line = node.getId().getLine();
		}
		{
			List<PFormal> copy = new ArrayList<PFormal>(node.getFormal());
			for (PFormal e : copy) {
				e.apply(this);
			}
		}
		{
			List<PVarDecl> copy = new ArrayList<PVarDecl>(node.getVarDecl());
			for (PVarDecl e : copy) {
				e.apply(this);
			}
		}
		{
			List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
			for (PStmt e : copy) {
				e.apply(this);
			}
		}
		outAMethod(node);
	}

	@Override
	public void caseAFormal(AFormal node) {
		inAFormal(node);
		if (node.getType() != null) {
			node.getType().apply(this);
			if (!typeof.equals(Types.INT) && !typeof.equals(Types.BOOL) && !typeof.equals(Types.INTARRAY) && null == table.get(typeof)){
				ReportException(new Exception("line " + line + ": Cannot find symbol: " + typeof));
			}
		}
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		
		outAFormal(node);
	}

	@Override
	public void caseAIntType(AIntType node) {
		inAIntType(node);
		typeof = Types.INT;
		outAIntType(node);
	}

	@Override
	public void caseABoolType(ABoolType node) {
		inABoolType(node);
		typeof = Types.BOOL;
		outABoolType(node);
	}

	@Override
	public void caseAIntArrayType(AIntArrayType node) {
		inAIntArrayType(node);
		typeof = Types.INTARRAY;
		outAIntArrayType(node);
	}

	@Override
	public void caseAUserType(AUserType node) {
		inAUserType(node);
		if (node.getId() != null) {
			node.getId().apply(this);
			typeof = node.getId().getText();
		}
		outAUserType(node);
	}

	@Override
	public void caseAReturnStmt(AReturnStmt node) {
		String methodName = currentMethod.getName().getText();
		String retType = Types.toStr(currentMethod.getRetType());
		
		inAReturnStmt(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (!typeof.equals(retType)){
				ReportException(new Exception("line " + line + ": " + methodName + " must return a value of type " + retType));
			}
		}
		outAReturnStmt(node);
	}

	@Override
	public void caseAIfStmt(AIfStmt node) {
		inAIfStmt(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (!typeof.equals(Types.BOOL)){
				ReportException(new Exception("line " + line + ": If stmt must have BOOL condition!"));
			}		
		}
		
		if (node.getYes() != null) {
			node.getYes().apply(this);
		}
		if (node.getNo() != null) {
			node.getNo().apply(this);
		}
		outAIfStmt(node);
	}

	@Override
	public void caseAWhileStmt(AWhileStmt node) {
		inAWhileStmt(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (!typeof.equals(Types.BOOL)){
				ReportException(new Exception("line " + line + ": WHILE stmt must have BOOL condition!"));
			}		
		}
		
		if (node.getStmt() != null) {
			node.getStmt().apply(this);
		}
		outAWhileStmt(node);
	}

	@Override
	public void caseAPrintStmt(APrintStmt node) {
		inAPrintStmt(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (!typeof.equals(Types.INT)){
				ReportException(new Exception("line " + line + ": expressions in print statements must of type " + Types.INT + "!"));
			}	
		}
		
		outAPrintStmt(node);
	}

	@Override
	public void caseAVarDecl(AVarDecl node) {
		inAVarDecl(node);
		if (node.getType() != null) {
			node.getType().apply(this);
			if (!typeof.equals(Types.INT) && !typeof.equals(Types.BOOL) && !typeof.equals(Types.INTARRAY) && null == table.get(typeof)){
				ReportException(new Exception("line " + line + ": Cannot find symbol: " + typeof));
			}
		}
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		outAVarDecl(node);
	}

	@Override
	public void caseAAsmtStmt(AAsmtStmt node) {
		String name;
		String type;
		line = node.getId().getLine();
		inAAsmtStmt(node);
		if (node.getId() != null) {
			node.getId().apply(this);	
		}
		name = node.getId().getText();
		type = lookupVar(name);

		if (node.getExp() != null) {
			node.getExp().apply(this);
		}
		
		if (!type.equals(typeof)){
			if (!compatible(typeof, type)){
				ReportException(new Exception("line " + line + ": assignment statement must be of type " + typeof));
			}
		}
		outAAsmtStmt(node);
	}

	@Override
	public void caseAArrayAsmtStmt(AArrayAsmtStmt node) {
		line = node.getId().getLine();
		inAArrayAsmtStmt(node);	
		if (node.getId() != null) {
			if (null == lookupVar(node.getId().getText())){
				ReportException(new Exception("line " + line + ": Cannot find symbol: " + node.getId().getText()));
			}
			node.getId().apply(this);
		}
		if (node.getIdx() != null) {
			node.getIdx().apply(this);
			if (!typeof.equals(Types.INT)){
				ReportException(new Exception("line " + line + ": Array index must be of type " + Types.INT));
			}
		}
		if (node.getVal() != null) {
			node.getVal().apply(this);
			if (!typeof.equals(Types.INT)){
				ReportException(new Exception("line " + line + ": Element assignment for an int array must be of type " + Types.INT));
			}
		}
		outAArrayAsmtStmt(node);
	}

	@Override
	public void caseAAndExp(AAndExp node) {
		String leftType;
		String rightType;
		inAAndExp(node);
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		leftType = typeof;

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		rightType = typeof;
		if (rightType.equals(Types.BOOL) && leftType.equals(Types.BOOL)){
			typeof = Types.BOOL;
		}
		else{
			ReportException(new Exception("line " + line + ": Both sides must be of type BOOL"));
		}
		outAAndExp(node);
	}

	@Override
	public void caseALtExp(ALtExp node) {
		inALtExp(node);
		String leftType;
		String rightType;
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		leftType = typeof;

		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		rightType = typeof;
		if (rightType.equals(Types.INT) && leftType.equals(Types.INT)){
			typeof = Types.BOOL;
		}
		else{
			ReportException(new Exception("line " + line + ": Both sides must be of type INT"));
		}
		outALtExp(node);
	}

	@Override
	public void caseAPlusExp(APlusExp node) {
		inAPlusExp(node);
		String leftType;
		String rightType;
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		leftType = typeof;
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		rightType = typeof;
		if (rightType.equals(Types.INT) && leftType.equals(Types.INT)){
			typeof = Types.INT;
		}
		else{
			ReportException(new Exception("line " + line + ": Both sides must be of type INT"));
		}

		outAPlusExp(node);
	}

	@Override
	public void caseAMinusExp(AMinusExp node) {
		inAMinusExp(node);
		String leftType;
		String rightType;
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		leftType = typeof;
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		rightType = typeof;
		if (rightType.equals(Types.INT) && leftType.equals(Types.INT)){
			typeof = Types.INT;
		}
		else{
			ReportException(new Exception("line " + line + ": Both sides must be of type INT"));
		}
		outAMinusExp(node);
	}

	@Override
	public void caseATimesExp(ATimesExp node) {
		inATimesExp(node);
		String leftType;
		String rightType;
		if (node.getLeft() != null) {
			node.getLeft().apply(this);
		}
		leftType = typeof;
		if (node.getRight() != null) {
			node.getRight().apply(this);
		}
		rightType = typeof;
		if (rightType.equals(Types.INT) && leftType.equals(Types.INT)){
			typeof = Types.INT;
		}
		else{
			ReportException(new Exception("line " + line + ": Both sides must be of type INT"));
		}
		outATimesExp(node);
	}

	@Override
	public void caseANotExp(ANotExp node) {
		inANotExp(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
		}
		if (!typeof.equals(Types.BOOL)){
			ReportException(new Exception("line " + line + ": ! operator must be applied to a boolean expression"));
		}
		outANotExp(node);
	}

	@Override
	public void caseARefExp(ARefExp node) {
		inARefExp(node);
		if (node.getName() != null) {
			node.getName().apply(this);
		}
		if (node.getIdx() != null) {
			node.getIdx().apply(this);
		}
		outARefExp(node);
	}

	@Override
	public void caseALengthExp(ALengthExp node) {
		inALengthExp(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
		}
		if (!typeof.equals(Types.INTARRAY)){
			ReportException(new Exception("line " + line + ": .length can only be applied to an int array"));
		}
		typeof = Types.INT;
		outALengthExp(node);
	}

	@Override
	public void caseAMethodExp(AMethodExp node) {
		String name;
		String className = "";
		ClassInfo cInfo;
		MethodInfo mInfo;
		String retType = null;
		line = node.getId().getLine();
		
		inAMethodExp(node);
		if (node.getObj() != null) {
			node.getObj().apply(this);
		}
		if (node.getId() != null) {
			node.getId().apply(this);
		}
		name = node.getId().getText();
		className = typeof;
		cInfo = table.get(className);
		
		while (null != cInfo){ 
			cInfo = table.get(className);
			className = cInfo.getName().getText(); 
			mInfo = cInfo.getMethodTable().get(name);
			if (null != mInfo){
				retType = checkMethodInvocation(line, mInfo, node.getArgs());
				cInfo = null;
			}
			else{
				if (null != cInfo.getSuper()){
					cInfo = table.get(cInfo.getSuper().getText());
					className = cInfo.getName().getText();
				}
				else{
					cInfo = null;
				}
			}		
		}

		if (null == retType){
			ReportException(new Exception("line " + line + ": Cannot find symbol: " + node.getId().getText()));
		}
		
		{
			List<PExp> copy = new ArrayList<PExp>(node.getArgs());
			for (PExp e : copy) {
				e.apply(this);
			}
		}

		typeof = retType;
		outAMethodExp(node);
	}
	
	@Override
	public void caseANumExp(ANumExp node) {
		inANumExp(node);
		typeof = Types.INT;
		outANumExp(node);
	}
	
	@Override
	public void caseAIdExp(AIdExp node) {
		inAIdExp(node);
		if (node.getId() != null) {
			if (null == lookupVar(node.getId().getText())){
				ReportException(new Exception("line " + line + ": Cannot find symbol: " + node.getId().getText()));
			}
			node.getId().apply(this);
			typeof = lookupVar(node.getId().getText());
		}
		outAIdExp(node);
	}

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

	@Override
	public void caseAAllocExp(AAllocExp node) {
		inAAllocExp(node);
		if (node.getExp() != null) {
			node.getExp().apply(this);
			if (!typeof.equals(Types.INT)){
				ReportException(new Exception("line " + line + ": Array dimension must be an " + Types.INT));
			}
		}
		typeof = Types.INTARRAY;
		outAAllocExp(node);
	}

	@Override
	public void caseANewExp(ANewExp node) {
		String name;
		inANewExp(node);
		if (node.getId() != null) {
			node.getId().apply(this);
			name = node.getId().getText();
			if (null == table.get(name)){
				ReportException(new Exception("line " + line + ": cannot find symbol: " + name));
			}
			typeof = table.get(name).getName().getText();
		}
		outANewExp(node);
	}

	public void ReportException(Exception e) {
		System.out.println(e.getMessage());
		System.exit(-1);
	}
}
