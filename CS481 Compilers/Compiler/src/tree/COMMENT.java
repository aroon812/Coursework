package tree;

/**
 * A COMMENT statement allows us to build comments into the IRT. These nodes are
 * technically statements, but have no executable content.
 */
public class COMMENT extends Stm {
	public String text;

	public COMMENT(String s) {
		text = s;
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
