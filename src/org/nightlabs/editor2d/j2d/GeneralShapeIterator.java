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
 * This class represents the iterator for General Paths.
 * It can be used to retrieve all of the elements in a GeneralShape.
 * The {@link GeneralShape#getPathIterator}
 *  method is used to create a
 * GeneralShapeIterator for a particular GeneralShape.
 * The iterator can be used to iterator the path only once.
 * Subsequent iterations require a new iterator.
 *
 * @see GeneralShape
 */
class GeneralShapeIterator implements PathIterator {
    int typeIdx = 0;
    int pointIdx   = 0;
    GeneralShape path;
    AffineTransform affine;

    private static final int curvesize[] = {2, 2, 4, 6, 0};

    /**
     * Constructs an iterator given a GeneralShape.
     * @see GeneralShape#getPathIterator
     */
    GeneralShapeIterator(GeneralShape path) {
        this(path, null);
    }

    /**
     * Constructs an iterator given a GeneralShape and an optional
     * AffineTransform.
     * @see GeneralShape#getPathIterator
     */
    GeneralShapeIterator(GeneralShape path, AffineTransform at) {
        this.path = path;
        this.affine = at;
    }

    /**
     * Return the winding rule for determining the interior of the
     * path.
     * @see PathIterator#WIND_EVEN_ODD
     * @see PathIterator#WIND_NON_ZERO
     */
    public int getWindingRule() {
        return path.getWindingRule();
    }

    /**
     * Tests if there are more points to read.
     * @return true if there are more points to read
     */
    public boolean isDone() {
        return (typeIdx >= path.numTypes);
    }

    /**
     * Moves the iterator to the next segment of the path forwards
     * along the primary direction of traversal as long as there are
     * more points in that direction.
     */
    public void next() {
        int type = path.pointTypes[typeIdx++];
        pointIdx += curvesize[type];
    }

    /**
     * Returns the coordinates and type of the current path segment in
     * the iteration.
     * The return value is the path segment type:
     * SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE.
     * A float array of length 6 must be passed in and may be used to
     * store the coordinates of the point(s).
     * Each point is stored as a pair of float x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point,
     * SEG_QUADTO will return two points,
     * SEG_CUBICTO will return 3 points
     * and SEG_CLOSE will not return any points.
     * @see PathIterator#SEG_MOVETO
     * @see PathIterator#SEG_LINETO
     * @see PathIterator#SEG_QUADTO
     * @see PathIterator#SEG_CUBICTO
     * @see PathIterator#SEG_CLOSE
     */
    public int currentSegment(float[] coords) {
        int type = path.pointTypes[typeIdx];
        int numCoords = curvesize[type];
        if (numCoords > 0 && affine != null) {
            affine.transform(path.pointCoords, pointIdx,
                             coords, 0,
                             numCoords / 2);
        } else {
            System.arraycopy(path.pointCoords, pointIdx, coords, 0, numCoords);
        }
        return type;
    }

    /**
     * Returns the coordinates and type of the current path segment in
     * the iteration.
     * The return value is the path segment type:
     * SEG_MOVETO, SEG_LINETO, SEG_QUADTO, SEG_CUBICTO, or SEG_CLOSE.
     * A double array of length 6 must be passed in and may be used to
     * store the coordinates of the point(s).
     * Each point is stored as a pair of double x,y coordinates.
     * SEG_MOVETO and SEG_LINETO types will return one point,
     * SEG_QUADTO will return two points,
     * SEG_CUBICTO will return 3 points
     * and SEG_CLOSE will not return any points.
     * @see PathIterator#SEG_MOVETO
     * @see PathIterator#SEG_LINETO
     * @see PathIterator#SEG_QUADTO
     * @see PathIterator#SEG_CUBICTO
     * @see PathIterator#SEG_CLOSE
     */
    public int currentSegment(double[] coords) {
        int type = path.pointTypes[typeIdx];
        int numCoords = curvesize[type];
        if (numCoords > 0 && affine != null) {
            affine.transform(path.pointCoords, pointIdx,
                             coords, 0,
                             numCoords / 2);
        } else {
            for (int i=0; i < numCoords; i++) {
                coords[i] = path.pointCoords[pointIdx + i];
            }
        }
        return type;
    }
}

