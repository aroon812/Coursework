package typecheck;

import java.util.LinkedList;
import java.util.ListIterator;

import minijava.node.AFormal;
import minijava.node.PFormal;
import minijava.node.TId;
import symtable.ClassInfo;
import symtable.ClassTable;
import symtable.MethodInfo;
import symtable.Types;

/**
 * This class examines the symbol table to see if the user tried to override
 * methods. We look through the method names for each class in the symbol table.
 * For each method name, we look up the inheritance hierarchy (if one exists)
 * for methods in superclasses with the same name but different signatures. This
 * sounds like an O(N^2) sort of thing, but with the classes and methods being
 * held in hash tables it's nearly O(N) where N is the total number of methods
 * in the program.
 */
public class CheckOverload {
	/**
	 * Returns true if the methods described by the two MethodInfo records have
	 * identical signatures.
	 */
	public static boolean signaturesMatch(MethodInfo m1, MethodInfo m2) {
		// If the return types don't match, the signatures don't match
		if (!Types.sameType(m1.getRetType(), m2.getRetType())) {
			return false;
		}

		LinkedList<PFormal> formals1 = m1.getFormals();
		LinkedList<PFormal> formals2 = m2.getFormals();
		// If arg count differs, signatures don't match
		if (formals1.size() != formals2.size()) {
			return false;
		}
		// Now we'll loop through the args and make sure the actuals match the
		// type of the formals.
		ListIterator<PFormal> iter = formals2.listIterator(0);
		for (PFormal arg1 : formals1) {
			// If a pair of args differ, signatures don't match
			if (!Types.sameType(((AFormal) arg1).getType(),
					((AFormal) iter.next()).getType())) {
				return false;
			}
		}
		// If we survived the loop, args matched and signatures were same
		return true;
	}



	/**
	 * For each method in child, look for a method of the same name in parent.
	 * If we find a match and their signatures aren't identical, complain and
	 * exit. (If the signatures DO match, it's overriding, not overloading.)
	 * @param child Subclass being inspected
	 * @param parent Superclass being inspected
	 */
	public static void checkMethodOverloading(ClassInfo child, ClassInfo parent) {
		for (String name : child.getMethodTable().getMethodNames()) {
			if (parent.getMethodTable().get(name) != null) {
				if (!signaturesMatch(parent.getMethodTable().get(name), child
						.getMethodTable().get(name))) {
					TId subName = child.getMethodTable().get(name).getName();
					System.err.println("Attempt to overload method "
							+ subName.getText() + " on line "
							+ subName.getLine() + " (original def in class "
							+ parent.getName().getText() + ")");
					System.exit(-1);
				}
			}
		}
	}

	/**
	 * For each class in the table, see if its methods overload methods in its
	 * superclasses (if there are any). This top-level method loops through each
	 * class in the table, and pairs it up with each class in its inheritance
	 * hierarchy. Call checkMethodOverloading for each pair to do the check.
	 * @param table The symbol table
	 */
	public static void checkAll(ClassTable table) throws Exception {
		TId superClass;
		ClassInfo info, parent;
		for (String name : table.getClassNames()) {
			info = table.get(name); // info record for a class

			// Traverse inheritance chain, looking for clashes. See if we
			// clash with parent, then with grandparent, etc.

			superClass = info.getSuper(); // Start with TId of parent
			while (superClass != null) { // if superclass exists...
				parent = table.get(superClass.getText()); // Dig up super's info
				if (parent == null) {
					break;
				}
				checkMethodOverloading(info, parent); // see if there's a clash
				superClass = parent.getSuper(); // find super's parent, repeat
			}
		}
	}
}
