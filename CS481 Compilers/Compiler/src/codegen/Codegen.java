//Aaron Thompson & Lukas Jimenez-Smith
package codegen;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;

import arch.Reg;
import irt.FindReg;
import irt.SearchTree;
import mips.MipsArch;
import symtable.ClassTable;
import symtable.MethodIRTinfo;
import tree.BINOP;
import tree.CALL;
import tree.CJUMP;
import tree.COMMENT;
import tree.CONST;
import tree.ESEQ;
import tree.EXPR;
import tree.Exp;
import tree.ExpList;
import tree.JUMP;
import tree.LABEL;
import tree.MEM;
import tree.MOVE;
import tree.NAME;
import tree.REG;
import tree.RETURN;
import tree.SEQ;
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
	private static final boolean ADDI_OPT = true;
	HashSet<String> forbidden;
	ClassTable symtab;
	PrintWriter out;
	PrintWriter backup; // Holds writer when we switch to StringWriter
	StringWriter sw; 	// The StringWriter embedded in the PrintWriter
	MethodIRTinfo curMethodInfo;
	boolean inMain = false;

	/**
	 * Constructor takes an output stream, and a reference to the ClassTable
	 * holding type information for the entire program.
	 * @param out    PrintWriter to which MIPS output is sent
	 * @param symtab Symbol table containing info on the program
	 */
	public Codegen(PrintWriter out, ClassTable symtab) {
		this.out = out;
		this.symtab = symtab;
		forbidden = new HashSet<String>();
		forbidden.add("$sp");
		forbidden.add("$gp");
	}

	/** Generate output without any indentation */
	void emitLeft(String s) {
		out.println(s);
	}

	/** Generate indented output. */
	void emit(String s) {
		out.println("\t" + s);
	}

	/**
	 * Munch the left, then the right. Pass a copy of the original allocator to 
	 * each branch, since we "start over" after the first Stm.
	 */
	void munchStm(SEQ s, Alloc alloc) {
		munchStm(s.left, new Alloc(alloc));
		munchStm(s.right, new Alloc(alloc));
	}

	/**
	 * A LABEL implies the start of a method. We need to generate the view-shift
	 * instructions at the top, after some helpful comments about which method 
	 * this is and how many args it takes.
	 * 
	 * @param s     The label node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(LABEL s, Alloc alloc) {
		String fullName = s.label.toString();

		if (fullName.contains(".")) {
			inMain = false;

			// If there's a . in its name it's a method in some class. Look up
			// the details in the symbol table.
			String className = fullName.substring(0, fullName.indexOf('.'));
			String methodName = fullName.substring(fullName.indexOf('.') + 1);

			curMethodInfo = symtab.get(className).getMethodTable()
					.get(methodName).getInfo();

			// Generate some helpful comments in the assembly
			emitLeft("\n#");
			emitLeft("# Method " + methodName + " in class " + className + ":");
			emitLeft("#  (" + methodName + " has " + curMethodInfo.numArgs
					+ " arg(s) and "
					+ (curMethodInfo.frameSize - curMethodInfo.numArgs)
					+ " local(s))");
			emitLeft("#\n");
			emitLeft(fullName + ":");

			/*
			 * Now it's time for the view-shift, but we can't know how many
			 * registers need saving until the body of the method has been
			 * translated. We'll use a sneaky trick: Translate the body but
			 * capture that output in a new PrintWriter. When we hit the RETURN
			 * node we can generate the prologue code, paste in the code for the
			 * method body, and do the final view-shift code. Our job here is
			 * just to swap in a new PrintWriter and clear the list of allocated
			 * registers in the allocator.
			 */
			this.backup = this.out;

			out = new PrintWriter(sw = new StringWriter());
			Alloc.clearAllocated(); 
		} else if (fullName.equals("main")) {
			inMain = true;
			emitLeft("\n#");
			emitLeft("# The main method.  Note that main does not save / restore any");
			emitLeft("# registers except $ra.  This is rude, but we're assuming that");
			emitLeft("# our caller doesn't have any state to be protected since we're");
			emitLeft("# the first procedure invoked.");
			emitLeft("#\n");
			emitLeft("main:");
			// emit("move $fp, $sp\t\t# Convert from MIPS conventions");
			emit("sw  $ra, 0($sp)");
		} else {
			inMain = false;
			// If it's not a method in some class, and it's not main, it's a label we've
			// introduced as a target for control flow. In which case, we just need to
			// output the name as a MIPS label.
			emitLeft(fullName + ":");
		}
	}

	/**
	 * A COMMENT node contains a human-readable comment to help make the IRT
	 * more legible. We'll translate it to a MIPS comment so it survives in the
	 * final output as well.
	 * @param s     The comment node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(COMMENT s, Alloc alloc) {
		emit("");
		emit("#");
		emit("# CMT: " + s.text);
		emit("#");
	}

	/**
	 * An IRT JUMP node can be directly translated to a MIPS jump instruction.
	 * @param s     The jump node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(JUMP s, Alloc alloc) {
		assert (s.exp instanceof NAME);		// Make sure the target is valid
		emit("j " + ((NAME) (s.exp)).label.toString());
	}

	/**
	 * Translate a conditional IRT jump to equivalent MIPS code.
	 * 
	 * @param s     The cjump node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(CJUMP s, Alloc alloc) {

		// CJUMPs are more involved than JUMPs. We have to munch the left and
		// right expression trees, being careful to handle the register
		// allocator properly, feed those results to the appropriate MIPS
		// conditional branch instruction, and use a MIPS jump instruction to
		// get to get to the appropriate target if the condition doesn't hold.
		// (I made sure that the false branch in my IRT was always in the
		// fall-through position, so I can omit the jump in the false case.)
        
        Alloc rightAlloc = new Alloc(alloc);
		Reg left = munchExp(s.left, alloc);
		rightAlloc.addForbiddenReg(left);
		Reg right = munchExp(s.right, rightAlloc);
		Reg result = alloc.getPrime();

		// See which kind of CJUMP it is. I only used these three in my IRT
		// so there's no need to do them all. And, in fact, now that there's a
		// BINOP.SLT, I probably don't need the .LT case anymore.
		switch (s.relop) {
		case CJUMP.EQ:
            emit("beq " + left + ", " + right + ", " + s.iftrue);
            emit("j " + s.iffalse);
			break;
		case CJUMP.NE:
            emit("bne " + left + ", " + right + ", " + s.iftrue);
            emit("j " + s.iffalse);
			break;
		case CJUMP.LT:
            emit("slt" + result + ", " + left + ", " + right);
            emit("bne" + left + ", " + "$zero" + ", " + s.iftrue);
            emit("j " + s.iffalse);
			break;
			/*
			 * case CJUMP.GT: emit("GT"); break; case CJUMP.LE: emit("LE"); break;
			 * case CJUMP.GE: emit("GE"); break; case CJUMP.ULT: emit("ULT"); break;
			 * case CJUMP.ULE: emit("ULE"); break; case CJUMP.UGT: emit("UGT");
			 * break; case CJUMP.UGE: emit("UGE"); break;
			 */
		default:
			throw new Error("Unknown op in CJUMP: " + s.relop);
		}
	}

	/**
	 * Translate a MOVE operation into equivalent MIPS instructions.
	 * @param s     The move node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(MOVE s, Alloc alloc) {
		// There are three kinds of MOVEs supported here: Simple moves between
		// registers, moves that put a calculated value into a register, and
		// moves that write to memory.

		// REG <-- REG
		if ((s.dst instanceof REG) && (s.src instanceof REG)) {

			Reg src = ((REG) s.src).reg;
            Reg dst = ((REG) s.dst).reg;
            
			if (!src.toString().equals(dst.toString())){
				emit("move " + dst + ", " + src); 
            }
            
		}

		// REG <-- Exp
		else if (s.dst instanceof REG) {
            Reg dst = ((REG)s.dst).reg;
            alloc.prime(dst);
            
			Reg r = munchExp(s.src, alloc);
			if (!r.toString().equals(dst.toString())){
				emit("move " + dst + ", " + r); 
            }
            
		}

		// MEM <-- Exp
		else if (s.dst instanceof MEM) {
			// Translate the Exp on the right-hand side to get the value to be
			// stored. Then translate the MEM's child tree to get the target memory
			// address in a register. Finally, issue a sw instruction.
			Alloc srcAlloc = new Alloc(alloc);
            srcAlloc.prime(null);
            
            Reg src = munchExp(s.src, srcAlloc);

			alloc.addForbiddenReg(src);
            Reg addr = munchExp(((MEM)s.dst).exp, alloc);
            
			emit("sw  " + src + ", 0(" + addr + ")");
		}
	}

	/**
	 * Translate an arithmetic expression to MIPS.
	 * @param s     The EXPR node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(EXPR s, Alloc alloc) {
		// The EXPR's subtree is the expression. Translate it.
		munchExp(s.exp, alloc);
	}

	/**
	 * Translate a RETURN node to MIPS. This marks the end of a method.
	 * @param s     The RETURN node we're translating
	 * @param alloc The register allocator to use
	 */
	void munchStm(RETURN s, Alloc alloc) {


		alloc.prime(new Reg("$v0"));		// Result needs to be in $v0
		Reg dst = munchExp(s.ret, alloc);	// Translate expression
		if (!dst.toString().equals("$v0")) {
			emit("move $v0, " + dst);
        }

        PrintWriter temp = this.backup;
        this.backup = this.out;
        this.out = temp;
		// Recall from the LABEL discussion that we held off on generating
		// the prologue view-shift code until now, and wrote the method's body
		// to a new PrintWriter. Can generate the prologue now, dump the
		// method body, then do the epilogue view-shift.
        String[] registers = Alloc.getAllocated();
        
   
        int mooooverJohn11 = 4 *  (registers.length + this.curMethodInfo.frameSize +  2 );

        emit("addi $sp, $sp, " + "-" + mooooverJohn11);
        emit("sw    $ra, 0($sp)");
        emit("sw    $gp, 4($sp)");
        int numArgs = curMethodInfo.numArgs;
        int offset = 8;
        for (int i = 0; i < numArgs; i++){
            emit("sw    $a" + i + ", " + (offset+(i*4)) + "($sp)");
        }

        offset = 4*(registers.length+2);
        for (int i = 0; i < registers.length; i++){
            emit("sw    " + registers[i] + ", " + (offset+(i*4)) + "($sp)");
        }
		
        emit(sw.toString());

        for (int i = 0; i < registers.length; i ++){
            emit("lw    " + registers[i] + ", " + (offset + (i*4)) + ("($sp)"));
        }
        

        emit("lw    $ra, 0($sp)");
        emit("addi  $sp, $sp, " + this.curMethodInfo.frameSize);
        emit("jr    $ra");

	}

	/**
	 * This is the top-level call for translating an IRT statement node.
	 * It calls one of the specific statement-translating routines to 
	 * generate the corresponding MIPS code.
	 * 
	 * @param s     The Stm node we're translating
	 * @param alloc The register allocator to use
	 */
	public void munchStm(Stm s, Alloc alloc) {
		if (s instanceof SEQ) {
			munchStm((SEQ) s, alloc);
		} else if (s instanceof LABEL) {
			munchStm((LABEL) s, alloc);
		} else if (s instanceof COMMENT) {
			munchStm((COMMENT) s, alloc);
		} else if (s instanceof JUMP) {
			munchStm((JUMP) s, alloc);
		} else if (s instanceof CJUMP) {
			munchStm((CJUMP) s, alloc);
		} else if (s instanceof MOVE) {
			munchStm((MOVE) s, alloc);
		} else if (s instanceof EXPR) {
			munchStm((EXPR) s, alloc);
		} else if (s instanceof RETURN) {
			munchStm((RETURN) s, alloc);
		} else {
			throw new Error("Gen.munchStm");
		}
	}

	/**
	 * Translates an arithmetic expression involving a binary operator to the
	 * corresponding MIPS code. In general this requires translating both the
	 * left and right subtrees, then applying the appropriate operation to
	 * produce the result.
	 * 
	 * @param e     The BINOP node we're translating
	 * @param alloc The register allocator to use
	 */
	Reg munchExp(BINOP e, Alloc alloc) {
		/* There are some subtle issues involving register allocation: We're passed an
		 * allocator to use as a starting point, but need to tailor it for the left
		 * and right branches. On the left, we need to avoid using any hard-coded
		 * registers from either the left or right branches of the IRT tree. On the
		 * right we only need to respect hard-coded registers in the right IRT
		 * subtree, but we also need to avoid using the register in which the left
		 * subtree has left its value. The basic strategy is to add hard-coded
		 * registers from the right subtree to the forbidden list of the allocator 
		 * passed in. We clone the allocator and use a copy to handle the left tree 
		 * (after adding hard-coded registers from the left subtree to its skip list), 
		 * then use the original allocator to process the right (after adding the left's 
		 * result register to the skip list).
		 */
		HashSet<String> rightRegisters = FindReg.find(e.right);
		alloc.addForbiddenSet(rightRegisters); 	// Find registers in right tree
        Alloc copy = new Alloc(alloc); 			// clone after updating skip list
        
        alloc.addForbiddenSet(FindReg.find(e.left));
        Reg left = munchExp(e.left, alloc);
        
		if (left.toString().equals("$v0") && rightRegisters.contains("$v0")) {
			Reg temp = alloc.nextReg();
			emit("move " + temp + ", $v0");
			left = temp;
		}
		copy.addForbiddenReg(left);
		copy.prime(alloc.getPrime());
		Reg right = munchExp(e.right, copy);
		alloc.prime(copy.getPrime());
        Reg result = left;
        
		if (this.forbidden.contains(left.toString())){
			if (right == null || this.forbidden.contains(right.toString())) {
				result = alloc.nextReg();
			} else {
				result = right;
			}
		}
		switch (e.binop) {
			case BINOP.PLUS:
				emit("add " + result + ", " + left + ", " + right);
				return result;
			case BINOP.MINUS:
				emit("sub " + result + ", " + left + ", " + right);
				return result;
			case BINOP.MUL:
				emit("mul " + result + ", " + left + ", " + right);
				return result;
			case BINOP.DIV:
				emit("div " + result + ", " + left + ", " + right);
				return result;
			case BINOP.AND:
				emit("and " + result + ", " + left + ", " + right);
				return result;
			case BINOP.OR:
				emit("or  " + result + ", " + left + ", " + right);
                return result; 
            case BINOP.SLT:
                emit("slt  " + result + ", " + left + ", " + right);
                return result; 
		} 
		throw new Error("BINOP operator does not exist: " + e.binop);
	}

	/**
	 * Translates an IRT MEM node to the corresponding MIPS code. We'll catch
	 * MEM nodes on the LHS of a MOVE when tiling from above. We'll only get
	 * here if the MEM appears on the RHS, which means we're doing a load. 
	 * 
	 * @param e     The MEM node we're translating
	 * @param alloc The register allocator to use
	 */
	Reg munchExp(MEM e, Alloc alloc) {
		Reg r = munchExp(e.exp, alloc);
		emit("lw  " + r + ", 0(" + r + ")");
		return r;
	}


	/**
	 * Translates an IRT REG node to MIPS.
	 * 
	 * @param e     The REG node we're translating
	 * @param alloc The register allocator to use
	 * @return
	 */
	Reg munchExp(REG e, Alloc alloc) { 
		// An IRT REG node corresponds to a MIPS register. Since we've used MIPS 
		// notation for our IRT registers, we can just return the REG's name.

		return e.reg;
	}

	/**
	 * Translates an ESEQ node to MIPS. ESEQ consist of a statement component
	 * followed by an expression.
	 * 
	 * @param e     The ESEQ node we're translating
	 * @param alloc The register allocator to use
	 */
	Reg munchExp(ESEQ e, Alloc alloc) {
		// Both parts of the ESEQ need translating -- the Seq part before the Exp.
		// When doing the Seq we need to be careful not to use any registers that
		// are hard-coded into the Exp part though.
        Alloc temp = new Alloc(alloc);

        alloc.addForbiddenSet(FindReg.find(e));
		temp.prime(null);
        munchStm(e.stm, temp); 
        
		return munchExp(e.exp, alloc); 
	}

	/**
	 * Translate a NAME node to MIPS. Name nodes should only appear in CALLs, and
	 * we process them as part of translating a CALL, so we should never visit a
	 * NAME node like this unless the IRT is broken somehow.
	 * 
	 * @param e		The NAME node we're translating
	 * @param alloc The register allocator to use
	 */
	Reg munchExp(NAME e, Alloc alloc) {
		emit("ERROR: got to undecorated NAME: " + e.label.toString());
		return new Reg("$zero");
	}

	/**
	 * Translate a CONST node to MIPS. Often, CONST nodes will be caught by
	 * tile-matches from above, but we still need a backup plan for those that
	 * aren't. We'll load the constant into the next available register.
	 * 
	 * @param e		The CONST node we're translating
	 * @param alloc The register allocator to use
	 */
	Reg munchExp(CONST e, Alloc alloc) {
		Reg r = alloc.nextReg();
		emit("li  " + r + ", " + String.valueOf(e.value));
		return r;
	}

	/**
	 * Translate a CALL node to MIPS. Calls to built-ins (e.g. malloc) are handled
	 * differently from method calls since built-ins don't need the static link.
	 * In general we need to generate code for evaluating the args, leave them in
	 * $a registers, then jump to the appropriate label. If necessary, also generate
	 * code for the static link and leave in $gp.
	 * 
	 * @param e
	 * @param clean
	 */
	Reg munchExp(CALL e, Alloc clean) {
		assert (e.func instanceof NAME);
		String name = ((NAME) (e.func)).label.toString();
		Reg dst, used;
		ExpList a = e.args;

		// Look to see if this CALL contains nested CALL nodes. If not, we can prime
		// the allocator with $a registers to encourage the code generation to leave
		// arg expression in $a's. If there are nested CALL nodes though, the inner
		// CALL will have to leave its result in $v0 and we'll need to do a move here
		// to get that value into an $a register.

		boolean nested = SearchTree.containsCALL(e);

		if (nested){
			emit("# setting up to call " + name + " (nested)");
		} else {
			emit("# setting up to call " + name);
        }
        
		if (name.equals("print") || name.equals("malloc") || name.equals("exit")) {
			if (!nested) {					  
				clean.prime(new Reg("$a0"));  
			}
			used = munchExp(a.head, clean);   
			if (!used.toString().equals("$a0")) {
				emit("move $a0, " + used);	 
			}
		} else {
			int aReg = 0;			// Start with a0
			Exp objRef = a.head;	// and the first arg expression
			Alloc copy, alloc;
			ArrayList<String> moves = new ArrayList<String>();

			// As with the built-ins, we prime with the appropriate $a register if
			// there are no nested CALL nodes. Otherwise we let the allocator
			// pick a register to hold the result across the CALL, and move it
			// into $a after the arg is processed. We accumulate these moves into a
			// single string and emit it just before the jal.

			alloc = clean;
			for (a = a.tail; a != null; a = a.tail) {
                aReg++;
				copy = new Alloc(alloc);
				dst = new Reg("$a" + aReg);
				if (!nested) {
					alloc.prime(dst); 
                }
                
				Reg cur = munchExp(a.head, alloc);
				if (!dst.toString().equals(cur.toString())) {
					if (cur.toString().equals("$v0")) {
						cur = alloc.nextReg();
						emit("move " + cur + ", $v0");
					} 
					moves.add("move " + dst + ", " + cur);
				} 
				copy.addForbiddenReg(cur);
				alloc = copy;
			} 
			// Now translate the object reference. We want it to end up in $gp, but
			// need to be careful not to use $gp yet if there are nested calls.
			if (!nested){
				alloc.prime(new Reg("$gp"));
            } 
            
			used = munchExp(objRef, alloc);
			if (!used.toString().equals("$gp")){
				emit("move $gp, " + used); 
            }
            
			for (String move : moves){
				emit(move); 
			}
		}
		emit("jal " + name);
		emit("# done with call to " + name);
		return MipsArch.V0;
	}

	/**
	 * This is the top-level call for translating an IRT expression node.
	 * It calls one of the specific expression-translating routines to 
	 * generate the corresponding MIPS code.
	 * 
	 * @param s     The Exp node we're translating
	 * @param alloc The register allocator to use
	 */
	public Reg munchExp(Exp e, Alloc alloc) {
		if (e instanceof BINOP) {
			return munchExp((BINOP) e, alloc);
		} else if (e instanceof MEM) {
			return munchExp((MEM) e, alloc);
		} else if (e instanceof REG) {
			return munchExp((REG) e, alloc);
		} else if (e instanceof ESEQ) {
			return munchExp((ESEQ) e, alloc);
		} else if (e instanceof NAME) {
			return munchExp((NAME) e, alloc);
		} else if (e instanceof CONST) {
			return munchExp((CONST) e, alloc);
		} else if (e instanceof CALL) {
			return munchExp((CALL) e, alloc);
		} else {
			throw new Error("Gen.munchExp");
		}
	}

	/**
	 * Translate starts the code-generation process. It creates a register allocator,
	 * then calls munch on the root of the tree.
	 * @param s  The root of the program tree
	 */
	public void translate(Stm s) {
		Alloc alloc = new Alloc();
		munchStm(s, alloc);
		// Now we're at the end of MAIN, time to do its epilogue
		emit("lw  $ra, 0($sp)");
		emit("jr $ra");
		out.flush();
	}

}