/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.editor2d.iofilter.svg;

import java.util.LinkedList;
import java.util.List;

import org.apache.batik.svggen.SVGIDGenerator;
import org.apache.log4j.Logger;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class EditorSVGIDGenerator
extends SVGIDGenerator
{
	private static final Logger logger = Logger.getLogger(EditorSVGIDGenerator.class);
	
	public EditorSVGIDGenerator() {
		super();
	}

	@Override
	public String generateID(String prefix)
	{
		String res = super.generateID(prefix);
		if (logger.isDebugEnabled())
			logger.debug("prefix = " + prefix + ", result = "+res);
		fireIDGenerated(res, prefix);
		return res;
	}
	
	private List<IDGeneratorListener> listener = null;
	protected List<IDGeneratorListener> getListener() {
		if (listener == null)
			listener = new LinkedList<IDGeneratorListener>();
		return listener;
	}
	
	public void addIDListener(IDGeneratorListener listener) {
		getListener().add(listener);
	}
	public void removeIDListener(IDGeneratorListener listener) {
		getListener().remove(listener);
	}
	
	private void fireIDGenerated(String id, String prefix) {
		for (IDGeneratorListener listener : getListener()) {
			listener.idGenerated(id, prefix);
		}
	}
	
}
