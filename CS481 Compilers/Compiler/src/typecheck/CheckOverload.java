//Aaron Thompson & Lukas Jimenez-Smith
package typecheck;

import java.util.LinkedList;
import java.util.ArrayList;
import minijava.node.PFormal;
import minijava.node.AFormal;
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

		for (int i = 0; i < formals1.size(); i++){
			AFormal af1 = (AFormal) formals1.get(i);
			AFormal af2 = (AFormal) formals2.get(i);
			boolean same = Types.sameType(af1.getType(), af2.getType());
			if (!same){
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
	 * @oparam table The symbol table
	 */
	public static void checkAll(ClassTable table) throws Exception {
		ClassInfo info, parent; 
		ArrayList<String> inheritanceChain = new ArrayList<String>(); 

		for (String name : table.getClassNames()) { 
			info = table.get(name); 
			parent = info; 

			while (null != parent && null != parent.getSuper()){
				String parentName = parent.getSuper().getText(); 
				inheritanceChain.add(parentName); 
				parent = table.get(parentName); 		
			}

			for (String iName : inheritanceChain){ 
				ClassInfo iInfo = table.get(iName); 
				checkMethodOverloading(info, iInfo);			
			}
		}
	}
}
