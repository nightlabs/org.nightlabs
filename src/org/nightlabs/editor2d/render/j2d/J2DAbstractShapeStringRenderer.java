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

package org.nightlabs.editor2d.render.j2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.editor2d.render.ShapeRenderer;
import org.nightlabs.editor2d.render.StringRenderer;
import org.nightlabs.editor2d.util.EditorModelUtil;
import org.nightlabs.i18n.unit.resolution.DPIResolutionUnit;

public abstract class J2DAbstractShapeStringRenderer
extends J2DBaseShapeRenderer
implements StringRenderer, ShapeRenderer
{
//	/**
//	 * LOG4J logger used by this class
//	 */
//	private static final Logger logger = Logger.getLogger(J2DAbstractShapeStringRenderer.class);
	
//	public static final Font DEFAULT_FONT = new Font("Arial", Font.BOLD, 12);
	public static final Font DEFAULT_FONT = new Font(null, Font.BOLD, 12);
	public static final Color DEFAULT_COLOR = Color.BLACK;
	
  /**
   * paints the ShapeDrawComponent and draws the String returned
   * in the abstract Method getString(ShapeDrawComponent sdc) on top of it
   */
  @Override
	public void paint(DrawComponent dc, Graphics2D g2d)
  {
    ShapeDrawComponent sdc = (ShapeDrawComponent) dc;
    GeneralShape shape = sdc.getGeneralShape();
    if (showFillColor)
    {
    	if (sdc.isFill()) {
        g2d.setPaint(getFillColor(sdc));
        g2d.fill(shape);
    	}
    }
    // TODO: if showString and no showFillColor the outline MUST be visible
    if (showLineColor)
    {
    	if (sdc.isShowStroke()) {
        g2d.setPaint(getLineColor(sdc));
        g2d.setStroke(sdc.getStroke());
        g2d.draw(shape);
    	}
    }
    if (showString) {
    	paintString(sdc, getString(sdc), g2d);
    }
  }
  
  /**
   * 
   * @param sdc The ShapeDrawComponent to get the String from
   * @return the String to render
   */
  public abstract String getString(ShapeDrawComponent sdc);
  
  /**
   * Subclasses can override this Method to determine what the fillColor
   * should be.
   * 
   * By default the FillColor of the ShapeDrawComponent is returned
   * 
   * @param sdc The ShapeDrawComponent to get the FillColor from (the default)
   * @return the FillColor to fill the ShapeDrawComponent
   */
  protected Color getFillColor(ShapeDrawComponent sdc) {
  	return sdc.getFillColor();
  }
  
  /**
   * Subclasses can override this Method to determine what the lineColor
   * should be.
   * 
   * By default the LineColor of the ShapeDrawComponent is returned
   * 
   * @param sdc The ShapeDrawComponent to get the LineColor from (the default)
   * @return the LineColor to fill the Line of the ShapeDrawComponent
   */
  protected Color getLineColor(ShapeDrawComponent sdc) {
  	return sdc.getLineColor();
  }
  
  private boolean showString = true;
	public boolean isShowString() {
		return showString;
	}
	public void setShowString(boolean showString) {
		this.showString = showString;
	}
			
	private Font font = DEFAULT_FONT;
	public Font getFont() {
		return font;
	}
	public void setFont(Font f) {
		this.font = f;
	}
  
	private Color fontColor = DEFAULT_COLOR;
	public Color getFontColor() {
		return fontColor;
	}
	public void setFontColor(Color fontColor) {
		this.fontColor = fontColor;
	}
    
	private boolean showLineColor = true;
	public boolean isShowLineColor() {
		return showLineColor;
	}
	public void setShowLineColor(boolean showLineColor) {
		this.showLineColor = showLineColor;
	}
	
	private boolean showFillColor = true;
	public boolean isShowFillColor() {
		return showFillColor;
	}
	public void setShowFillColor(boolean showColor) {
		this.showFillColor = showColor;
	}
	
	private GlyphVector glyphVector = null;
	private Rectangle glyphBounds = null;
	private Rectangle shapeBounds = null;
	private Point glyphLocation = null;
	 		
	protected GlyphVector createGlyphVector(String s, FontRenderContext frc) {
	  return font.createGlyphVector(frc, s);
	}
	
  protected void paintString(ShapeDrawComponent sdc, String s, Graphics2D g2d)
  {
  	if (s != null)
  	{
//  		if (resolutionFactor == -1) {
  		resolutionFactor = initResolutionFactor(g2d, sdc);
//  		}
  		// TODO: use scaled FontRenderContext to create GlyphVector
      glyphVector = createGlyphVector(getString(sdc), getFontRenderContext());
      shapeBounds = sdc.getBounds();

      GeneralShape textShape = new GeneralShape(glyphVector.getOutline());
      AffineTransform at = new AffineTransform(getResolutionTransform());
      textShape.transform(at);
      glyphBounds = textShape.getBounds();
      
      glyphLocation = EditorModelUtil.getLeftTopCenterLocation(glyphBounds.getBounds(), shapeBounds);
      AffineTransform translate = AffineTransform.getTranslateInstance(glyphLocation.x, glyphLocation.y);
      textShape.transform(translate);
      g2d.setPaint(getFontColor());
      g2d.fill(textShape);
  	}
  }
	
  private double resolutionFactor = -1;
  protected double getResolutionFactor()
  {
  	return resolutionFactor;
  }
  
  protected double initResolutionFactor(Graphics2D g2d, DrawComponent dc)
  {
  	// TODO: determine deviceResolution from Graphics2D
  	double deviceDPI = 72;
		double documentDPI = dc.getRoot().getResolution().getResolutionX(new DPIResolutionUnit());
		double factor = documentDPI / deviceDPI;
//		logger.debug("resolutionFactor = "+factor);
		return factor;
  }
  
  private FontRenderContext frc = null;
  public FontRenderContext getFontRenderContext()
  {
//  	if (frc == null) {
  		frc = new FontRenderContext(getResolutionTransform(), true, false);
//  	}
    return frc;
  }
  
  private AffineTransform resolutionAT = null;
  protected AffineTransform getResolutionTransform()
  {
//  	if (resolutionAT == null)
//  	{
  		resolutionAT = new AffineTransform();
  		double factor = getResolutionFactor();
  		if (factor != -1)
  			resolutionAT.scale(factor, factor);
//  	}
  	return resolutionAT;
  }
  
}
