package mips;

import arch.Arch;
import arch.Reg;

/**
 * This class specifies MIPS-specific details like the names of the registers
 * used in MIPS for the stack pointer, static link, and return address, as well
 * as groups of register names that are callee-saved and caller-saved according
 * to the MIPS specification. We're only targeting MIPS, but the book's code
 * tries to be general enough that we could swap in a different architecture's
 * details here and everything would still work. A MIPS frame allocates space for 
 * <ol>
 *   <li> the return address, $ra 
 *   <li> the frame pointer, $fp 
 *   <li> the static link ("this") 
 *   <li> up to four parameters $a0-$a3
 *   <li> local variables as necessary
 * </ol>
 */
public class MipsArch extends Arch {
	public static final Reg ZERO = new Reg("$zero"); // zero reg
	public static final Reg V0 = new Reg("$v0"); 	// function result
	public static final Reg SL = new Reg("$gp"); 	// caller-saved, static link
	public static final Reg SP = new Reg("$sp"); 	// stack pointer
	public static final Reg FP = new Reg("$fp"); 	// callee-save (frame pointer)
	public static final Reg RA = new Reg("$ra"); 	// return address
	public static final Reg[] ARGS = { new Reg("$a0"), new Reg("$a1"),
		new Reg("$a2"), new Reg("$a3") };
	public static final Reg[] CALLEE = { new Reg("$s0"), new Reg("$s1"),
		new Reg("$s2"), new Reg("$s3"), new Reg("$s4"), new Reg("$s5"),
		new Reg("$s6"), new Reg("$s7") };
	public static final Reg[] CALLER = { new Reg("$t0"), new Reg("$t1"),
		new Reg("$t2"), new Reg("$t3"), new Reg("$t4"), new Reg("$t5"),
		new Reg("$t6"), new Reg("$t7"), new Reg("$t8") };

	/* (non-Javadoc)
	 * @see arch.Arch#FP()
	 */
	@Override
	public Reg FP() {
		return FP;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#SP()
	 */
	@Override
	public Reg SP() {
		return SP;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#RA()
	 */
	@Override
	public Reg RA() {
		return RA;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#RV()
	 */
	@Override
	public Reg RV() {
		return V0;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#SL()
	 */
	@Override
	public Reg SL() {
		return SL;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#args()
	 */
	@Override
	public Reg[] args() {
		return ARGS;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#calleeSaved()
	 */
	@Override
	public Reg[] calleeSaved() {
		return CALLEE;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#callerSaved()
	 */
	@Override
	public Reg[] callerSaved() {
		return CALLER;
	}

	/* (non-Javadoc)
	 * @see arch.Arch#wordSize()
	 */
	@Override
	public int wordSize() {
		return 4;
	}

	private int count = 0;

	/**
	 * Reset the register returned by getReg() so that it will return the first
	 * of the callee-saved registers again on its next call.
	 */
	public void resetCount() {
		count = 0;
	}

	/**
	 * Returns a different MIPS register on each call. It initially returns the
	 * first of the callee-saved registers ($s0), and on subsequent calls will
	 * return callee-saved registers in sequence, then move on to caller-saved
	 * registers. Once all caller-saved have been exhausted, it starts generating
	 * nonsensical register names.
	 * @return MIPS registers, in sequence
	 */
	public Reg getReg() {
		if (count < CALLEE.length) {
			return CALLEE[count++];
		} 
		else {
			int idx = count++ - CALLEE.length; // index into caller-saved array
			if (idx < CALLER.length) {
				return CALLER[count++ - CALLEE.length];
			}
			else {
				return new Reg("extra"+idx);
			}
		}
	}
}
