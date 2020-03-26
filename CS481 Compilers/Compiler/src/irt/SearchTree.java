package irt;

import tree.BINOP;
import tree.CALL;
import tree.CJUMP;
import tree.ESEQ;
import tree.EXPR;
import tree.Exp;
import tree.ExpList;
import tree.JUMP;
import tree.MEM;
import tree.MOVE;
import tree.RETURN;
import tree.SEQ;
import tree.Stm;

/**
 * This class contains methods to traverse an IRT tree and search for specific
 * kinds of nodes.
 * 
 * @author Brad Richards
 */
public class SearchTree {
	static String node;
	static String call = new CALL(null, null).getClass().getName();


	/**
	 * Takes a CALL node and searches it to see if any of its arguments contain
	 * CALL nodes as well. 
	 * @param e  An IRT CALL node
	 * @return   True if the call contains nested CALL nodes
	 */
	public static boolean containsCALL(CALL e) {
		node = call;
		for (ExpList a = e.args; a != null; a = a.tail) {
			if (traverseExp(a.head)) {
				return true;
			}
		}
		return false;
	}


	/**
	 * Search an IRT expression for a node of a particular type.
	 * @param tree   The IRT tree to search
	 * @param target The kind of node we're looking for
	 * @return       True if the tree contains a node of the specified type
	 */
	public static boolean findInExp(Exp tree, Object target) {
		node = target.getClass().getName();
		return traverseExp(tree);
	}

	/**
	 * Search an IRT statement for a node of a particular type.
	 * @param tree   The IRT tree to search
	 * @param target The kind of node we're looking for
	 * @return       True if the tree contains a node of the specified type
	 */
	public static boolean findInStm(Stm tree, Object target) {
		node = target.getClass().getName();
		return traverseStm(tree);
	}

	private static boolean same(Object o) {
		return node.equals(o.getClass().getName());
	}

	/**
	 * All of the methods below have to do with the traversal.
	 */

	private static boolean traverseStm(SEQ s) {
		return (traverseStm(s.left) || traverseStm(s.right));
	}

	private static boolean traverseStm(CJUMP s) {
		return (traverseExp(s.left) || traverseExp(s.right));
	}

	private static boolean traverseStm(MOVE s) {
		return (traverseExp(s.dst) || traverseExp(s.src));
	}

	private static boolean traverseStm(EXPR s) {
		return traverseExp(s.exp);
	}

	private static boolean traverseStm(RETURN s) {
		return traverseExp(s.ret);
	}

	private static boolean traverseStm(JUMP s) {
		return traverseExp(s.exp);
	}

	private static boolean traverseStm(Stm s) {
		if (same(s)) {
			return true;
		}

		if (s instanceof SEQ) {
			return traverseStm((SEQ) s);
		} else if (s instanceof CJUMP) {
			return traverseStm((CJUMP) s);
		} else if (s instanceof MOVE) {
			return traverseStm((MOVE) s);
		} else if (s instanceof EXPR) {
			return traverseStm((EXPR) s);
		} else if (s instanceof RETURN) {
			return traverseStm((RETURN) s);
		} else if (s instanceof JUMP) {
			return traverseStm((JUMP) s);
		}
		else {
			return false;
			// Nothing in a LABEL or COMMENT
		}
	}

	private static boolean traverseExp(BINOP e) {
		return (traverseExp(e.left) || traverseExp(e.right));
	}

	private static boolean traverseExp(MEM e) {
		return traverseExp(e.exp);
	}

	private static boolean traverseExp(ESEQ e) {
		return (traverseStm(e.stm) || traverseExp(e.exp));
	}

	private static boolean traverseExp(CALL e) {
		for (ExpList a = e.args; a != null; a = a.tail) {
			if (traverseExp(a.head)) {
				return true;
			}
		}
		return false;
	}

	private static boolean traverseExp(Exp e) {
		if (same(e)) {
			return true;
		}

		if (e instanceof BINOP) {
			return traverseExp((BINOP) e);
		} else if (e instanceof MEM) {
			return traverseExp((MEM) e);
		} else if (e instanceof ESEQ) {
			return traverseExp((ESEQ) e);
		} else if (e instanceof CALL) {
			return traverseExp((CALL) e);
		}
		else {
			return false;
			// Nothing in a NAME, CONST, or REG
		}
	}
}
