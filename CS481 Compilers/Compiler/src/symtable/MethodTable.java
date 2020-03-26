package symtable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import minijava.node.AMethod;
import minijava.node.PFormal;
import minijava.node.PMethod;
import minijava.node.PType;
import minijava.node.PVarDecl;
import minijava.node.TId;

/**
 * This class maintains information on a COLLECTION of methods. It maps method
 * names to MethodInfo records.
 */
public class MethodTable {
	HashMap<String, MethodInfo> table = new HashMap<String, MethodInfo>();

	/**
	 * The constructor is passed a list of methods as would be built by the
	 * parser. It adds entries for each method in the list via the local put()
	 * method.
	 */
	public MethodTable(LinkedList<PMethod> methods) throws Exception {
		if (methods == null) {
			return;
		}
		for (PMethod pm : methods) {
			AMethod m = (AMethod) pm;
			this.put(m.getId(), m.getType(), m.getFormal(), m.getVarDecl());
		}
	}

	/**
	 * Add an entry to the table, with the method name as key and the
	 * appropriate MethodInfo structure as value. If the method name already
	 * appears in the table, throw a MethodClashException. We might also
	 * encounter a VarClashException while building the MethodInfo structure, so
	 * either could be thrown by put().
	 */
	public void put(TId id, PType retType, LinkedList<PFormal> formals,
			LinkedList<PVarDecl> locals) throws Exception {
		String name = id.getText();
		if (table.containsKey(name)) { // Method with that name is in table
			String msg = name + " redeclared on line " + id.getLine();
			throw new MethodClashException(msg);
		}

		table.put(name, new MethodInfo(retType, id, formals, locals));
	}

	/** Lookup and return the MethodInfo for the specified method */
	public MethodInfo get(String name) {
		return table.get(name);
	}

	/** Return all method names in the table */
	public Set<String> getMethodNames() {
		return table.keySet();
	}

	/**
	 * Print out info on all methods in the table. Don't forget that MethodInfo
	 * structures already know how to dump themselves.
	 */
	public void dump() {
		if (table == null) {
			return;
		}
		for (String name : table.keySet()) {
			System.out.print("\n" + name + " ");
			table.get(name).dump();
		}

	}

	public void dumpIRT(boolean dot) {
		//TODO: Fill in the guts of this method.
	}
}
