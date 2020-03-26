package tree;

/**
 * This was added to the collection of IRT nodes so that we could build fully
 * executable programs in IRT. It's used to identify the returned expression so
 * that the CALL knows where to find its final value.
 */
public class RETURN extends Stm {
	public Exp ret;

	public RETURN(Exp r) {
		ret = r;
	}

	@Override
	public ExpList kids() {
		return new ExpList(ret, null);
	}

	@Override
	public Stm build(ExpList kids) {
		return new RETURN(kids.head);
	}

	public Stm link = null;

	@Override
	public Stm next() {
		return link;
	}
}
