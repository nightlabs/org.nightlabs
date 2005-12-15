/*
 * Created on 05.06.2005
 */
package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * Interface used for optimized painting.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface ISmartUpdateFigure {
	/**
	 * Paint region on Draw2D Graphics
	 * 
	 * @param graphics The graphics to draw on
	 * @param region The region to draw
	 */
	public void paintRegion(Graphics graphics, Rectangle region);
	/**
	 * Paint region on Swing Graphics2D
	 * 
	 * @param graphics The graphics to draw on
	 * @param region The region to draw
	 */
	public void paintRegion(Graphics2D graphics, Rectangle region);
	
	/**
	 * Rebuild the information
	 * used for optimized painting. 
	 */
	public void refresh();

	/**
	 * Rebuild the information used
	 * for optimized painting concerning
	 * the given Figure.
	 * 
	 * @param figure The Figure that has changed
	 */
	public void refresh(IFigure figure);
}
