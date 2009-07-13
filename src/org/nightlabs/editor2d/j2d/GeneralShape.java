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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.IllegalPathStateException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nightlabs.editor2d.util.GeomUtil;

import sun.awt.geom.Crossings;

/**
 * The <code>GeneralShape</code> class represents a geometric path
 * constructed from straight lines, and quadratic and cubic
 * (B&eacute;zier) curves.  It can contain multiple subpaths.
 * <p>
 * The winding rule specifies how the interior of a path is
 * determined.  There are two types of winding rules:
 * EVEN_ODD and NON_ZERO.
 * <p>
 * An EVEN_ODD winding rule means that enclosed regions
 * of the path alternate between interior and exterior areas as
 * traversed from the outside of the path towards a point inside
 * the region.
 * <p>
 * A NON_ZERO winding rule means that if a ray is
 * drawn in any direction from a given point to infinity
 * and the places where the path intersects
 * the ray are examined, the point is inside of the path if and only if
 * the number of times that the path crosses the ray from
 * left to right does not equal the  number of times that the path crosses
 * the ray from right to left.
 */
public class GeneralShape
implements Shape, Cloneable, Serializable
{
	private static final long serialVersionUID = 1L;

	/**
	 * An even-odd winding rule for determining the interior of
	 * a path.
	 */
	public static final int WIND_EVEN_ODD = PathIterator.WIND_EVEN_ODD;

	/**
	 * A non-zero winding rule for determining the interior of a
	 * path.
	 */
	public static final int WIND_NON_ZERO = PathIterator.WIND_NON_ZERO;

	// For code simplicity, copy these constants to our namespace
	// and cast them to byte constants for easy storage.
	private static final byte SEG_MOVETO  = (byte) PathIterator.SEG_MOVETO;
	private static final byte SEG_LINETO  = (byte) PathIterator.SEG_LINETO;
	private static final byte SEG_QUADTO  = (byte) PathIterator.SEG_QUADTO;
	private static final byte SEG_CUBICTO = (byte) PathIterator.SEG_CUBICTO;
	private static final byte SEG_CLOSE   = (byte) PathIterator.SEG_CLOSE;

	protected byte[] pointTypes;
	protected float[] pointCoords;
	int numTypes;
	int numCoords;
	int windingRule;
	private Rectangle2D bounds;
	private List<TransformListener> transformListeners;
	private List<PathSegment> pathSegments;

	public byte[] getPointTypes() {
		return pointTypes;
	}
	public void setPointTypes(byte[] _pointTypes) {
		pointTypes = _pointTypes;
		bounds = null;
	}

	public int getNumCoords() {
		return numCoords;
	}
	public void setNumCoords(int numCoords) {
		this.numCoords = numCoords;
		bounds = null;
		pathSegments = null;
	}

	public int getNumTypes() {
		return numTypes;
	}
	public void setNumTypes(int numTypes) {
		this.numTypes = numTypes;
		bounds = null;
		pathSegments = null;
	}

	public int getSize() {
		return numTypes;
	}

	public float[] getPointCoords() {
		return pointCoords;
	}
	public void setPointCoords(float[] pointCoords) {
		this.pointCoords = pointCoords;
		bounds = null;
		pathSegments = null;
	}

	static final int INIT_SIZE = 20;
	static final int EXPAND_MAX = 500;

	/**
	 * Constructs a new <code>GeneralShape</code> object.
	 * If an operation performed on this path requires the
	 * interior of the path to be defined then the default NON_ZERO
	 * winding rule is used.
	 * @see #WIND_NON_ZERO
	 */
	public GeneralShape() {
		this(WIND_NON_ZERO, INIT_SIZE, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralShape</code> object with the specified
	 * winding rule to control operations that require the interior of the
	 * path to be defined.
	 * @param rule the winding rule
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public GeneralShape(int rule) {
		this(rule, INIT_SIZE, INIT_SIZE);
	}

	/**
	 * Constructs a new <code>GeneralShape</code> object with the specified
	 * winding rule and the specified initial capacity to store path
	 * coordinates. This number is an initial guess as to how many path
	 * segments are in the path, but the storage is expanded
	 * as needed to store whatever path segments are added to this path.
	 * @param rule the winding rule
	 * @param initialCapacity the estimate for the number of path segments
	 * in the path
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	public GeneralShape(int rule, int initialCapacity) {
		this(rule, initialCapacity, initialCapacity);
	}

	/**
	 * Constructs a new <code>GeneralShape</code> object with the specified
	 * winding rule and the specified initial capacities to store point types
	 * and coordinates.
	 * These numbers are an initial guess as to how many path segments
	 * and how many points are to be in the path, but the
	 * storage is expanded as needed to store whatever path segments are
	 * added to this path.
	 * @param rule the winding rule
	 * @param initialTypes the estimate for the number of path segments
	 * in the path
	 * @param initialCapacity the estimate for the number of points
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 */
	GeneralShape(int rule, int initialTypes, int initialCoords) {
		setWindingRule(rule);
		pointTypes = new byte[initialTypes];
		pointCoords = new float[initialCoords * 2];
	}

	/**
	 * Constructs a new <code>GeneralShape</code> object from an arbitrary
	 * {@link Shape} object.
	 * All of the initial geometry and the winding rule for this path are
	 * taken from the specified <code>Shape</code> object.
	 * @param s the specified <code>Shape</code> object
	 */
	public GeneralShape(Shape s) {
		this(WIND_NON_ZERO, INIT_SIZE, INIT_SIZE);
		PathIterator pi = s.getPathIterator(null);
		setWindingRule(pi.getWindingRule());
		append(pi, false);
	}

	private void needRoom(int newTypes, int newCoords, boolean needMove) {
		if (needMove && numTypes == 0) {
			throw new IllegalPathStateException("missing initial moveto "+
			"in path definition");
		}
		int size = pointCoords.length;
		if (numCoords + newCoords > size) {
			int grow = size;
			if (grow > EXPAND_MAX * 2) {
				grow = EXPAND_MAX * 2;
			}
			if (grow < newCoords) {
				grow = newCoords;
			}
			float[] arr = new float[size + grow];
			System.arraycopy(pointCoords, 0, arr, 0, numCoords);
			pointCoords = arr;
		}
		size = pointTypes.length;
		if (numTypes + newTypes > size) {
			int grow = size;
			if (grow > EXPAND_MAX) {
				grow = EXPAND_MAX;
			}
			if (grow < newTypes) {
				grow = newTypes;
			}
			byte[] arr = new byte[size + grow];
			System.arraycopy(pointTypes, 0, arr, 0, numTypes);
			pointTypes = arr;
		}
	}

	/**
	 * Adds a point to the path by moving to the specified
	 * coordinates.
	 * @param x the specified x-coordinate
	 * @param y the specified y-coordinate
	 */
	public synchronized void moveTo(float x, float y) {
		if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
			pointCoords[numCoords - 2] = x;
			pointCoords[numCoords - 1] = y;
		} else {
			needRoom(1, 2, false);
			pointTypes[numTypes++] = SEG_MOVETO;
			pointCoords[numCoords++] = x;
			pointCoords[numCoords++] = y;
		}
		pathSegments = null;
		bounds = null;
	}

	/**
	 * Adds a point to the path by drawing a straight line from the
	 * current coordinates to the new specified coordinates.
	 * @param x the specified x-coordinate
	 * @param y the specified y-coordinate
	 */
	public synchronized void lineTo(float x, float y) {
		needRoom(1, 2, true);
		pointTypes[numTypes++] = SEG_LINETO;
		pointCoords[numCoords++] = x;
		pointCoords[numCoords++] = y;
		pathSegments = null;
		bounds = null;
	}

	/**
	 * Adds a curved segment, defined by two new points, to the path by
	 * drawing a Quadratic curve that intersects both the current
	 * coordinates and the coordinates (x2,&nbsp;y2), using the
	 * specified point (x1,&nbsp;y1) as a quadratic parametric control
	 * point.
	 * @param x1 the x-coordinate of the first quadratic control point
	 * @param y1 the y-coordinate of the first quadratic control point
	 * @param x2 the x-coordinate of the final endpoint
	 * @param y2 the y-coordinate of the final endpoint
	 */
	public synchronized void quadTo(float x1, float y1, float x2, float y2) {
		needRoom(1, 4, true);
		pointTypes[numTypes++] = SEG_QUADTO;
		pointCoords[numCoords++] = x1;
		pointCoords[numCoords++] = y1;
		pointCoords[numCoords++] = x2;
		pointCoords[numCoords++] = y2;
		pathSegments = null;
		bounds = null;
	}

	/**
	 * Adds a curved segment, defined by three new points, to the path by
	 * drawing a B&eacute;zier curve that intersects both the current
	 * coordinates and the coordinates (x3,&nbsp;y3), using the
	 * specified points (x1,&nbsp;y1) and (x2,&nbsp;y2) as
	 * B&eacute;zier control points.
	 * @param x1 the x-coordinate of the first B&eacute;ezier
	 *		control point
	 * @param y1 the y-coordinate of the first B&eacute;ezier
	 *		control point
	 * @param x2 the x-coordinate of the second B&eacute;zier
	 *		control point
	 * @param y2 the y-coordinate of the second B&eacute;zier
	 *		control point
	 * @param x3 the x-coordinate of the final endpoint
	 * @param y3 the y-coordinate of the final endpoint
	 */
	public synchronized void curveTo(float x1, float y1,
			float x2, float y2,
			float x3, float y3) {
		needRoom(1, 6, true);
		pointTypes[numTypes++] = SEG_CUBICTO;
		pointCoords[numCoords++] = x1;
		pointCoords[numCoords++] = y1;
		pointCoords[numCoords++] = x2;
		pointCoords[numCoords++] = y2;
		pointCoords[numCoords++] = x3;
		pointCoords[numCoords++] = y3;
		pathSegments = null;
		bounds = null;
	}

	/**
	 * Closes the current subpath by drawing a straight line back to
	 * the coordinates of the last <code>moveTo</code>.  If the path is already
	 * closed then this method has no effect.
	 */
	public synchronized void closePath()
	{
		if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
			needRoom(1, 0, true);
			pointTypes[numTypes++] = SEG_CLOSE;
		}
		bounds = null;
		pathSegments = null;
	}

	/**
	 * Appends the geometry of the specified <code>Shape</code> object to the
	 * path, possibly connecting the new geometry to the existing path
	 * segments with a line segment.
	 * If the <code>connect</code> parameter is <code>true</code> and the
	 * path is not empty then any initial <code>moveTo</code> in the
	 * geometry of the appended <code>Shape</code>
	 * is turned into a <code>lineTo</code> segment.
	 * If the destination coordinates of such a connecting <code>lineTo</code>
	 * segment match the ending coordinates of a currently open
	 * subpath then the segment is omitted as superfluous.
	 * The winding rule of the specified <code>Shape</code> is ignored
	 * and the appended geometry is governed by the winding
	 * rule specified for this path.
	 * @param s the <code>Shape</code> whose geometry is appended
	 * to this path
	 * @param connect a boolean to control whether or not to turn an
	 * initial <code>moveTo</code> segment into a <code>lineTo</code>
	 * segment to connect the new geometry to the existing path
	 */
	public void append(Shape s, boolean connect) {
		PathIterator pi = s.getPathIterator(null);
		append(pi,connect);
	}

	/**
	 * Appends the geometry of the specified
	 * {@link PathIterator} object
	 * to the path, possibly connecting the new geometry to the existing
	 * path segments with a line segment.
	 * If the <code>connect</code> parameter is <code>true</code> and the
	 * path is not empty then any initial <code>moveTo</code> in the
	 * geometry of the appended <code>Shape</code> is turned into a
	 * <code>lineTo</code> segment.
	 * If the destination coordinates of such a connecting <code>lineTo</code>
	 * segment match the ending coordinates of a currently open
	 * subpath then the segment is omitted as superfluous.
	 * The winding rule of the specified <code>Shape</code> is ignored
	 * and the appended geometry is governed by the winding
	 * rule specified for this path.
	 * @param pi the <code>PathIterator</code> whose geometry is appended to
	 * this path
	 * @param connect a boolean to control whether or not to turn an
	 * initial <code>moveTo</code> segment into a <code>lineTo</code> segment
	 * to connect the new geometry to the existing path
	 */
	public void append(PathIterator pi, boolean connect) {
		float coords[] = new float[6];
		while (!pi.isDone()) {
			switch (pi.currentSegment(coords)) {
			case SEG_MOVETO:
				if (!connect || numTypes < 1 || numCoords < 2) {
					moveTo(coords[0], coords[1]);
					break;
				}
				if (pointTypes[numTypes - 1] != SEG_CLOSE &&
						pointCoords[numCoords - 2] == coords[0] &&
						pointCoords[numCoords - 1] == coords[1])
				{
					// Collapse out initial moveto/lineto
					break;
				}
				// NO BREAK;
			case SEG_LINETO:
				lineTo(coords[0], coords[1]);
				break;
			case SEG_QUADTO:
				quadTo(coords[0], coords[1],
						coords[2], coords[3]);
				break;
			case SEG_CUBICTO:
				curveTo(coords[0], coords[1],
						coords[2], coords[3],
						coords[4], coords[5]);
				break;
			case SEG_CLOSE:
				closePath();
				break;
			}
			pi.next();
			connect = false;
		}
		pathSegments = null;
		bounds = null;
	}

	/**
	 * Returns the fill style winding rule.
	 * @return an integer representing the current winding rule.
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @see #setWindingRule
	 */
	public synchronized int getWindingRule() {
		return windingRule;
	}

	/**
	 * Sets the winding rule for this path to the specified value.
	 * @param rule an integer representing the specified
	 * winding rule
	 * @exception <code>IllegalArgumentException</code> if
	 *		<code>rule</code> is not either
	 *		<code>WIND_EVEN_ODD</code> or
	 *		<code>WIND_NON_ZERO</code>
	 * @see #WIND_EVEN_ODD
	 * @see #WIND_NON_ZERO
	 * @see #getWindingRule
	 */
	public void setWindingRule(int rule) {
		if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
			throw new IllegalArgumentException("winding rule must be "+
					"WIND_EVEN_ODD or "+
			"WIND_NON_ZERO");
		}
		windingRule = rule;
	}

	/**
	 * Returns the coordinates most recently added to the end of the path
	 * as a {@link Point2D} object.
	 * @return a <code>Point2D</code> object containing the ending
	 * coordinates of the path or <code>null</code> if there are no points
	 * in the path.
	 */
	public synchronized Point2D getCurrentPoint() {
		if (numTypes < 1 || numCoords < 2) {
			return null;
		}
		int index = numCoords;
		if (pointTypes[numTypes - 1] == SEG_CLOSE) {
			loop:
				for (int i = numTypes - 2; i > 0; i--) {
					switch (pointTypes[i]) {
					case SEG_MOVETO:
						break loop;
					case SEG_LINETO:
						index -= 2;
						break;
					case SEG_QUADTO:
						index -= 4;
						break;
					case SEG_CUBICTO:
						index -= 6;
						break;
					case SEG_CLOSE:
						break;
					}
				}
		}
		return new Point2D.Float(pointCoords[index - 2],
				pointCoords[index - 1]);
	}

	/**
	 * Resets the path to empty.  The append position is set back to the
	 * beginning of the path and all coordinates and point types are
	 * forgotten.
	 */
	public synchronized void reset() {
		numTypes = numCoords = 0;
		bounds = null;
		pathSegments = null;
	}

	public void addTransformListener(TransformListener listener)
	{
		if (transformListeners == null)
			transformListeners = new ArrayList<TransformListener>();

		transformListeners.add(listener);
	}
	public void removeTransformListener(TransformListener listener)
	{
		if (transformListeners == null)
			transformListeners = new ArrayList<TransformListener>();

		transformListeners.remove(listener);
	}


	protected void fireTransformChanged(AffineTransform _at)
	{
		if (transformListeners == null)
			return;

		for (Iterator<TransformListener> it = transformListeners.iterator(); it.hasNext(); )
		{
			TransformListener listener = it.next();
			listener.transformChanged(_at);
		}
	}

	public boolean hasTransformListener()
	{
		if (transformListeners != null)
			return true;
		else
			return false;
	}

	/**
	 * Transforms the geometry of this path using the specified
	 * {@link AffineTransform}.
	 * The geometry is transformed in place, which permanently changes the
	 * boundary defined by this object.
	 * @param at the <code>AffineTransform</code> used to transform the area
	 */
	public void transform(AffineTransform at)
	{
		at.transform(pointCoords, 0, pointCoords, 0, numCoords / 2);
		pathSegments = null;
		bounds = null;
		fireTransformChanged(at);
	}

	/**
	 * Returns a new transformed <code>Shape</code>.
	 * @param at the <code>AffineTransform</code> used to transform a
	 * new <code>Shape</code>.
	 * @return a new <code>Shape</code>, transformed with the specified
	 * <code>AffineTransform</code>.
	 */
	public synchronized Shape createTransformedShape(AffineTransform at) {
		GeneralShape gp = (GeneralShape) clone();
		if (at != null) {
			gp.transform(at);
		}
		return gp;
	}

	/**
	 * Return the bounding box of the path.
	 * @return a {@link java.awt.Rectangle} object that
	 * bounds the current path.
	 */
	public java.awt.Rectangle getBounds() {
		return getBounds2D().getBounds();
	}

	/**
	 * Returns the bounding box of the path.
	 * @return a {@link Rectangle2D} object that
	 *          bounds the current path.
	 */
	public synchronized Rectangle2D getBounds2D()
	{
		if (bounds == null)
		{
			float x1, y1, x2, y2;
			int i = numCoords;
			if (i > 0) {
				y1 = y2 = pointCoords[--i];
				x1 = x2 = pointCoords[--i];
				while (i > 0) {
					float y = pointCoords[--i];
					float x = pointCoords[--i];
					if (x < x1) x1 = x;
					if (y < y1) y1 = y;
					if (x > x2) x2 = x;
					if (y > y2) y2 = y;
				}
			} else {
				x1 = y1 = x2 = y2 = 0.0f;
			}
			return new Rectangle2D.Float(x1, y1, x2 - x1, y2 - y1);
		}
		else
			return bounds;
	}

	/**
	 * Tests if the specified coordinates are inside the boundary of
	 * this <code>Shape</code>.
	 * @param x the specified x-coordinate
	 * @param y the specified y-coordinate
	 * @return <code>true</code> if the specified coordinates are inside this
	 * <code>Shape</code>; <code>false</code> otherwise
	 */
	public boolean contains(double x, double y) {
		if (numTypes < 2) {
			return false;
		}
//		int cross = Curve.crossingsForPath(getPathIterator(null), x, y);
		int cross = GeomUtil.pointCrossingsForPath(getPathIterator(null), x, y);
		if (windingRule == WIND_NON_ZERO) {
			return (cross != 0);
		} else {
			return ((cross & 1) != 0);
		}
	}

	/**
	 * Tests if the specified <code>Point2D</code> is inside the boundary
	 * of this <code>Shape</code>.
	 * @param p the specified <code>Point2D</code>
	 * @return <code>true</code> if this <code>Shape</code> contains the
	 * specified <code>Point2D</code>, <code>false</code> otherwise.
	 */
	public boolean contains(Point2D p) {
		return contains(p.getX(), p.getY());
	}

	/**
	 * Tests if the specified rectangular area is inside the boundary of
	 * this <code>Shape</code>.
	 * @param x the specified x-coordinates
	 * @param y the specified y-coordinates
	 * @param w the width of the specified rectangular area
	 * @param h the height of the specified rectangular area
	 * @return <code>true</code> if this <code>Shape</code> contains
	 * the specified rectangluar area; <code>false</code> otherwise.
	 */
	public boolean contains(double x, double y, double w, double h) {
		// FIXME: This has to be substituted by a Java-1.6-conform non-proprietary method.
		Crossings c = Crossings.findCrossings(getPathIterator(null),
				x, y, x+w, y+h);
		return (c != null && c.covers(y, y+h));
	}

	/**
	 * Tests if the specified <code>Rectangle2D</code>
	 * is inside the boundary of this <code>Shape</code>.
	 * @param r a specified <code>Rectangle2D</code>
	 * @return <code>true</code> if this <code>Shape</code> bounds the
	 * specified <code>Rectangle2D</code>; <code>false</code> otherwise.
	 */
	public boolean contains(Rectangle2D r) {
		return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Tests if the interior of this <code>Shape</code> intersects the
	 * interior of a specified set of rectangular coordinates.
	 * @param x the specified x-coordinate
	 * @param y the specified y-coordinate
	 * @param w the width of the specified rectangular coordinates
	 * @param h the height of the specified rectangular coordinates
	 * @return <code>true</code> if this <code>Shape</code> and the
	 * interior of the specified set of rectangular coordinates intersect
	 * each other; <code>false</code> otherwise.
	 */
	public boolean intersects(double x, double y, double w, double h) {
		Crossings c = Crossings.findCrossings(getPathIterator(null),
				x, y, x+w, y+h);
		return (c == null || !c.isEmpty());
	}

	/**
	 * Tests if the interior of this <code>Shape</code> intersects the
	 * interior of a specified <code>Rectangle2D</code>.
	 * @param r the specified <code>Rectangle2D</code>
	 * @return <code>true</code> if this <code>Shape</code> and the interior
	 * 		of the specified <code>Rectangle2D</code> intersect each
	 * 		other; <code>false</code> otherwise.
	 */
	public boolean intersects(Rectangle2D r) {
		return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Returns a <code>PathIterator</code> object that iterates along the
	 * boundary of this <code>Shape</code> and provides access to the
	 * geometry of the outline of this <code>Shape</code>.
	 * The iterator for this class is not multi-threaded safe,
	 * which means that this <code>GeneralShape</code> class does not
	 * guarantee that modifications to the geometry of this
	 * <code>GeneralShape</code> object do not affect any iterations of
	 * that geometry that are already in process.
	 * @param at an <code>AffineTransform</code>
	 * @return a new <code>PathIterator</code> that iterates along the
	 * boundary of this <code>Shape</code> and provides access to the
	 * geometry of this <code>Shape</code>'s outline
	 */
	public PathIterator getPathIterator(AffineTransform at) {
		return new GeneralShapeIterator(this, at);
	}

	/**
	 * Returns a <code>PathIterator</code> object that iterates along the
	 * boundary of the flattened <code>Shape</code> and provides access to the
	 * geometry of the outline of the <code>Shape</code>.
	 * The iterator for this class is not multi-threaded safe,
	 * which means that this <code>GeneralShape</code> class does not
	 * guarantee that modifications to the geometry of this
	 * <code>GeneralShape</code> object do not affect any iterations of
	 * that geometry that are already in process.
	 * @param at an <code>AffineTransform</code>
	 * @param flatness the maximum distance that the line segments used to
	 *		approximate the curved segments are allowed to deviate
	 *		from any point on the original curve
	 * @return a new <code>PathIterator</code> that iterates along the flattened
	 * <code>Shape</code> boundary.
	 */
	public PathIterator getPathIterator(AffineTransform at, double flatness) {
		return new FlatteningPathIterator(getPathIterator(at), flatness);
	}

	/**
	 * Creates a new object of the same class as this object.
	 *
	 * @return     a clone of this instance.
	 * @exception  OutOfMemoryError            if there is not enough memory.
	 * @see        java.lang.Cloneable
	 * @since      1.2
	 */
	@Override
	public Object clone()
	{
		byte[] copyPointTypes = new byte[this.pointTypes.length];
		System.arraycopy(pointTypes, 0, copyPointTypes, 0, this.pointTypes.length);
		float[] copyPointCoords = new float[this.pointCoords.length];
		System.arraycopy(pointCoords, 0, copyPointCoords, 0, this.pointCoords.length);
		GeneralShape copy = new GeneralShape();
		copy.windingRule = this.windingRule;
		copy.numTypes = this.numTypes;
		copy.pointTypes = copyPointTypes;
		copy.numCoords = this.numCoords;
		copy.pointCoords = copyPointCoords;
		copy.bounds = null;
		copy.pathSegments = null;
		return copy;
	}

	GeneralShape(int windingRule,
			byte[] pointTypes,
			int numTypes,
			float[] pointCoords,
			int numCoords)
			{
		// used to construct from native
		this.windingRule = windingRule;
		this.pointTypes = pointTypes;
		this.numTypes = numTypes;
		this.pointCoords = pointCoords;
		this.numCoords = numCoords;
			}

	@Override
	public boolean equals(Object o)
	{
		if (o instanceof GeneralShape)
		{
			GeneralShape gs = (GeneralShape) o;

			// windigRule
			if (this.windingRule != gs.getWindingRule())
				return false;

			// numCoords
			if (this.numCoords != gs.getNumCoords())
				return false;

			// numTypes
			if (this.numTypes != gs.getNumTypes())
				return false;

			// pointCoords
			if (this.pointCoords.length != gs.getPointCoords().length) {
				return false;
			}
			else {
				float[] gsPointCoords = gs.getPointCoords();
				for (int i=0; i<pointCoords.length; i++) {
					float f = pointCoords[i];
					if (f != gsPointCoords[i])
						return false;
				}
			}

			// pointTypes
			if (this.pointTypes.length != gs.pointTypes.length) {
				return false;
			}
			else {
				byte[] gsPointTypes = gs.getPointTypes();
				for (int i=0; i<pointTypes.length; i++) {
					byte b = pointTypes[i];
					if (b != gsPointTypes[i])
						return false;
				}
			}

		} // if (o instanceof GeneralShape)
		else {
			return false;
		}
		return true;
	}

	public synchronized void setLastPoint(float x, float y)
	{
		if (numTypes < 1 || numCoords < 2) {
			return;
		}
		int index = numCoords;
		if (pointTypes[numTypes - 1] == PathIterator.SEG_CLOSE) {
			loop:
				for (int i = numTypes - 2; i > 0; i--) {
					switch (pointTypes[i]) {
					case PathIterator.SEG_MOVETO:
						break loop;
					case PathIterator.SEG_LINETO:
						index -= 2;
						break;
					case PathIterator.SEG_QUADTO:
						index -= 4;
						break;
					case PathIterator.SEG_CUBICTO:
						index -= 6;
						break;
					case PathIterator.SEG_CLOSE:
						break;
					}
				}
		}
		pointCoords[index - 2] = x;
		pointCoords[index - 1] = y;
		bounds = null;
	}

	public synchronized void setPathSegment(int type, int index, float[] coords)
	{
		// TODO: check why it sometimes works wrong
		System.out.println("GeneralShape.setPathSegment");
		switch (type)
		{
		case (PathIterator.SEG_MOVETO):
			pointCoords[index] = coords[0];
		pointCoords[index+1] = coords[1];
		break;
		case (PathIterator.SEG_LINETO):
			pointCoords[index] = coords[0];
		pointCoords[index+1] = coords[1];
		break;
		case (PathIterator.SEG_QUADTO):
			pointCoords[index] = coords[0];
		pointCoords[index+1] = coords[1];
		pointCoords[index+2] = coords[2];
		pointCoords[index+3] = coords[3];
		break;
		case (PathIterator.SEG_CUBICTO):
			pointCoords[index] = coords[0];
		pointCoords[index+1] = coords[1];
		pointCoords[index+2] = coords[2];
		pointCoords[index+3] = coords[3];
		pointCoords[index+4] = coords[4];
		pointCoords[index+5] = coords[5];
		break;
		}
		bounds = null;
	}

//	public static final float DEFAULT_TOLERANCE = 2;

//	public int pathSegmentContains(float x1, float y1)
//	{
//	return pathSegmentContains(x1, y1, true);
//	}

//	public int pathSegmentContains(float x1, float y1, boolean tolerance)
//	{
//	return pathSegmentContains(x1, y1, DEFAULT_TOLERANCE, tolerance);
//	}

//	public int pathSegmentContains(Point2D p, boolean tolerance)
//	{
//	return pathSegmentContains((float)p.getX(), (float)p.getY(), DEFAULT_TOLERANCE, tolerance);
//	}

//	/**
//	* Checks if any PathSegment contains the given Point + Distance Tolerance
//	*
//	* @param x1 x-Coordinate from the Point
//	* @param y1 y-Coordinate from the Point
//	* @param tolerance The Distance-Tolerance
//	* @return The startIndex of the pointCoords[]-Array for the matching PathSegment
//	*/
//	public int pathSegmentContains(float x1, float y1, float toleranceValue, boolean tolerance)
//	{
//	for (int i=0; i<pointCoords.length-1; i = i+2) {
//	float x2 = pointCoords[i];
//	float y2 = pointCoords[i+1];
//	if (tolerance)
//	{
//	if (interior (x1, y1, x2, y2, toleranceValue)) {
//	return i;
//	}
//	} else {
//	if (x1 == x2 && y1 == y2)
//	return i;
//	}
//	}
//	return -1;
//	}

//	/**
//	* Checks if the distance from one Point to another is within the given distance
//	*
//	* @param x1 x-Coordinate from Point 1
//	* @param y1 y-Coordinate from Point 1
//	* @param x2 x-Coordinate from Point 2
//	* @param y2 y-Coordinate from Point 2
//	* @param tolerance The Distance-Tolerance
//	*
//	* @return true if the distance is smaller than the tolerance otherwise false
//	*/
//	public static boolean interior(float x1, float y1, float x2, float y2, float tolerance)
//	{
////	TODO: maybe generating a Rectangle and call contains(x,y) is faster
//	double dist = Math.sqrt(Math.pow((x1-x2),2) - Math.pow((y1-y2),2));
//	return dist <= tolerance ? true : false;
//	}

	protected void initPathSegments()
	{
		pathSegments = new ArrayList<PathSegment>();
		int index = 0;
		float[] coords = new float[6];
		for (PathIterator pi = getPathIterator(null); !pi.isDone(); pi.next())
		{
			int segType = pi.currentSegment(coords);
			switch (segType)
			{
			case (PathIterator.SEG_MOVETO):
				PathSegment ps = new PathSegment(segType, index, coords, this);
				pathSegments.add(ps);
				index = index + 2;
			break;
			case (PathIterator.SEG_LINETO):
				PathSegment ps1 = new PathSegment(segType, index, coords, this);
				pathSegments.add(ps1);
				index = index + 2;
			break;
			case (PathIterator.SEG_QUADTO):
				PathSegment ps2 = new PathSegment(segType, index, coords, this);
				pathSegments.add(ps2);
				index = index + 4;
			break;
			case (PathIterator.SEG_CUBICTO):
				PathSegment ps3 = new PathSegment(segType, index, coords, this);
				pathSegments.add(ps3);
				index = index + 6;
			break;
			case (PathIterator.SEG_CLOSE):
//				PathSegment ps4 = new PathSegment(segType, index, coords, this);
//				pathSegments.add(ps4);
//				index = index + 2;
				break;
			}
		}
	}

	public List<PathSegment> getPathSegments()
	{
		if (pathSegments == null)
			initPathSegments();

		return pathSegments;
	}

	public PathSegment getPathSegment(int index)
	{
		if (pathSegments == null)
			initPathSegments();

		return pathSegments.get(index);
	}

	public PathSegment getPathSegmentAt(int x, int y)
	{
		for (Iterator<PathSegment> it = getPathSegments().iterator(); it.hasNext(); ) {
			PathSegment ps = it.next();
			int contains = ps.contains(x,y);
			if (contains != PathSegment.NO_POINT)
				return ps;
		}
		return null;
	}

	public int getPointCount()
	{
		int counter = 0;
		for (PathIterator pi = getPathIterator(null); !pi.isDone(); pi.next()) {
			counter++;
		}
		return counter;
	}

}

