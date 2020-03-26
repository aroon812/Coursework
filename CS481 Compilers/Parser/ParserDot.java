/**
 * This visitor produces output that can be fed to dot, a graph-
 * drawing package.  (Dot is part of the graphviz package.)  This
 * code is based off of code by Michelle Strout, which was in turn
 * based off of code by Brad Richards.
 */

import java.io.PrintWriter;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Stack;

//These imports are all for SableCC-generated packages.
import minijava.analysis.DepthFirstAdapter;
import minijava.lexer.Lexer;
import minijava.node.Node;
import minijava.node.Start;
import minijava.node.Token;
import minijava.parser.Parser;

/**
 * We extend the DepthFirstAdapter built for us by SableCC so that it
 * generates a dot-style graph description of the tree being traversed.
 */
public class ParserDot extends DepthFirstAdapter {
    private int nodeCount = 0;
    private PrintWriter out;
    private Stack<Integer> nodeStack;

    /** Constructor takes a PrintWriter, and stores in instance var. */
    public ParserDot(PrintWriter out) {
        this.out = out;
        this.nodeStack = new Stack<Integer>();
    }

    /** 
     * Override the defaultCase method that's invoked on each node visited.  
     * This new version generates an entry for dot that describes the nodes 
     * to which the current node is connected.  This requires information
     * about the node's parent, which is at the top of the node stack.
     */
    public void defaultCase(Node node) {
        nodeDotOutput(node);
    }

    /** 
     * Pushes a node number onto the stack for each node visited.  It 
     * watches for the first node encountered and generates the "preamble"
     * for dot as required.
     *
     * @param node  The node being visited
     */
    public void defaultIn(Node node) {
        if (nodeStack.empty()) {
            out.println("digraph ParseGraph {");
        }

        // Generate a dot entry for this node
        nodeDotOutput(node);

        // Store "ourselves" on the stack so this node's children
        // can generate links from us.  It's nodeCount-1 since the
        // output routine above already did an increment.
        nodeStack.push(nodeCount-1);   
    }

    /**
     * Tidy up as we leave a node.  Pop the stack, and watch for the case
     * where we leave the very last node.
     *
     * @param node  The node we're done visiting
     */
    public void defaultOut(Node node) {
        nodeStack.pop();
        if (nodeStack.empty()) {
            out.println("}");
        }
        out.flush();
    }

    /**
     * Generates an entry for dot that describes which node "points to" this one. 
     * This requires information about the node's parent, which is at the top of 
     * the node stack.
     *
     * @param node  The node being visited
     */
    private void nodeDotOutput(Node node)
    {
        // Label this node for dot
        out.print(nodeCount+" [ label=\"");
        printNodeName(node);
        if (node instanceof Token) {
            out.print("\\n");
            out.print(((Token)node).getText());
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
     * A helper that trims a node's class name before printing it.
     * (E.g., "Package.node.TAsmt" --> "TAsmt".) 
     *
     * @param node  The node being printed
     */
    private void printNodeName(Node node) {
        String fullName = node.getClass().getName();
        String name = fullName.substring(fullName.lastIndexOf('.')+1);

        out.print(name);
    }

    /**
     * The main method creates a parser object that receives its input from a lexer
     * object, and applies an instance of this class to the top-level node in the
     * parse tree.  By default, it expects its input as a command-line argument (a
     * string).  If no input is found, it parses a fixed sample program.  You can read
     * from standard input if you uncomment the alternate Parser creation line.  Note
     * that this code assumes that the top-level nonterminal in your program is called
     * Stm.
     */
    public static void main(String[] args) {
        String expr = (args.length == 0) ? 
                "a := 5 + 3; \n b := a + 10 + c + 1; \n print (b, 5, 2+1)" : 
            args[0];
        Parser parser = new Parser(new Lexer(new PushbackReader(new StringReader(expr))));
        //    Parser parser = new Parser(new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024)));

        // Create a ParserTest object, and apply it to the root of the tree
        try {
            Start start = parser.parse();
            start.getPProgram().apply(new ParserDot(new PrintWriter(System.out)));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}
