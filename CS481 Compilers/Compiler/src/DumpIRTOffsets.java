import irt.BuildAccess;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PushbackReader;

import minijava.lexer.Lexer;
import minijava.node.Start;
import minijava.parser.Parser;
import symtable.ClassTable;
import symtable.SymTableVisitor;
import typecheck.CheckOverload;
import typecheck.TypecheckVisitor;
import typecheck.UseDefVisitor;

/* NOTE: This is a subset of what Compile already does. */

/**
 * This driver code is basically the same as the Typecheck driver: it
 * instantiates a lexer and parser from the SableCC-generated classes, feeds it
 * some sample input, then applies a SymTableVisitor to the tree to build a
 * symbol table. After the typechecking phase, we run BuildAccess on the symbol
 * table and dump the results.
 *
 * We extend the DepthFirstAdapter built for us by SableCC, which is basically
 * the same as the base Visitor class you implemented as part of your first
 * assignment. Visitors invoke a defaultCase method on each node they visit. We
 * override this method so that it prints info about a node.
 */
public class DumpIRTOffsets {
	/**
	 * The main method creates an instance of SableCC's Parser that's fed tokens
	 * by an instance of SableCC's Lexer. If there's a command-line argument it's
	 * used as a filename and the file's text is fed to the Lexer. Otherwise we
	 * look to System.in for the text to process. The parser produces an AST, 
	 * which is then traversed by the PrintVisitor.
	 * 
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {
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

		ClassTable table;
		try {
			// Ask our parser object to do its thing. Store the AST in start.
			Start start = parser.parse();

			// Retrieve the top-level Program node from start, and apply a
			// SymTableVisitor object to it to build the symbol table.
			start.getPProgram().apply(visitor);
			table = visitor.getTable();
			System.out.println("Symbol table built.");

			// Now look for semantic issues: Traverse the tree with three
			// different visitors to check for overloading, typecheck errors,
			// and use of uninitialized variables.
			CheckOverload.checkAll(table); 
			System.out.println("No overloading.");
			start.getPProgram().apply(new TypecheckVisitor(table));
			System.out.println("Passed typecheck.");
			start.getPProgram().apply(new UseDefVisitor(table));

			// Now, finally, build all of the IRT fragments and print them.
			BuildAccess.build(table);
			table.dumpIRT(false);
		} catch (Exception e) {
			System.err.println(e);
			System.exit(-1);
		}
	}
}
