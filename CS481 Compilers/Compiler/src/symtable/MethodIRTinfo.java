package symtable;

/**
 * For each method, we keep the total number of words required to store its
 * arguments and local variables.
 */
public class MethodIRTinfo {
	public int frameSize; // In words, includes args
	public int numArgs;

	/**
	 * Build a MethodIRTinfo object for a method that requires a total
	 * <tt>frameSize</tt> words of storage for its parameters and local
	 * variables.
	 */
	public MethodIRTinfo(int frameSize, int numArgs) {
		this.frameSize = frameSize;
		this.numArgs = numArgs;
	}

}
