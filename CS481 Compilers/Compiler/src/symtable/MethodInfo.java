package symtable;

import java.util.LinkedList;

import minijava.node.AFormal;
import minijava.node.PFormal;
import minijava.node.PType;
import minijava.node.PVarDecl;
import minijava.node.TId;

// Needed for IRT phase

/** 
 * A MethodInfo instance records information about a single MiniJava method.
 * It contains references to the method's return type, formal parameters, and
 * its local variables, in addition to the method's name.  
 * @author Brad Richards
 */
public class MethodInfo {
	// ClassInfo parent;
	PType retType;
	TId name;
	LinkedList<PFormal> formals;
	VarTable locals; 			// Contains entries for parameters as well

	// ====================================================================
	// Stuff used once we get the the IRT phase
	//
	MethodIRTinfo info;

	public MethodIRTinfo getInfo() {
		return info;
	}

	public void setInfo(MethodIRTinfo i) {
		info = i;
	}

	// ====================================================================

	/** 
	 * The constructor stores away references to the return type and formals,
	 * and builds a VarTable containing both the local variables and the 
	 * formals.  If variable name clashes are found (within locals, formals,
	 * or across locals and formals) we throw a VarClashException.
	 * @param retType  The method's return type
	 * @param name     The method's name (a TId, not a String)
	 * @param formals  A list of the method's formal variables (params)
	 * @param locals   A list of the method's local variables
	 */
	public MethodInfo(PType retType, TId name, LinkedList<PFormal> formals,
			LinkedList<PVarDecl> locals) throws VarClashException {

		this.retType = retType;
		this.name = name;
		this.formals = formals;
		this.locals = new VarTable(locals); // Put local vars in table

		// Now add formals to the table of local variable declarations.
		// This may cause a VarClashException, but we'll just pass it on.
		// (The exception will say that the *formal* is the redeclaration)
		for (PFormal p : formals) {
			this.locals.put(((AFormal) p).getId(), ((AFormal) p).getType());
		}
	}

	public TId getName() {
		return name;
	}

	public PType getRetType() {
		return retType;
	}

	public LinkedList<PFormal> getFormals() {
		return formals;
	}

	public VarTable getLocals() {
		return locals;
	}

	/**
	 * Print info about the return type, formals, and local variables. It's OK
	 * if the formals appear in the local table as well. In fact, it's a *good*
	 * thing since dump will help us debug later if necessary, and we'll want to
	 * see exactly what's in the VarTable.
	 */
	public void dump() {
		System.out.print("( ");
		if (formals != null) {
			for (PFormal pf : formals) {
				AFormal arg = (AFormal) pf;
				System.out.print(arg.getId().getText() + ":"
						+ Types.toString(arg.getType()) + " ");
			}
		}
		System.out.println(") : " + Types.toString(retType));
		if (locals != null) {
			locals.dump();
		}
	}

	/**
	 * Like dump(), but displays the IRT fragments for accessing local vars
	 * as well.
	 * 
	 * @param dot True if we want output that could be fed to the dot utility.
	 */
	public void dumpIRT(boolean dot) {
		System.out.print("( ");
		if (formals != null) {
			for (PFormal pf : formals) {
				AFormal arg = (AFormal) pf;
				System.out.print(arg.getId().getText() + ":"
						+ Types.toString(arg.getType()) + " ");
			}
		}
		System.out.println(") : " + Types.toString(retType));
		System.out.println("Accessors for parameters and locals:");
		if (locals != null) {
			locals.dumpIRT(dot);
		}
	}
}
