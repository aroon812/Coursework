import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackReader;
import java.util.Stack;

//These imports are all for SableCC-generated packages.
import minijava.analysis.DepthFirstAdapter;
import minijava.lexer.Lexer;
import minijava.node.Node;
import minijava.node.Start;
import minijava.node.Token;
import minijava.parser.Parser;

/**
 * This visitor produces output that can be fed to dot, a graph-
 * drawing package.  (Dot is part of the GraphVis package.)  This
 * code is based off of code by Michelle Strout, which was in turn
 * based off of code by Brad Richards. We extend the DepthFirstAdapter 
 * built for us by SableCC so that it generates a dot-style graph 
 * description of the tree being traversed.
 */
public class PrintTreeDot extends DepthFirstAdapter {
	private int nodeCount = 0;
	private PrintWriter out;
	private Stack<Integer> nodeStack;

	/** Constructor takes a PrintWriter, and stores in instance var. */
	public PrintTreeDot(PrintWriter out) {
		this.out = out;
		this.nodeStack = new Stack<Integer>();
	}

	/**
	 * Override the defaultCase method that's invoked on each node visited. This
	 * new version generates an entry for dot that describes the nodes to which
	 * the current node is connected. This requires information about the node's
	 * parent, which is at the top of the node stack.
	 */
	@Override
	public void defaultCase(Node node) {
		nodeDotOutput(node);
	}

	/**
	 * Pushes a node number onto the stack for each node visited. It watches for
	 * the first node encountered and generates the "preamble" for dot as
	 * required.
	 */
	@Override
	public void defaultIn(Node node) {
		if (nodeStack.empty()) {
			out.println("digraph ParseGraph {");
		}

		// Generate a dot entry for this node
		nodeDotOutput(node);

		// Store "ourselves" on the stack so this node's children
		// can generate links from us. It's nodeCount-1 since the
		// output routine above already did an increment.
		nodeStack.push(nodeCount - 1);
	}

	/**
	 * Tidy up as we leave a node. Pop the stack, and watch for the case where
	 * we leave the very last node.
	 */
	@Override
	public void defaultOut(Node node) {
		nodeStack.pop();
		if (nodeStack.empty()) {
			out.println("}");
		}
		out.flush();
	}

	/**
	 * Generates an entry for dot that describes which node "points to" this
	 * one. This requires information about the node's parent, which is at the
	 * top of the node stack.
	 */
	private void nodeDotOutput(Node node) {
		// Label this node for dot
		out.print(nodeCount + " [ label=\"");
		printNodeName(node);
		if (node instanceof Token) {
			out.print("\\n");
			out.print(((Token) node).getText());
		}
		out.println("\" ];");

		// Generate info on link from parent to this node
		if (!nodeStack.empty()) {
			out.print(nodeStack.peek());
			out.print(" -> ");
			out.println(nodeCount);
		}

		// increment to account for this new node
		nodeCount++;
	}

	/**
	 * A helper that trims a node's class name before printing it. (E.g.,
	 * "Package.node.TAsmt" --> "TAsmt".)
	 */
	private void printNodeName(Node node) {
		String fullName = node.getClass().getName();
		String name = fullName.substring(fullName.lastIndexOf('.') + 1);

		out.print(name);
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
			start.getPProgram().apply(new PrintTreeDot(new PrintWriter(System.out)));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
