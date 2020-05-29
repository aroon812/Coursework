/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import java.util.*;
import minijava.analysis.*;

@SuppressWarnings("nls")
public final class AFormalList extends PFormalList
{
    private PFormal _f_;
    private final LinkedList<PFormalsTail> _t_ = new LinkedList<PFormalsTail>();

    public AFormalList()
    {
        // Constructor
    }

    public AFormalList(
        @SuppressWarnings("hiding") PFormal _f_,
        @SuppressWarnings("hiding") List<?> _t_)
    {
        // Constructor
        setF(_f_);

        setT(_t_);

    }

    @Override
    public Object clone()
    {
        return new AFormalList(
            cloneNode(this._f_),
            cloneList(this._t_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAFormalList(this);
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

    public LinkedList<PFormalsTail> getT()
    {
        return this._t_;
    }

    public void setT(List<?> list)
    {
        for(PFormalsTail e : this._t_)
        {
            e.parent(null);
        }
        this._t_.clear();

        for(Object obj_e : list)
        {
            PFormalsTail e = (PFormalsTail) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._t_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._f_)
            + toString(this._t_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._f_ == child)
        {
            this._f_ = null;
            return;
        }

        if(this._t_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._f_ == oldChild)
        {
            setF((PFormal) newChild);
            return;
        }

        for(ListIterator<PFormalsTail> i = this._t_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PFormalsTail) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}