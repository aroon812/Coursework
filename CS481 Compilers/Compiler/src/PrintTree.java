import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackReader;

import minijava.analysis.DepthFirstAdapter;
import minijava.lexer.Lexer;
import minijava.node.Node;
import minijava.node.Start;
import minijava.node.Token;
import minijava.parser.Parser;

/**
 * This driver code will instantiate a lexer and parser from the SableCC-
 * generated classes, parse input from System.in, and display the tree built by
 * the parser. If the parser is generating ASTs the AST will be printed,
 * otherwise the parse tree is printed.
 *
 * We extend the DepthFirstAdapter built for us by SableCC, which is basically
 * the same as the base Visitor class you implemented as part of your first
 * assignment. Visitors invoke a defaultCase method on each node they visit. We
 * override this method so that it prints info about a node.
 */
public class PrintTree extends DepthFirstAdapter {
	private int depth = 0;
	private PrintWriter out;

	/** Constructor takes a PrintWriter, and stores in instance var. */
	public PrintTree(PrintWriter out) {
		this.out = out;
	}

	/**
	 * Override the default action taken at each visited node -- print the
	 * node's text at the appropriate level of indentation.
	 */
	@Override
	public void defaultCase(Node node) {
		indent();
		out.println(((Token) node).getText());
	}

	/**
	 * When descending into a new node, increment the indentation level.
	 * Decrement the level when leaving a node.
	 */
	@Override
	public void defaultIn(Node node) {
		indent();
		printNodeName(node);
		out.println();

		depth = depth + 1;
	}

	@Override
	public void defaultOut(Node node) {
		depth = depth - 1;
		out.flush();
	}

	/**
	 * A helper that trims a node's class name before printing it. (E.g.,
	 * "node.TAsmt" --> "TAsmt".)
	 */
	private void printNodeName(Node node) {
		String fullName = node.getClass().getName();
		String name = fullName.substring(fullName.lastIndexOf('.') + 1);

		out.print(name);
	}

	private void indent() {
		for (int i = 0; i < depth; i++) {
			out.write("   ");
		}
	}

	/**
	 * The main method creates an instance of SableCC's Parser that's fed tokens
	 * by an instance of SableCC's Lexer. The Lexer takes its input from a file
	 * if a filename is given on the command line, otherwise it reads from
	 * System.in.
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

		// Create a PrintTree object, and apply it to the root of the tree
		try {
			Start start = parser.parse();
			start.getPProgram().apply(new PrintTree(new PrintWriter(System.out)));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
