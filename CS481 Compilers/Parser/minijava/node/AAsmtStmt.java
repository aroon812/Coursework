/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class AAsmtStmt extends PStmt
{
    private TId _name_;
    private TAsmt _asmt_;
    private PExp _e_;
    private TSemi _semi_;

    public AAsmtStmt()
    {
        // Constructor
    }

    public AAsmtStmt(
        @SuppressWarnings("hiding") TId _name_,
        @SuppressWarnings("hiding") TAsmt _asmt_,
        @SuppressWarnings("hiding") PExp _e_,
        @SuppressWarnings("hiding") TSemi _semi_)
    {
        // Constructor
        setName(_name_);

        setAsmt(_asmt_);

        setE(_e_);

        setSemi(_semi_);

    }

    @Override
    public Object clone()
    {
        return new AAsmtStmt(
            cloneNode(this._name_),
            cloneNode(this._asmt_),
            cloneNode(this._e_),
            cloneNode(this._semi_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAAsmtStmt(this);
    }

    public TId getName()
    {
        return this._name_;
    }

    public void setName(TId node)
    {
        if(this._name_ != null)
        {
            this._name_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._name_ = node;
    }

    public TAsmt getAsmt()
    {
        return this._asmt_;
    }

    public void setAsmt(TAsmt node)
    {
        if(this._asmt_ != null)
        {
            this._asmt_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._asmt_ = node;
    }

    public PExp getE()
    {
        return this._e_;
    }

    public void setE(PExp node)
    {
        if(this._e_ != null)
        {
            this._e_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._e_ = node;
    }

    public TSemi getSemi()
    {
        return this._semi_;
    }

    public void setSemi(TSemi node)
    {
        if(this._semi_ != null)
        {
            this._semi_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._semi_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._name_)
            + toString(this._asmt_)
            + toString(this._e_)
            + toString(this._semi_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._name_ == child)
        {
            this._name_ = null;
            return;
        }

        if(this._asmt_ == child)
        {
            this._asmt_ = null;
            return;
        }

        if(this._e_ == child)
        {
            this._e_ = null;
            return;
        }

        if(this._semi_ == child)
        {
            this._semi_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._name_ == oldChild)
        {
            setName((TId) newChild);
            return;
        }

        if(this._asmt_ == oldChild)
        {
            setAsmt((TAsmt) newChild);
            return;
        }

        if(this._e_ == oldChild)
        {
            setE((PExp) newChild);
            return;
        }

        if(this._semi_ == oldChild)
        {
            setSemi((TSemi) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}