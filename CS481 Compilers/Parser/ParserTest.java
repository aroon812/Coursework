/**
 * This driver code will instantiate a lexer and parser from the SableCC-
 * generated classes, feed it some sample input, and display the resulting
 * parse tree.  The code is a lightly-modified version of TreeDumper.java
 * at http://www.natpryce.com/articles/000531.html
 */

// These imports are all for SableCC-generated packages.

import minijava.analysis.DepthFirstAdapter;
import minijava.lexer.Lexer;
import minijava.node.Node;
import minijava.node.Start;
import minijava.node.Token;
import minijava.parser.Parser;

// Now we import some general Java I/O classes

import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.io.InputStreamReader;

/**
 * We extend the DepthFirstAdapter built for us by SableCC, which is
 * basically the same as the base Visitor class you implemented
 * as part of your first assignment.  Visitors invoke a defaultCase 
 * method on each node they visit.  We override this method so that it
 * prints info about a node.
 */
public class ParserTest extends DepthFirstAdapter {
    /* Used to control indentation when printing node info */
    private int depth = 0;
    /* The output stream we're printing to */
    private PrintWriter out;

    /** Constructor takes a PrintWriter, and stores it. */
    public ParserTest(PrintWriter out) {
        this.out = out;
    }

    /** 
     * Override the default action taken at each visited node -- print
     * the node's text at the appropriate level of indentation. 
     */
    public void defaultCase(Node node) {
        indent();
        out.println(((Token)node).getText());
    }

    /** 
     * When descending into a new node, increment the indentation level.
     * Decrement the level when leaving a node. 
     *
     * @param node  The node from the parse tree that we're visiting.
     */
    public void defaultIn(Node node) {
        indent();
        printNodeName(node);
        out.println();

        depth = depth+1;
    }

    /** 
     * When leaving a node, decrement the indentation level.
     *
     * @param node  The node from the parse tree that we're leaving.
     */
    public void defaultOut(Node node) {
        depth = depth-1;
        out.flush();
    }

    /** 
     * A helper that trims a node's class name before printing it.
     * (E.g., "node.TAsmt" --> "TAsmt".) 
     *
     * @param node  The node to be printed.
     */
    private void printNodeName(Node node) {
        String fullName = node.getClass().getName();
        String name = fullName.substring(fullName.lastIndexOf('.')+1);

        out.print(name);
    }

    private void indent() {
        for (int i = 0; i < depth; i++) out.write("   ");
    }

    /**
     * The main method creates an instance of SableCC's Parser that's fed 
     * tokens by an instance of SableCC's Lexer.  The Lexer takes its input 
     * from a string passed as an argument on the command line.  If there is
     * no input, the lexer is given "a := 1+2" as a sample program.  You can 
     * uncomment the second line if you want to read programs from standard 
     * input instead of a string.
     */
    public static void main(String[] args) {
        String expr = (args.length == 0) ? "a := 1+2" : args[0];
        //Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(expr))));
        Parser parser = new Parser(new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024)));

        // Create a ParserTest object (a visitor), and apply it to the root 
        // of the tree
        try {
            Start start = parser.parse();
            start.getPProgram().apply(new ParserTest(new PrintWriter(System.out)));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
