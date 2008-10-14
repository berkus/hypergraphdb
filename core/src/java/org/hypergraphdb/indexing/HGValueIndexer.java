package org.hypergraphdb.indexing;

import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.storage.ByteArrayConverter;

/**
 * 
 * <p>
 * An indexer that not only determines the key in an index entry, but the value
 * as well. By default, <code>HGIndexer</code> implementation  provide a key by
 * which to index hypergraph atoms. In other words, atoms are the "default" values
 * for index entries. A <code>HGValueIndexer</code> provides also the value in an
 * index entry in cases where it is not the atom itself. 
 * </p>
 *
 * @author Borislav Iordanov
 *
 */
public abstract class HGValueIndexer extends HGIndexer
{
	public HGValueIndexer()
	{		
	}
	
	public HGValueIndexer(HGHandle type)
	{
		super(type);
	}
	
	/**
	 * <p>
	 * Return the value of an index entry based on the passed in atom. 
	 * </p>
	 */
	public abstract Object getValue(HyperGraph graph, Object atom);
	
	/**
	 * <p>
	 * Return a <code>ByteArrayConverter</code> capable of converting index
	 * entry values to/from byte arrays.
	 * </p>
	 * 
	 * @param graph
	 * @return
	 */
	public abstract ByteArrayConverter<?> getValueConverter(HyperGraph graph);
}