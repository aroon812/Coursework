/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class ANumLiteral extends PLiteral
{
    private TNum _num_;

    public ANumLiteral()
    {
        // Constructor
    }

    public ANumLiteral(
        @SuppressWarnings("hiding") TNum _num_)
    {
        // Constructor
        setNum(_num_);

    }

    @Override
    public Object clone()
    {
        return new ANumLiteral(
            cloneNode(this._num_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANumLiteral(this);
    }

    public TNum getNum()
    {
        return this._num_;
    }

    public void setNum(TNum node)
    {
        if(this._num_ != null)
        {
            this._num_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._num_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._num_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._num_ == child)
        {
            this._num_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._num_ == oldChild)
        {
            setNum((TNum) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
