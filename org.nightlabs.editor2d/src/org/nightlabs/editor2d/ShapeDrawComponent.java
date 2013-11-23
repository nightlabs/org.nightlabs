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

package org.nightlabs.editor2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.i18n.unit.resolution.Resolution;

public interface ShapeDrawComponent
extends DrawComponent, IFillable
{
//	public static final int STROKE_SOLID = 1;
//	public static final int STROKE_DASHED_1 = 2;
//	public static final int STROKE_DASHED_2 = 3;
//	public static final int STROKE_DASHED_3 = 4;
//	public static final int STROKE_DASHED_4 = 5;

	static class StrokeUtil
	{
		public static Stroke getStroke(float strokeWidth, LineStyle lineStyle, Resolution res)
		{
//			float resolutionScale = (float) UnitUtil.getResolutionScale(72, res);
//			float dash = resolutionScale;
			float dash = 1f;
			int dashPhase = 0;
//			float miterLimit = 10.0f * resolutionScale;
			float miterLimit = 10.0f;
//			float lineWidth = strokeWidth * resolutionScale;
			float lineWidth = strokeWidth;
			float[] dashArray = new float[] {dash, dash};

			if (lineStyle == LineStyle.SOLID) {
				return new BasicStroke(lineWidth);
			}
			else if (lineStyle == LineStyle.DASHED_1) {
//				dash = 6f * resolutionScale;
				dash = 6f;
				dashArray = new float[] {dash, dash};
			}
			else if (lineStyle == LineStyle.DASHED_2) {
//				dash = 4f * resolutionScale;
				dash = 4f;
				dashArray = new float[] {dash, dash};
			}
			else if (lineStyle == LineStyle.DASHED_3) {
//				dash = 2f * resolutionScale;
				dash = 2f;
				dashArray = new float[] {dash, dash};
			}
			else if (lineStyle == LineStyle.DASHED_4) {
//				dashArray = new float[]{3f * resolutionScale, 6f * resolutionScale};
				dashArray = new float[]{3f, 6f};
				dashPhase = 1;
			}
			return new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, miterLimit, dashArray, dashPhase);
		}
	}

	public enum LineStyle {
		SOLID,
		DASHED_1,
		DASHED_2,
		DASHED_3,
		DASHED_4,
	}

	public static final String PROP_FILL_COLOR = "fillColor";
	public static final String PROP_LINE_COLOR = "lineColor";
	public static final String PROP_LINE_STYLE = "lineStyle";
	public static final String PROP_LINE_WIDTH = "lineWidth";
	public static final String PROP_GENERAL_SHAPE = "generalShape";
	public static final String PROP_SHOW_STROKE = "showStroke";

	/**
	 * The Default Fill Color = WHITE
	 */
	public static final Color FILL_COLOR_DEFAULT = Color.WHITE;
	/**
	 * The Default Line Color = BLACK
	 */
	public static final Color LINE_COLOR_DEFAULT = Color.BLACK;
//	/**
//	* The Default Line Style = 1
//	*/
//	public static final int LINE_STYLE_DEFAULT = 1;
	/**
	 * The Default Line Style = SOLID
	 */
	public static final LineStyle LINE_STYLE_DEFAULT = LineStyle.SOLID;

	/**
	 * The Default Line Width = 1
	 */
	public static final float LINE_WIDTH_DEFAULT = 1;

//	/**
//	 * The Default GeneralShape = null
//	 */
//	public static final GeneralShape GENERAL_SHAPE_DEFAULT = null;
	/**
	 * The Default Fill Behaviour = true
	 */
	public static final boolean FILL_DEFAULT = true;

	/**
	 * returns the fillColor of the ShapeDrawComponent
	 * 
	 * @return the fillColor
	 */
	Color getFillColor();

	/**
	 * sets the fillColor of the ShapeDrawComponent
	 * 
	 * @param value the new fillColor to set
	 */
	void setFillColor(Color value);

	/**
	 * returns the lineColor of the ShapeDrawComponent
	 * 
	 * @return the lineColor
	 */
	Color getLineColor();

	/**
	 * sets the lineColor of the ShapeDrawComponent
	 * 
	 * @param value the new lineColor to set
	 */
	void setLineColor(Color value);

//	/**
//	* returns the lineStyle of the ShapeDrawComponent
//	* by default the lineStyle is {@link ShapeDrawComponent#STROKE_SOLID}
//	*
//	* @return the lineStyle of the ShapeDrawComponent
//	* @see RenderConstants
//	*/
//	int getLineStyle();

//	/**
//	* sets the lineStyle of the ShapeDrawComponent
//	* the permitted values are
//	*
//	* STROKE_SOLID,
//	* STROKE_DASHED_1,
//	* STROKE_DASHED_2,
//	* STROKE_DASHED_3,
//	* STROKE_DASHED_4
//	*
//	* @param value the new lineStyle to set
//	*/
//	void setLineStyle(int value);

	/**
	 * returns the {@link LineStyle} of the ShapeDrawComponent
	 * by default the lineStyle is {@link LineStyle#SOLID}
	 * 
	 * @return the lineStyle of the ShapeDrawComponent
	 */
	LineStyle getLineStyle();

	/**
	 * sets the {@link LineStyle} of the ShapeDrawComponent
	 * 
	 * @param lineStyle the new lineStyle to set
	 */
	void setLineStyle(LineStyle lineStyle);

	/**
	 * returns the width of the stroke
	 * 
	 * @return the width of the stroke
	 */
	float getLineWidth();

	/**
	 * sets the new width of the stroke
	 * 
	 * @param value the new width of the stroke
	 */
	void setLineWidth(float value);

	/**
	 * returns the current transformed {@link GeneralShape} of the ShapeDrawComponent
	 * 
	 * @return the current transformed {@link GeneralShape} of the ShapeDrawComponent
	 */
	GeneralShape getGeneralShape();

	/**
	 * sets the transformed shape of the ShapeDrawComponent
	 * @param value the transformed shape of the ShapeDrawComponent to set
	 */
	void setGeneralShape(GeneralShape value);

	/**
	 * returns the original / initial GeneralShape of the ShapeDrawComponent.
	 * when {@link DrawComponent#transform()} is called
	 * only the AffineTransform is concated and a clone of this original shape
	 * is transformed to produce the current generalShape returned by
	 * {@link ShapeDrawComponent#getGeneralShape()}. This is done to minimize
	 * rounding problems when transforming a GeneralShape multiple times
	 * 
	 * @return  the original / initial GeneralShape of the ShapeDrawComponent
	 */
	public GeneralShape getOriginalShape();

	/**
	 * returns true if the stroke (outline) is visible or false if not
	 * @return if the stroke is visible or not
	 */
	boolean isShowStroke();

	/**
	 * determines if the stroke (outline) is visible or not
	 * @param showStroke determines if the stroke is visible or not
	 */
	void setShowStroke(boolean showStroke);

	/**
	 * returns the {@link Stroke} which represents the outline of the ShapeDrawComponent
	 * and is determined by the lineWidth and the lineStyle
	 * 
	 * @return the {@link Stroke} which represents the outline of the ShapeDrawComponent
	 */
	public Stroke getStroke();
} // ShapeDrawComponent
