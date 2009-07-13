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

import java.awt.RenderingHints;

public class RenderingHintsManager
{
	public static final int RENDER_MODE_DEFAULT = 0;
	public static final int RENDER_MODE_QUALITY = 1;
	public static final int RENDER_MODE_SPEED = 2;
//	public static final int RENDER_MODE_CUSTOM = 4;
		
	protected static RenderingHintsManager _sharedInstance = null;
	public static RenderingHintsManager sharedInstance()
	{
		if (_sharedInstance == null)
			_sharedInstance = new RenderingHintsManager();
		
		return _sharedInstance;
	}
	
	protected RenderingHintsManager() {
		super();
	}
		
//	public RenderingHintsManager()
//	{
//		super();
//		setDefaultRenderMode(getRenderingHints());
//	}
		
	private RenderingHints renderingHints =
		new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
	
	public RenderingHints getRenderingHints() {
		return renderingHints;
	}
			
	protected boolean checkAlphaInterpolation(Object alpha)
	{
		if ( !(alpha.equals(RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT)) ||
				 !(alpha.equals(RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY)) ||
				 !(alpha.equals(RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED)) )
		{
			return false;
		}
		return true;
	}
	
	public Object getAlphaInterpolation() {
		return renderingHints.get(RenderingHints.KEY_ALPHA_INTERPOLATION);
	}
	
	public void setAlphaInterpolation(Object alphaInterpolation)
	{
		if (checkAlphaInterpolation(alphaInterpolation))
			renderingHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, alphaInterpolation);
	}
	
	protected boolean checkAntiAliasing(Object antiAlias)
	{
		if ( !(antiAlias.equals(RenderingHints.VALUE_ANTIALIAS_DEFAULT)) ||
				 !(antiAlias.equals(RenderingHints.VALUE_ANTIALIAS_OFF)) ||
				 !(antiAlias.equals(RenderingHints.VALUE_ANTIALIAS_ON)) )
		{
			return false;
		}
		return true;
	}

	public Object getAntiAliasing() {
		return renderingHints.get(RenderingHints.KEY_ANTIALIASING);
	}
	public void setAntiAliasing(Object antiAliasing) {
		if (checkAntiAliasing(antiAliasing))
			renderingHints.put(RenderingHints.KEY_ANTIALIASING, antiAliasing);
	}
	
	protected boolean checkColorRendering(Object colorRendering)
	{
		if ( !(colorRendering.equals(RenderingHints.VALUE_COLOR_RENDER_DEFAULT)) ||
				 !(colorRendering.equals(RenderingHints.VALUE_COLOR_RENDER_QUALITY)) ||
				 !(colorRendering.equals(RenderingHints.VALUE_COLOR_RENDER_SPEED)) )
		{
			return false;
		}
		return true;
	}
	
	public Object getColorRendering() {
		return renderingHints.get(RenderingHints.KEY_COLOR_RENDERING);
	}
	public void setColorRendering(Object colorRendering) {
		if (checkColorRendering(colorRendering))
			renderingHints.put(RenderingHints.KEY_COLOR_RENDERING, colorRendering);
	}
	
	protected boolean checkDithering(Object dithering)
	{
		if ( !(dithering.equals(RenderingHints.VALUE_DITHER_DEFAULT)) ||
				 !(dithering.equals(RenderingHints.VALUE_DITHER_DISABLE)) ||
				 !(dithering.equals(RenderingHints.VALUE_DITHER_ENABLE)) )
		{
			return false;
		}
		return true;
	}
	
	public Object getDithering() {
		return renderingHints.get(RenderingHints.KEY_DITHERING);
	}
	public void setDithering(Object dithering) {
		if (checkDithering(dithering))
			renderingHints.put(RenderingHints.KEY_DITHERING, dithering);
	}
	
	protected boolean checkInterpolation(Object interpolation)
	{
		if ( !(interpolation.equals(RenderingHints.VALUE_INTERPOLATION_BICUBIC)) ||
				 !(interpolation.equals(RenderingHints.VALUE_INTERPOLATION_BILINEAR)) ||
				 !(interpolation.equals(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR)) )
		{
			return false;
		}
		return true;
	}
	
	public Object getInterpolation() {
		return renderingHints.get(RenderingHints.KEY_INTERPOLATION);
	}
	public void setInterpolation(Object interpolation)
	{
		if (checkInterpolation(interpolation))
			renderingHints.put(RenderingHints.KEY_INTERPOLATION, interpolation);
	}
	
	protected boolean checkRendering(Object rendering)
	{
		if ( !(rendering.equals(RenderingHints.VALUE_RENDER_DEFAULT)) ||
				 !(rendering.equals(RenderingHints.VALUE_RENDER_QUALITY)) ||
				 !(rendering.equals(RenderingHints.VALUE_RENDER_SPEED)) )
		{
			return false;
		}
		return true;
	}

	public Object getRendering() {
		return renderingHints.get(RenderingHints.KEY_RENDERING);
	}
	public void setRendering(Object rendering) {
		if (checkRendering(rendering))
			renderingHints.put(RenderingHints.KEY_RENDERING, rendering);
	}
	
	protected boolean checkStrokeControl(Object stroke)
	{
		if ( !(stroke.equals(RenderingHints.VALUE_STROKE_DEFAULT)) ||
				 !(stroke.equals(RenderingHints.VALUE_STROKE_NORMALIZE)) ||
				 !(stroke.equals(RenderingHints.VALUE_STROKE_PURE)) )
		{
			return false;
		}
		return true;
	}
 
	public Object getStrokeControl() {
		return renderingHints.get(RenderingHints.KEY_STROKE_CONTROL);
	}
	public void setStrokeControl(Object strokeControl) {
		if (checkStrokeControl(strokeControl))
			renderingHints.put(RenderingHints.KEY_STROKE_CONTROL, strokeControl);
	}
	
	protected boolean checkTextAntiAliasing(Object textAntiAliasing)
	{
		if ( !(textAntiAliasing.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT)) ||
				 !(textAntiAliasing.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_OFF)) ||
				 !(textAntiAliasing.equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON)) )
		{
			return false;
		}
		return true;
	}
	
	public Object getTextAntiAliasing() {
		return renderingHints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
	}
	public void setTextAntiAliasing(Object textAntiAliasing) {
		if (checkTextAntiAliasing(textAntiAliasing))
			renderingHints.put(RenderingHints.KEY_TEXT_ANTIALIASING, textAntiAliasing);
	}
	
	public void setRenderMode(int renderMode)
	{
		switch (renderMode)
		{
			case(RENDER_MODE_DEFAULT):
				setDefaultRenderMode(getRenderingHints());
				break;
			case(RENDER_MODE_QUALITY):
				setQualityRenderMode(getRenderingHints());
				break;
			case(RENDER_MODE_SPEED):
				setSpeedRenderMode(getRenderingHints());
				break;
		}
	}
		
	public static void setDefaultRenderMode(RenderingHints rh)
	{
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DEFAULT);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
		rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
	}
	
	public static void setQualityRenderMode(RenderingHints rh)
	{
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	public static void setSpeedRenderMode(RenderingHints rh)
	{
		rh.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		rh.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		rh.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
		rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		rh.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
	}
	
	public static void setRenderMode(RenderingHints rh, int renderMode)
	{
		switch (renderMode)
		{
			case(RENDER_MODE_DEFAULT):
				setDefaultRenderMode(rh);
				break;
			case(RENDER_MODE_QUALITY):
				setQualityRenderMode(rh);
				break;
			case(RENDER_MODE_SPEED):
				setSpeedRenderMode(rh);
				break;
		}
	}
}
