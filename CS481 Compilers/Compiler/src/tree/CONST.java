package tree;

/**
 * A CONST expression represents a signed integer constant.
 */
public class CONST extends Exp {
	public int value;

	public CONST(int v) {
		value = v;
	}

	@Override
	public ExpList kids() {
		return null;
	}

	@Override
	public Exp build(ExpList kids) {
		return this;
	}
}
