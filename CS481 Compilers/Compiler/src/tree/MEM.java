package tree;

/**
 * A MEM expression represents the contents of a word of memory starting at the
 * specified address.
 */
public class MEM extends Exp {
	public Exp exp;

	public MEM(Exp e) {
		exp = e;
	}

	@Override
	public ExpList kids() {
		return new ExpList(exp, null);
	}

	@Override
	public Exp build(ExpList kids) {
		return new MEM(kids.head);
	}
}
