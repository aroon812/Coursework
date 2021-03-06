/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class AFormalsTail extends PFormalsTail
{
    private TComma _comma_;
    private PFormal _f_;

    public AFormalsTail()
    {
        // Constructor
    }

    public AFormalsTail(
        @SuppressWarnings("hiding") TComma _comma_,
        @SuppressWarnings("hiding") PFormal _f_)
    {
        // Constructor
        setComma(_comma_);

        setF(_f_);

    }

    @Override
    public Object clone()
    {
        return new AFormalsTail(
            cloneNode(this._comma_),
            cloneNode(this._f_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFormalsTail(this);
    }

    public TComma getComma()
    {
        return this._comma_;
    }

    public void setComma(TComma node)
    {
        if(this._comma_ != null)
        {
            this._comma_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._comma_ = node;
    }

    public PFormal getF()
    {
        return this._f_;
    }

    public void setF(PFormal node)
    {
        if(this._f_ != null)
        {
            this._f_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._f_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._comma_)
            + toString(this._f_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._comma_ == child)
        {
            this._comma_ = null;
            return;
        }

        if(this._f_ == child)
        {
            this._f_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._comma_ == oldChild)
        {
            setComma((TComma) newChild);
            return;
        }

        if(this._f_ == oldChild)
        {
            setF((PFormal) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
