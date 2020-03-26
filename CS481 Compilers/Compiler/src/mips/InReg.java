package mips;

import tree.Exp;
import tree.REG;
import arch.Reg;

/**
 * This subclass of Access describes how to get at items in MIPS registers. We
 * have to support both flavors of getTree accessor, though they both do exactly
 * the same thing --- return an IRT expression that describes which register the
 * data's in.
 */
public class InReg extends arch.Access {
	Reg temp; // The register we're stored in

	public InReg(Reg t) {
		temp = t;
	}

	@Override
	public String toString() {
		return temp.toString();
	}

	public Reg getReg() {
		return temp;
	}

	@Override
	public Exp getTree() {
		return new REG(temp);
	}

	@Override
	public Exp getTree(Exp base) {
		return new REG(temp);
	}
}
