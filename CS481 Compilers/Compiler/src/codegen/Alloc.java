package codegen;

import java.util.HashSet;

import arch.Reg;

/**
 * Implements a register allocator with the functionality necessary for supporting
 * the code-generation phase of our compiler. It allocates registers by number,
 * from smallest to largest, though an allocator can be "primed" with a register
 * to use next. A set of registers to be avoided can be specified as well.
 * 
 * @author Brad Richards
 */

public class Alloc {
	private static final int MIN = arch.Reg.min;  // Steal bounds from Arch.Reg
	private static final int MAX = arch.Reg.max;
	private static int HIGH_WATER = MIN;
	private HashSet<String> avoid;  	// Names of registers we should avoid
	private int next = MIN;        		// First register to allocate
	private Reg prime;					// Register to use first, or null
	private static HashSet<String> allocated = new HashSet<String>();	// Registers used

	/**
	 * Create a new register allocator with an empty set of registers
	 * to avoid.
	 */
	public Alloc() {
		avoid = new HashSet<String>();  // Empty set
		prime = null;
	}

	/**
	 * Creates a new register allocator that will avoid using registers
	 * from the speficied set.
	 * 
	 * @param avoid  Set of registers to avoid using.
	 */
	public Alloc(HashSet<String> avoid) {
		this.avoid = avoid;
		prime = null;
	}

	/**
	 * Copy constructor makes a deep copy of the specified allocator. The
	 * new allocator has identical sets of forbidden registers and already
	 * allocated registers, and copies the primed register if there is one.
	 * 
	 * @param other  An existing allocator we want to copy.
	 */
	public Alloc(Alloc other) {
		this.avoid = new HashSet<String>(other.avoid);
		this.next = other.next;
		if (other.prime == null) {
			this.prime = null;
		} else {
			this.prime = new Reg(other.prime.toString());
		}
	}

	/**
	 * In general, the allocator uses registers from lowest to highest
	 * register number. This getter returns the number of the largest
	 * register allocated, which gives us information about whether we've
	 * exceeded the number of actual MIPS registers.
	 * 
	 * @return Number of largest register allocated.
	 */
	public static int getHighWater() { return HIGH_WATER; }


	/**
	 * "Push" the specified register into the allocator. It will be the next 
	 * one used when the allocator's asked to use a new register. Having this
	 * functionality can help us get results in the correct registers when
	 * translating parameters or return values, for example.
	 * 
	 * @param next  The next register to be used.
	 */
	public void prime(Reg next) {
		prime = next;
	}

	/**
	 * Return the "primed" register (the one to use next). If there's no primed
	 * register this returns null.
	 * 
	 * @return Primed register, or null if there isn't one.
	 */
	public Reg getPrime() { return prime; }

	/**
	 * Returns an array containing the names of the registers allocated
	 * so far. It removes fixed-purpose registers such as $v0, $gp, $sp,
	 * and the $a registers.
	 * 
	 * @return Array containing names of allocated registers.
	 */
	public static String[] getAllocated() { 
		allocated.remove("$v0");
		allocated.remove("$gp");
		allocated.remove("$sp");
		for(int i=0; i<4; i++) {
			allocated.remove("$a"+i);
		}
		return allocated.toArray(new String[0]); 
	}

	/**
	 * Clear the set of allocated registers. This does not change the way
	 * registers are allocated -- it just forgets the names of the registers
	 * allocated so far.
	 */
	public static void clearAllocated() { allocated.clear(); }

	/**
	 * Returns true if the specified register has been used by the allocator.
	 * This method's behavior is not affected by calls to clearAllocated(). It
	 * will return true if a register has been used even if the set of allocated
	 * registers is cleared.
	 * 
	 * @param r
	 * @return  True if the register has been used, false otherwise.
	 */
	public boolean isAllocated(Reg r) { return avoid.contains(r.toString()); }


	/**
	 * Adds a single register to the group of registers that the allocator
	 * will avoid using.
	 * 
	 * @param reg The register to be added to the forbidden list.
	 */
	public void addForbiddenReg(Reg reg) {
		avoid.add(reg.toString());
	}


	/**
	 * Adds the specified set of register names to the group that the allocator
	 * will avoid using.
	 * 
	 * @param used  The set of additional register names to avoid using.
	 */
	public void addForbiddenSet(HashSet<String> used) {
		for( String s : used ) {
			avoid.add(s);
		}
	}

	/**
	 * Replaces the set of register names to avoid using with the
	 * contents of the set passed as parameter.
	 * 
	 * @param used  The set of names to avoid using.
	 */
	public void setForbidden(HashSet<String> used) {
		avoid = used;
	}



	/**
	 * Allocate a register. If there's a "primed" register, it will be allocated next.
	 * If not, it will return registers in increasing order, skipping those in the
	 * set of forbidden registers.
	 * 
	 * @return A register to be used in MIPS assembly.
	 */
	public Reg nextReg() {
		Reg r;
		if (prime != null) {
			r = prime;
			avoid.add(prime.toString());
			allocated.add(prime.toString());
			prime = null;
			return r;
		}

		// If there wasn't a prime, try registers in order until we find one
		// that's not on the forbidden list.
		do {
			r = new Reg("$"+next++);
		} while (avoid.contains(r.toString()));
		HIGH_WATER = Math.max(HIGH_WATER, next-1);
		if (next > MAX+1) {
			System.err.println("Exceeded actual MIPS registers: "+r);
		}

		allocated.add(r.toString());
		return r;
	}

	/**
	 * Return a string describing this allocator's state.
	 * @return Descriptive string.
	 */
	@Override
	public String toString() {
		if (prime == null) {
			return "Alloc at "+next+", won't use "+avoid;
		} else {
			return "Alloc at "+next+" w/prime "+prime+", won't use "+avoid;
		}
	}

}