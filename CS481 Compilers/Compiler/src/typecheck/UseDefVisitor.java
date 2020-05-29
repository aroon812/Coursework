package typecheck;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import minijava.analysis.DepthFirstAdapter;
import minijava.node.AArrayAsmtStmt;
import minijava.node.AAsmtStmt;
import minijava.node.AFormal;
import minijava.node.AIdExp;
import minijava.node.AIfStmt;
import minijava.node.AMethod;
import minijava.node.ARefExp;
import minijava.node.AVarDecl;
import minijava.node.AWhileStmt;
import minijava.node.PFormal;
import minijava.node.PStmt;
import minijava.node.PVarDecl;
import minijava.node.TId;
import symtable.ClassTable;

/** 
 * This class traverses a MiniJava Abstract Syntax Tree and looks for 
 * unused variables, and variables that are used before being initialized.
 */
public class UseDefVisitor extends DepthFirstAdapter {
	ClassTable table; 	// The symbol table, created in previous pass
	InitTable inits; 	// Ptr to current table of initialized vars
	ArrayList<PVarDecl> currentLocals; // Used to detect non-local vars

	public UseDefVisitor(ClassTable tab) {
		table = tab;
	}

	/**
	 * A variable is inherited if it's not in the list of currentLocals. We know
	 * it must be one or the other, or the typecheck would've failed.
	 */
	private boolean inherited(String var) {
		// It's inherited if it's not a local
		for (PVarDecl e : currentLocals) {
			String name = ((AVarDecl) e).getId().getText();
			if (name.equals(var)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Lookup a var in the current table, and complain if we're using it before
	 * initialization.
	 */
	private void useCheck(TId var) {
		assert (inits != null);
		if (inits.get(var.getText()) == null) {
			// No entry is an error unless we inherited this var
			if (inherited(var.getText())) {
				System.err.println("Nonlocal var " + var.getText()
				+ " may have been used on line " + var.getLine()
				+ " without being initialized");
			} else {
				System.err.println("Variable " + var.getText()
				+ " was used without being initialized on line "
				+ var.getLine());
				System.exit(-1);
			}
		} else if (inits.get(var.getText()) == Status.Maybe) {
			System.err.println("Variable " + var.getText()
			+ " was possibly used without being initialized on line "
			+ var.getLine());
		}
	}

	@Override
	public void caseAMethod(AMethod node) {
		inAMethod(node);
		assert ((node.getType() != null) && (node.getId() != null));
		// Create init table, add formals to table as initialized
		// Ignore locals -- they'll have entries added if we init them
		currentLocals = new ArrayList<PVarDecl>(node.getVarDecl());
		inits = new InitTable(node.getFormal());

		// Look through statements and note initializations; look for
		// uses of uninitialized vars.
		List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
		for (PStmt e : copy) {
			e.apply(this);
		}

		// Now we go back through the locals, and see if there are any
		// that never get written.
		List<PVarDecl> vars = currentLocals;
		for (PVarDecl e : vars) {
			String name = ((AVarDecl) e).getId().getText();
			if (inits.get(name) == null) {
				System.err.println("Warning: " + name + " unused on line "
						+ ((AVarDecl) e).getId().getLine());
			}
		}
		outAMethod(node);
	}

	/**
	 * Hide current table. Create a new table and traverse true path, then
	 * create another and traverse the false path. Merge information from the
	 * two paths (yes+yes=yes, _+maybe=maybe) and add entries to existing table,
	 * then put updated table back in place.
	 */
	@Override
	public void caseAIfStmt(AIfStmt node) {
		inAIfStmt(node);
		assert ((node.getExp() != null) && (node.getYes() != null) && (node
				.getNo() != null));
		// Visit exp to look for uses of uninitialized vars
		node.getExp().apply(this);

		InitTable old = inits; // Hang on to old table
		inits = new InitTable(old); // Start a fresh one, do TRUE branch
		node.getYes().apply(this);
		InitTable yes = inits; // Don't lose the TRUE branch info
		inits = new InitTable(old); // Start a fresh one, do FALSE branch
		node.getNo().apply(this);
		old.mergeIf(yes, inits); // Incorporate info into old table
		inits = old; // Restore old table
		outAIfStmt(node);
	}

	@Override
	public void caseAWhileStmt(AWhileStmt node) {
		inAWhileStmt(node);
		assert ((node.getExp() != null) && (node.getStmt() != null));
		// Visit exp to look for uses of uninitialized vars
		node.getExp().apply(this);
		InitTable old = inits; // Hang on to original table
		inits = new InitTable(old); // Make a fresh one for body
		node.getStmt().apply(this); // Process body
		old.mergeWhile(inits); // Incorporate info into main table
		inits = old; // Restore main table
		outAWhileStmt(node);
	}

	/**
	 * Here's where we catch assignments to vars. Note that we don't do anything
	 * special with arrays: "array = new int[10];" will cause the entire array
	 * to become initialized. This parallels what javac does.
	 */
	@Override
	public void caseAAsmtStmt(AAsmtStmt node) {
		inAAsmtStmt(node);
		assert ((node.getId() != null) && (node.getExp() != null));
		node.getExp().apply(this); // Look at RHS before adding Yes!
		inits.put(node.getId().getText(), Status.Yes);
		outAAsmtStmt(node);
	}

	/**
	 * The AsmtStmt case above will set entire array's name to Yes once it's
	 * initialized. We won't try to do finer-grained analysis, so this case only
	 * checks uses and doesn't ADD to the table.
	 */
	@Override
	public void caseAArrayAsmtStmt(AArrayAsmtStmt node) {
		inAArrayAsmtStmt(node);
		assert ((node.getId() != null) && (node.getIdx() != null) && (node
				.getVal() != null));
		node.getIdx().apply(this); // Visit idx and RHS, but don't modify
		node.getVal().apply(this); // table -- this isn't considered an init.
		outAArrayAsmtStmt(node);
	}

	// Exps from here on down.

	/**
	 * Arrays are treated as a whole. No need to look at *which* item is being
	 * used -- look up "entire array" by its name. Need to watch for issues with
	 * the index expression too.
	 */
	@Override
	public void caseARefExp(ARefExp node) {
		inARefExp(node);
		if (node.getName() != null) {
			if (node.getName() instanceof AIdExp)
			{
				useCheck(((AIdExp) (node.getName())).getId()); // Check on array
				// name
			}
		}
		if (node.getIdx() != null) {
			node.getIdx().apply(this);
		}
		outARefExp(node);
	}

	@Override
	public void caseAIdExp(AIdExp node) {
		inAIdExp(node);
		if (node.getId() != null) {
			useCheck(node.getId()); // Check var name
			node.getId().apply(this);
		}
		outAIdExp(node);
	}
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
