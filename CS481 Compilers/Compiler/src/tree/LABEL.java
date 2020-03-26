package tree;

import arch.Label;

/**
 * A LABEL statement attaches a label to a particular point in the IRT program.
 * This label can then be used as the target for a jump or call.
 */
public class LABEL extends Stm {
	public Label label;

	public LABEL(Label l) {
		label = l;
	}

	@Override
	public ExpList kids() {
		return null;
	}

	@Override
	public Stm build(ExpList kids) {
		return this;
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
