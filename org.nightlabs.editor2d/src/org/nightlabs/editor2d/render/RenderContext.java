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

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;


/**
 * The Base Interface for different RenderContexts,
 * which means different implementations of Render-Engines,
 * e.g. Graphics2D (AWT/Swing) or GC (SWT) or Graphics (Draw2D) etc.
 * 
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public interface RenderContext<GraphicsType>
{
	/**
	 * returns the renderContextType for the RenderContext,
	 * which identifies the GraphicsType
	 * 
	 * @return the renderContextType
	 */
	String getRenderContextType();
	
	/**
	 * sets the renderContextType which identifies the GraphicsType
	 * 
	 * @param renderContextType the renderContextType to set
	 */
	void setRenderContextType(String renderContextType);
	
	/**
	 * paint the {@link DrawComponent} to the given GraphicsType
	 * 
	 * @param dc the {@link DrawComponent} to paint
	 * @param graphicsType the specific GraphicsType implementation
	 * @see J2DRenderContext
	 */
	public void paint(DrawComponent dc, GraphicsType graphicsType);
}
