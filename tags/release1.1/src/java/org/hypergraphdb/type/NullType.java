/* 
 * This file is part of the HyperGraphDB source distribution. This is copyrighted 
 * software. For permitted uses, licensing options and redistribution, please see  
 * the LicensingInformation file at the root level of the distribution.  
 * 
 * Copyright (c) 2005-2010 Kobrix Software, Inc.  All rights reserved. 
 */
package org.hypergraphdb.type;

import org.hypergraphdb.HGHandle;

import org.hypergraphdb.HGPersistentHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.IncidenceSetRef;
import org.hypergraphdb.LazyRef;

public class NullType implements HGAtomType
{
    private HyperGraph graph;
    
	public void setHyperGraph(HyperGraph hg)
	{
	    this.graph = hg;
	}

	public Object make(HGPersistentHandle handle, LazyRef<HGHandle[]> targetSet, IncidenceSetRef incidenceSet)
	{
		return null;
	}

	public HGPersistentHandle store(Object instance)
	{
		return graph.getHandleFactory().makeHandle();
	}

	public void release(HGPersistentHandle handle)
	{
	}

	public boolean subsumes(Object general, Object specific)
	{
		return false;
	}
}
