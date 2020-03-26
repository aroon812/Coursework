/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class TThis extends Token {
	public TThis() {
		super.setText("this");
	}

	public TThis(int line, int pos) {
		super.setText("this");
		setLine(line);
		setPos(pos);
	}

	@Override
	public Object clone() {
		return new TThis(getLine(), getPos());
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseTThis(this);
	}

	@Override
	public void setText(@SuppressWarnings("unused") String text) {
		throw new RuntimeException("Cannot change TThis text.");
	}
}
