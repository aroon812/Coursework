/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class ANot1Notexp extends PNotexp
{
    private PBracdot _bracdot_;

    public ANot1Notexp()
    {
        // Constructor
    }

    public ANot1Notexp(
        @SuppressWarnings("hiding") PBracdot _bracdot_)
    {
        // Constructor
        setBracdot(_bracdot_);

    }

    @Override
    public Object clone()
    {
        return new ANot1Notexp(
            cloneNode(this._bracdot_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseANot1Notexp(this);
    }

    public PBracdot getBracdot()
    {
        return this._bracdot_;
    }

    public void setBracdot(PBracdot node)
    {
        if(this._bracdot_ != null)
        {
            this._bracdot_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._bracdot_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._bracdot_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._bracdot_ == child)
        {
            this._bracdot_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._bracdot_ == oldChild)
        {
            setBracdot((PBracdot) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
