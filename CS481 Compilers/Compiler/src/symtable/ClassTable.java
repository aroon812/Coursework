/**
 * These classes are used to represent the symbol table.  There are
 * classes for describing methods and classes (MethodInfo and ClassInfo),
 * and HashMap classes that hold info records for collections of
 * vars, methods and classes (VarTable, MethodTable, ClassTable).
 * Name clashes *within* a class are reported as the tables are built,
 * but no attempt is made to catch clashes across classes (as might
 * occur if subclasses try to override vars or methods in their
 * superclasses).
 */

package symtable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import minijava.node.PMethod;
import minijava.node.PVarDecl;
import minijava.node.TId;

/**
 * A ClassTable records information about a COLLECTION of class definitions.
 */
public class ClassTable {
	HashMap<String, ClassInfo> table = new HashMap<String, ClassInfo>();

	/**
	 * Add an entry for class "id" that extends "extendsId" and has instance
	 * variables "vars" and methods "methods". It could throw a
	 * ClassClashException if the class name is already in the table, or it
	 * could pass along Var or Method clash exceptions found while processing
	 * the linked lists.
	 */
	public void put(TId id, TId extendsId, LinkedList<PVarDecl> vars,
			LinkedList<PMethod> methods) throws Exception {
		String name = id.getText();
		if (table.containsKey(name)) {
			String msg = name + " redeclared on line " + id.getLine();
			throw new ClassClashException(msg); // There was a clash
		}
		table.put(name, new ClassInfo(id, extendsId, vars, methods));
	}

	public void putMain(String className, String methodName) throws Exception {

	}

	/** Lookup and return the ClassInfo record for the specified class */
	public ClassInfo get(String id) {
		return table.get(id);
	}

	/** Return all method names in the table */
	public Set<String> getClassNames() {
		return table.keySet();
	}

	/** dump prints info on each of the classes in the table */
	public void dump() {
		for (String s : table.keySet()) {
			table.get(s).dump();
		}
	}

	/**
	 * dump prints info on each of the classes in the table and displays IRT
	 * info as well.
	 * 
	 * @param b
	 */
	public void dumpIRT(boolean dot) {
		for (String s : table.keySet()) {
			table.get(s).dumpIRT(dot);
		}
	}
}
