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
package org.nightlabs.editor2d.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
@SuppressWarnings("unchecked")
public class BaseRenderer
implements Renderer
{
	public BaseRenderer() {
		super();
	}
	
	private Map<String, RenderContext> contextType2RenderContext = new HashMap<String, RenderContext>();
	public void addRenderContext(RenderContext rc)
	{
		if (rc == null)
			throw new IllegalArgumentException("param rc must not be null!");

		contextType2RenderContext.put(rc.getRenderContextType(), rc);
	}

	public RenderContext getRenderContext(String renderContextType) {
		return contextType2RenderContext.get(renderContextType);
	}

	public Collection<RenderContext> getRenderContexts() {
		return contextType2RenderContext.values();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		
		if (obj == null)
			return false;
		
		if (!(obj instanceof BaseRenderer))
			return false;
		
		BaseRenderer renderer = (BaseRenderer) obj;
		
		for (Iterator<Map.Entry<String, RenderContext>> it = contextType2RenderContext.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, RenderContext> entry = it.next();
			String renderContextType = entry.getKey();
			RenderContext rc = renderer.getRenderContext(renderContextType);
			if (!entry.getValue().equals(rc))
				return false;
		}
		
		return true;
	}
	
}
