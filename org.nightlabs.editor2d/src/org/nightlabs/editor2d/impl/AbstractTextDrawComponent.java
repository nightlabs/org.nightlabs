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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.j2d.TransformListener;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public abstract class AbstractTextDrawComponent
extends ShapeDrawComponentImpl
implements TextDrawComponent
{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractTextDrawComponent.class);

	private int fontSize = FONT_SIZE_DEFAULT;
	private boolean bold = BOLD_DEFAULT;
	private boolean italic = ITALIC_DEFAULT;
	private String fontName = FONT_NAME_DEFAULT;
	private transient Font font = FONT_DEFAULT;
	private int fontStyle = Font.PLAIN;

	private int originalX = 0;
	private int originalY = 0;

	private boolean transformed = false;
	private transient AffineTransform frcAT = null;
	private transient FontRenderContext frc = null;
	private static final transient AffineTransform identityAT = new AffineTransform();

	/**
	 *  Standard Constructor (only for for {@link Editor2DFactory})
	 */
	public AbstractTextDrawComponent() {
		super();
	}

	/**
	 * Creates an AbstractTextDrawComponent.
	 *
	 * @param text the text as string
	 * @param font the {@link Font}
	 * @param x the x-coordinate (left top)
	 * @param y the y-coordinate (left top)
	 * @param parent the parent {@link DrawComponentContainer}.
	 */
	public AbstractTextDrawComponent(String text, Font font, int x, int y,
			DrawComponentContainer parent)
	{
		// TODO turn around constructor make a font out of the fontName, fontSize and fontStyle and not
		// the other way round
		this(text, font.getName(), font.getSize(), font.getStyle(), x, y, parent);
	}

	/**
	 * Creates an AbstractTextDrawComponent.
	 *
	 * @param text the text as string
	 * @param fontName the name of the font
	 * @param fontSize the size of the font
	 * @param fontStyle the style of the font
	 * @param x the x-coordinate (left top)
	 * @param y the y-coordinate (left top)
	 * @param parent the parent {@link DrawComponentContainer}.
	 */
	public AbstractTextDrawComponent(String text, String fontName, int fontSize, int fontStyle,
			int x, int y, DrawComponentContainer parent)
	{
		if (text == null) {
			throw new IllegalArgumentException("Param text must not be null");
		}
		if (text.isEmpty()) {
			text = " ";
		}

		this.x = x;
		this.y = y;

		this.originalX = x;
		this.originalY = y;

		primSetFillColor(Color.BLACK);
		this.fontName = fontName;
		this.fontSize = fontSize;
		this.fontStyle = fontStyle;
		if ((fontStyle & Font.BOLD) != 0)
			this.bold = true;
		if ((fontStyle & Font.ITALIC) != 0)
			this.italic = true;
		primSetParent(parent);
		updateFont();
		primSetShowStroke(false);
		setInternalText(text);
		primSetName(text);
		update();
	}

	@Override
	public void setGeneralShape(GeneralShape newGeneralShape)
	{
		super.setGeneralShape(newGeneralShape);
		getGeneralShape().addTransformListener(transformListener);
	}

	public boolean isTransformed() {
		return transformed;
	}

	private transient TransformListener transformListener = new TransformListener()
	{
		public void transformChanged(AffineTransform at)
		{
			// TODO: does not work for rotation because then scaleX, scaleY also change
//			LOGGER.debug("transform Changed!");
			double scaleY = at.getScaleY();
			double scaleX = at.getScaleX();
			if (scaleY != 1 || scaleX != 1) {
				transformed = true;
				logger.debug("TextDrawComponent is transformed!");
			}
		}
	};

	protected Point2D convertLeftTopToBaseline(float x, float y)
	{
		double yOffset = getAscent();
		return new Point2D.Float(x, (float) (y+yOffset));
	}

	protected int convertDouble(double d) {
		return (int) Math.rint(d);
	}

//	private int convertDouble(double d, int i)
//	{
//		int ceil = (int) Math.ceil(d);
//		int floor = (int) Math.floor(d);
//		if (ceil == i) {
//			return ceil;
//		}
//		else if (floor == i) {
//			return floor;
//		}
//		int rounded = (int) Math.rint(d);
//		return rounded;
//	}

	protected GlyphVector createGlyphVector(String text, FontRenderContext frc, Font f)
	{
		if (text == null) {
			text = " ";
		}
		return f.layoutGlyphVector(frc, text.toCharArray(), 0, text.length(), 0); // It's documented that this is required for some "complex" scripts like Arabic or Thai. Thus we use this instead createGlyphVector.
	}

	/**
	 *
	 * @param glyphVector the {@link GlyphVector} to create a {@link GeneralShape} for.
	 * @param x the x-coordinate (left top)
	 * @param y the y-coordinate (left top)
	 * @return the created GeneralShape which left top corner of the bounds should be the same as the given x and y
	 */
	protected GeneralShape createTextShape(GlyphVector glyphVector, float x, float y)
	{
		Point2D p = convertLeftTopToBaseline(x, y);
		// check if string is empty
		if (p != null) {
			float newY = (float) p.getY();
			float newX = (float) p.getX();
			Shape glyphShape = glyphVector.getOutline(newX, newY);
			GeneralShape gs = null;
			if (glyphShape.getBounds().equals(BOUNDS_DEFAULT)) {
				gs = new GeneralShape(new Rectangle((int)Math.rint(newX), (int)Math.rint(newY), 0, 0));
			}
			else {
				gs = new GeneralShape(glyphShape);
			}

			FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
			gs.transform(getFontRenderContextTransform());

			double resolutionScale = getFontRenderContextTransform().getScaleY();
//			double resolutionScale = 1;
//			fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
			double ascent = fontMetrics.getAscent() * resolutionScale;
			double descent = fontMetrics.getDescent() * resolutionScale;
			double height = ascent + descent;
			double shapeHeight = gs.getBounds().getHeight();
			double diffHeight = height - shapeHeight;
			double diffAscentDescent = ascent - descent;
//			double transformedX = (float) gs.getBounds().getX();
//			double transformedY = (float) gs.getBounds().getY();
//			double translateX = oldX - transformedX;
//			double translateY = oldY - transformedY;

//			Point2D transformedXY = getFontRenderContextTransform().transform(new Point2D.Double(x,y), null);
			Point2D transformedXY = getFontRenderContextTransform().transform(p, null);
			double transformedX = transformedXY.getX();
			double transformedY = transformedXY.getY();
			double translateX = newX - transformedX;
			double translateY = newY - transformedY;
			// TODO FIXME XXX Find out this resolution check is necessary? A nicer solution would be great.
			if (resolutionScale != 1.0) {
				translateY = newY - transformedY + diffAscentDescent;
			}

			AffineTransform translateAT = new AffineTransform();
			translateAT.translate(translateX, translateY);
			gs.transform(translateAT);

			if (logger.isDebugEnabled()) {
				logger.debug("createTextShape()");
				logger.debug("x = "+x);
				logger.debug("y = "+y);
				logger.debug("newX = "+newX);
				logger.debug("newY = "+newY);
				logger.debug("transformedX = "+transformedX);
				logger.debug("transformedY = "+transformedY);
				logger.debug("ascent = "+ascent);
				logger.debug("descent = "+descent);
				logger.debug("height = "+height);
				logger.debug("diffHeight = "+diffHeight);
				logger.debug("diffAscentDescent = "+diffAscentDescent);
				logger.debug("resolutionScale = "+resolutionScale);
				logger.debug("gs.getBounds() = "+gs.getBounds());
				logger.debug("");
			}
			return gs;
		}
		return null;
	}

	@Override
	public Rectangle getBounds()
	{
		if (bounds == null || BOUNDS_DEFAULT.equals(bounds)) {
			if (getRotation() == 0) {
				bounds = calculateBounds();
			}
			else {
				bounds = super.getBounds();
			}
		}
		return bounds;
//		return super.getBounds();
	}

	protected Point2D getTransformedOriginalXY()
	{
		Point2D.Double p = new Point2D.Double(originalX, originalY);
		Point2D result = getAffineTransform().transform(p, null);
//		if (logger.isDebugEnabled()) {
//			logger.debug("getTransformedOriginalXY()");
//			logger.debug("affineTransform = "+getAffineTransform());
//			logger.debug("originalX = "+originalX);
//			logger.debug("originalY = "+originalY);
//			logger.debug("resultX = "+result.getX());
//			logger.debug("resultY = "+result.getY());
//			logger.debug("");
//		}
		return result;
	}

	protected double getAscent()
	{
//		TextLayout layout = new TextLayout(getText(), getFont(), getFontRenderContext());
//		return layout.getAscent();

		// we use deprecated method here, because this is the only way (I have found) to obtain fontMetrics without graphics object
		// and only fontMetrics returns the MAX ascent and MAX decent for all characters of this font.
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
		double ascent = fontMetrics.getAscent();
		return ascent;
	}

	/**
	 * Calculates the bounds so that the height is always ascent + descent, the width is the width of the generated shape
	 * and x, y corresponds to the transformed originalX and originalY by the AffineTransform returned by {@link #getAffineTransform()}.
	 *
	 * @return the calculated bounds
	 */
	protected Rectangle calculateBounds()
	{
		Rectangle shapeBounds = super.getBounds();
		double scale = getAffineTransform().getScaleY();
		// TextLayout constructor throws an exception when calling it with a zero-length string:
		String txt = getText();
		if (txt == null || txt.isEmpty())
			txt = " ";
		TextLayout layout = new TextLayout(txt, getFont(), getFontRenderContext());
//		double ascent = layout.getAscent();
//		double descent = layout.getDescent();
		FontMetrics fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(getFont());
		double resolutionScale = getFontRenderContext().getTransform().getScaleY();
		double ascent = fontMetrics.getAscent() * resolutionScale;
		double descent = fontMetrics.getDescent() * resolutionScale;

		double scaledAscent = scale * ascent;;
		double scaledDescent = scale * descent;

		int newHeight = convertDouble(scaledAscent + scaledDescent);

		Point2D newXY = getTransformedOriginalXY();
		int newX = convertDouble(newXY.getX());
		int newY = convertDouble(newXY.getY());

		Rectangle bounds = new Rectangle(newX, newY, shapeBounds.width, newHeight);

		boolean fitInBounds = bounds.contains(shapeBounds);
		double diffY = shapeBounds.y - bounds.y;
		double diffHeight = shapeBounds.height - bounds.height;

		if (logger.isDebugEnabled())
		{
			logger.debug("calculateBounds()");
			logger.debug("x = "+x);
			logger.debug("y = "+y);
			logger.debug("text = "+getText());
			logger.debug("scale = "+scale);
			logger.debug("ascent = "+ascent);
			logger.debug("descent = "+descent);
			logger.debug("shapeBounds = "+shapeBounds);
			logger.debug("textLayoutBounds = "+layout.getBounds().getBounds());
			logger.debug("newX = "+newX);
			logger.debug("newY = "+newY);
			logger.debug("newHeight = "+newHeight);
			logger.debug("returned bounds = "+bounds);
			logger.debug("fitInBounds = "+fitInBounds);
			logger.debug("diffY = "+diffY);
			logger.debug("diffHeight = "+diffHeight);
			logger.debug("");
		}
		return bounds;
	}

	public FontRenderContext getFontRenderContext()
	{
		if (frc == null) {
			frc = new FontRenderContext(getFontRenderContextTransform(), true, false);
//			LOGGER.debug("frc AffineTransform = "+getFontRenderContextTransform());
		}
		return frc;
	}

	protected double getResolutionFactorX(int deviceDPI)
	{
		double documentDPI_X = getRoot().getResolution().getResolutionX(new DPIResolutionUnit());
		double factorX = documentDPI_X / deviceDPI;
		return factorX;
	}

	protected double getResolutionFactorY(int deviceDPI)
	{
		double documentDPI_Y = getRoot().getResolution().getResolutionY(new DPIResolutionUnit());
		double factorY = documentDPI_Y / deviceDPI;
		return factorY;
	}

	protected AffineTransform getFontRenderContextTransform()
	{
		if (frcAT == null)
		{
			frcAT = new AffineTransform();
			// TODO check if 72 is correct
			int defaultDPI = 72;
			double factorX = getResolutionFactorX(defaultDPI);
			double factorY = getResolutionFactorY(defaultDPI);
			frcAT.scale(factorX, factorY);
		}
		return frcAT;
	}

	protected void updateFont()
	{
		if (font == null)
			font = getFont();

		if (font.getFontName().equals(fontName) &&
				font.isBold() == isBold() &&
				font.isItalic() == isItalic() &&
				font.getSize() == fontSize)
			return;

		fontStyle = Font.PLAIN;
		if (isBold())
			fontStyle = fontStyle | Font.BOLD;
		if (isItalic())
			fontStyle = fontStyle | Font.ITALIC;

		font = new Font(fontName, fontStyle, fontSize);
	}

//	protected void update()
//	{
//		updateFont();
//		FontRenderContext frc = getFontRenderContext();
//
//		if (getGeneralShape() != null && getGeneralShape().hasTransformListener())
//			getGeneralShape().removeTransformListener(transformListener);
//
////		if (getOriginalShape() != null) {
////			x = getOriginalShape().getBounds().x;
////			y = getOriginalShape().getBounds().y;
////		}
//		GeneralShape gs = createTextShape(createGlyphVector(getText(), frc, getFont()), originalX, originalY);
//		// check if string is empty
//		if (gs != null) {
//			primSetGeneralShape(gs);
//			getGeneralShape().transform(getAffineTransform());
//			getGeneralShape().addTransformListener(transformListener);
//		}
//
//		primSetRotationX(ROTATION_X_DEFAULT);
//		primSetRotationY(ROTATION_Y_DEFAULT);
//	}

	protected void update()
	{
		transform(identityAT);
		logger.debug("update()");
	}

	public String getText() {
		return getInternalText();
	}

	public void setText(String newText)
	{
		String oldText = getInternalText();
		setInternalText(newText);
		primSetName(getInternalText());

		if (!newText.equals(oldText)) {
			update();
			firePropertyChange(PROP_TEXT, oldText, getInternalText());
		}
	}

	/**
	 * Returns the text of the {@link TextDrawComponent}.
	 * Implement this method instead of {@link #getText()} and return the value of the internal String member of your subclass.
	 *
	 * @return the (internal) text which has previously been set by {@link #setInternalText(String)}
	 */
	protected abstract String getInternalText();

	/**
	 * Sets the internal text (string) member. Implement this instead of {@link #setText(String)}.
	 * @param newText the new (internal) text to set
	 */
	protected abstract void setInternalText(String newText);

	public void setFont(Font newFont)
	{
		Font oldFont = font;
		font = newFont;
		update();
		firePropertyChange(PROP_FONT, oldFont, font);
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int newFontSize)
	{
		int oldFontSize = fontSize;
		fontSize = newFontSize;

		update();
		firePropertyChange(PROP_FONT_SIZE, oldFontSize, fontSize);
	}

	public boolean isBold() {
		return bold;
	}

	public void setBold(boolean newBold)
	{
		boolean oldBold = bold;
		bold = newBold;
		if (oldBold != bold) {
			update();
			firePropertyChange(PROP_BOLD, oldBold, bold);
		}
	}

	public boolean isItalic() {
		return italic;
	}

	public void setItalic(boolean newItalic)
	{
		boolean oldItalic = italic;
		italic = newItalic;
		if (oldItalic != italic) {
			update();
			firePropertyChange(PROP_ITALIC, oldItalic, italic);
		}
	}

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String newFontName)
	{
		String oldFontName = fontName;
		fontName = newFontName;
		if (!oldFontName.equals(fontName)) {
			update();
			firePropertyChange(PROP_FONT_NAME, oldFontName, fontName);
		}
	}

	public Font getFont()
	{
		if (font == null) {
			font = new Font(fontName, fontStyle, fontSize);
		}
		return font;
	}

	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (text: ");
		result.append(getText());
		result.append(", fontSize: ");
		result.append(fontSize);
		result.append(", bold: ");
		result.append(bold);
		result.append(", italic: ");
		result.append(italic);
		result.append(", fontName: ");
		result.append(fontName);
		result.append(", font: ");
		result.append(font);
		result.append(')');
		return result.toString();
	}

	@Override
	public void transform(AffineTransform at, boolean fromParent)
	{
		super.transform(at, fromParent);
		if (getGeneralShape() != null) {
			if (!getGeneralShape().hasTransformListener())
				getGeneralShape().addTransformListener(transformListener);
		}
	}

	@Override
	public Object clone(DrawComponentContainer parent)
	{
		AbstractTextDrawComponent text = (AbstractTextDrawComponent) super.clone(parent);
		text.font = new Font(fontName, fontStyle, fontSize);
		text.fontName = fontName;
		text.bold = bold;
		text.fontSize = fontSize;
		text.fontStyle = fontStyle;
		text.italic = italic;
		String txt = this.getText();
		if (txt != null)
			text.setText(txt);

		text.frc = frc;
		text.originalX = originalX;
		text.originalY = originalY;
		return text;
	}

	/* (non-Javadoc)
	* @see org.nightlabs.editor2d.impl.ShapeDrawComponentImpl#initGeneralShape()
	*/
	@Override
	protected GeneralShape initGeneralShape() {
		updateFont();
		originalShape = createTextShape(createGlyphVector(getText(), getFontRenderContext(), getFont()), originalX, originalY);
		return super.initGeneralShape();
	}

}
