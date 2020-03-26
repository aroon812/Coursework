package symtable;

import java.util.LinkedList;

import minijava.node.PMethod;
import minijava.node.PVarDecl;
import minijava.node.TId;
import arch.Reg;

/**
 * ClassInfo stores information about a single class, including the name of its
 * superclass, info on its fields (in a VarTable), and information for all of
 * its methods (in a MethodTable). Initially we just store type information for
 * these items, though in later stages of the project the symbol table will also
 * store IRT fragments for accessing variables and creating instances.
 * 
 * @author Brad Richards
 */

public class ClassInfo {
	TId className; 		// TId holding our name, line number, etc.
	TId superClass; 	// Our superclass, if we have one
	VarTable vars; 		// A VarTable holding info on all instance vars
	MethodTable methods; // Table of info on methods

	// ====================================================================
	// Stuff added once we get to the IRT phase. The ClassIRTinfo object records
	// the total number of words required for the instance variables
	// (including those we inherit), and can generate IRT code to create
	// and initialize instances of this class. Since the IRTinfo object
	// records the number of words used, we can access that from subclasses.
	//
	ClassIRTinfo info;

	public ClassIRTinfo getIRTinfo() {
		return info;
	}

	public void setIRTinfo(ClassIRTinfo i) {
		info = i;
	}

	// ====================================================================

	/**
	 * The constructor takes all info associated with a subclass definition, but
	 * can be passed null for unused fields in the case of a base or main class
	 * declaration. Names are passed as TId rather than String so we can
	 * retrieve line number, etc, from the token if necessary.
	 * 
	 * @param className	 Token containing this Class's name
	 * @param superClass Token containing name of its superclass
	 * @param vars		 A list of instance variable declarations
	 * @param methods	 A list of method declarations
	 * @throws Exception If a class with this name already exists
	 */
	public ClassInfo(TId className, TId superClass, LinkedList<PVarDecl> vars,
			LinkedList<PMethod> methods) throws Exception {
		this.className = className;
		this.superClass = superClass;
		this.vars = new VarTable(vars); // Populate table from list
		this.methods = new MethodTable(methods); // Ditto.
	}

	/**
	 * @return Returns the class's name.
	 */
	public TId getName() {
		return className;
	}

	/**
	 * @return Returns the name of the superclass.
	 */
	public TId getSuper() {
		return superClass;
	}

	/**
	 * @return Returns the VarTable holding info on the instance variables.
	 */
	public VarTable getVarTable() {
		return vars;
	}

	/**
	 * @return Returns the table holding info on all of the methods in this class.
	 */
	public MethodTable getMethodTable() {
		return methods;
	}

	/**
	 * Should print the class name, the class it extends if there is one, then
	 * print the VarTable and MethodTable to display the names and their types.
	 */
	public void dump() {
		System.out.println("-------------------------------------");
		if (superClass == null) {
			System.out.println("Class: " + className.getText());
		} else {
			System.out.println("Class: " + className.getText() + "  Extends: "
					+ superClass.getText());
		}
		System.out.println("-------------------------------------");
		if (vars != null) {
			vars.dump();
		}
		if (methods != null) {
			methods.dump();
		}
		System.out.println("\n");
	}

	public void dumpIRT(boolean dot) {

		//TODO Fill in the guts of this method -- but not until the IRT checkpoint.

	}
}
