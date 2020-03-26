package tree;

import arch.Label;
import arch.LabelList;

/**
 * JUMP statements unconditionally transfer control to an address in the
 * program. The first argument to the constructor is an expression describing
 * the address. The second is a list of all possible destinations for use in
 * dataflow analysis. (The one-argument constructor uses a specific label for
 * both.)
 */
public class JUMP extends Stm {
	public Exp exp;
	public LabelList targets;

	public JUMP(Exp e, LabelList t) {
		exp = e;
		targets = t;
	}

	public JUMP(Label target) {
		this(new NAME(target), new LabelList(target, null));
	}

	@Override
	public ExpList kids() {
		return new ExpList(exp, null);
	}

	@Override
	public Stm build(ExpList kids) {
		return new JUMP(kids.head, targets);
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
