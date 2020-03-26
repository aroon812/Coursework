package typecheck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Set;

import minijava.analysis.DepthFirstAdapter;
import minijava.node.AFormal;
import minijava.node.PFormal;
import minijava.node.PVarDecl;
import symtable.ClassTable;

/** 
 * This class traverses a MiniJava Abstract Syntax Tree and looks for 
 * unused variables, and variables that are used before being initialized.
 */
public class UseDefVisitor extends DepthFirstAdapter {
	ClassTable table; // The symbol table, created in previous pass
	InitTable inits; // Ptr to current table of initialized vars
	ArrayList<PVarDecl> currentLocals; // Used to detect non-local vars

	public UseDefVisitor(ClassTable tab) {
		table = tab;
	}

	// TODO: Need to override some visitor methods here to implement the logic
}



enum Status {
	Maybe, Yes
};

/**
 * Records initialization info on a collection of variables. A variable name is
 * mapped to a Status value -- "Maybe" (possibly initialized), or "Yes"
 * (definitely initialized). No such thing as a "No" entry. Uninitialized vars
 * don't have entries in the table.
 */
class InitTable {
	Hashtable<String, Status> table;

	/** no-arg constructor builds a blank table */
	public InitTable() {
		table = new Hashtable<String, Status>();
	}

	/** Build a new table that's a clone of an existing table */
	public InitTable(InitTable old) {
		table = new Hashtable<String, Status>(old.table);
	}

	/** Add entries for formals, indicating they're initialized */
	public InitTable(LinkedList<PFormal> formals) {
		table = new Hashtable<String, Status>();
		for (PFormal f : formals) {
			table.put(((AFormal) f).getId().getText(), Status.Yes);
		}
	}

	/**
	 * Add a new entry if there wasn't one before, or if our new information is
	 * more precise than the old.
	 */
	public void put(String var, Status info) {
		Status old = table.get(var);
		if ((old == null) || (old.ordinal() < info.ordinal())) {
			table.put(var, info);
		}
	}

	/** Look up the status info on a variable */
	public Status get(String var) {
		return table.get(var);
	}

	/** Remove the entry for the specified var */
	public void remove(String var) {
		table.remove(var);
	}

	/** List all the vars in the table */
	public Set<String> getVarNames() {
		return table.keySet();
	}

	/**
	 * Merge info from a table of vars definitely or possibly initialized by the
	 * body of a while loop. If the new table has an entry for a var and we have
	 * none, we should add a Maybe (assuming that entries will be Yes or Maybe
	 * in the incoming table). If we already have an entry it's either Maybe (in
	 * which case no action need be taken), or Yes (in which case no action
	 * SHOULD be taken). Our put() method is smart enough to add entries only if
	 * they're "better" than the existing info, so it doesn't hurt to do a put()
	 * for each var in the new table.
	 */
	public void mergeWhile(InitTable newInfo) {
		if (newInfo != null) {
			for (String name : newInfo.getVarNames()) {
				put(name, Status.Maybe);
			}
		}
	}

	/**
	 * Merge info from both paths of a conditional. If the existing table
	 * already has a Yes for a given entry, skip it -- we won't learn anything
	 * new. Otherwise, if both input tables have a Yes, add a Yes to the main
	 * table. If only one branch has an entry for a given var, add a Maybe to
	 * the main table, regardless of the value along the branch. (If it's Yes,
	 * we add Maybe since the path may not be taken. If it's Maybe, we similarly
	 * add Maybe).
	 */
	public void mergeIf(InitTable t, InitTable f) {
		if (t != null && f != null) {
			// Loop through set of keys from table t. Remove corresponding
			// entries from table f as we go. After this loop, entries in f
			// must only have appeared in f.
			for (String var : t.getVarNames()) {
				// Examine values along t,f, decide on addition
				if (t.get(var) != f.get(var)) {
					// Maybe,
					put(var, Status.Maybe);
				} else if ((t.get(var) == Status.Yes)
						&& (f.get(var) == Status.Yes))
				{
					put(var, Status.Yes); // Add Yes if t,f both say so
				}
				f.remove(var); // Remove from f so we don't reconsider
			}
			// Now look through any remaining entries in f. We know they
			// don't even appear in t's table, so any entry here ==> Maybe.
			for (String var : f.getVarNames()) {
				put(var, Status.Maybe);
			}
		}
	}
}
