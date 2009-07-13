/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.datastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * This class serves to manage objects of type T as a directed graph.
 * It provides methods to traverse the nodes/elements of the graph topologically.
 *
 * @version $Revision$ - $Date$
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 * @param <T> The type of the elements that are to be represented by graph nodes.
 */
public class DirectedGraph<T> {
	private Map<T, DirectedGraphNode<T>> nodes = null;

	/**
	 * The feature according to which the nodes are topologically ordered and hence the direction
	 * according to which the graph is traversed.
	 *
	 * @author Marius Heinzmann - marius[at]nightlabs[dot]com
	 */
	public enum SortingDirection
	{
		/**
		 * Start with all nodes having zero incoming edges (inDegree == 0).
		 */
		InDegree,
		/**
		 * Start with all nodes having zero outgoing edges (outDegree == 0).
		 */
		OutDegree;
	}

	public DirectedGraph(Collection<? extends IDirectedGraphNode<T>> elems)
	{
		nodes = new HashMap<T, DirectedGraphNode<T>>();

		// insert all nodes into the tree
		for (IDirectedGraphNode<T> elem : elems) {
			nodes.put(elem.getValue(), new DirectedGraphNode<T>(elem.getValue()));
		}

		// generate edges between nodes
		for (IDirectedGraphNode<T> elem : elems) {
			for (T dep : elem.getChildren()) {
				nodes.get(elem.getValue()).addOutgoingEdge(nodes.get(dep));
			}
		}
	}

	/**
	 * Returns a list of the nodes of the graph sorted topologically.
	 *
	 * @return a list of the nodes of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no topological traversal is possible.
	 */
	public List<DirectedGraphNode<T>> sortNodesTopologically() throws CycleException {
		return sortNodesTopologically(SortingDirection.InDegree);
	}

	/**
	 * Returns a list of the nodes of the graph sorted topologically.
	 *
	 * @param direction the direction according to which the graph is traversed.
	 * @return a list of the nodes of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no topological traversal is possible.
	 */
	public List<DirectedGraphNode<T>> sortNodesTopologically(SortingDirection direction) throws CycleException {
		return sortNodesTopologically(null, direction);
	}

	/**
	 * Returns a list of the nodes of the graph sorted topologically. Ties between elements are broken by using the
	 * given {@link Comparator}. "Smaller" elements appear ahead of "greater" ones in the traversal.
	 *
	 * @param comparator The {@link Comparator} that is used to determine the next element in the traversal if there
	 * 		are multiple elements with the same "precedence". Can be null if no special order is needed.
	 * @return a list of the nodes of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no topological traversal is possible.
	 */
	public List<DirectedGraphNode<T>> sortNodesTopologically(final Comparator<T> comparator) throws CycleException {
		return sortNodesTopologically(comparator, SortingDirection.InDegree);
	}

	/**
	 * Returns a list of the nodes of the graph sorted topologically. Ties between elements are broken by using the
	 * given {@link Comparator}. "Smaller" elements appear ahead of "greater" ones in the traversal.
	 *
	 * @param comparator The {@link Comparator} that is used to determine the next element in the traversal if there
	 * 		are multiple elements with the same "precedence". Can be null if no special order is needed.
	 * @param direction the direction according to which the graph is traversed.
	 * @return a list of the nodes of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no topological traversal is possible.
	 */
	public List<DirectedGraphNode<T>> sortNodesTopologically(final Comparator<T> comparator, SortingDirection direction) throws CycleException {
		List<DirectedGraphNode<T>> remainingNodes = new ArrayList<DirectedGraphNode<T>>();
		List<DirectedGraphNode<T>> sortedNodes = new ArrayList<DirectedGraphNode<T>>();
		Queue<DirectedGraphNode<T>> edgeDegreeZeroQueue;

		// if a comparator is given, we use a priority queue to pick the next element, if not, we just a list.
		if (comparator != null) {
			Comparator<DirectedGraphNode<T>> nodeComparator = new Comparator<DirectedGraphNode<T>>() {
				public int compare(DirectedGraphNode<T> o1, DirectedGraphNode<T> o2) {
					return comparator.compare(o1.getElement(), o2.getElement());
				}
			};
			edgeDegreeZeroQueue = new PriorityQueue<DirectedGraphNode<T>>(10, nodeComparator);
		}	else {
			edgeDegreeZeroQueue = new LinkedList<DirectedGraphNode<T>>();
		}

		// add all elements to the status structures
		for (DirectedGraphNode<T> node : nodes.values())
		{
			node.backupEdges();

			if (node.getEdgeDegree(direction) == 0) {
				edgeDegreeZeroQueue.add(node);
			}
			else {
				remainingNodes.add(node);
			}
		}

		// add elements with in-degree zero to the traversed-list, remove the associated edges and carry on.
		while (!edgeDegreeZeroQueue.isEmpty()) {
			DirectedGraphNode<T> source = edgeDegreeZeroQueue.poll();

			sortedNodes.add(source);

			for (DirectedGraphNode<T> depNode : source.getChildren(direction))
			{
				depNode.removeEdge(direction, source);
				if (depNode.getEdgeDegree(direction) == 0) {
					edgeDegreeZeroQueue.add(depNode);
					remainingNodes.remove(depNode);
				}
			}
		}

		// restore in-degree information for further traversals
		for (DirectedGraphNode<T> node : nodes.values()) {
			node.restoreOriginalEdges();
		}

		if (!remainingNodes.isEmpty())
		{
			Set<Object> cyclicElements = new HashSet<Object>(remainingNodes.size());
			for (DirectedGraphNode<T> remainingNode : remainingNodes)
			{
				cyclicElements.add(remainingNode.getElement());
			}
			throw new CycleException(remainingNodes.toString(), cyclicElements);
		}

		return sortedNodes;
	}

	/**
	 * Returns a list of the elements of the graph sorted topologically.
	 * @return a list of the elements of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no
	 * 				topological traversal is possible.
	 */
	public List<T> sortElementsTopologically() throws CycleException {
		return sortElementsTopologically(SortingDirection.InDegree);
	}

	/**
	 * Returns a list of the elements of the graph sorted topologically.
	 * @param direction the direction according to which the graph is traversed.
	 * @return a list of the elements of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no
	 * 				topological traversal is possible.
	 */
	public List<T> sortElementsTopologically(SortingDirection direction) throws CycleException {
		return sortElementsTopologically(null, direction);
	}

	/**
	 * Returns a list of the elements of the graph sorted topologically. Ties between elements are broken by using the
	 * given {@link Comparator}. "Smaller" elements appear ahead of "greater" ones.
	 *
	 * @param comparator The {@link Comparator} that is used to determine the next element in the traversal if there
	 * 		are multiple elements with the same "precedence". Can be null if no special order is needed.
	 * @return a list of the elements of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no
	 * 				topological traversal is possible.
	 */
	public List<T> sortElementsTopologically(Comparator<T> comparator) throws CycleException {
		return sortElementsTopologically(comparator, SortingDirection.InDegree);
	}

	/**
	 * Returns a list of the elements of the graph sorted topologically. Ties between elements are broken by using the
	 * given {@link Comparator}. "Smaller" elements appear ahead of "greater" ones.
	 *
	 * @param comparator The {@link Comparator} that is used to determine the next element in the traversal if there
	 * 		are multiple elements with the same "precedence". Can be null if no special order is needed.
	 * @param direction the direction according to which the graph is traversed.
	 * @return a list of the elements of the graph sorted topologically.
	 * @throws CycleException when a cycle is detected in the graph and thus no
	 * 				topological traversal is possible.
	 */
	public List<T> sortElementsTopologically(Comparator<T> comparator, SortingDirection direction) throws CycleException {
		List<DirectedGraphNode<T>> sortedNodes = sortNodesTopologically(comparator, direction);
		List<T> sortedElements = new ArrayList<T>();
		for (DirectedGraphNode<T> node : sortedNodes)
			sortedElements.add(node.getElement());

		return sortedElements;
	}

	@Override
	public String toString() {
		String toReturn = "";
		for (DirectedGraphNode<T> node : nodes.values()) {
			toReturn += node.toString() + "\n";
//			+ " {";
//			for (DirectedGraphNode<T> child : node.getChildren())
//				toReturn += child.toString() + ",";
//			toReturn += "}\n";
		}
		return toReturn;
	}
}

class DirectedGraphNode<T>
{
	private List<DirectedGraphNode<T>> outgoingEdges = new ArrayList<DirectedGraphNode<T>>();
	private List<DirectedGraphNode<T>> incomingEdges = new ArrayList<DirectedGraphNode<T>>();
	private List<DirectedGraphNode<T>> outgoingEdgesStored;
	private List<DirectedGraphNode<T>> incomingEdgesStored;
	private T value;

	public DirectedGraphNode(T value)
	{
		assert value != null;
		this.value = value;
	}

	public int getEdgeDegree(DirectedGraph.SortingDirection order)
	{
		if (order == DirectedGraph.SortingDirection.InDegree)
			return getIncomingEdges().size();
		else
			return getOutgoingEdges().size();
	}

	public int getInDegree()
	{
		return incomingEdges.size();
	}

	public int getOutDegree()
	{
		return outgoingEdges.size();
	}

	/**
	 * Removes the given edge depending on the sorting strategie. If it is sorted according to the
	 * inDegree, then edges are only removed from the set of outgoing ones, else from the incoming
	 * ones.
	 *
	 * @param feature sorting feature.
	 * @param child the edge to remove.
	 */
	public void removeEdge(DirectedGraph.SortingDirection feature, DirectedGraphNode<T> child)
	{
		if (DirectedGraph.SortingDirection.InDegree == feature)
			getIncomingEdges().remove(child);
		else
			getOutgoingEdges().remove(child);
	}

	public void addOutgoingEdge(DirectedGraphNode<T> child)
	{
		if (!outgoingEdges.contains(child))
		{
			outgoingEdges.add(child);
			child.incomingEdges.add(this);
		}
	}

	public void addIncomingEdge(DirectedGraphNode<T> child)
	{
		if (!incomingEdges.contains(child))
		{
			incomingEdges.add(child);
			child.outgoingEdges.add(this);
		}
	}

	public List<DirectedGraphNode<T>> getChildren(DirectedGraph.SortingDirection feature)
	{
		if (feature == DirectedGraph.SortingDirection.InDegree)
			return getOutgoingEdges();

		return getIncomingEdges();
	}

	public List<DirectedGraphNode<T>> getIncomingEdges()
	{
		return incomingEdges;
	}

	public List<DirectedGraphNode<T>> getOutgoingEdges()
	{
		return outgoingEdges;
	}

	public void backupEdges()
	{
		incomingEdgesStored = new ArrayList<DirectedGraphNode<T>>(incomingEdges);
		outgoingEdgesStored = new ArrayList<DirectedGraphNode<T>>(outgoingEdges);
	}

	public void restoreOriginalEdges()
	{
		incomingEdges.clear();
		incomingEdges.addAll(incomingEdgesStored);
		outgoingEdges.clear();
		outgoingEdges.addAll(outgoingEdgesStored);
	}

	public T getElement()
	{
		return value;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("( ");
		sb.append(value.toString()).append(", outgoing edges = { ");
		for (DirectedGraphNode<T> edge : getOutgoingEdges())
		{
			sb.append(edge.getElement()).append(", ");
		}
		sb.delete(sb.length()-2, sb.length());

		sb.append(" }; incoming edges = { ");
		for (DirectedGraphNode<T> edge : getIncomingEdges())
		{
			sb.append(edge.getElement()).append(", ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append(" }");

		return sb.toString();
	}
}
