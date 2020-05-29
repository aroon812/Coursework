package irt;

import java.util.LinkedList;

import minijava.node.AFormal;
import minijava.node.PFormal;
import minijava.node.TId;
import symtable.ClassIRTinfo;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodIRTinfo;
import symtable.MethodInfo;
import symtable.MethodTable;
import symtable.VarInfo;
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
			// Add up total space for inherited instance vars
			words = 0;
			superClass = cInfo.getSuper(); 	// Start with TId of parent
			while (superClass != null) { 	// if superclass exists...
				parent = table.get(superClass.getText()); // Dig up super's info
				if (parent == null) {
					break;
				}
				if (parent.getVarTable() != null)
				{
					words += parent.getVarTable().size(); // Add in the vars
				}
				superClass = parent.getSuper(); 	// find super's parent, repeat
			}

			// We know how much space has to be set aside for the inherited
			// instance vars. Now we need to create an IRTinfo instance that
			// knows about the *total* space (local+inherited instance vars),
			// and create Access entries for each of the instance vars. (Even
			// if this class overrides an inherited var, we still need to leave
			// *room* for the inherited var, so we add up all inherited vars.)
			cInfo.setIRTinfo(new ClassIRTinfo(words + cInfo.getVarTable().size()));
			generateVarAccesses(cInfo.getVarTable(), words);

			// For each method, we need to generate a list of Access instances
			// for formal parameters and local variables.
			methods = cInfo.getMethodTable(); 	// Class' methods
			for (String m : methods.getMethodNames()) {
				mInfo = methods.get(m);
				buildMethod(mInfo);
			}

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
		if (vars == null) {
			return;
		}
		for (String name : vars.getVarNames()) {
			vars.getInfo(name).setAccess(new mips.InFrame(offset * 4));
			offset++;
		}
	}

	/**
	 * For each method, we need to generate a list of Access instances for
	 * formal parameters and local variables. We'll do the parameters first, in
	 * order. Next we'll loop through the co-mingled locals and parameters,
	 * adding entries for those that don't already have them.
	 * @param info  The table of methods that need processing
	 */
	private static void buildMethod(MethodInfo info) {
		int offset = START_OFFSET;
		LinkedList<PFormal> formals = info.getFormals();
		VarTable vars = info.getLocals();
		VarInfo vi;

		// Add Access annotations for the formal parameters
		if (formals != null) {
			for (PFormal pf : formals) {
				vi = vars.getInfo(((AFormal) pf).getId().getText());
				vi.setAccess(new mips.InFrame(offset * 4));
				offset++;
			}
		}

		// Now look through the whole table of vars (locals + formals)
		// and find homes for those that haven't got Access info.
		if (vars != null) {
			for (String name : vars.getVarNames()) {
				vi = vars.getInfo(name);
				if (vi.getAccess() == null) {
					vi.setAccess(new mips.InFrame(offset * 4));
					offset++;
				}
			}
		}

		// offset got incremented one too many times in the code above,
		// so we -1 to get it back. It also started at START_OFFSET, so
		// we subtract that off to get the word count for just the args
		// and locals in this method.
		int numFormals = formals == null ? 0 : formals.size();
		info.setInfo(new MethodIRTinfo(offset - START_OFFSET, numFormals));
	}
}
