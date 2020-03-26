import irt.BuildAccess;
import irt.IRTVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.PushbackReader;

import minijava.lexer.Lexer;
import minijava.node.Start;
import minijava.parser.Parser;
import simulator.Sim;
import symtable.ClassTable;
import symtable.SymTableVisitor;
import typecheck.CheckOverload;
import typecheck.TypecheckVisitor;
import typecheck.UseDefVisitor;
import codegen.Codegen;

/**
 * We extend the DepthFirstAdapter built for us by SableCC, which is basically
 * the same as the base Visitor class you implemented as part of your first
 * assignment. Visitors invoke a defaultCase method on each node they visit. We
 * override this method so that it prints info about a node.
 */
public class Compile {

	/**
	 * The main method creates an instance of SableCC's Parser that's fed tokens by
	 * an instance of SableCC's Lexer. (The Lexer takes its input from System.in.)
	 * The parser produces an AST, which is then traversed by the PrintVisitor.
	 * 
	 * Accepts the following command-line arguments:
	 * 
	 * -v Print version and usage information 
	 * -tp Print textual representation of AST and stop 
	 * -td Generate DOT output for the AST and stop 
	 * -ps Print symbol table contents and stop 
	 * -fp Print IRT accessor and constructor fragments as text and stop 
	 * -fd Generate DOT output for IRT accessor and constructor fragments 
	 * -ip Print entire IRT tree 
	 * -id Generate DOT output for entire IRT tree 
	 * -sim Create full IRT tree and run it in simulator 
	 * 
	 */
	public static void main(String[] args) {
		Parser parser = null; //new Parser(new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024)));
		String command = "";

		// Legal usage is "compile -arg" or "compile -arg filename". Complain if there
		// too few or too many args, or if the args have the wrong format.
		if (args.length == 0 || args.length > 2 || !args[0].startsWith("-") || args[0].equals("-v")) {
			System.out.println("Usage: compile -arg [filename]");
			System.out.println("Valid command-line arguments are:");
			System.out.println("-v\tPrint version and usage information");
			System.out.println("-tp\tPrint textual representation of AST and stop");
			System.out.println("-td\tGenerate DOT output for the AST and stop");
			System.out.println("-ps\tPrint symbol table contents and stop");
			System.out.println("-fp\tPrint IRT accessor and constructor fragments as text and stop");
			System.out.println("-fd\tGenerate DOT output for IRT accessor and constructor fragments");
			System.out.println("-ip\tPrint entire IRT tree");
			System.out.println("-id\tGenerate DOT output for entire IRT tree");
			System.out.println("-sim\tFeed IRT to the simulator");
			System.out.println("-loudsim\tFeed IRT to the simulator, with verbose output");
			System.out.println("-asm\tGenerate MIPS assembly code");
			System.exit(-1);
		}  

		// Set up the Lexer and Parser. If we get this far we've got 1 or 2 args and the first 
		// starts with "-". If there are two args we'll assume the second one is a filename, 
		// otherwise we read from system.in.
		if (args.length == 2) {
			try {
				parser = new Parser(new Lexer(new PushbackReader(
						new InputStreamReader(new FileInputStream(new File(args[1]))))));
			} catch (FileNotFoundException e) {
				System.err.println("Wasn't able to read from file "+args[1]);
				System.err.println(e);
				System.exit(-1);
			}
		} else {
			parser = new Parser(new Lexer(new PushbackReader(new InputStreamReader(System.in), 1024)));
		}

		// Now see what the arg told us to do
		switch (args[0]) {
		case "-tp":
		case "-td":
		case "-ps":
		case "-fp":
		case "-fd":
		case "-ip":
		case "-id":
		case "-sim":
		case "-loudsim":
		case "-asm":
			command = args[0];
			break;
		default:
			System.err.println("Unknown command-line argument: " + args[0]);
			System.err.println("Use -v for usage information.");
			System.exit(-1);
		}

		// If we get this far we've got a legal argument and valid source of input.
		// Time to start compiling! The code below looks like it ought to be in a
		// switch statement, but the options aren't independent. If they want -sim,
		// for example, we've still got to parse, typecheck, build IRT, etc. We just
		// exit out at various stages depending on the command.

		SymTableVisitor visitor = new SymTableVisitor();

		ClassTable table;
		try {
			// Ask our parser object to do its thing. Store the AST in start.
			Start start = parser.parse();

			// Retrieve the top-level Program node from start, and apply a
			// PrintVisitor object to it.
			if (command.equals("-tp")) {
				start.getPProgram().apply(new PrintTree(new PrintWriter(System.out)));
				System.exit(0);
			}
			if (command.equals("-td")) {
				start.getPProgram().apply(new PrintTreeDot(new PrintWriter(System.out)));
				System.exit(0);
			}

			// See if we should print the table and then stop
			start.getPProgram().apply(visitor);
			table = visitor.getTable();
			if (command.equals("-ps")) {
				table.dump();
				System.exit(0);
			}

			CheckOverload.checkAll(table); // See about overloading errors
			// System.err.println("No overloading.");
			start.getPProgram().apply(new TypecheckVisitor(table));
			// System.err.println("Passed typecheck.");
			start.getPProgram().apply(new UseDefVisitor(table));
			BuildAccess.build(table); // Generate IRT fragments

			// Now see if the user was just interested in the accessors
			if (command.equals("-fp")) {
				table.dumpIRT(false);
				System.exit(0);
			}
			if (command.equals("-fd")) {
				table.dumpIRT(true);
				System.exit(0);
			}

			IRTVisitor IRTgen = new IRTVisitor(table);
			start.getPProgram().apply(IRTgen);
			//new Print().prStm(IRTgen.getProgram());
			//System.err.println(FindReg.find(IRTgen.getProgram()));

			if (command.equals("-id")) {
				tree.PrintDot.printStm(IRTgen.getProgram());
				System.exit(0);
			}
			if (command.equals("-ip")) {
				tree.Print.prStm(IRTgen.getProgram());
				System.exit(0);
			}
			if (command.equals("-sim")) {
				//tree.Print.prStm(IRTgen.getProgram());
				Sim s = new Sim(IRTgen.getProgram()); // Create simulator around our IRT
				s.runProgram(false); // Run the program, without debugging output
				System.exit(0);
			}
			if (command.equals("-loudsim")) {
				//tree.Print.prStm(IRTgen.getProgram());
				Sim s = new Sim(IRTgen.getProgram()); // Create simulator around our IRT
				s.runProgram(true); // Run the program, with debugging output
				System.exit(0);
			}
			if (command.equals("-asm")) {
				System.out.println(header);
				new Codegen(new PrintWriter(System.out), table).translate(IRTgen.getProgram());
				System.exit(0);
			}
			// If we get this far something weird happened. We didn't find an option, or
			// we would've exited after handling it.
			System.err.println("ERROR: Still alive after "+args[0]);
		} catch (AssertionError e) {
			System.err.println("Assertion failed:"+e);
			System.exit(-1);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static String header =  "#\n" +
			"# MIPS assembly header for MiniJava code.  Defines the \"built-in\"\n" +
			"# functions, and does initialization for the memory allocator.  It\n" +
			"# then jumps to a procedure named \"main\" (so there had better be one),\n" +
			"# and exits once main returns.\n" +
			"#\n" +
			"        .data\n" +
			"        .align 2\n" +
			"_mem:   .space 102400       # 100K of heap space\n" +
			"_next:  .word 0\n" +
			"_nl:    .asciiz \"\\n\"\n" +
			"_exit:  .asciiz \"Exited with value \"\n" +
			"\n" +
			"        .text\n" +
			"\n" +
			"        la   $s0, _mem \n" +
			"        la   $s1, _next  \n" +
			"        sw   $s0, 0($s1)    # Set up ptr to start of malloc pool\n" +
			"        jal  main           # Jump to start of Minijava program\n" +
			"        nop\n" +
			"        li    $v0 10\n" +
			"        syscall             # syscall 10 (exit)  \n" +
			"        \n" +
			"#\n" +
			"# Implements the \"built-in\" print.  It expects a single integer\n" +
			"# argument to be passed in $a0.\n" +
			"#\n" +
			"print:  \n" +
			"        li   $v0, 1         # Specify the \"print int\" syscall\n" +
			"        syscall             # Arg is in $a0, so just do call\n" +
			"        la   $a0, _nl       # Load addr of \\n string\n" +
			"        li   $v0, 4         # Specify the \"print string\" syscall\n" +
			"        syscall             # Print the string\n" +
			"        jr   $ra        \n" +
			"        \n" +
			"#\n" +
			"# Implements the \"built-in\" exit.  It expects a single integer\n" +
			"# argument to be passed in $a0.\n" +
			"#\n" +
			"exit:  \n" +
			"        move $s0, $a0       # Store the integer arg \n" +
			"        la   $a0, _exit     # Load addr of \"exit\" string\n" +
			"        li   $v0, 4         # Specify the \"print string\" syscall\n" +
			"        syscall             # Print the string\n" +
			"        move $a0, $s0       # Set up the integer arg for printing\n" +
			"        li   $v0, 1         # Specify the \"print int\" syscall\n" +
			"        syscall             # Print the integer\n" +
			"        la   $a0, _nl       # Load addr of \\n string\n" +
			"        li   $v0, 4         # Specify the \"print string\" syscall\n" +
			"        syscall             # Print the \\n\n" +
			"        li    $v0 10        # Specify the MIPS exit syscall\n" +
			"        syscall             # exit\n" +
			"\n" +
			"#\n" +
			"# Implements a quick and dirty \"malloc\" that draws from a fixed-size\n" +
			"# pool of memory, and never frees or reallocates memory.  Expects a\n" +
			"# single integer argument to be passed in $a0.  Written so that it\n" +
			"# uses only $a and $v registers and therefore needs no stack frame.\n" +
			"# (Look into into sbrk as a better way to allocate memory.)\n" +
			"#\n" +
			"malloc: \n" +
			"        addi $a0, $a0, 3    # Round up to next word boundary\n" +
			"        srl  $a0, $a0, 2    # Remove lowest two bits by shifting\n" +
			"        sll  $a0, $a0, 2    #  right and then back to left\n" +
			"        la   $a1, _next     # Global pointing to free memory\n" +
			"        lw   $v0, 0($a1)    # Load its contents\n" +
			"        add  $v1, $v0, $a0  # Bump up to account for this chunk\n" +
			"        sw   $v1, 0($a1)    # Store new value back in global\n" +
			"        jr   $ra\n";
}
