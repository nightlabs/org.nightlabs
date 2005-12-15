/*
 * Created on 05.06.2005
 *
 */
package org.nightlabs.editor2d.figures;

import java.util.Collection;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public interface FigureTile {
	
	public boolean intersects(Rectangle rect);
	public Rectangle getBounds();
	
	public Collection getTileFigures();
	public void addFigure(IFigure figure);
	public void removeFigure(IFigure figure);
	
	public Collection getIntersectingFigures(Rectangle rect);
}
