package tree;

/**
 * ESEQ expressions represent a "statement + expression" sequence like those in
 * the straight-line language from the book. The constructor takes a statement
 * and an expression, and it's understood that the statement is executed first,
 * then the expression is evaluated to produce the value of the overall
 * expression.
 */
public class ESEQ extends Exp {
	public Stm stm;
	public Exp exp;

	public ESEQ(Stm s, Exp e) {
		stm = s;
		exp = e;
	}

	@Override
	public ExpList kids() {
		throw new Error("kids() not applicable to ESEQ");
	}

	@Override
	public Exp build(ExpList kids) {
		throw new Error("build() not applicable to ESEQ");
	}
}
