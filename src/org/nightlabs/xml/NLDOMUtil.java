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
package org.nightlabs.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * Helper methods to be used when xalan is not an option.
 *
 * @author Alexander Bieber <alex [AT] nightlabs [DOT] de>
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NLDOMUtil
{
	/**
	 * Find a single node by a given path.
	 *
	 * @param root The node to start from
	 * @param path A path to the node to find. Separate elements by "/"
	 * @return The first Node that can be found along the given path, or <code>null</code> if none
	 * 	can not be found.
	 */
	public static Node findSingleNode(Node root, String path)
	{
		Collection<Node> singleNode = findNodeList(root, path, true, false);
		if (singleNode.isEmpty())
			return null;

		// return the first element found when traversing the DOM-tree.
		return singleNode.iterator().next();
//		TODO: remove comments
//		StringTokenizer tok = new StringTokenizer(path, "/");
//		List<String> elements = new ArrayList<String>();
//		while (tok.hasMoreTokens()) {
//			elements.add(tok.nextToken());
//		}
//		Node searchRoot = root;
//		Node searchResult = null;
//		for (String element : elements) {
//			searchResult = findElementNode(element, searchRoot);
//			if (searchResult == null)
//				return null;
//		}
//		return searchResult;
	}


	/**
	 * Find a list of nodes by a given path.
	 *
	 * @param root The node to start from.
	 * @param path A path to the nodes to find. Separate elements by "/".
	 * @return A list with the first Node that can be found along the given path and all its siblings with the same node-name.
	 */
	public static Collection<Node> findNodeList(Node root, String path)
	{
		return findNodeList(root, path, true, true);
	}

	/**
	 * Find a list of nodes by a given path.
	 *
	 * @param root The node to start from.
	 * @param path A path to the nodes to find. Separate elements by "/".
	 * @param onlyReturnFirstHit Whether to return only the first element with the given name, or to
	 * 	include all siblings with the same name as well.
	 * @param checkSiblingsOfFirstHit whether or not to search all parallel paths.
	 * 	E.g. given <code>path="test/element"</code> and there is a DOM-tree that has several paths
	 * 	with that name, then we ought to return all nodes at the end of all those paths. If this
	 * 	parameter is <code>false</code> only the first of such paths is considered.
	 * @return A list with the all Nodes that can be found along the given path(s) and all its
	 * 	siblings with the same node-name. <b>Note</b>: This method never returns <code>null</code>.
	 */
	public static Collection<Node> findNodeList(Node root, String path, boolean onlyReturnFirstHit,
			boolean checkSiblingsOfFirstHit)
	{
		StringTokenizer tok = new StringTokenizer(path, "/");
		List<String> elements = new ArrayList<String>(tok.countTokens());
		while (tok.hasMoreTokens())
		{
			elements.add(tok.nextToken());
		}

		Collection<Node> currentLevelNodes = Collections.singletonList(root);
		Collection<Node> nextLevelNodes = null;
		// Do a breadth first search according to the given path
		for (String element : elements)
		{
			nextLevelNodes = new LinkedList<Node>();
			for (Node currentLevelNode : currentLevelNodes)
			{
				nextLevelNodes.addAll(findChildNodes(currentLevelNode, element));
			}

			currentLevelNodes = nextLevelNodes;
		}

		if (onlyReturnFirstHit && ! currentLevelNodes.isEmpty())
		{
			final Node firstHit = currentLevelNodes.iterator().next();

			// we only have the first element from the bottom-most level , we need to check
			// its siblings.
			if (checkSiblingsOfFirstHit)
			{
				Node resultsParent = firstHit.getParentNode();
				currentLevelNodes = findChildNodes(resultsParent, elements.get(elements.size()-1));
			}
			else if (currentLevelNodes.size() > 1)
			{
				currentLevelNodes = new LinkedList<Node>();
				currentLevelNodes.add(firstHit);
			}
		}

		// In the last iteration, the real search result is put into the nextLevelNodeCollection,
		// hence it is now also in the currentLevelNodeCollection.
		return currentLevelNodes;
	}

	/**
	 * Returns all children of the given <code>root</code> node that have the given
	 * <code>childName</code>.
	 *
	 * @param root the node, among whose children will be searched.
	 * @param childName the name of the children to return
	 * @return all children of the given <code>root</code> node that have the given
	 * <code>childName</code>.
	 */
	private static Collection<Node> findChildNodes(Node root, String childName)
	{
		if (root == null)
			return Collections.emptySet();

		List<Node> matchingChildren = new LinkedList<Node>();
		NodeList childNodes = root.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++)
		{
			final Node child = childNodes.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE)
			{
				final String nodeName = child.getLocalName();
				if ((nodeName != null) && (nodeName.equals(childName)))
				{
					matchingChildren.add(child);
				}
			}
		}
		return matchingChildren;
	}

	/**
	 * Recursively find the first element with the given node-name, starting from
	 * the given root.
	 *
	 * <p><b>Note</b>: If you know the path under which to search for the given element, then it's
	 * better to use {@link #findSingleNode(Node, String)} instead of this method.</p>
	 *
	 * @param elementName The node-name of the element to find.
	 * @param root The node to start from.
	 * @return The first node that can be found with the given node-name in the node tree starting
	 * 	from the given root.
	 */
	public static Node findElementNode(String elementName, Node root){

		Node matchingNode = null;

		//Check to see if root is the desired element. If so return root.
		if (root == null)
			return null;
		if (root.getNodeType() == Node.ELEMENT_NODE) {

			String nodeName = root.getLocalName();

			if((nodeName != null) && (nodeName.equals(elementName))) {
				return root;
			}
		}

		//Check to see if root has any children if not return null
		if(!(root.hasChildNodes()))
			return null;

		//Root has children, so continue searching for them
		NodeList childNodes = root.getChildNodes();
		int noChildren = childNodes.getLength();
		for(int i = 0; i < noChildren; i++){
			if(matchingNode == null){
				Node child = childNodes.item(i);
				matchingNode = findElementNode(elementName,child);
			} else break;

		}

		return matchingNode;
	}

  /**
   * Get the value string of an attribute and ensure it is not empty
   * or <code>null</code>.
   * @param node The element
   * @param name The name of the attribute
   * @return the attribute value
   */
  public static String getNonEmptyAttributeValue(Node node, String name)
  {
    String value = getAttributeValue(node, name);
    if(value == null || "".equals(value))
      throw new IllegalArgumentException("Attribute with name '"+name+"' is empty or does not exist for node "+node.getNodeName());
    return value;
  }

  /**
   * Get the value string of an attribute.
   * @param node The element
   * @param name The name of the attribute
   * @return the attribute value or <code>null</code> if the
   * 		attribute cannot be found.
   */
  public static String getAttributeValue(Node node, String name)
  {
    NamedNodeMap attributes = node.getAttributes();
    if(attributes == null)
      return null;
    Node namedItem = attributes.getNamedItem(name);
    if(namedItem == null)
      return null;
    return namedItem.getNodeValue();
  }

  /**
   * Get a text value from an element (i.e. the text within the opening and the closing tags).
   * This will return all nested text nodes concatenated to one string. Inner XML elements
   * or comments are not included.
   * @param node the node from which to obtain the text.
   * @return the text - an empty string if there is no text content, never <code>null</code>
   */
  public static String getTextContent(Node node)
  {
	  StringBuilder sb = new StringBuilder();
	  NodeList nl = node.getChildNodes();
	  for (int i = 0; i < nl.getLength(); ++i) {
		  Node ntxt = nl.item(i);
		  if(ntxt.getNodeType() == Node.TEXT_NODE) {
			  if (sb == null)
				  sb = new StringBuilder();
			  sb.append(((Text)ntxt).getData());
		  }
	  }
	  return sb.toString();
  }


  /**
   * Get a text value from an element (i.e. the text within the opening and the closing tags).
   * @param node the node from which to obtain the text.
   * @param beautify whether or not to trim and apply some other formatting. This means not only to
   *		remove white spaces before and after the actual text but additionally to remove double white spaces
   *		inbetween and to understand "\" as escape char with "\t" for a tab, "\n" for a line feed and "\\" for a "\".
   *		Note, that <code>beautify == true</code> will remove all "\r" from the text! If you really need Windows CRs,
   *		you have to use <code>beautify == false</code> or replace all "\n" by "\r\n".
   * @return the text or <code>null</code> if the given node contains no text
   */
  public static String getTextContent(Node node, boolean beautify)
  {
	  String text = getTextContent(node);

	  if (text.isEmpty())
		  return null;

	  if (!beautify)
		  return text;

	  // beautify
	  String lastToken = null;
	  boolean lastTokenWasLF = false;
	  StringTokenizer st = new StringTokenizer(text.trim().replace("\r", ""), "\\\n\r \t", true);
	  StringBuilder sb = new StringBuilder();
	  while (st.hasMoreTokens()) {
		  String token = st.nextToken();
		  if ("\n".equals(token)) {
			  if (lastTokenWasLF) {
				  sb.append(token);
				  lastTokenWasLF = false;
			  }
			  else
				  lastTokenWasLF = true;
		  }
		  else if (" ".equals(token)) {
			  if ("\n".equals(lastToken)) {
				  if (sb.charAt(sb.length() - 1) != ' ' && sb.charAt(sb.length() - 1) != '\t' && sb.charAt(sb.length() - 1) != '\n')
					  sb.append(" ");
			  }
			  else if (!" ".equals(lastToken) && !"\t".equals(lastToken))
				  sb.append(token);
		  }
		  else if ("\t".equals(token)) {
			  if ("\n".equals(lastToken)) {
				  if (sb.charAt(sb.length() - 1) != ' ' && sb.charAt(sb.length() - 1) != '\t' && sb.charAt(sb.length() - 1) != '\n')
					  sb.append(" ");
			  }
			  else if (!" ".equals(lastToken) && !"\t".equals(lastToken))
				  sb.append(token);
		  }
		  else {
			  if ("\\".equals(lastToken)) { // the first character of the current token is escaped => split
				  char escapedChar = token.charAt(0);
				  token = token.substring(1);

				  switch (escapedChar) {
				  case 'n':
					  sb.append('\n');
					  break;
				  case 't':
					  sb.append('\t');
					  break;
				  default:
					  sb.append(escapedChar);
				  break;
				  }
			  }
			  else if ("\n".equals(lastToken) && sb.charAt(sb.length() - 1) != ' ' && sb.charAt(sb.length() - 1) != '\t' && sb.charAt(sb.length() - 1) != '\n')
				  sb.append(" ");

			  if (!"\\".equals(token))
				  sb.append(token);

			  lastTokenWasLF = false;
		  }

		  lastToken = token;
	  }

	  return sb.toString();
  }

  /**
   * Serialize a SAX document to a string.
   * @param document The SAX document
   * @param charset The documents character set
   * @return The document as String
   * @throws UnsupportedEncodingException If the named encoding is not supported.
   */
  public static String getDocumentAsString(Document document, String charset) throws UnsupportedEncodingException
  {
  	return getDocumentAsString(document, charset, null, null);
  }

  /**
   * Serialize a SAX document to a string.
   * @param document The SAX document
   * @param charset The documents character set
   * @param publicId the public DTD or <code>null</code> for no public DTD.
   * @param systemId the system DTD or <code>null</code> for no system DTD.
   * @return The document as String
   * @throws UnsupportedEncodingException If the named encoding is not supported.
   */
  public static String getDocumentAsString(Document document, String charset, String publicId, String systemId) throws UnsupportedEncodingException
  {
  	ByteArrayOutputStream out = new ByteArrayOutputStream();
  	writeDocument(document, out, charset, publicId, systemId);
  	return out.toString(charset);
  }

  /**
   * @param element
   * @param charset
   * @return //TODO: write comment
   */
  public static String getElementAsString(Element element, String charset)
  {
  	try {
  		TransformerFactory tf = TransformerFactory.newInstance();
  		Transformer transformer = tf.newTransformer();
  		StringWriter sw = new StringWriter();
  		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//  		trans.setOutputProperty(OutputKeys.STANDALONE, "yes");
  		transformer.transform(new DOMSource(element), new StreamResult(sw));
  		return sw.toString();
  	} catch(TransformerException e) {
  		throw new RuntimeException("Transforming element to String failed: " + e.getMessage(), e);
  	}
  }

  /**
   * Serialize a SAX document to a byte array.
   * @param document The SAX document
   * @param charset The documents character set
   * @return The document as byte array
   */
  public static byte[] getDocumentAsByteArray(Document document, String charset)
  {
  	return getDocumentAsByteArray(document, charset, null, null);
  }

  /**
   * Serialize a SAX document to a byte array.
   * @param document The SAX document
   * @param charset The documents character set
   * @param publicId the public DTD or <code>null</code> for no public DTD.
   * @param systemId the system DTD or <code>null</code> for no system DTD.
   * @return The document as byte array
   */
  public static byte[] getDocumentAsByteArray(Document document, String charset, String publicId, String systemId)
  {
  	ByteArrayOutputStream out = new ByteArrayOutputStream();
  	writeDocument(document, out, charset, publicId, systemId);
  	return out.toByteArray();
  }

  /**
   * Serialize a SAX document to an output stream.
   * @param document The SAX document
   * @param out The target output stream. This stream is - of course - <b>not</b> closed by this method!
   *		It closed the stream in earlier versions of this class, which was an error! Always the method that
   *		creates a stream must close it itself in a try...finally-block.
   * @param charset The documents character set
   */
  public static void writeDocument(Document document, OutputStream out, String charset)
  {
  	writeDocument(document, out, charset, null, null);
  }

  /**
   * Serialize a SAX document to an output stream.
   *
   * @param document The SAX document
   * @param out The target output stream. This stream is - of course - <b>not</b> closed by this method!
   *		It closed the stream in earlier versions of this class, which was an error! Always the method that
   *		creates a stream must close it itself in a try...finally-block.
   * @param charset The documents character set
   * @param publicId the public DTD or <code>null</code> for no public DTD.
   * @param systemId the system DTD or <code>null</code> for no system DTD.
   */
  public static void writeDocument(Document document, OutputStream out, String charset, String publicId, String systemId)
  {
  	 OutputFormat of = new OutputFormat("XML", charset, true);
  	 of.setIndent(1);
  	 of.setIndenting(true);
  	 if(publicId != null || systemId != null)
  		 of.setDoctype(publicId, systemId);
  	 try {
  		 XMLSerializer serializer = new XMLSerializer(out, of);
    	 serializer.asDOMSerializer();
    	 serializer.serialize(document.getDocumentElement());
//	  	 out.close(); // This method did not create the OutputStream, hence it MUST *NOT* close it!!! Marco.
  	 } catch(IOException e) {
  		 // TODO why is IOException catched? it should be thrown. Marc
  		 throw new RuntimeException("Serializing xml to stream failed", e);
  	 }
  }

	/**
	 * Find a node with the given path and attribute. If more than one
	 * node exist, the first node found will be returned
	 * @param document The SAX document
	 * @param path A path to the node to find. Separate elements by "/"
	 * @param nameValuePairs The name and values of the attribute
	 * @return The node or <code>null</code> if no node could be found for the
	 * 		given parameters.
	 */
	public static Node findNodeByAttribute(Node document, String path, String... nameValuePairs)
	{
		Collection<Node> nodes = findNodesByAttribute(document, path, 1, nameValuePairs);
		return nodes.isEmpty() ? null : nodes.iterator().next();
	}

	/**
	 * Find all nodes with the given path and attribute.
	 * @param document The SAX document
	 * @param path A path to the node to find. Separate elements by "/"
	 * @param nameValuePairs The name and values of the attribute
	 * @return The nodes or or an empty collection if no node could be found for the
	 * 		given parameters.
	 */
	public static Collection<Node> findNodesByAttribute(Node document, String path, String... nameValuePairs)
	{
		return findNodesByAttribute(document, path, -1, nameValuePairs);
	}

	/**
	 * Find all nodes with the given path and attribute.
	 * @param document The SAX document
	 * @param path A path to the node to find. Separate elements by "/"
	 * @param limit How many nodes to return maximal. (Integer.MAX_VALUE == infinity)
	 * @param nameValuePairs The name and values of the attribute
	 * @return The nodes or or an empty collection if no node could be found for the
	 * 		given parameters.
	 */
	public static Collection<Node> findNodesByAttribute(Node document, String path, int limit, String... nameValuePairs)
	{
		if(nameValuePairs.length % 2 != 0)
			throw new IllegalArgumentException("Name value pairs must have even length");
		Collection<Node> nodes = findNodeList(document, path);
		Collection<Node> found = new ArrayList<Node>(Math.max(limit, Math.min(10, nodes.size())));
		for (Node node : nodes) {
			if(found.size() == limit)
				break;
			boolean match = true;
			for (int i = 0; i < nameValuePairs.length; i+=2) {
	      String name = nameValuePairs[i];
	      String value = nameValuePairs[i+1];
	      if(!value.equals(getAttributeValue(node, name))) {
	      	match = false;
	      	break;
	      }
      }
			if(match)
				found.add(node);
		}
		return found;
	}

	/**
	 * Set the text content for a node with a comment.
	 * @param node The node to set the content for
	 * @param comment The comment to add
	 * @param content The content to set
	 */
	public static void setTextContentWithComment(Node node, String comment, String content)
	{
		node.setTextContent("");
		node.appendChild(node.getOwnerDocument().createComment(comment));
		node.appendChild(node.getOwnerDocument().createTextNode(content));
	}
}
