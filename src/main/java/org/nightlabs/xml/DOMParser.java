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

import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/**
 * You should use always this DOMParser instead of the jakarta original one, because
 * even if validation is switched off, it tries to resolve the external dtds, means
 * it needs an online connection. Thus, this Parser can be switched to OFFLINE mode,
 * which is the default.
 * <p>
 * To test the whole project for validity, you can set the global constant DEFAULT_ONLINE_MODE
 * to true - in case you don't call setOnlineMode(...) within your modules.
 *
 * @author marco
 */
public class DOMParser extends org.apache.xerces.parsers.DOMParser
{
	public static final boolean DEFAULT_ONLINE_MODE = false;
	
	public DOMParser()
		throws SAXException
	{
		super();
		setOnlineMode(DEFAULT_ONLINE_MODE);
	}
	public DOMParser(SymbolTable arg0)
		throws SAXException
	{
		super(arg0);
		setOnlineMode(DEFAULT_ONLINE_MODE);
	}
	public DOMParser(SymbolTable arg0, XMLGrammarPool arg1)
		throws SAXException
	{
		super(arg0, arg1);
		setOnlineMode(DEFAULT_ONLINE_MODE);
	}
	public DOMParser(XMLParserConfiguration arg0)
		throws SAXException
	{
		super(arg0);
		setOnlineMode(DEFAULT_ONLINE_MODE);
	}
	
	protected EntityResolver defaultEntityResolver = null;
	protected DummyEntityResolver dummyEntityResolver = null;
	
	/**
	 * Even if the feature <i>validation</i> is switched off, the
	 * Parser tries to resolve the DTD. This means, to simply switch the feature
	 * off, is not sufficient. Hence, if online is false, this method
	 * creates a special DummyEntityResolver which returns empty Entities.
	 * Because they are unusable, <i>validation</i> is switched off additionally.
	 * If you call this method with online = <code>true</code> validation
	 * will be re-enabled.
	 * <p>
	 * for the list of parser features see
	 * <a href="http://xml.apache.org/xerces2-j/features.html">http://xml.apache.org/xerces2-j/features.html</a>
	 *
	 * @param online Whether we are online or not.
	 * @throws SAXNotRecognizedException
	 * @throws SAXNotSupportedException
	 */
	public void setOnlineMode(boolean online)
		throws SAXNotRecognizedException, SAXNotSupportedException
	{
		// list of features: http://xml.apache.org/xerces2-j/features.html
		// We deactivate validation because lomboz generates non-valid xml files, if
		// there are no beans in a jar.
		// And because we might be offline.
		this.setFeature("http://xml.org/sax/features/validation", online);
		if (!online && defaultEntityResolver == null) {
			defaultEntityResolver = this.getEntityResolver();
			if (dummyEntityResolver == null)
				dummyEntityResolver = new DummyEntityResolver();
			this.setEntityResolver(dummyEntityResolver);
		}
		else if (online && dummyEntityResolver != null)  // We check for dummyEntityResolver,
			this.setEntityResolver(defaultEntityResolver); // because the defaultEntityResolver might
			                                               // be null from the beginning.
	}
}
