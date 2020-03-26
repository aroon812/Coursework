package tree;

/**
 * A MOVE statement is essentially an assignment statement. The first expression
 * represents the destination address (or register), and the second is the
 * expression whose value should be stored there.
 */
public class MOVE extends Stm {
	public Exp dst, src;

	public MOVE(Exp d, Exp s) {
		dst = d;
		src = s;
	}

	@Override
	public ExpList kids() {
		if (dst instanceof MEM) {
			return new ExpList(((MEM) dst).exp, new ExpList(src, null));
		} else {
			return new ExpList(src, null);
		}
	}

	@Override
	public Stm build(ExpList kids) {
		if (dst instanceof MEM) {
			return new MOVE(new MEM(kids.head), kids.tail.head);
		} else {
			return new MOVE(dst, kids.head);
		}
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
