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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nightlabs.util.CollectionUtil;

/**
 * This class is a modified version of a trie (see e.g. <a href="http://de.wikipedia.org/wiki/Trie">Wikipedia</a>).
 * <p>
 * It allows to insert objects of type {@link T} by specifying a search path in the form of
 * a string array and efficiently retrieve them afterwards by specifying search paths, again.
 * One can also get all the elements in the subtrie rooted at the end of a given path using
 * method {@link #getSubtrieElements(String[])}.
 * </p><p>
 * This behaviour is especially useful in context of wildcards:
 * If one inserts several objects with the following paths:
 * <ul>
 * <li>object 1: <code>foo,bar,test</code></li>
 * <li>object 2: <code>foo,bar,tmp</code></li>
 * <li>object 3: <code>foo,bar,bla</code></li>
 * </ul>
 * one can get all the elements matching the path <code>foo,bar,*</code> by getting the subtrie rooted
 * at the end of the path <code>foo,bar</code>.
 * </p>
 *
 * @version $Revision$ - $Date$
 * @author Tobias Langner <!-- tobias[dot]langner[at]nightlabs[dot]de -->
 *
 * @param <T> The type of the objects to be managed in the PrefixTree
 */
public class PrefixTree<T> {
	private TrieNode<T> root = new TrieNode<T>(null);

	public PrefixTree() {}

	/**
	 * Inserts the given element at the given path.
	 * @param path the path where to insert the element.
	 * @param element the element to be inserted.
	 */
	public void insert(String[] path, T element) {
		insert(element, path, 0, root);
	}

	protected void insert(T element, String[] path, int index, TrieNode<T> node) {
		TrieNode<T> childNode = node.getChild(path[index]);

		if (childNode == null) {
			childNode = node.addChild(path[index]);

			if (index == path.length-1) { // last element was inserted
				childNode.setElement(element);
				return;
			}
		}
		insert(element, path, index+1, childNode);
	}

	public TrieNode<T> getNode(String[] path) {
		TrieNode<T> currentNode = root;

		for (String element : path) {
			currentNode = currentNode.getChild(element);
			if (currentNode == null)
				return null;
		}

		return currentNode;
	}

	/**
	 * Returns a list of the nodes of the subtrie rooted at the given path.
	 * @param path the path that identifies the subtree.
	 * @return a list of the nodes of the subtrie rooted at the given path.
	 */
	@SuppressWarnings("unchecked")
	public List<T> getSubtrieElements(String[] path) {
//
//		TrieNode<T> currentNode = root;
//
//		for (int i = 0; i < path.length; i++) {
//			currentNode = currentNode.getChild(path[i]);
//			if (currentNode == null)
//				return Collections.EMPTY_LIST;
//		}
		TrieNode<T> node = getNode(path);
		if (node == null)
			return Collections.EMPTY_LIST;
		else
			return traverseSubtrie(node, null);
	}

	@SuppressWarnings("unchecked")
	public Map<String, T> getSubtrieElementsWithKeys(String[] path) {
		TrieNode<T> node = getNode(path);
		if (node == null)
			return Collections.EMPTY_MAP;
		else
			return traverseSubtrieWithKeys(node, CollectionUtil.array2ArrayList(path), null);
	}

	/**
	 * Returns a list containing all elements in the subtrie rooted at the given node.
	 *
	 * @param rootNode The root node of the subtrie whose elements are to be returned.
	 * @param traversedElements a growing list containing the elements traversed so far.
	 * @return a list containing all elements in the subtrie rooted at the given node.
	 */
	private List<T> traverseSubtrie(TrieNode<T> rootNode, List<T> traversedElements) {
		if (traversedElements == null)
			traversedElements = new LinkedList<T>();

		if (rootNode.hasElement())
			//traversedElements.add(new LinkedList<String>(path));
			traversedElements.add(rootNode.getElement());

		for (String key : rootNode.getChildrenMap().keySet()) {
			traverseSubtrie(rootNode.getChild(key), traversedElements);
		}

		return traversedElements;
	}

	private Map<String, T> traverseSubtrieWithKeys(TrieNode<T> root, List<String> path, Map<String, T> elems) {
		if (elems == null)
			elems = new HashMap<String, T>();

		if (root.hasElement()) {
			elems.put(path.toString(), root.getElement());
		}

		for (String key : root.getChildrenMap().keySet()) {
			path.add(key);
			traverseSubtrieWithKeys(root.getChild(key), path, elems);
			path.remove(key);
		}

		return elems;
	}

	@Override
	public String toString() {
		Map<String, T> elems = getSubtrieElementsWithKeys(new String[0]);
		String toReturn = "";
		for (String key : elems.keySet()){
			toReturn += key + "\n" + "\t-> " + elems.get(key) + "\n";
		}
		return toReturn;

//		List<T> trieElements = getSubtrieElements(new String[0]);
//		String toReturn = "";
//		for (T elem : trieElements) {
//			toReturn += elem + "\n";
//		}
//		return toReturn;
	}
}

class TrieNode<T> {
	private Map<String, TrieNode<T>> children = new HashMap<String, TrieNode<T>>();
	private T element;

	public TrieNode(T element) {
		this.element = element;
	}

	public TrieNode<T> addChild(String key) {
		TrieNode<T> childNode = new TrieNode<T>(null);
		children.put(key, childNode);
		return childNode;
	}

	public TrieNode<T> getChild(String key) {
		return children.get(key);
	}

	public Collection<TrieNode<T>> getChildren() {
		return children.values();
	}

	public Map<String, TrieNode<T>> getChildrenMap() {
		return children;
	}

	public void setElement(T element) {
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	public boolean hasElement() {
		return element != null;
	}

	@Override
	public String toString() {
		return element.toString();
	}
}