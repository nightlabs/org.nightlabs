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
package org.nightlabs.editor2d.j2d;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * @author Daniel Mazurek
 *
 */
public class GeneralShapeUtil {
	 public static GeneralShape removePathSegment(GeneralShape generalShape, int index)
	  {
	    if (generalShape == null)
	      throw new IllegalArgumentException("Param generalShape MUST not be null!"); //$NON-NLS-1$
	      
	    if (index > generalShape.getNumTypes())
	      throw new IndexOutOfBoundsException("Param index is out of GeneralShape PathSegment Bounds!"); //$NON-NLS-1$

	    if (index == 0)
	      removeFirstPathSegment(generalShape);
	    
	    float[] coords = new float[6];
	    int pathIndex = 0;
	    GeneralShape gs = new GeneralShape();
	    boolean indexSet = false;
	    for (PathIterator pi = generalShape.getPathIterator(new AffineTransform()); !pi.isDone(); pi.next())
	    {
	      if (pathIndex == index)
	      {
	        pathIndex = -1;
	        indexSet = true;
	        continue;
	      }
	                
	      int segType = pi.currentSegment(coords);
	      switch (segType)
	      {
		      case (PathIterator.SEG_MOVETO):
		        gs.moveTo(coords[0], coords[1]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_LINETO):
		        gs.lineTo(coords[0], coords[1]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_QUADTO):
		        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_CUBICTO):
		        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_CLOSE):
		        gs.closePath();
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
	      }
	    }
	    return gs;
	  }
	  
	  public static GeneralShape removeFirstPathSegment(GeneralShape generalShape)
	  {
	    float[] coords = new float[6];
	    int pathIndex = 0;
	    GeneralShape gs = new GeneralShape();
	    for (PathIterator pi = generalShape.getPathIterator(null); !pi.isDone(); pi.next())
	    {
	      int segType = pi.currentSegment(coords);
	      switch (segType)
	      {
		      case (PathIterator.SEG_MOVETO):
		      	pathIndex++;
		        break;
		      case (PathIterator.SEG_LINETO):
		        if (pathIndex == 1)
		          gs.moveTo(coords[0], coords[1]);
		        else
		          gs.lineTo(coords[0], coords[1]);
		      	pathIndex++;
		        break;
		      case (PathIterator.SEG_QUADTO):
		        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
		      	pathIndex++;
		        break;
		      case (PathIterator.SEG_CUBICTO):
		        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
		      	pathIndex++;
		        break;
		      case (PathIterator.SEG_CLOSE):
		        pathIndex++;
		        break;
	      }
	    }
	    return gs;
	  }
	  
	  public static GeneralShape addPathSegment(GeneralShape generalShape, int type, int index, float[] newCoords)
	  {
	    float[] coords = new float[6];
	    GeneralShape gs = new GeneralShape();
	    int pathIndex = 0;
	    boolean indexSet = false;
	    for (PathIterator pi = generalShape.getPathIterator(null); !pi.isDone(); pi.next())
	    {
	      if (pathIndex == index)
	      {
	        switch (type)
	        {
		      	case (PathIterator.SEG_MOVETO):
		      	  gs.moveTo(newCoords[0], newCoords[1]);
		      	  break;
		      	case (PathIterator.SEG_LINETO):
		      	  gs.lineTo(newCoords[0], newCoords[1]);
		      	  break;
			      case (PathIterator.SEG_QUADTO):
			        gs.quadTo(newCoords[0], newCoords[1], newCoords[2], newCoords[3]);
			        break;
			      case (PathIterator.SEG_CUBICTO):
			        gs.curveTo(newCoords[0], newCoords[1], newCoords[2], newCoords[3], newCoords[4], newCoords[5]);
			        break;
	        }
	        pathIndex = -1;
	        indexSet = true;
	      }
	      
	      int segType = pi.currentSegment(coords);
	      switch (segType)
	      {
		      case (PathIterator.SEG_MOVETO):
		        gs.moveTo(coords[0], coords[1]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_LINETO):
		        gs.lineTo(coords[0], coords[1]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_QUADTO):
		        gs.quadTo(coords[0], coords[1], coords[2], coords[3]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_CUBICTO):
		        gs.curveTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
		      case (PathIterator.SEG_CLOSE):
		        gs.closePath();
		      	if (!indexSet)
		      	  pathIndex++;
		        break;
	      }
	    }
	    return gs;
	  }
}
