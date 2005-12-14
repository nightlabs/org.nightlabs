/*
 * Created on 07.06.2005
 */
package com.nightlabs.editor2d.figures;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPart;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public interface BufferedFreeformLayer {
	/**
	 * Should force the buffer to 
	 * be rebuild before painting the
	 * next time.
	 */
	public void refresh();

	/**
	 * Should try to update the buffer
	 * concerning a change in the given
	 * Figure.
	 */
	public void refresh(IFigure figure);

	/**
	 * Most buffered Layers will need a
	 * EditPart (or its root Control) to
	 * know about their buffer sizes etc.
	 * 
	 * @param editPart The EditPart.
	 */
	public void init(EditPart editPart);
	
//	public void repaint();
}