package irt;

import java.util.HashSet;

import mips.MipsArch;
import tree.BINOP;
import tree.CALL;
import tree.CJUMP;
import tree.ESEQ;
import tree.EXPR;
import tree.Exp;
import tree.ExpList;
import tree.MEM;
import tree.MOVE;
import tree.REG;
import tree.RETURN;
import tree.SEQ;
import tree.Stm;

/**
 * This class contains methods to traverse an IRT tree and collect the
 * names of any registers that have been hard-coded into the IRT.
 * 
 * @author Brad Richards
 */

public class FindReg {
	private static HashSet<String> registers;	// Holds registers we find

	/**
	 * Search the IRT tree, add any registers we encounter to the set, and
	 * return the set once we're finished.
	 */
	public static HashSet<String> find(Object tree) {
		registers = new HashSet<String>();
		if (tree instanceof Stm) {
			traverseStm((Stm) tree);
		} else {
			traverseExp((Exp) tree);
		}
		return registers;
	}

	/**
	 * All of the methods below have to do with the traversal.
	 */

	private static void traverseStm(SEQ s) {
		traverseStm(s.left);
		traverseStm(s.right);
	}

	private static void traverseStm(CJUMP s) {
		traverseExp(s.left);
		traverseExp(s.right);
	}

	private static void traverseStm(MOVE s) {
		traverseExp(s.dst);
		traverseExp(s.src);
	}

	private static void traverseStm(EXPR s) {
		traverseExp(s.exp);
	}

	private static void traverseStm(RETURN s) {
		traverseExp(s.ret);
	}

	private static void traverseStm(Stm s) {
		if (s instanceof SEQ) {
			traverseStm((SEQ) s);
		} else if (s instanceof CJUMP) {
			traverseStm((CJUMP) s);
		} else if (s instanceof MOVE) {
			traverseStm((MOVE) s);
		} else if (s instanceof EXPR) {
			traverseStm((EXPR) s);
		} else if (s instanceof RETURN)
		{
			traverseStm((RETURN) s);
			// If it's a LABEL, COMMENT, or JUMP, nothing to do
		}
	}

	private static void traverseExp(BINOP e) {
		traverseExp(e.left);
		traverseExp(e.right);
	}

	private static void traverseExp(MEM e) {
		traverseExp(e.exp);
	}

	private static void traverseExp(REG e) {
		registers.add(e.reg.toString());
	}

	private static void traverseExp(ESEQ e) {
		traverseStm(e.stm);
		traverseExp(e.exp);
	}

	private static void traverseExp(CALL e) {
		for (ExpList a = e.args; a != null; a = a.tail) {
			traverseExp(a.head);
		}
		registers.add(MipsArch.V0.toString());	// CALL nodes also use $v0
	}

	private static void traverseExp(Exp e) {
		if (e instanceof BINOP) {
			traverseExp((BINOP) e);
		} else if (e instanceof MEM) {
			traverseExp((MEM) e);
		} else if (e instanceof REG) {
			traverseExp((REG) e);
		} else if (e instanceof ESEQ) {
			traverseExp((ESEQ) e);
		} else if (e instanceof CALL)
		{
			traverseExp((CALL) e);
			// NAME, CONST, can't hide a Reg
		}
	}
}
