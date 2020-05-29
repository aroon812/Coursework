package tree;

/** A pretty-printer for IRT code */
public class Print {

	static java.io.PrintStream out = System.out;

	static void indent(int d) {
		for (int i = 0; i < d; i++) {
			System.out.print(' ');
		}
	}

	static void say(String s) {
		out.print(s);
	}

	static void sayln(String s) {
		say(s);
		say("\n");
	}

	static void prStm(SEQ s, int d) {
		indent(d);
		sayln("SEQ(");
		prStm(s.left, d + 1);
		sayln(",");
		prStm(s.right, d + 1);
		say(")");
	}

	static void prStm(LABEL s, int d) {
		indent(d);
		say("LABEL ");
		say(s.label.toString());
	}

	static void prStm(COMMENT s, int d) {
		indent(d);
		say("COMMENT:  " + s.text);
	}

	static void prStm(JUMP s, int d) {
		indent(d);
		sayln("JUMP(");
		prExp(s.exp, d + 1);
		say(")");
	}

	static void prStm(CJUMP s, int d) {
		indent(d);
		say("CJUMP(");
		switch (s.relop) {
		case CJUMP.EQ:
			say("EQ");
			break;
		case CJUMP.NE:
			say("NE");
			break;
		case CJUMP.LT:
			say("LT");
			break;
		case CJUMP.GT:
			say("GT");
			break;
		case CJUMP.LE:
			say("LE");
			break;
		case CJUMP.GE:
			say("GE");
			break;
		case CJUMP.ULT:
			say("ULT");
			break;
		case CJUMP.ULE:
			say("ULE");
			break;
		case CJUMP.UGT:
			say("UGT");
			break;
		case CJUMP.UGE:
			say("UGE");
			break;
		default:
			throw new Error("Unknown OP in CJUMP: " + s.relop);
		}
		sayln(",");
		prExp(s.left, d + 1);
		sayln(",");
		prExp(s.right, d + 1);
		sayln(",");
		indent(d + 1);
		say(s.iftrue.toString());
		say(",");
		say(s.iffalse.toString());
		say(")");
	}

	static void prStm(MOVE s, int d) {
		indent(d);
		sayln("MOVE(");
		prExp(s.dst, d + 1);
		sayln(",");
		prExp(s.src, d + 1);
		say(")");
	}

	static void prStm(EXPR s, int d) {
		indent(d);
		sayln("EXPR(");
		prExp(s.exp, d + 1);
		say(")");
	}

	static void prStm(RETURN s, int d) {
		indent(d);
		sayln("RETURN(");
		prExp(s.ret, d + 1);
		say(")");
	}

	public static void prStm(Stm s, int d) {
		if (s instanceof SEQ) {
			prStm((SEQ) s, d);
		} else if (s instanceof LABEL) {
			prStm((LABEL) s, d);
		} else if (s instanceof COMMENT) {
			prStm((COMMENT) s, d);
		} else if (s instanceof JUMP) {
			prStm((JUMP) s, d);
		} else if (s instanceof CJUMP) {
			prStm((CJUMP) s, d);
		} else if (s instanceof MOVE) {
			prStm((MOVE) s, d);
		} else if (s instanceof EXPR) {
			prStm((EXPR) s, d);
		} else if (s instanceof RETURN) {
			prStm((RETURN) s, d);
		} else {
			throw new Error("Unknown Stm node in Print: "
					+ s.getClass().getName());
		}
	}

	static void prExp(BINOP e, int d) {
		indent(d);
		say("BINOP(");
		switch (e.binop) {
		case BINOP.PLUS:
			say("PLUS");
			break;
		case BINOP.MINUS:
			say("MINUS");
			break;
		case BINOP.MUL:
			say("MUL");
			break;
		case BINOP.DIV:
			say("DIV");
			break;
		case BINOP.AND:
			say("AND");
			break;
		case BINOP.OR:
			say("OR");
			break;
		case BINOP.LSHIFT:
			say("LSHIFT");
			break;
		case BINOP.RSHIFT:
			say("RSHIFT");
			break;
		case BINOP.ARSHIFT:
			say("ARSHIFT");
			break;
		case BINOP.XOR:
			say("XOR");
			break;
		case BINOP.SLT:
			say("SLT");
			break;
		default:
			throw new Error("Unknown OP in BINOP: " + e.binop);
		}
		sayln(",");
		prExp(e.left, d + 1);
		sayln(",");
		prExp(e.right, d + 1);
		say(")");
	}

	static void prExp(MEM e, int d) {
		indent(d);
		sayln("MEM(");
		prExp(e.exp, d + 1);
		say(")");
	}

	static void prExp(REG e, int d) {
		indent(d);
		say("REG ");
		say(e.reg.toString());
	}

	static void prExp(ESEQ e, int d) {
		indent(d);
		sayln("ESEQ(");
		prStm(e.stm, d + 1);
		sayln(",");
		prExp(e.exp, d + 1);
		say(")");

	}

	static void prExp(NAME e, int d) {
		indent(d);
		say("NAME ");
		say(e.label.toString());
	}

	static void prExp(CONST e, int d) {
		indent(d);
		say("CONST ");
		say(String.valueOf(e.value));
	}

	static void prExp(CALL e, int d) {
		indent(d);
		sayln("CALL(");
		prExp(e.func, d + 1);
		for (ExpList a = e.args; a != null; a = a.tail) {
			sayln(",");
			prExp(a.head, d + 2);
		}
		say(")");
	}

	public static void prExp(Exp e, int d) {
		if (e instanceof BINOP) {
			prExp((BINOP) e, d);
		} else if (e instanceof MEM) {
			prExp((MEM) e, d);
		} else if (e instanceof REG) {
			prExp((REG) e, d);
		} else if (e instanceof ESEQ) {
			prExp((ESEQ) e, d);
		} else if (e instanceof NAME) {
			prExp((NAME) e, d);
		} else if (e instanceof CONST) {
			prExp((CONST) e, d);
		} else if (e instanceof CALL) {
			prExp((CALL) e, d);
		} else {
			throw new Error("Unknown Exp node in Print: "
					+ e.getClass().getName());
		}
	}

	public static void prStm(Stm s) {
		prStm(s, 0);
		say("\n");
	}

	public static void prExp(Exp e) {
		prExp(e, 0);
		say("\n");
	}

}
