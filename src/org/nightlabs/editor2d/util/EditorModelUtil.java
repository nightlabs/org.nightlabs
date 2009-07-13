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

package org.nightlabs.editor2d.util;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Comparator;
import java.util.List;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.PositionConstants;
import org.nightlabs.math.MathUtil;

public class EditorModelUtil
{
	public EditorModelUtil() {
		super();
	}

	public static boolean checkAlignment(int alignment)
	{
		if (alignment == PositionConstants.ALIGNMENT_LEFT ||
				alignment == PositionConstants.ALIGNMENT_CENTER ||
				alignment == PositionConstants.ALIGNMENT_RIGHT)
		{
			return true;
		}
		else
			throw new IllegalArgumentException("Param alignment = "+alignment+" is not valid!");
	}
	
//	public static void alignDrawComponents(List<DrawComponent> drawComponents,
//			double distance, int alignment)
//	{
//		checkAlignment(alignment);
//		if (drawComponents != null)
//		{
//			DrawComponent firstDC = null;
//			DrawComponent lastDC = null;
//			Point2D firstPoint = null;
//			Point2D lastPoint = null;
//			if (alignment == PositionConstants.ALIGNMENT_LEFT)
//			{
//				firstDC = drawComponents.get(drawComponents.size()-1);
//				lastDC = drawComponents.get(0);
//				firstPoint = new Point2D.Double(firstDC.getBounds().getMinX(), firstDC.getBounds().getMinY());
//				lastPoint = new Point2D.Double(lastDC.getBounds().getMinX(), lastDC.getBounds().getMinY());
//				for (int i=drawComponents.size()-1; i>=0; i--)
//				{
//					DrawComponent dc = drawComponents.get(i);
//					Point2D p = MathUtil.getPointOnLineWithDistance(firstPoint, lastPoint, distance * i);
//					dc.setLocation((int)p.getX(), (int)p.getY());
//				}
//			}
//			else if (alignment == PositionConstants.ALIGNMENT_RIGHT)
//			{
//				firstDC = (DrawComponent) drawComponents.get(0);
//				lastDC = (DrawComponent) drawComponents.get(drawComponents.size()-1);
//				firstPoint = new Point2D.Double(firstDC.getBounds().getMinX(), firstDC.getBounds().getMinY());
//				lastPoint = new Point2D.Double(lastDC.getBounds().getMinX(), lastDC.getBounds().getMinY());
//				for (int i=drawComponents.size()-1; i>=0; i--)
//				{
//					DrawComponent dc = drawComponents.get(i);
//					Point2D p = MathUtil.getPointOnLineWithDistance(firstPoint, lastPoint, distance * i);
//					dc.setLocation((int)p.getX(), (int)p.getY());
//				}
//			}
//			else if (alignment == PositionConstants.ALIGNMENT_CENTER)
//			{
//				// TODO: implement this
//			}
//		}
//	}

	public static void alignDrawComponents(List<? extends DrawComponent> drawComponents,
			double distance, int alignment)
	{
		checkAlignment(alignment);
		if (drawComponents != null)
		{
			DrawComponent firstDC = null;
			DrawComponent lastDC = null;
			Point2D firstPoint = null;
			Point2D lastPoint = null;
			if (alignment == PositionConstants.ALIGNMENT_LEFT)
			{
				firstDC = drawComponents.get(drawComponents.size()-1);
				lastDC = drawComponents.get(0);
				firstPoint = new Point2D.Double(firstDC.getBounds().getMinX(), firstDC.getBounds().getMinY());
				lastPoint = new Point2D.Double(lastDC.getBounds().getMinX(), lastDC.getBounds().getMinY());
				for (int i=0; i<drawComponents.size(); i++)
				{
					DrawComponent dc = drawComponents.get(i);
					Point2D p = MathUtil.getPointOnLineWithDistance(firstPoint, lastPoint, distance * i);
					dc.setLocation((int)p.getX(), (int)p.getY());
				}
			}
			else if (alignment == PositionConstants.ALIGNMENT_RIGHT)
			{
				firstDC = drawComponents.get(0);
				lastDC = drawComponents.get(drawComponents.size()-1);
				firstPoint = new Point2D.Double(firstDC.getBounds().getMinX(), firstDC.getBounds().getMinY());
				lastPoint = new Point2D.Double(lastDC.getBounds().getMinX(), lastDC.getBounds().getMinY());
				for (int i=0; i<drawComponents.size(); i++)
				{
					DrawComponent dc = drawComponents.get(i);
					int size = drawComponents.size()-1;
					Point2D p = MathUtil.getPointOnLineWithDistance(firstPoint, lastPoint, distance * (size-i) );
					dc.setLocation((int)p.getX(), (int)p.getY());
				}
			}
			else if (alignment == PositionConstants.ALIGNMENT_CENTER)
			{
				// TODO: is not implemented
				throw new UnsupportedOperationException();
//				firstDC = (DrawComponent) drawComponents.get(0);
//				lastDC = (DrawComponent) drawComponents.get(drawComponents.size()-1);
//				firstPoint = new Point2D.Double(firstDC.getBounds().getMinX(), firstDC.getBounds().getMinY());
//				lastPoint = new Point2D.Double(lastDC.getBounds().getMinX(), lastDC.getBounds().getMinY());
//				for (int i=0; i<drawComponents.size(); i++)
//				{
//					DrawComponent dc = drawComponents.get(i);
//					int size = drawComponents.size();
//					Point2D p = MathUtil.getPointOnLineWithDistance(firstPoint, lastPoint, distance * (size-i) );
//					dc.setLocation((int)p.getX(), (int)p.getY());
//				}
			}
		}
	}
	
	private static double[] matrix = new double[6];
	public static AffineTransform checkAffineTransform(AffineTransform at)
	{
		at.getMatrix(matrix);
		for (int i=0; i<matrix.length; i++) {
			matrix[i] = checkFactor(matrix[i]);
		}
		return new AffineTransform(matrix);
	}
	
  public static double checkFactor(double factor)
  {
    if (Double.isInfinite(factor) || Double.isNaN(factor))
      factor = 1d;
    
    return factor;
  }
	
  public static float checkFactor(float factor)
  {
    if (Float.isInfinite(factor) || Float.isNaN(factor))
      factor = 1f;
    
    return factor;
  }
  
  public static double getConstrainedValue(double value, double limit)
  {
    if (value > limit || value < -limit) {
      return value%limit;
    }
    return value;
  }
  
  public static double calcDiffRotation(double newRotation, double oldRotation)
  {
    double degreesToRotate = 0;
    newRotation = getConstrainedValue(newRotation, 360.0d);
  	  	
  	if (newRotation == 0)
  		degreesToRotate = -oldRotation;
  	else if (newRotation <= oldRotation)
  		degreesToRotate = -(oldRotation - newRotation);
  	else
  		degreesToRotate = newRotation - oldRotation;
  	  	
  	return degreesToRotate;
  }
  
  /**
   * calcs the location for r2, so that it is placed in the middle of r1
   * 
   * @param r1 The Rectangle which is the reference for the location of param r2
   * @param r2 The Rectangle which should be placed in the middle of r1
   * @return the Location of the second param r2, which is then located in the middle of
   * bounds of param r2
   */
  public static java.awt.Point getLeftTopCenterLocation(java.awt.Rectangle r1, java.awt.Rectangle r2)
  {
    int diffWidth = (r2.width - r1.width) / 2;
    int diffHeight = (r2.height - r1.height) / 2;
    int diffX = r2.x - r1.x;
    int diffY = r2.y - r1.y;
    return new java.awt.Point(diffX + diffWidth, diffY + diffHeight);
  }
  
  public static Comparator<DrawComponent> idComparator = new Comparator<DrawComponent>()
  {
		public int compare(DrawComponent dc1, DrawComponent dc2)
		{
			if (dc1.getId() < dc2.getId())
				return -1;
			else if (dc1.getId() > dc2.getId())
				return 1;
			else
				return 0;
		}
	};
}
