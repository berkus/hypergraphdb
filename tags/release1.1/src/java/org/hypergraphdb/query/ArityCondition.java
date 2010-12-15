/* 
 * This file is part of the HyperGraphDB source distribution. This is copyrighted 
 * software. For permitted uses, licensing options and redistribution, please see  
 * the LicensingInformation file at the root level of the distribution.  
 * 
 * Copyright (c) 2005-2010 Kobrix Software, Inc.  All rights reserved. 
 */
package org.hypergraphdb.query;

import org.hypergraphdb.HGException;
import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGLink;

/**
 * <p>
 * The <code>ArityCondition</code> is a simply predicate condition that checks the arity
 * (i.e. the size of a target set) of its argument. A link is an atom whose target set has an arity > 0.
 * Thus to check whether an atom is a link, one can use the condition <code>new Not(new ArityCondition(0))</code>.
 * </p>
 *  
 * @author Borislav Iordanov
 */
public class ArityCondition implements HGQueryCondition, HGAtomPredicate 
{
	private int arity;
	
	public ArityCondition()
	{
		arity = 0;
	}
	public ArityCondition(int arity)
	{
		this.arity = arity;
	}
	
	/**
	 * <p>Return true if <code>handle</code> to a HyperGraph link and
	 * <code>false</code> if it refers to a HyperGraph node.</p>
	 */
	public boolean satisfies(HyperGraph hg, HGHandle handle) 
	{
		if (hg.isLoaded(handle))
		{
			Object atom = hg.get(handle);
			if (! (atom instanceof HGLink))
				return arity == 0;
			else
				return ((HGLink)atom).getArity() == arity;
		}
		else
		{
			HGPersistentHandle [] layout = hg.getStore().getLink(hg.getPersistentHandle(handle));
			if (layout == null)
				throw new HGException("Cound not find atom refered to by " + handle + " in HyperGraph store.");
			else
				return layout.length == arity + 2;
		}
	}
	
	public int hashCode() 
	{ 
		return arity; 
	}
	
	public boolean equals(Object x)
	{
		if (! (x instanceof ArityCondition))
			return false;
		else
			return arity == ((ArityCondition)x).arity;
	}
	
	public String toString()
	{
		StringBuffer result = new StringBuffer();
		result.append("arity(");
		result.append(String.valueOf(arity));
		result.append(")");
		return result.toString();
	}

	public int getArity()
	{
		return arity;
	}

	public void setArity(int arity)
	{
		this.arity = arity;
	}
	
}
