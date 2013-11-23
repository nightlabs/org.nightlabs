/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.impl;

import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import org.nightlabs.editor2d.DrawComponentContainer;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 */
public abstract class AbstractConstrainedRotationTextDrawComponent
extends AbstractTextDrawComponent
{
	private static final long serialVersionUID = 1L;
	private static final List<Double> constrainedRotationValues;
	static
	{
		constrainedRotationValues = new LinkedList<Double>();
		constrainedRotationValues.add(0d);
		constrainedRotationValues.add(90d);
		constrainedRotationValues.add(180d);
		constrainedRotationValues.add(270d);
	}

	public AbstractConstrainedRotationTextDrawComponent()
	{
		super();
		init();
	}

	public AbstractConstrainedRotationTextDrawComponent(String text, Font font,
			int x, int y, DrawComponentContainer parent)
	{
		super(text, font, x, y, parent);
		init();
	}

	public AbstractConstrainedRotationTextDrawComponent(String text,
			String fontName, int fontSize, int fontStyle, int x, int y,
			DrawComponentContainer parent)
	{
		super(text, fontName, fontSize, fontStyle, x, y, parent);
		init();
	}

	protected void init()
	{
		setChangedRotationCenterAllowed(false);
		setConstrainedRotationValues(constrainedRotationValues);
		setRotationCenterPosition(RotationCenterPositions.LEFT_TOP);
	}

	@Override
	public int getRotationX()
	{
		int rotationX = super.getRotationX();
		if (isChangedRotationCenterAllowed() && rotationX != ROTATION_X_DEFAULT) {
			return rotationX;
		}
		else
		{
			double rotation = getRotation();
			switch(getRotationCenterPosition())
			{
				case LEFT_TOP:
					if (rotation == 0)
						return getRotationCenterPositionX(RotationCenterPositions.LEFT_TOP);
					else if (rotation == 90)
						return getRotationCenterPositionX(RotationCenterPositions.RIGHT_TOP);
					else if (rotation == 180)
						return getRotationCenterPositionX(RotationCenterPositions.RIGHT_BOTTOM);
					else if (rotation == 270)
						return getRotationCenterPositionX(RotationCenterPositions.LEFT_BOTTOM);
				case LEFT_BOTTOM:
					if (rotation == 0)
						return getRotationCenterPositionX(RotationCenterPositions.LEFT_BOTTOM);
					else if (rotation == 90)
						return getRotationCenterPositionX(RotationCenterPositions.LEFT_TOP);
					else if (rotation == 180)
						return getRotationCenterPositionX(RotationCenterPositions.RIGHT_TOP);
					else if (rotation == 270)
						return getRotationCenterPositionX(RotationCenterPositions.RIGHT_BOTTOM);
				case RIGHT_TOP:
					return (int) getBounds().getMaxX();
				case RIGHT_BOTTOM:
					return (int) getBounds().getMaxX();
				case CENTER:
					return (int) getBounds().getCenterX();
			}
		}
		return rotationX;
	}

	@Override
	public int getRotationY()
	{
		int rotationY = super.getRotationY();
		if (isChangedRotationCenterAllowed() && rotationY != ROTATION_Y_DEFAULT) {
			return rotationY;
		}
		else
		{
			double rotation = getRotation();
			switch(getRotationCenterPosition())
			{
				case LEFT_TOP:
					if (rotation == 0)
						return getRotationCenterPositionY(RotationCenterPositions.LEFT_TOP);
					else if (rotation == 90)
						return getRotationCenterPositionY(RotationCenterPositions.RIGHT_TOP);
					else if (rotation == 180)
						return getRotationCenterPositionY(RotationCenterPositions.RIGHT_BOTTOM);
					else if (rotation == 270)
						return getRotationCenterPositionY(RotationCenterPositions.LEFT_BOTTOM);
				case LEFT_BOTTOM:
					if (rotation == 0)
						return getRotationCenterPositionY(RotationCenterPositions.LEFT_BOTTOM);
					else if (rotation == 90)
						return getRotationCenterPositionY(RotationCenterPositions.LEFT_TOP);
					else if (rotation == 180)
						return getRotationCenterPositionY(RotationCenterPositions.RIGHT_TOP);
					else if (rotation == 270)
						return getRotationCenterPositionY(RotationCenterPositions.LEFT_BOTTOM);
				case RIGHT_TOP:
					return (int) getBounds().getMaxY();
				case RIGHT_BOTTOM:
					return (int) getBounds().getMaxY();
				case CENTER:
					return (int) getBounds().getCenterY();
			}
		}
		return rotationY;
	}
}
