package symtable;

import minijava.analysis.DepthFirstAdapter;
import minijava.node.ABaseClassDecl;
import minijava.node.AMainClassDecl;
import minijava.node.ASubClassDecl;

/** 
 * This visitor class builds a symbol table as it traverses the tree. The
 * table, an instance of ClassTable, can be returned via getTable().
 */
public class SymTableVisitor extends DepthFirstAdapter {
	private ClassTable table = new ClassTable();

	/** getTable returns the entire table */
	public ClassTable getTable() {
		return table;
	}

	/**
	 * A MainClassDecl can't have any instance vars or methods. All we need to
	 * know is its name. Pass null for the remaining args.
	 */
	@Override
	public void caseAMainClassDecl(AMainClassDecl node) {
		inAMainClassDecl(node);
		assert (node.getId() != null);
		try {
			// node.getId().setText("javamain");
			table.put(node.getId(), null, null, null);
		} catch (Exception e) {
			ReportException(e);
		}
		outAMainClassDecl(node);
	}

	/**
	 * A BaseClassDecl has local vars and methods (each of which could possibly
	 * be an empty list). We pass null for the name of the class being extended.
	 */
	@Override
	public void caseABaseClassDecl(ABaseClassDecl node) {
		inABaseClassDecl(node);
		assert (node.getId() != null);
		try {
			table.put(node.getId(), null, node.getVarDecl(), node.getMethod());
		} catch (Exception e) {
			ReportException(e);
		}
		;
		outABaseClassDecl(node);
	}

	/**
	 * A SubClassDecl has local vars, methods, and an the name of the class
	 * being extended. Pass 'em all to put.
	 */
	@Override
	public void caseASubClassDecl(ASubClassDecl node) {
		inASubClassDecl(node);
		assert (node.getId() != null);
		assert (node.getExtends() != null);
		try {
			table.put(node.getId(), node.getExtends(), node.getVarDecl(), node.getMethod());
		} catch (Exception e) {
			ReportException(e);
		}
		outASubClassDecl(node);
	}

	public void ReportException(Exception e) {
		System.out.println(e);
		System.exit(-1);
	}
}
