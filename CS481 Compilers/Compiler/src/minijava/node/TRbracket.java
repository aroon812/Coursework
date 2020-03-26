/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class TRbracket extends Token {
	public TRbracket() {
		super.setText("]");
	}

	public TRbracket(int line, int pos) {
		super.setText("]");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TRbracket(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTRbracket(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TRbracket text.");
	}
}
