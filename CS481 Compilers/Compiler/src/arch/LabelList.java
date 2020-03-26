package arch;

/**
 * A data structure holding a list of Labels.
 */
public class LabelList {
	public Label head;
	public LabelList tail;

	/**
	 * Create a LabelList from the specified head and tail.
	 * @param h  The label to appear at the head of the list
	 * @param t  A LabelList to use as the tail
	 */
	public LabelList(Label h, LabelList t) {
		head = h;
		tail = t;
	}
}
