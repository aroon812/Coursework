package tree;

/**
 * A SEQ statement is the composition of two IRT statements.
 */
public class SEQ extends Stm {
	public Stm left, right;

	public SEQ(Stm l, Stm r) {
		left = l;
		right = r;
	}

	@Override
	public ExpList kids() {
		throw new Error("kids() not applicable to SEQ");
	}

	@Override
	public Stm build(ExpList kids) {
		throw new Error("build() not applicable to SEQ");
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
