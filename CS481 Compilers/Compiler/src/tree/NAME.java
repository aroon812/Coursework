package tree;

import arch.Label;

/**
 * a NAME expression represents a pre-existing label in our IRT program. It can
 * be used to specify the destination for jumps or calls.
 */
public class NAME extends Exp {
	public Label label;

	public NAME(Label l) {
		label = l;
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
