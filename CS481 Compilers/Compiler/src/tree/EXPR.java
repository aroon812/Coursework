package tree;

/**
 * EXPR nodes make "statements out of expressions". It allows us to evaluate an
 * expression (presumably for its side effects), and discard the result.
 */
public class EXPR extends Stm {
	public Exp exp;

	public EXPR(Exp e) {
		exp = e;
	}

	@Override
	public ExpList kids() {
		return new ExpList(exp, null);
	}

	@Override
	public Stm build(ExpList kids) {
		return new EXPR(kids.head);
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
