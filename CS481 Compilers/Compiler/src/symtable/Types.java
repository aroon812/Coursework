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
	 * Returns a String representing the input type.
	 */
	public static String toString(PType type) {
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
	 * Two types are the same if they produce the same String as output.
	 */
	public static boolean sameType(PType t1, PType t2) {
		return (toString(t1).equals(toString(t2)));
	}
}
