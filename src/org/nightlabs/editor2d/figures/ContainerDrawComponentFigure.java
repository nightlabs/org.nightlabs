/*
 * Created on 06.06.2005
 */
package org.nightlabs.editor2d.figures;

import java.awt.Graphics2D;
import java.util.Iterator;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;

/**
 * Overrides paint methods of DrawComponentFigures
 * and paints only its children instead.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 *
 */
public class ContainerDrawComponentFigure extends DrawComponentFigure {
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics2D graphics) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			if (figure instanceof DrawComponentFigure) {
				((DrawComponentFigure)figure).paint(graphics);
			}
		}
	}  
	
	/**
	 * Overridden to paint only children
	 */
	public void paint(Graphics graphics) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure figure = (Figure) iter.next();
			figure.paint(graphics);
		}
	}
	
}
