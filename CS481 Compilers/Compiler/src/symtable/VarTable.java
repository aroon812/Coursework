package symtable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import minijava.node.AVarDecl;
import minijava.node.PType;
import minijava.node.PVarDecl;
import minijava.node.TId;

/**
 * A VarTable records information about a COLLECTION of variables and their
 * types. An exception is thrown if we try to add a duplicate name.
 */
public class VarTable {
	HashMap<String, VarInfo> table = new HashMap<String, VarInfo>();

	/** Constructor populates table from an initial list of VarDecls. */
	public VarTable(LinkedList<PVarDecl> vars) throws VarClashException {
		if (vars == null) {
			return;
		}
		for (PVarDecl decl : vars) {
			AVarDecl var = (AVarDecl) decl;
			this.put(var.getId(), var.getType());
		}
	}

	/** Allow the option of adding individual entries as well. */
	public void put(TId id, PType type) throws VarClashException {
		String name = id.getText();
		if (table.containsKey(name)) {
			String msg = name + " redeclared on line " + id.getLine();
			throw new VarClashException(msg); // There was a clash
		}
		table.put(name, new VarInfo(type)); // No clash; add new binding
	}

	/** Lookup and return the type of a variable */
	public PType get(String name) {
		if (table.get(name) == null) {
			return null;
		} else {
			return table.get(name).getType();
		}
	}

	/** Lookup and return a variable's whole VarInfo record */
	public VarInfo getInfo(String name) {
		return table.get(name);
	}

	/** Return all var names in the table */
	public Set<String> getVarNames() {
		return table.keySet();
	}

	public int size() {
		return table.size();
	}

	/** Print out the entire contents of the table */
	public void dump() {
		if (table == null) {
			return;
		}
		for (String name : table.keySet()) {
			System.out.println("  " + name + " : " + table.get(name));
		}
	}

	public void dumpIRT(boolean dot) {
		if (table == null) {
			return;
		}
		tree.REG base = new tree.REG(new arch.Reg("$base"));
		for (String name : table.keySet()) {
			System.out.println("  " + name + " : " + table.get(name));
			if (dot) {
				tree.PrintDot.printExp(table.get(name).getAccess()
						.getTree(base));
			} else {
				tree.Print.prExp(table.get(name).getAccess().getTree(base), 4);
			}
			System.out.println("\n");
		}
	}
}
