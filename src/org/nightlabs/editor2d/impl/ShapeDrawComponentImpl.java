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

package org.nightlabs.editor2d.impl;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.io.Serializable;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.render.BaseRenderer;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DShapeDefaultRenderer;

public class ShapeDrawComponentImpl
extends DrawComponentImpl
implements ShapeDrawComponent, Serializable
{
	private static final long serialVersionUID = 1L;
	private Color fillColor = FILL_COLOR_DEFAULT;
	private Color lineColor = LINE_COLOR_DEFAULT;
	private LineStyle lineStyle = LINE_STYLE_DEFAULT;
	private float lineWidth = LINE_WIDTH_DEFAULT;
	protected transient GeneralShape generalShape = null; // = GENERAL_SHAPE_DEFAULT;
	private boolean fill = FILL_DEFAULT;
	protected GeneralShape originalShape = null;
	private boolean showStroke = true;
	private transient Stroke stroke = null;

	public ShapeDrawComponentImpl() {
		super();
	}

	public boolean isFill() {
		return fill;
	}
	public void setFill(boolean newFill) {
		boolean oldFill = fill;
		primSetFill(newFill);
		firePropertyChange(PROP_FILL, oldFill, fill);
	}
	protected void primSetFill(boolean fill) {
		this.fill = fill;
	}

	public Color getFillColor() {
		return fillColor;
	}
	public void setFillColor(Color newFillColor) {
		Color oldFillColor = fillColor;
		primSetFillColor(newFillColor);
		firePropertyChange(PROP_FILL_COLOR, oldFillColor, fillColor);
	}
	protected void primSetFillColor(Color newFillColor) {
		fillColor = newFillColor;
	}

	public Color getLineColor() {
		return lineColor;
	}
	public void setLineColor(Color newLineColor) {
		Color oldLineColor = lineColor;
		primSetLineColor(newLineColor);
		firePropertyChange(PROP_LINE_COLOR, oldLineColor, lineColor);
	}
	protected void primSetLineColor(Color newLineColor) {
		lineColor = newLineColor;
	}

//	public int getLineStyle() {
//	return lineStyle;
//	}
//	public void setLineStyle(int newLineStyle) {
//	int oldLineStyle = lineStyle;
//	primSetLineStyle(newLineStyle);
//	firePropertyChange(PROP_LINE_COLOR, oldLineStyle, lineStyle);
//	}
//	protected void primSetLineStyle(int newLineStyle) {
//	lineStyle = newLineStyle;
//	stroke = null;
//	}

	public LineStyle getLineStyle() {
		return lineStyle;
	}
	public void setLineStyle(LineStyle newLineStyle) {
		LineStyle oldLineStyle = lineStyle;
		primSetLineStyle(newLineStyle);
		firePropertyChange(PROP_LINE_COLOR, oldLineStyle, lineStyle);
	}
	protected void primSetLineStyle(LineStyle newLineStyle) {
		lineStyle = newLineStyle;
		stroke = null;
	}

//	public int getLineWidth() {
//	return lineWidth;
//	}
//	public void setLineWidth(int newLineWidth) {
//	int oldLineWidth = lineWidth;
//	primSetLineWidth(newLineWidth);
//	firePropertyChange(PROP_LINE_WIDTH, oldLineWidth, lineWidth);
//	}
//	protected void primSetLineWidth(int newLineWidth) {
//	lineWidth = newLineWidth;
//	stroke = null;
//	}

	public float getLineWidth() {
		return lineWidth;
	}
	public void setLineWidth(float newLineWidth) {
		float oldLineWidth = lineWidth;
		primSetLineWidth(newLineWidth);
		firePropertyChange(PROP_LINE_WIDTH, oldLineWidth, lineWidth);
	}
	protected void primSetLineWidth(float newLineWidth) {
		lineWidth = newLineWidth;
		stroke = null;
	}

	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (fillColor: ");
		result.append(fillColor);
		result.append(", lineColor: ");
		result.append(lineColor);
		result.append(", lineStyle: ");
		result.append(lineStyle);
		result.append(", lineWidth: ");
		result.append(lineWidth);
		result.append(", generalShape: ");
		result.append(generalShape);
		result.append(", fill: ");
		result.append(fill);
		result.append(')');
		return result.toString();
	}

	/**
	 * @return the Bounds of the ShapeDrawComponent
	 * if a GeneralShape is present (!= null), those bounds are returned,
	 * else a new Rectangle with the values of the members x, y, width, height is returned
	 *
	 */
	@Override
	public Rectangle getBounds()
	{
		if (getGeneralShape() != null)
			return getGeneralShape().getBounds();
		else
			return super.getBounds();
	}

	public GeneralShape getOriginalShape() {
		return originalShape;
	}

	public boolean isShowStroke() {
		return showStroke;
	}
	public void setShowStroke(boolean showStroke) {
		primSetShowStroke(showStroke);
		firePropertyChange(PROP_SHOW_STROKE, !showStroke, showStroke);
	}
	protected void primSetShowStroke(boolean showStroke) {
		this.showStroke = showStroke;
	}

	public Stroke getStroke()
	{
		if (stroke == null) {
			if (getRoot() != null)
				stroke = StrokeUtil.getStroke(getLineWidth(), getLineStyle(), getRoot().getResolution());
			else
				stroke = StrokeUtil.getStroke(getLineWidth(), getLineStyle(), null);
		}
		return stroke;
	}

	/**
	 * transforms the GeneralShape based on the given AffineTransform
	 * this is done by first cloning the originalShape and then transforming the clone,
	 * this reduces rounding errors when transforming multiple times
	 * @param at the AffineTransform which contains the transform Information
	 * @see java.awt.geom.AffineTransform
	 */
	@Override
	public void transform(AffineTransform at, boolean fromParent)
	{
		// TODO remove check if generalShape is transient
		if (generalShape != null) {
			super.transform(at, fromParent);
			generalShape = initGeneralShape();
			generalShape.transform(getAffineTransform());
		}

		if (!fromParent && getParent() != null)
			getParent().notifyChildTransform(this);

		clearBounds();
	}

	protected GeneralShape initGeneralShape() {
		if (originalShape != null)
			return (GeneralShape) originalShape.clone();
		else {
			return new GeneralShape(new Rectangle(x, y, 0, 0));
		}
	}

	public GeneralShape getGeneralShape()
	{
//		TODO: uncomment if transient generalShape works
		if (generalShape == null) {
			generalShape = initGeneralShape();
			generalShape.transform(getAffineTransform());
		}
		return generalShape;
	}

	public void setGeneralShape(GeneralShape newGeneralShape)
	{
		GeneralShape oldGeneralShape = generalShape;
		primSetGeneralShape(newGeneralShape);
		firePropertyChange(PROP_GENERAL_SHAPE, oldGeneralShape, generalShape);
	}

	protected void primSetGeneralShape(GeneralShape gs)
	{
		generalShape = gs;
		originalShape = (GeneralShape) gs.clone();
		clearBounds();
	}

	@Override
	public Class<? extends DrawComponent> getRenderModeClass() {
		return ShapeDrawComponent.class;
	}

	@Override
	public String getTypeName() {
		return "Shape";
	}

	@Override
	public Object clone(DrawComponentContainer parent)
	{
		ShapeDrawComponentImpl sdc = (ShapeDrawComponentImpl) super.clone(parent);

		if (generalShape != null)
			sdc.generalShape = (GeneralShape) generalShape.clone();

		if (originalShape != null)
			sdc.originalShape = (GeneralShape) originalShape.clone();

		sdc.fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), fillColor.getAlpha());
		sdc.lineColor = new Color(lineColor.getRed(), lineColor.getGreen(), lineColor.getBlue(), lineColor.getAlpha());

		sdc.fill = fill;
		sdc.lineStyle = lineStyle;
		sdc.lineWidth = lineWidth;

		return sdc;
	}

	@Override
	protected Renderer initDefaultRenderer() {
		Renderer r = new BaseRenderer();
		r.addRenderContext(new J2DShapeDefaultRenderer());
		return r;
	}

	@Override
	public void dispose()
	{
		super.dispose();
		generalShape = null;
		originalShape = null;
		fillColor = null;
		lineColor = null;
		stroke = null;
	}

} //ShapeDrawComponentImpl
