import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import minijava.lexer.Lexer;
import minijava.node.Start;
import minijava.parser.Parser;
import symtable.SymTableVisitor;

/**
 * This driver code will instantiate a lexer and parser from the SableCC-
 * generated classes, feed them some sample input, then apply a
 * SymTableVisitor to the tree to build a symbol table.  After the
 * table's built, it dumps the table so we can see what's in it.
 * 
 * @author Brad Richards
 */
public class BuildTable {

	/**
	 * The main method creates an instance of SableCC's Parser that's fed tokens
	 * by an instance of SableCC's Lexer.  (The Lexer takes its input from 
	 * System.in, unless a filename is passed as a command-line argument.)  The 
	 * parser produces an AST, which is then traversed by the PrintVisitor.
	 */
	public static void main(String[] args) {
		Parser parser = null;
		if (args.length > 0) {
			try {
				parser = new Parser(new Lexer(new PushbackReader(
						new InputStreamReader(
								new FileInputStream(new File(args[0]))))));
			} catch (FileNotFoundException e) {
				System.err.println("Wasn't able to read from file "+args[0]);
				System.err.println(e);
				System.exit(-1);
			}
		} else {
			parser = new Parser(new Lexer(new PushbackReader(
					new InputStreamReader(System.in), 1024)));
		}

		SymTableVisitor visitor = new SymTableVisitor();

		try {
			// Ask our parser to do its thing.  Store the AST's root in start.
			Start start = parser.parse();

			// Retrieve the top-level Program node from start, and apply 
			// our symbol table visitor to it.
			start.getPProgram().apply(visitor);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		// The visitor created a symbol table for us, and that table has a
		// dump() method to display its contents.  Time to see what we got...
		visitor.getTable().dump();
	}
}
