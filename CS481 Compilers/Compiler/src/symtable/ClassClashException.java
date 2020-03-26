package symtable;

/**
 * Defines a custom Exception we can throw if we discover two classes
 * with identical names in a program.
 * 
 * @author Brad Richards
 */

public class ClassClashException extends java.lang.Exception {
	public ClassClashException(String msg) {
		super(msg);
	}
}
