package codegen;

import java.io.PrintWriter;

import symtable.ClassTable;
import tree.Stm;

/**
 * The Codegen class traverses an IRT and produces equivalent MIPS assembly
 * code. It sends the MIPS output to the PrintWriter passed to the constructor.
 */
/**
 * 
 * @author Brad Richards
 */
public class Codegen {

	/**
	 * Constructor takes an output stream, and a reference to the ClassTable
	 * holding type information for the entire program.
	 * 
	 * @param out    PrintWriter to which MIPS output is sent
	 * @param symtab Symbol table containing info on the program
	 */
	public Codegen(PrintWriter out, ClassTable symtab) {

	}

	/**
	 * Translate starts the code-generation process. 
	 * 
	 * @param s  The root of the program tree
	 */
	public void translate(Stm s) {

	}

}
