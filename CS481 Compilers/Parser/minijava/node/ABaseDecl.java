/* This file was generated by SableCC (http://www.sablecc.org/). */

package minijava.node;

import java.util.*;
import minijava.analysis.*;

@SuppressWarnings("nls")
public final class ABaseDecl extends PBaseDecl
{
    private TClas _clas_;
    private TId _name_;
    private TLbrace _lbrace_;
    private final LinkedList<PVarDecl> _vars_ = new LinkedList<PVarDecl>();
    private final LinkedList<PMethodDecl> _methods_ = new LinkedList<PMethodDecl>();
    private TRbrace _rbrace_;

    public ABaseDecl()
    {
        // Constructor
    }

    public ABaseDecl(
        @SuppressWarnings("hiding") TClas _clas_,
        @SuppressWarnings("hiding") TId _name_,
        @SuppressWarnings("hiding") TLbrace _lbrace_,
        @SuppressWarnings("hiding") List<?> _vars_,
        @SuppressWarnings("hiding") List<?> _methods_,
        @SuppressWarnings("hiding") TRbrace _rbrace_)
    {
        // Constructor
        setClas(_clas_);

        setName(_name_);

        setLbrace(_lbrace_);

        setVars(_vars_);

        setMethods(_methods_);

        setRbrace(_rbrace_);

    }

    @Override
    public Object clone()
    {
        return new ABaseDecl(
            cloneNode(this._clas_),
            cloneNode(this._name_),
            cloneNode(this._lbrace_),
            cloneList(this._vars_),
            cloneList(this._methods_),
            cloneNode(this._rbrace_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseABaseDecl(this);
    }

    public TClas getClas()
    {
        return this._clas_;
    }

    public void setClas(TClas node)
    {
        if(this._clas_ != null)
        {
            this._clas_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._clas_ = node;
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

    public TLbrace getLbrace()
    {
        return this._lbrace_;
    }

    public void setLbrace(TLbrace node)
    {
        if(this._lbrace_ != null)
        {
            this._lbrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._lbrace_ = node;
    }

    public LinkedList<PVarDecl> getVars()
    {
        return this._vars_;
    }

    public void setVars(List<?> list)
    {
        for(PVarDecl e : this._vars_)
        {
            e.parent(null);
        }
        this._vars_.clear();

        for(Object obj_e : list)
        {
            PVarDecl e = (PVarDecl) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._vars_.add(e);
        }
    }

    public LinkedList<PMethodDecl> getMethods()
    {
        return this._methods_;
    }

    public void setMethods(List<?> list)
    {
        for(PMethodDecl e : this._methods_)
        {
            e.parent(null);
        }
        this._methods_.clear();

        for(Object obj_e : list)
        {
            PMethodDecl e = (PMethodDecl) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._methods_.add(e);
        }
    }

    public TRbrace getRbrace()
    {
        return this._rbrace_;
    }

    public void setRbrace(TRbrace node)
    {
        if(this._rbrace_ != null)
        {
            this._rbrace_.parent(null);
        }

        if(node != null)
        {
            if(node.parent() != null)
            {
                node.parent().removeChild(node);
            }

            node.parent(this);
        }

        this._rbrace_ = node;
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._clas_)
            + toString(this._name_)
            + toString(this._lbrace_)
            + toString(this._vars_)
            + toString(this._methods_)
            + toString(this._rbrace_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._clas_ == child)
        {
            this._clas_ = null;
            return;
        }

        if(this._name_ == child)
        {
            this._name_ = null;
            return;
        }

        if(this._lbrace_ == child)
        {
            this._lbrace_ = null;
            return;
        }

        if(this._vars_.remove(child))
        {
            return;
        }

        if(this._methods_.remove(child))
        {
            return;
        }

        if(this._rbrace_ == child)
        {
            this._rbrace_ = null;
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        if(this._clas_ == oldChild)
        {
            setClas((TClas) newChild);
            return;
        }

        if(this._name_ == oldChild)
        {
            setName((TId) newChild);
            return;
        }

        if(this._lbrace_ == oldChild)
        {
            setLbrace((TLbrace) newChild);
            return;
        }

        for(ListIterator<PVarDecl> i = this._vars_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PVarDecl) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator<PMethodDecl> i = this._methods_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PMethodDecl) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        if(this._rbrace_ == oldChild)
        {
            setRbrace((TRbrace) newChild);
            return;
        }

        throw new RuntimeException("Not a child.");
    }
}
