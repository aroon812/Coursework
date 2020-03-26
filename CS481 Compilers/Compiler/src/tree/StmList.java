package tree;

/**
 * A data structure holding a list of Stms.
 */
public class StmList {
	public Stm head;
	public StmList tail;

	public StmList(Stm h, StmList t) {
		head = h;
		tail = t;
	}
}
