package symtable;

import minijava.node.ABoolType;
import minijava.node.AIntArrayType;
import minijava.node.AIntType;
import minijava.node.AUserType;
import minijava.node.PType;

public class Types {
	public static final String BOOL = "boolean";
	public static final String INT = "int";
	public static final String INTARRAY = "int[]";

	/**
	 * Converts a type descriptor from our AST to its string representation.
	 * @param type  A PType node from the AST
	 * @return  The corresponding type string
	 */
	public static String toStr(PType type) {
		if (type instanceof ABoolType) {
			return BOOL;
		} else if (type instanceof AIntType) {
			return INT;
		} else if (type instanceof AIntArrayType) {
			return INTARRAY;
		} else if (type instanceof AUserType) {
			return ((AUserType) type).getId().getText();
		} else {
			return "unknown";
		}
	}
 
	/**
	 * Returns true if the two type descriptors have the same type.  It
	 * converts both to their string representations and compares the
	 * two strings.
	 * @param t1  The first type descriptor
	 * @param t2  The second type descriptor
	 * @return  True if the types are the same, false otherwise.
	 */
	public static boolean sameType(PType t1, PType t2) {
		return (toStr(t1).equals(toStr(t2)));
	}
}
