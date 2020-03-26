package irt;

import minijava.node.TId;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import symtable.MethodTable;
import symtable.VarTable;

/**
 * This class contains methods for generating IRT fragments to access variables
 * and construct and initialize instances of the methods in the Java program
 * being compiled.
 * 
 * @author Brad Richards
 */

public class BuildAccess {
	/*
	 * The offset into the stack frame (in words) at which the first parameter
	 * or local var should be placed. The current stack layout puts $ra at 0 and
	 * $gp at 1, so other data starts at 2.
	 */
	public static final int START_OFFSET = 2;

	/**
	 * Traverse the entire symbol table, visiting each class, method, and
	 * variable. Variables get annotated with an Access object that describes
	 * how to reference them, and ClassInfo records get an IRTinfo record that
	 * knows how to allocate and initialize instances.
	 * @param table      The ClassTable holding info all all classes in the program
	 * @throws Exception If we discover name clashes
	 */
	public static void build(ClassTable table) throws Exception {
		TId superClass;
		ClassInfo cInfo, parent;
		MethodTable methods;
		MethodInfo mInfo;
		int words;

		// Foreach class in the symbol table
		for (String cName : table.getClassNames()) {
			cInfo = table.get(cName); 		// info record for a class
			// TODO: Finish implementing the items below 

			// Add up total space for inherited instance vars

			// We know how much space has to be set aside for the inherited
			// instance vars. Now we need to create an IRTinfo instance that
			// knows about the *total* space (local+inherited instance vars),
			// and create Access entries for each of the instance vars. (Even
			// if this class overrides an inherited var, we still need to leave
			// *room* for the inherited var, so we add up all inherited vars.)

			// For each method, we need to generate a list of Access instances
			// for formal parameters and local variables.

		}
	}

	/**
	 * Add an Access object for each instance variable in the class. The
	 * accesses will always be from memory, not registers, and will use the
	 * appropriate offset from the start of an object descriptor.
	 * @param vars   A table of variables that need IRT accessors
	 * @param offset Offset within the instance where vars should start 
	 */
	private static void generateVarAccesses(VarTable vars, int offset) {

		// TODO: Finish implementing this method 
	}

	/**
	 * For each method, we need to generate a list of Access instances for
	 * formal parameters and local variables. We'll do the parameters first, in
	 * order. Next we'll loop through the co-mingled locals and parameters,
	 * adding entries for those that don't already have them.
	 * @param info  The table of methods that need processing
	 */
	private static void buildMethod(MethodInfo info) {
		// TODO: Finish implementing the items below 

		// Add Access annotations for the formal parameters

		// Now look through the whole table of vars (locals + formals)
		// and find homes for those that haven't got Access info.
	}
}
