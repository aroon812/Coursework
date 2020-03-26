package tree;

import arch.Reg;

/**
 * A REG expression represents the value stored in a particular register.
 */
public class REG extends Exp {
	public Reg reg;

	public REG(Reg r) {
		reg = r;
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
