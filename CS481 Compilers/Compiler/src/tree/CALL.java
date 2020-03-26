package tree;

/**
 * Represents the invocation of a method. It's an Exp since the method may have
 * a return value. The constructor takes an expression describing the method to
 * be called, and a list of expressions corresponding to the arguments
 */
public class CALL extends Exp {
	public Exp func;
	public ExpList args;

	public CALL(Exp f, ExpList a) {
		func = f;
		args = a;
	}

	@Override
	public ExpList kids() {
		return new ExpList(func, args);
	}

	@Override
	public Exp build(ExpList kids) {
		return new CALL(kids.head, kids.tail);
	}

}
