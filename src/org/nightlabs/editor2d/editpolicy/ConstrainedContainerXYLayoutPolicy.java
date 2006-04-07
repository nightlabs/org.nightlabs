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
package org.nightlabs.editor2d.editpolicy;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.nightlabs.editor2d.request.EditorBoundsRequest;
import org.nightlabs.editor2d.util.EditorUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public abstract class ConstrainedContainerXYLayoutPolicy 
extends XYLayoutEditPolicy 
{

	public ConstrainedContainerXYLayoutPolicy() {
		super();
	}

	private static final Dimension DEFAULT_SIZE = new Dimension(-1, -1);	
	
  public Rectangle getConstraintRectangleFor(Point point) 
  {
    Point p = point.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(p);
		figure.translateFromParent(p);
		p.translate(getLayoutOrigin().getNegated());				
		return new Rectangle(p, DEFAULT_SIZE);
  }
  
  public Point getConstraintPointFor(Point point) 
  {
    Point p = point.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(p);
		figure.translateFromParent(p);
		p.translate(getLayoutOrigin().getNegated());				
		return p;
  }  
  
  public Rectangle getConstraintRectangleFor(Rectangle rectangle) 
  {
    Rectangle r = rectangle.getCopy();
		IFigure figure = getLayoutContainer();
		figure.translateToRelative(r);
		figure.translateFromParent(r);
    r.translate(getLayoutOrigin().getNegated());
    return r;
  }	
  
  protected Point getScrollOffset() {
    return EditorUtil.getScrollOffset(getHost());
  }	  
  
	/**
	 * Generates a draw2d constraint for the given <code>EditorBoundsRequest</code>. If the
	 * EditorBoundsRequest has a size, {@link #getConstraintFor(Rectangle)} is called with a
	 * Rectangle of that size and the result is returned. This is used during size-on-drop
	 * creation. Otherwise, {@link #getConstraintFor(Point)} is returned.
	 * <P>
	 * The EditorBoundsRequest location is relative the Viewer. The location is made
	 * layout-relative before calling one of the methods mentioned above.
	 * @param request the EditorCreateRequest
	 * @return a draw2d constraint
	 */
	protected Object getConstraintFor(EditorBoundsRequest request) 
	{	  
		IFigure figure = getLayoutContainer();
		Point where = request.getLocation().getCopy();
		Dimension size = request.getSize();
				
		figure.translateToRelative(where);
		figure.translateFromParent(where);
		where.translate(getLayoutOrigin().getNegated());		
	
		if (size == null || size.isEmpty())
			return getConstraintFor(where);
		else {
			//TODO Probably should use PrecisionRectangle at some point instead of two 
			// geometrical objects
			size = size.getCopy();
			figure.translateToRelative(size);
			figure.translateFromParent(size);									
			return getConstraintFor(new Rectangle(where, size));
		}		
	}   
}
