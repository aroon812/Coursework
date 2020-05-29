/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import minijava.analysis.*;

@SuppressWarnings("nls")
public final class ABaseDeclClassDecl extends PClassDecl
{
    private PBaseDecl _baseDecl_;

    public ABaseDeclClassDecl()
    {
        // Constructor
    }

    public ABaseDeclClassDecl(
        @SuppressWarnings("hiding") PBaseDecl _baseDecl_)
    {
        // Constructor
        setBaseDecl(_baseDecl_);

    }

    @Override
    public Object clone()
    {
        return new ABaseDeclClassDecl(
            cloneNode(this._baseDecl_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABaseDeclClassDecl(this);
    }

    public PBaseDecl getBaseDecl()
    {
        return this._baseDecl_;
    }

    public void setBaseDecl(PBaseDecl node)
    {
        if(this._baseDecl_ != null)
        {
            this._baseDecl_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._baseDecl_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._baseDecl_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._baseDecl_ == child)
        {
            this._baseDecl_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._baseDecl_ == oldChild)
        {
            setBaseDecl((PBaseDecl) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}