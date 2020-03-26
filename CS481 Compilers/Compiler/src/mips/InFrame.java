package mips;

import tree.BINOP;
import tree.CONST;
import tree.Exp;
import tree.MEM;

/**
 * The InFrame subclass of Access describes how to get at items on the MIPS
 * stack. If we're not given a base address, we just generate a constant
 * corresponding to the offset, otherwise we add the offset to a base address to
 * compute a new memory address.
 */
public class InFrame extends arch.Access {
	private int offset;

	public InFrame(int offset) {
		this.offset = offset;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		return "" + offset;
	}

	@Override
	public Exp getTree() {
		return new CONST(offset);
	}

	/*
	 * this is the most useful getTree because we can actually build the full
	 * memory expression
	 */
	@Override
	public Exp getTree(Exp base) {
		return new MEM(new BINOP(BINOP.PLUS, base, new CONST(offset)));
	}
}
