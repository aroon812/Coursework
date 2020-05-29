package tree;

/**
 * A data structure for representing lists of Exps.
 */
public class ExpList {
	public Exp head;
	public ExpList tail;

	public ExpList(Exp h, ExpList t) {
		head = h;
		tail = t;
	}
}


