package fing.model;

import cytoscape.graph.dynamic.util.DynamicGraphRepresentation;
import cytoscape.util.intr.IntArray;
import cytoscape.util.intr.IntEnumerator;
import cytoscape.util.intr.IntIterator;
import cytoscape.util.intr.IntHash;
import cytoscape.util.intr.IntIntHash;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Vector;
import phoebe.event.GraphPerspectiveChangeListener;

public class FGraphPerspective
{
	public void addGraphPerspectiveChangeListener(
			GraphPerspectiveChangeListener listener)
	{
		// This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis[0] = GraphPerspectiveChangeListenerChain.add(m_lis[0], listener);
	}

	public void removeGraphPerspectiveChangeListener(
			GraphPerspectiveChangeListener listener)
	{
		// This method is not thread safe; synchronize on an object to make it
		// so.
		m_lis[0] = GraphPerspectiveChangeListenerChain.remove(m_lis[0],
				listener);
	}

	// The object returned shares the same RootGraph with this object.
	public Object clone()
	{
		final IntEnumerator nativeNodes = m_graph.nodes();
		final IntIterator rootGraphNodeInx = new IntIterator() {
			public boolean hasNext()
			{
				return nativeNodes.numRemaining() > 0;
			}

			public int nextInt()
			{
				return m_nativeToRootNodeInxMap.getIntAtIndex(nativeNodes
						.nextInt());
			}
		};
		final IntEnumerator nativeEdges = m_graph.edges();
		final IntIterator rootGraphEdgeInx = new IntIterator() {
			public boolean hasNext()
			{
				return nativeEdges.numRemaining() > 0;
			}

			public int nextInt()
			{
				return m_nativeToRootEdgeInxMap.getIntAtIndex(nativeEdges
						.nextInt());
			}
		};
		return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
	}

	public FRootGraph getRootGraph()
	{
		return m_root;
	}

	public int getNodeCount()
	{
		return m_graph.nodes().numRemaining();
	}

	public int getEdgeCount()
	{
		return m_graph.edges().numRemaining();
	}

	public Iterator<FNode> nodesIterator()
	{
		final IntEnumerator nodes = m_graph.nodes();
		return new Iterator<FNode>() {
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public boolean hasNext()
			{
				return nodes.numRemaining() > 0;
			}

			public FNode next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				return m_root.getNode(m_nativeToRootNodeInxMap
						.getIntAtIndex(nodes.nextInt()));
			}
		};
	}

	public int[] getNodeIndicesArray()
	{
		IntEnumerator nodes = m_graph.nodes();
		final int[] returnThis = new int[nodes.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootNodeInxMap.getIntAtIndex(nodes
					.nextInt());
		return returnThis;
	}

	public Iterator<FEdge> edgesIterator()
	{
		final IntEnumerator edges = m_graph.edges();
		return new Iterator<FEdge>() {
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			public boolean hasNext()
			{
				return edges.numRemaining() > 0;
			}

			public FEdge next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				return m_root.getEdge(m_nativeToRootEdgeInxMap
						.getIntAtIndex(edges.nextInt()));
			}
		};
	}

	public int[] getEdgeIndicesArray()
	{
		IntEnumerator edges = m_graph.edges();
		final int[] returnThis = new int[edges.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(edges
					.nextInt());
		return returnThis;
	}

	public boolean hideNode(int rootGraphNodeInx)
	{
		return m_weeder.hideNode(this, rootGraphNodeInx);
	}

	public void hideNodes(int[] rootGraphNodeInx)
	{
		m_weeder.hideNodes(this, rootGraphNodeInx);
	}

	public FNode restoreNode(FNode node)
	{
		if (node.getRootGraph() == m_root
				&& restoreNode(node.getRootGraphIndex()))
			return node;
		else
			return null;
	}

	public boolean restoreNode(int rootGraphNodeInx)
	{
		boolean res = _restoreNode(rootGraphNodeInx);
		if (res)
		{
			final GraphPerspectiveChangeListener listener = m_lis[0];
			if (listener != null)
			{
				listener
						.graphPerspectiveChanged(new GraphPerspectiveNodesRestoredEvent(
								this, new int[] { rootGraphNodeInx }));
			}
		}
		return res;
	}

	private boolean _restoreNode(final int rootGraphNodeInx)
	{
		if (!(rootGraphNodeInx < 0)) return false;
		int nativeNodeInx = m_rootToNativeNodeInxMap.get(~rootGraphNodeInx);
		if (m_root.getNode(rootGraphNodeInx) == null
				|| !(nativeNodeInx < 0 || nativeNodeInx == Integer.MAX_VALUE))
			return false;
		nativeNodeInx = m_graph.nodeCreate();
		m_rootToNativeNodeInxMap.put(~rootGraphNodeInx, nativeNodeInx);
		m_nativeToRootNodeInxMap.setIntAtIndex(rootGraphNodeInx, nativeNodeInx);
		return true;
	}

	public void restoreNodes(int[] nodes)
	{
		Vector<Integer> successes = new Vector<Integer>(nodes.length);
		for (int i = 0; i < nodes.length; i++)
			if (_restoreNode(nodes[i]))  
				 successes.add(nodes[i]);
		if (successes.size() == 0) return;
		final GraphPerspectiveChangeListener l = m_lis[0];
		if (l == null) return;
		successes.trimToSize();
		int[] res = new int[successes.size()];
		for (int i = 0; i < successes.size(); i++)
			res[i] = successes.get(i);
		l.graphPerspectiveChanged(new GraphPerspectiveNodesRestoredEvent(this,
				res));
	}

	public boolean hideEdge(int rootGraphEdgeInx)
	{
		return m_weeder.hideEdge(this, rootGraphEdgeInx);
	}

	public void hideEdges(int[] rootGraphEdgeInx)
	{
		m_weeder.hideEdges(this, rootGraphEdgeInx);
	}

	public boolean restoreEdge(int rootGraphEdgeInx)
	{
		boolean res = _restoreEdge(rootGraphEdgeInx);
		if (res)
		{
			final GraphPerspectiveChangeListener listener = m_lis[0];
			if (listener != null)
			{
				listener
						.graphPerspectiveChanged(new GraphPerspectiveEdgesRestoredEvent(
								this, new int[] { rootGraphEdgeInx }));
			}
		}
		return res;
	}
	
	// Returns 0 if unsuccessful; otherwise returns the root index of edge.
	private boolean _restoreEdge(final int rootGraphEdgeInx)
	{
		if (!(rootGraphEdgeInx < 0)) return false;
		int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~rootGraphEdgeInx);
		if (m_root.getEdge(rootGraphEdgeInx) == null
				|| !(nativeEdgeInx < 0 || nativeEdgeInx == Integer.MAX_VALUE))
			return false;
		final int rootGraphSourceNodeInx = m_root
				.getEdgeSourceIndex(rootGraphEdgeInx);
		final int rootGraphTargetNodeInx = m_root
				.getEdgeTargetIndex(rootGraphEdgeInx);
		int nativeSourceNodeInx = m_rootToNativeNodeInxMap
				.get(~rootGraphSourceNodeInx);
		int nativeTargetNodeInx = m_rootToNativeNodeInxMap
				.get(~rootGraphTargetNodeInx);
		
		Vector<Integer> restoredNodeRootInx = new Vector<Integer>(2);
		if (nativeSourceNodeInx < 0 || nativeSourceNodeInx == Integer.MAX_VALUE)
			if(_restoreNode(rootGraphSourceNodeInx))
			   restoredNodeRootInx.add(rootGraphSourceNodeInx);
		
		if (nativeTargetNodeInx < 0 || nativeTargetNodeInx == Integer.MAX_VALUE)
		{
			if(_restoreNode(rootGraphTargetNodeInx))
			    restoredNodeRootInx.add(rootGraphTargetNodeInx);
		}
		if (restoredNodeRootInx.size() > 0 && m_lis[0] != null)
		{
			restoredNodeRootInx.trimToSize();
			int[] res = new int[restoredNodeRootInx.size()];
			for(int i = 0; i < res.length; i++)
				res[i] = restoredNodeRootInx.get(i);
				m_lis[0].graphPerspectiveChanged(
						new GraphPerspectiveNodesRestoredEvent(this, res));
		}
		nativeEdgeInx = m_graph.edgeCreate(nativeSourceNodeInx,
				nativeTargetNodeInx, m_root.isEdgeDirected(rootGraphEdgeInx));
		m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx, nativeEdgeInx);
		m_nativeToRootEdgeInxMap.setIntAtIndex(rootGraphEdgeInx, nativeEdgeInx);
		return true;
	}

	public FGraphPerspective join(FGraphPerspective persp)
	{
		final FGraphPerspective thisPersp = this;
		if (!(persp instanceof FGraphPerspective)) return null;
		final FGraphPerspective otherPersp = (FGraphPerspective) persp;
		if (otherPersp.m_root != thisPersp.m_root) return null;
		final IntEnumerator thisNativeNodes = thisPersp.m_graph.nodes();
		final IntEnumerator otherNativeNodes = otherPersp.m_graph.nodes();
		final IntIterator rootGraphNodeInx = new IntIterator() {
			public boolean hasNext()
			{
				return thisNativeNodes.numRemaining() > 0
						|| otherNativeNodes.numRemaining() > 0;
			}

			public int nextInt()
			{
				if (thisNativeNodes.numRemaining() > 0)
					return thisPersp.m_nativeToRootNodeInxMap
							.getIntAtIndex(thisNativeNodes.nextInt());
				else
					return otherPersp.m_nativeToRootNodeInxMap
							.getIntAtIndex(otherNativeNodes.nextInt());
			}
		};
		final IntEnumerator thisNativeEdges = thisPersp.m_graph.edges();
		final IntEnumerator otherNativeEdges = otherPersp.m_graph.edges();
		final IntIterator rootGraphEdgeInx = new IntIterator() {
			public boolean hasNext()
			{
				return thisNativeEdges.numRemaining() > 0
						|| otherNativeEdges.numRemaining() > 0;
			}

			public int nextInt()
			{
				if (thisNativeEdges.numRemaining() > 0)
					return thisPersp.m_nativeToRootEdgeInxMap
							.getIntAtIndex(thisNativeEdges.nextInt());
				else
					return otherPersp.m_nativeToRootEdgeInxMap
							.getIntAtIndex(otherNativeEdges.nextInt());
			}
		};
		return new FGraphPerspective(m_root, rootGraphNodeInx, rootGraphEdgeInx);
	}

	public int[] neighborsArray(final int nodeIndex)
	{
		int[] adjacentEdgeIndices = getAdjacentEdgeIndicesArray(nodeIndex,
				true, true, true);
		if (adjacentEdgeIndices == null) return null;
		m_hash.empty();
		final IntHash neighbors = m_hash;
		for (int i = 0; i < adjacentEdgeIndices.length; i++)
		{
			int neighborIndex = (nodeIndex
					^ getEdgeSourceIndex(adjacentEdgeIndices[i]) ^ getEdgeTargetIndex(adjacentEdgeIndices[i]));
			neighbors.put(~neighborIndex);
		}
		IntEnumerator en = neighbors.elements();
		final int[] returnThis = new int[en.numRemaining()];
		int index = -1;
		while (en.numRemaining() > 0)
			returnThis[++index] = ~(en.nextInt());
		return returnThis;
	}

	public int getIndex(FNode node)
	{
		if (node.getRootGraph() == m_root
				&& getRootGraphNodeIndex(node.getRootGraphIndex()) == node
						.getRootGraphIndex())
			return node.getRootGraphIndex();
		else
			return 0;
	}

	public int getNodeIndex(int rootGraphNodeInx)
	{
		return getRootGraphNodeIndex(rootGraphNodeInx);
	}

	public int getRootGraphNodeIndex(int rootGraphNodeInx)
	{
		if (!(rootGraphNodeInx < 0)) return 0;
		final int nativeNodeInx = m_rootToNativeNodeInxMap
				.get(~rootGraphNodeInx);
		if (nativeNodeInx < 0 || nativeNodeInx == Integer.MAX_VALUE) return 0;
		return rootGraphNodeInx;
	}

	public FNode getNode(int rootGraphNodeInx)
	{
		return m_root.getNode(getRootGraphNodeIndex(rootGraphNodeInx));
	}

	public int getIndex(FEdge edge)
	{
		if (edge.getRootGraph() == m_root
				&& getRootGraphEdgeIndex(edge.getRootGraphIndex()) == edge
						.getRootGraphIndex())
			return edge.getRootGraphIndex();
		else
			return 0;
	}

	public int getEdgeIndex(int rootGraphEdgeInx)
	{
		return getRootGraphEdgeIndex(rootGraphEdgeInx);
	}

	public int getRootGraphEdgeIndex(int rootGraphEdgeInx)
	{
		if (!(rootGraphEdgeInx < 0)) return 0;
		final int nativeEdgeInx = m_rootToNativeEdgeInxMap
				.get(~rootGraphEdgeInx);
		if (nativeEdgeInx < 0 || nativeEdgeInx == Integer.MAX_VALUE) return 0;
		return rootGraphEdgeInx;
	}

	public FEdge getEdge(int rootGraphEdgeInx)
	{
		return m_root.getEdge(getRootGraphEdgeIndex(rootGraphEdgeInx));
	}

	public int getEdgeSourceIndex(int edgeInx)
	{
		if (!(edgeInx < 0)) return 0;
		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
		final int nativeSrcNodeInx = m_graph.edgeSource(nativeEdgeInx);
		if (nativeSrcNodeInx < 0) return 0;
		return m_nativeToRootNodeInxMap.getIntAtIndex(nativeSrcNodeInx);
	}

	public int getEdgeTargetIndex(int edgeInx)
	{
		if (!(edgeInx < 0)) return 0;
		final int nativeEdgeInx = m_rootToNativeEdgeInxMap.get(~edgeInx);
		final int nativeTrgNodeInx = m_graph.edgeTarget(nativeEdgeInx);
		if (nativeTrgNodeInx < 0) return 0;
		return m_nativeToRootNodeInxMap.getIntAtIndex(nativeTrgNodeInx);
	}

	public int[] getAdjacentEdgeIndicesArray(int nodeInx, boolean undirected,
			boolean incomingDirected, boolean outgoingDirected)
	{
		if (!(nodeInx < 0)) return new int[0];
		final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx);
		final IntEnumerator adj = m_graph.edgesAdjacent(nativeNodeInx,
				outgoingDirected, incomingDirected, undirected);
		if (adj == null) return new int[0];
		final int[] returnThis = new int[adj.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap.getIntAtIndex(adj
					.nextInt());
		return returnThis;
	}

	public int[] getConnectingEdgeIndicesArray(int[] nodeInx)
	{
		final IntHash nativeNodeBucket = new IntHash();
		for (int i = 0; i < nodeInx.length; i++)
		{
			if (!(nodeInx[i] < 0)) return null;
			final int nativeNodeInx = m_rootToNativeNodeInxMap.get(~nodeInx[i]);
			if (m_graph.nodeExists(nativeNodeInx))
				nativeNodeBucket.put(nativeNodeInx);
			else
				return null;
		}
		m_hash.empty();
		final IntHash nativeEdgeBucket = m_hash;
		final IntEnumerator nativeNodeEnum = nativeNodeBucket.elements();
		while (nativeNodeEnum.numRemaining() > 0)
		{
			final int nativeNodeIndex = nativeNodeEnum.nextInt();
			final IntEnumerator nativeAdjEdgeEnum = m_graph.edgesAdjacent(
					nativeNodeIndex, true, false, true);
			while (nativeAdjEdgeEnum.numRemaining() > 0)
			{
				final int nativeCandidateEdge = nativeAdjEdgeEnum.nextInt();
				final int nativeOtherEdgeNode = (nativeNodeIndex
						^ m_graph.edgeSource(nativeCandidateEdge) ^ m_graph
						.edgeTarget(nativeCandidateEdge));
				if (nativeOtherEdgeNode == nativeNodeBucket
						.get(nativeOtherEdgeNode))
					nativeEdgeBucket.put(nativeCandidateEdge);
			}
		}
		final IntEnumerator nativeReturnEdges = nativeEdgeBucket.elements();
		final int[] returnThis = new int[nativeReturnEdges.numRemaining()];
		for (int i = 0; i < returnThis.length; i++)
			returnThis[i] = m_nativeToRootEdgeInxMap
					.getIntAtIndex(nativeReturnEdges.nextInt());
		return returnThis;
	}

	public void finalize()
	{
		m_root.removeRootGraphChangeListener(m_changeSniffer);
	}
	// Nodes and edges in this graph are called "native indices" throughout
	// this class.
	private final DynamicGraphRepresentation m_graph;
	private final FRootGraph m_root;
	// This is an array of length 1 - we need an array as an extra reference
	// to a reference because some other inner classes need to know what the
	// current listener is.
	private final GraphPerspectiveChangeListener[] m_lis;
	// RootGraph indices are negative in these arrays.
	private final IntArray m_nativeToRootNodeInxMap;
	private final IntArray m_nativeToRootEdgeInxMap;
	// RootGraph indices are ~ (complements) of the real RootGraph indices
	// in these hashtables.
	private final IntIntHash m_rootToNativeNodeInxMap;
	private final IntIntHash m_rootToNativeEdgeInxMap;
	// This is a utilitarian hash that is used as a collision detecting
	// bucket of ints. Don't forget to empty() it before using it.
	private final IntHash m_hash;
	private final GraphWeeder m_weeder;
	// We need to remove this listener from the RootGraph during finalize().
	private final RootGraphChangeSniffer m_changeSniffer;

	// rootGraphNodeInx need not contain all endpoint nodes corresponding to
	// edges in
	// rootGraphEdgeInx - this is calculated automatically by this constructor.
	// If any index does not correspond to an existing node or edge, an
	// IllegalArgumentException is thrown. The indices lists need not be
	// non-repeating - the logic in this constructor handles duplicate
	// filtering.
	public FGraphPerspective(FRootGraph root, IntIterator rootGraphNodeInx,
			IntIterator rootGraphEdgeInx) throws IllegalArgumentException // If
	// any
	// index
	// is
	// not
	// in
	// RootGraph.
	{
		m_graph = new DynamicGraphRepresentation();
		m_root = root;
		m_lis = new GraphPerspectiveChangeListener[1];
		m_nativeToRootNodeInxMap = new IntArray();
		m_nativeToRootEdgeInxMap = new IntArray();
		m_rootToNativeNodeInxMap = new IntIntHash();
		m_rootToNativeEdgeInxMap = new IntIntHash();
		m_hash = new IntHash();
		m_weeder = new GraphWeeder(m_graph, m_nativeToRootNodeInxMap,
				m_nativeToRootEdgeInxMap, m_rootToNativeNodeInxMap,
				m_rootToNativeEdgeInxMap, m_lis);
		m_changeSniffer = new RootGraphChangeSniffer(m_weeder);
		while (rootGraphNodeInx.hasNext())
		{
			final int rootNodeInx = rootGraphNodeInx.nextInt();
			if (m_root.getNode(rootNodeInx) != null)
			{
				if (m_rootToNativeNodeInxMap.get(~rootNodeInx) >= 0) continue;
				final int nativeNodeInx = m_graph.nodeCreate();
				m_rootToNativeNodeInxMap.put(~rootNodeInx, nativeNodeInx);
				m_nativeToRootNodeInxMap.setIntAtIndex(rootNodeInx,
						nativeNodeInx);
			} else
				throw new IllegalArgumentException("node with index "
						+ rootNodeInx + " not in RootGraph");
		}
		while (rootGraphEdgeInx.hasNext())
		{
			final int rootEdgeInx = rootGraphEdgeInx.nextInt();
			if (m_root.getEdge(rootEdgeInx) != null)
			{
				if (m_rootToNativeEdgeInxMap.get(~rootEdgeInx) >= 0) continue;
				final int rootSrcInx = m_root.getEdgeSourceIndex(rootEdgeInx);
				final int rootTrgInx = m_root.getEdgeTargetIndex(rootEdgeInx);
				final boolean edgeDirected = m_root.isEdgeDirected(rootEdgeInx);
				int nativeSrcInx = m_rootToNativeNodeInxMap.get(~rootSrcInx);
				if (nativeSrcInx < 0)
				{
					nativeSrcInx = m_graph.nodeCreate();
					m_rootToNativeNodeInxMap.put(~rootSrcInx, nativeSrcInx);
					m_nativeToRootNodeInxMap.setIntAtIndex(rootSrcInx,
							nativeSrcInx);
				}
				int nativeTrgInx = m_rootToNativeNodeInxMap.get(~rootTrgInx);
				if (nativeTrgInx < 0)
				{
					nativeTrgInx = m_graph.nodeCreate();
					m_rootToNativeNodeInxMap.put(~rootTrgInx, nativeTrgInx);
					m_nativeToRootNodeInxMap.setIntAtIndex(rootTrgInx,
							nativeTrgInx);
				}
				final int nativeEdgeInx = m_graph.edgeCreate(nativeSrcInx,
						nativeTrgInx, edgeDirected);
				m_rootToNativeEdgeInxMap.put(~rootEdgeInx, nativeEdgeInx);
				m_nativeToRootEdgeInxMap.setIntAtIndex(rootEdgeInx,
						nativeEdgeInx);
			} else
				throw new IllegalArgumentException("edge with index "
						+ rootEdgeInx + " not in RootGraph");
		}
		//m_root.addRootGraphChangeListener(m_changeSniffer);
	}

	// Cannot have any recursize reference to a FGraphPerspective in this
	// object instance - we want to allow garbage collection of unused
	// GraphPerspective objects.
	private final static class RootGraphChangeSniffer implements
			RootGraphChangeListener
	{
		private final GraphWeeder m_weeder;

		private RootGraphChangeSniffer(GraphWeeder weeder)
		{
			m_weeder = weeder;
		}

		public final void rootGraphChanged(RootGraphChangeEvent evt)
		{
			if ((evt.getType() & RootGraphChangeEvent.NODES_REMOVED_TYPE) != 0)
				m_weeder.hideNodes((FGraphPerspective) evt.getSource(), evt
						.getRemovedNodeIndices());
			if ((evt.getType() & RootGraphChangeEvent.EDGES_REMOVED_TYPE) != 0)
				m_weeder.hideEdges(((FGraphPerspective) evt.getSource()), evt
						.getRemovedEdgeIndices());
		}
	}

	// An instance of this class cannot have any recursive reference to a
	// FGraphPerspective object. The idea behind this class is to allow
	// garbage collection of unused GraphPerspective objects. This class
	// is used by the RootGraphChangeSniffer to remove nodes/edges from
	// a GraphPerspective; this class is also used by this GraphPerspective
	// implementation itself.
	private final static class GraphWeeder
	{
		private final DynamicGraphRepresentation m_graph;
		private final IntArray m_nativeToRootNodeInxMap;
		private final IntArray m_nativeToRootEdgeInxMap;
		private final IntIntHash m_rootToNativeNodeInxMap;
		private final IntIntHash m_rootToNativeEdgeInxMap;
		// This is an array of length 1 - we need an array as an extra reference
		// to a reference because the surrounding GraphPerspective will be
		// modifying the entry at index 0 in this array.
		private final GraphPerspectiveChangeListener[] m_lis;

		private GraphWeeder(DynamicGraphRepresentation graph,
				IntArray nativeToRootNodeInxMap,
				IntArray nativeToRootEdgeInxMap,
				IntIntHash rootToNativeNodeInxMap,
				IntIntHash rootToNativeEdgeInxMap,
				GraphPerspectiveChangeListener[] listener)
		{
			m_graph = graph;
			m_nativeToRootNodeInxMap = nativeToRootNodeInxMap;
			m_nativeToRootEdgeInxMap = nativeToRootEdgeInxMap;
			m_rootToNativeNodeInxMap = rootToNativeNodeInxMap;
			m_rootToNativeEdgeInxMap = rootToNativeEdgeInxMap;
			m_lis = listener;
		}

		// RootGraphChangeSniffer is not to call this method. We rely on
		// the specified node still existing in the RootGraph in this method.
		private boolean hideNode(FGraphPerspective source, int rootGraphNodeInx)
		{
			boolean returnThis = _hideNode(source, rootGraphNodeInx);
			if (returnThis)
			{
				final GraphPerspectiveChangeListener listener = m_lis[0];
				if (listener != null)
				{
					listener
							.graphPerspectiveChanged(new GraphPerspectiveNodesHiddenEvent(
									source, new int[] { rootGraphNodeInx }));
				}
			}
			return returnThis;
		}

		// Don't call this method from outside this inner class.
		private boolean _hideNode(FGraphPerspective source,
				final int rootGraphNodeInx)
		{
			if (!(rootGraphNodeInx < 0)) return false;
			final int nativeNodeIndex = m_rootToNativeNodeInxMap
					.get(~rootGraphNodeInx);
			if (nativeNodeIndex < 0) return false;
			final IntEnumerator nativeEdgeInxEnum = m_graph.edgesAdjacent(
					nativeNodeIndex, true, true, true);
			if (nativeEdgeInxEnum == null) return false;
			if (nativeEdgeInxEnum.numRemaining() > 0)
			{
				final int[] edgeRemoveArr = new int[nativeEdgeInxEnum
						.numRemaining()];
				for (int i = 0; i < edgeRemoveArr.length; i++)
				{
					final int rootGraphEdgeInx = m_nativeToRootEdgeInxMap
							.getIntAtIndex(nativeEdgeInxEnum.nextInt());
					// The edge returned by the RootGraph won't be null even if
					// this
					// hideNode operation is triggered by a node being removed
					// from
					// the underlying RootGraph - this is because when a node is
					// removed
					// from an underlying RootGraph, all touching edges to that
					// node are
					// removed first from that RootGraph, and corresponding edge
					// removal
					// events are fired before the node removal event is fired.
					edgeRemoveArr[i] = rootGraphEdgeInx;
				}
				hideEdges(source, edgeRemoveArr);
			}
			// nativeNodeIndex tested for validity with adjacentEdges() above.
			if (m_graph.nodeRemove(nativeNodeIndex))
			{
				m_rootToNativeNodeInxMap.put(~rootGraphNodeInx,
						Integer.MAX_VALUE);
				m_nativeToRootNodeInxMap.setIntAtIndex(0, nativeNodeIndex);
				return true;
			} else
				throw new IllegalStateException(
						"internal error - node didn't exist, its adjacent edges did");
		}

		// RootGraphChangeSniffer is not to call this method. We rely on
		// the specified nodes still existing in the RootGraph in this method.
		private final void hideNodes(FGraphPerspective source, int[] rootNodeInx)
		{
			Vector<Integer> successes = new Vector<Integer>(rootNodeInx.length);
			for (int i = 0; i < rootNodeInx.length; i++)
				if (_hideNode(source, rootNodeInx[i])) successes.add(i);
			if (successes.size() == 0) return;
			GraphPerspectiveChangeListener l = m_lis[0];
			if (l == null) return;
			successes.trimToSize();
			int[] res = new int[successes.size()];
			for (int i = 0; i < successes.size(); i++)
				res[i] = successes.get(i);
			l.graphPerspectiveChanged(new GraphPerspectiveNodesHiddenEvent(
					source, res));
		}

		// RootGraphChangeSniffer is not to call this method. We rely on
		// the specified edge still existing in the RootGraph in this method.
		private final boolean hideEdge(FGraphPerspective source,
				int rootGraphEdgeInx)
		{
			final boolean res = _hideEdge(rootGraphEdgeInx);
			if (!res) return res;
			final GraphPerspectiveChangeListener listener = m_lis[0];
			if (listener != null)
			{
				listener
						.graphPerspectiveChanged(new GraphPerspectiveEdgesHiddenEvent(
								source, new int[] { rootGraphEdgeInx }));
			}
			return res;
		}

		// Don't call this method from outside this inner class.
		// Returns 0 if and only if hiding this edge was unsuccessful.
		// Otherwise returns the input parameter, the root edge index.
		private boolean _hideEdge(int rootGraphEdgeInx)
		{
			if (!(rootGraphEdgeInx < 0)) return false;
			final int nativeEdgeIndex = m_rootToNativeEdgeInxMap
					.get(~rootGraphEdgeInx);
			if (nativeEdgeIndex < 0) return false;
			if (m_graph.edgeRemove(nativeEdgeIndex))
			{
				m_rootToNativeEdgeInxMap.put(~rootGraphEdgeInx,
						Integer.MAX_VALUE);
				m_nativeToRootEdgeInxMap.setIntAtIndex(0, nativeEdgeIndex);
				return true;
			} else
				return false;
		}

		// RootGraphChangeSniffer is not to call this method. We rely on
		// the specified edges still existing in the RootGraph in this method.
		private void hideEdges(FGraphPerspective source, int[] edges)
		{
			Vector<Integer> successes = new Vector<Integer>(edges.length);
			for (int i = 0; i < edges.length; i++)
				if (_hideEdge(edges[i])) successes.add(edges[i]);
			if (successes.size() == 0) return;
			final GraphPerspectiveChangeListener l = m_lis[0];
			if (l == null) return;
			successes.trimToSize();
			final int[] res = new int[successes.size()];
			for(int i = 0; i < successes.size(); i++)
				res[i] = successes.get(i);
			l.graphPerspectiveChanged(new GraphPerspectiveEdgesHiddenEvent(
					source, res));
		}
	}
}