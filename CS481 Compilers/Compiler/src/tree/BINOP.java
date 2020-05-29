package tree;

/**
 * The BINOP expression represents all of the binary operators in our IRT
 * language: +, -, *, /, &, |, shifts, and xor. It defines constants for each of
 * these specific operators.
 */
public class BINOP extends Exp {
	public int binop;
	public Exp left, right;

	public BINOP(int b, Exp l, Exp r) {
		binop = b;
		left = l;
		right = r;
	}

	public final static int PLUS = 0, MINUS = 1, MUL = 2, DIV = 3, AND = 4,
			OR = 5, LSHIFT = 6, RSHIFT = 7, ARSHIFT = 8, XOR = 9,
			SLT = 10;	// Added in 2020

	@Override
	public ExpList kids() {
		return new ExpList(left, new ExpList(right, null));
	}

	@Override
	public Exp build(ExpList kids) {
		return new BINOP(binop, kids.head, kids.tail.head);
	}
}
