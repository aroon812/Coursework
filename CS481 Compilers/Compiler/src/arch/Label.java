package arch;

/**
 * A Label represents an assembly-language address.
 */
public class Label {
	protected String name;
	protected static int count;

	/**
	 * a printable representation of the label, for use in assembly language
	 * output.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Makes a new label that prints as "name". Warning: avoid using the
	 * same label more than once in your IRT or it'll result in broken
	 * assembly code.
	 */
	public Label(String n) {
		name = n;
	}

	/**
	 * Makes a new label with an arbitrary name.
	 */
	public Label() {
		this("L" + count++);
	}

}
