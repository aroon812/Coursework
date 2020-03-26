/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class AIfStmt extends PStmt {
	private PExp _exp_;
	private PStmt _yes_;
	private PStmt _no_;

	public AIfStmt() {
		// Constructor
	}

	public AIfStmt(@SuppressWarnings("hiding") PExp _exp_,
			@SuppressWarnings("hiding") PStmt _yes_,
			@SuppressWarnings("hiding") PStmt _no_) {
		// Constructor
		setExp(_exp_);

		setYes(_yes_);

		setNo(_no_);

	}

	@Override
	public Object clone() {
		return new AIfStmt(cloneNode(this._exp_), cloneNode(this._yes_),
				cloneNode(this._no_));
	}

	@Override
	public void apply(Switch sw) {
		((Analysis) sw).caseAIfStmt(this);
	}

	public PExp getExp() {
		return this._exp_;
	}

	public void setExp(PExp node) {
		if (this._exp_ != null) {
			this._exp_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._exp_ = node;
	}

	public PStmt getYes() {
		return this._yes_;
	}

	public void setYes(PStmt node) {
		if (this._yes_ != null) {
			this._yes_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._yes_ = node;
	}

	public PStmt getNo() {
		return this._no_;
	}

	public void setNo(PStmt node) {
		if (this._no_ != null) {
			this._no_.parent(null);
		}

		if (node != null) {
			if (node.parent() != null) {
				node.parent().removeChild(node);
			}

			node.parent(this);
		}

		this._no_ = node;
	}

	@Override
	public String toString() {
		return "" + toString(this._exp_) + toString(this._yes_)
				+ toString(this._no_);
	}

	@Override
	void removeChild(@SuppressWarnings("unused") Node child) {
		// Remove child
		if (this._exp_ == child) {
			this._exp_ = null;
			return;
		}

		if (this._yes_ == child) {
			this._yes_ = null;
			return;
		}

		if (this._no_ == child) {
			this._no_ = null;
			return;
		}

		throw new RuntimeException("Not a child.");
	}

	@Override
	void replaceChild(@SuppressWarnings("unused") Node oldChild,
			@SuppressWarnings("unused") Node newChild) {
		// Replace child
		if (this._exp_ == oldChild) {
			setExp((PExp) newChild);
			return;
		}

		if (this._yes_ == oldChild) {
			setYes((PStmt) newChild);
			return;
		}

		if (this._no_ == oldChild) {
			setNo((PStmt) newChild);
			return;
		}

		throw new RuntimeException("Not a child.");
	}
}
