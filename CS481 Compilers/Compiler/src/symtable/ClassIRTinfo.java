package symtable;

import tree.BINOP;
import tree.CALL;
import tree.CONST;
import tree.ESEQ;
import tree.Exp;
import tree.ExpList;
import tree.MEM;
import tree.MOVE;
import tree.NAME;
import tree.REG;
import tree.SEQ;
import tree.Stm;
import arch.Label;
import arch.Reg;

/**
 * For each class, we need to keep track of the total amount of space required
 * to store its instance variables (including those it inherits). In addition to
 * recording the number of words required, this class includes a method that can
 * produce IRT code to allocate and initialize memory for an instance. (Since we
 * don't have static instance variables, we initialize every instance var in the
 * object when it's created.)
 * 
 * @author Brad Richards
 */
public class ClassIRTinfo {
	private int words;

	/**
	 * Builds an IRTinfo object for a class.
	 * @param words # of words of storage used for this class's instance vars,
	 *  including those inherited from parent classes.
	 */
	public ClassIRTinfo(int words) {
		this.words = words;
	}

	/**
	 * Creates an IRT expression that serves as a constructor for instances of
	 * this class. The IRT allocates the required number of bytes, stores a 0
	 * in each word of memory, and leaves the address of the allocated block of
	 * memory (the object reference) in the specified register.
	 * @param dest The register that should hold the object reference
	 * @return The IRT for this class's constuctor
	 */
	//public Exp getInit(Reg dest) {
		// TODO: Finish this method
	//}

	/**
	 * Returns the number of 
	 * @return
	 */
	public int getWords() {
		return words;
	}
/*
	public static void test(int n) {
		symtable.ClassIRTinfo i = new symtable.ClassIRTinfo(n);
		tree.Print.prExp(i.getInit(new Reg()));
		tree.PrintDot.printExp(i.getInit(new Reg()));
	}
}
*/
}
