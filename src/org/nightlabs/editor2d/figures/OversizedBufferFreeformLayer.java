/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
 * 
 **/
package org.nightlabs.editor2d.figures;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformListener;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.J2DGraphics;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Control;
import org.holongate.j2d.J2DRegistry;

import org.nightlabs.editor2d.util.EditorUtil;

/**
 * Buffers its contents for optimized painting.
 * 
 * @author Daniel Mazurek
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class OversizedBufferFreeformLayer 
extends Layer
implements FreeformFigure, BufferedFreeformLayer
{
	public static final Logger LOGGER = Logger.getLogger(OversizedBufferFreeformLayer.class);
	
	//  private FreeformHelper helper = new FreeformHelper(this);
	private LayerFreeformHelper helper = new LayerFreeformHelper(this);
	
	/**
	 * The offscreen buffer
	 */
	private BufferedImage bufferedImage;
	/**
	 * The control is needed to know the buffers needed size
	 */
	private Control viewerControl;
	/**
	 * The editpart is needed for EditorUtil methods and 
	 * to retrieve the control. It is passed to the constructor
	 */
	private EditPart editPart;
	/**
	 * Current zoom, buffer will be cleared if the
	 * editPart's zoom differes from that
	 */
	private double currentZoom = 1.0;
	/**
	 * The scroll offset at the time when the buffer was (re)created 
	 */
	private Point bufferedScrollOffset;
	/**
	 * The current size of the associated Control
	 */
	private Point currentSize = new Point(0,0);
	/**
	 * The current size of the buffer
	 */
	private Point bufferSize = new Point(0,0);
	/**
	 * The translation within the buffer caused by its size
	 * given in coordinatates of the controls
	 * system so not scaled with the current zoom
	 */
	private Point bufferTranslation = new Point(0,0);
	/**
	 * The origin within the buffer for the copy operation used in paint
	 */
	private Point bufferOrigin = new Point(0,0);
	/**
	 * To be able to paint children even if not an instance of DrawComponentFigure
	 */
	private List nonDCFChildren = new LinkedList();
	
	
	protected Point calculateBufferFactors() {
		Point result = new Point(1,1);
		Point realSizeBounds = EditorUtil.toAbsolute(editPart, currentSize.x, currentSize.y);
		Point so = EditorUtil.getScrollOffset(editPart);
//		Point realSO = EditorUtil.toAbsolute(editPart, so.x, so.y);
		Rectangle childBound = getChildBounds();
		if (childBounds == null)
			return result;
		if (realSizeBounds.x >= childBound.width && realSizeBounds.y >= childBound.height) {
			// all children fit in the control 
			// do not overdimension buffer
			return result;
		}
		else {
			int addX = 1;
			int addY = 1;
			if (so.x > 0)
				addX = 2;
			if (so.y > 0)
				addY = 2;
			result.setLocation(
					Math.min(4, (childBound.width / realSizeBounds.x)+addX),
					Math.min(4, (childBound.height / realSizeBounds.y)+addY)
				);
		}
		LOGGER.debug("Calculated buffer factors: "+result);
		return result;
	}
	
	/**
	 * Returns the offscreen buffer (recrates if neccassary) and sets
	 * {@link #bufferOrigin} to the correct value concerning the 
	 * current scroll offset.
	 * 
	 * @return The offscreen buffer
	 */
	protected BufferedImage getBufferedImage() {
		
		double tmpZoom = EditorUtil.getZoom(editPart);
		if (tmpZoom != currentZoom) {			
			// clear buffer if zoom changed
			currentZoom = EditorUtil.getZoom(editPart);
			clearBuffer();
		}
		if (bufferedScrollOffset == null) {
			clearBuffer();
		}
		if (bufferedImage == null) {
			long time = System.currentTimeMillis();
			currentSize.setLocation(viewerControl.getSize().x, viewerControl.getSize().x);
			Point factors = calculateBufferFactors();
			bufferSize.setLocation(currentSize.x * factors.x, currentSize.y*factors.y);
			if (bufferedImage == null) {
				bufferedImage = new BufferedImage(bufferSize.x, bufferSize.y, BufferedImage.TYPE_INT_ARGB);
			}
			Graphics2D bufferedGraphics  = bufferedImage.createGraphics();
//			bufferedGraphics.copyArea()
			try {
				bufferedGraphics.setClip(null);
				bufferedGraphics.scale(currentZoom, currentZoom);
				bufferedScrollOffset = EditorUtil.getScrollOffset(editPart);
				Point offsetTranslation = EditorUtil.toAbsolute(
						editPart, 
						bufferedScrollOffset.x, 
						bufferedScrollOffset.y
				);
				bufferTranslation.setLocation((bufferSize.x - currentSize.x) / 2, (bufferSize.y - currentSize.y) / 2);
				Point absoluteBufferTranslation = EditorUtil.toAbsolute(
						editPart, 
						bufferTranslation.x,
						bufferTranslation.y
						
				);
				bufferedGraphics.translate(
						absoluteBufferTranslation.x-offsetTranslation.x, 
						absoluteBufferTranslation.y-offsetTranslation.y
				);
				J2DRegistry.initGraphics(bufferedGraphics);
				nonDCFChildren.clear();
				for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
					Figure figure = (Figure) iter.next();
					if (figure instanceof DrawComponentFigure) {
						// let all children draw on the buffer
						((DrawComponentFigure)figure).paint(bufferedGraphics);
					}
					else {
						// Figure can not draw on a Graphics2D
						// will be painted unbuffered
						nonDCFChildren.add(figure);
					}
				}
				LOGGER.debug("buffer created in "+(System.currentTimeMillis()-time)+" ms");
			}
			finally {
				if (bufferedGraphics != null)
				bufferedGraphics.dispose();
			}
			
		}
		Point scrollOffset = EditorUtil.getScrollOffset(editPart);
		bufferOrigin.setLocation(0,0);
		bufferOrigin.translate(
			(scrollOffset.x-bufferedScrollOffset.x)+bufferTranslation.x, 
			(scrollOffset.y-bufferedScrollOffset.y)+bufferTranslation.y
		);		
		if (
				(bufferOrigin.x < 0 || bufferOrigin.y < 0) ||
				(bufferOrigin.x > (bufferSize.x - currentSize.x) || bufferOrigin.y > (bufferSize.y - currentSize.y))
			) {
			// clear buffer if region to copy is outside
			// of the buffer
			clearBuffer();
			return getBufferedImage();
		}
		return bufferedImage;
	}
	
	public void paint(Graphics graphics) {
//		super.paint(graphics);
//		if (true)
//			return;
		long time = System.currentTimeMillis();
		if (graphics instanceof J2DGraphics) {
			J2DGraphics j2dGraphics = (J2DGraphics)graphics;
//			j2dGraphics.setClip((Rectangle)null);
			j2dGraphics.clipRect(null);
			
			// get / create the buffer 
			BufferedImage buffer = getBufferedImage();
			
			// create the Graphics where the buffer is drawn on
			Graphics2D g2d = j2dGraphics.createGraphics2D();
			// scale it invers of the current zoom ...
			g2d.scale(1/currentZoom, 1/currentZoom);			
			// and translate it with the current scroll offset
			// so 0,0 will be drawn on top left of the control			
			Point scrollOffset = EditorUtil.getScrollOffset(editPart);
			g2d.translate(scrollOffset.x, scrollOffset.y);
			
			// now copy the buffer region
			g2d.setPaint(Color.WHITE);
			g2d.fillRect(-2, -2, currentSize.x+2, currentSize.y+2);
			g2d.drawImage(
				buffer,
				0, 0, currentSize.x, currentSize.y,
				bufferOrigin.x, bufferOrigin.y, bufferOrigin.x+currentSize.x, bufferOrigin.y+currentSize.y,
				null
			);
			g2d.dispose();
			// Paint all children that are not an instance of DrawComponentFigure
			for (Iterator iter = nonDCFChildren.iterator(); iter.hasNext();) {
				Figure figure = (Figure) iter.next();
				figure.paint(graphics);
			}
//			LOGGER.debug("paint called");
		}
		else {
			super.paint(graphics);
		}
//		LOGGER.debug("painted in "+(System.currentTimeMillis()-time));
	}
	
	
	protected void clearBuffer() {
		// TODO: make sure this is called when editor is closed
		if (bufferedImage != null) {
			bufferedImage.flush();
//			long time = System.currentTimeMillis();
//			System.gc();
//			LOGGER.debug("gc() took "+(System.currentTimeMillis()-time)+" ms");
		}
		bufferedImage = null;
		childBounds = null;
//		LOGGER.debug("buffer cleared()");
	}
	
	
	/**
	 * @see BufferedFreeformLayer#refresh()
	 */
	public void refresh() {
		clearBuffer();
	}

	/**
	 * @see BufferedFreeformLayer#refresh(Figure)
	 */
	public void refresh(IFigure figure) {
		clearBuffer();
	}
	
//	public void refresh(List figures) {
//		clearBuffer();
//	}
		
	private Rectangle childBounds;
	
	protected Rectangle getChildBounds() {  	
		if (childBounds == null) {
			for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
				Figure figure = (Figure) iter.next();
				Rectangle figureBounds = figure.getBounds();
				if (childBounds == null)				
					childBounds = new Rectangle(figureBounds);
				else
					childBounds.union(figureBounds);		
			}
		}
		return childBounds;  		
	}
	
	/**
	 * Constructs a new OversizedBufferFreeformLayer. The
	 * root of the given editPart is consulted to 
	 * retrieve the parent Graphical viewer and
	 * its Control.
	 * 
	 * @param editPart The editPart.
	 */
	public OversizedBufferFreeformLayer() 
	{
		//TODO remove this workaround, and instead try to catch the Layer-selection in the outline
		//     I think this is ok now (Alex)
		super.setBounds(new Rectangle(-Integer.MAX_VALUE / 2, -Integer.MAX_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE));
	}
	
	public void init(EditPart editPart) {
		this.editPart = editPart;
		EditPartViewer viewer = editPart.getRoot().getViewer();
		if (viewer instanceof ScrollingGraphicalViewer) {		  	
			ScrollingGraphicalViewer graphicalViewer = (ScrollingGraphicalViewer) viewer;
			Control control = graphicalViewer.getControl();
			this.viewerControl = control;
		}		
	}
	
	/**
	 * @see IFigure#add(IFigure, Object, int)
	 */
	public void add(IFigure child, Object constraint, int index) {
		super.add(child, constraint, index);
		helper.hookChild(child);
		clearBuffer();
	}
	
	/**
	 * @see FreeformFigure#addFreeformListener(FreeformListener)
	 */
	public void addFreeformListener(FreeformListener listener) {
		addListener(FreeformListener.class, listener);
	}
	
	/**
	 * @see FreeformFigure#fireExtentChanged()
	 */
	public void fireExtentChanged() {
		Iterator iter = getListeners(FreeformListener.class);
		while (iter.hasNext())
			((FreeformListener)iter.next())
			.notifyFreeformExtentChanged();
	}
	
//	/**
//	 * Overrides to do nothing.
//	 * @see Figure#fireMoved()
//	 */
//	protected void fireMoved() { }
	
	/**
	 * @see FreeformFigure#getFreeformExtent()
	 */
	public Rectangle getFreeformExtent() {
		return helper.getFreeformExtent();
	}
	
	/**
	 * @see Figure#primTranslate(int, int)
	 */
	public void primTranslate(int dx, int dy) {
		bounds.x += dx;
		bounds.y += dy;
	}
	
	/**
	 * @see IFigure#remove(IFigure)
	 */
	public void remove(IFigure child) {
		helper.unhookChild(child);
		super.remove(child);
		clearBuffer();
	}
	
	/**
	 * @see FreeformFigure#removeFreeformListener(FreeformListener)
	 */
	public void removeFreeformListener(FreeformListener listener) {
		removeListener(FreeformListener.class, listener);
	}
	
	/**
	 * @see FreeformFigure#setFreeformBounds(Rectangle)
	 */
	public void setFreeformBounds(Rectangle bounds) {
		//  	clearBuffer();
		//    LOGGER.debug("setFreeformBounds("+bounds+")");
		//  	helper.setFreeformBounds(bounds);
	} 
	
	public void setBounds(Rectangle rect) {
		//  	clearBuffer();
		//    LOGGER.debug("setBounds("+rect+")");
		//    super.setBounds(rect);
	}
	
	
}
