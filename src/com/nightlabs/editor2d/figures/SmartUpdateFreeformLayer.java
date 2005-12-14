/*
 * Created on 05.06.2005
 *
 */
package com.nightlabs.editor2d.figures;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Control;

import com.nightlabs.editor2d.util.EditorUtil;

/**
 * @author Alexander Bieber <alex[AT]nightalbs[DOT]de>
 *
 */
public class SmartUpdateFreeformLayer 
	extends FreeformLayer
	implements BufferedFreeformLayer
{
	public static final int TYPE_SMART_UPDATE_ONLY = 1;
	public static final int TYPE_USE_OFFSCREEN_BUFFER = 2;
	
	private Rectangle notifiedDamage;
	private EditPart editPart;
	private UpdateManager updateManager;
	
	private UpdateListener updateListener = new UpdateListener() {
		public void notifyPainting(Rectangle damage, Map dirtyRegions) {
		  	if (editPart == null)
		  		return;
		    notifiedDamage = damage;
		    notifiedDamage = EditorUtil.toAbsolute(editPart, damage);
		}
		public void notifyValidating() {
		}
	};
	
	
	/**
	 */
	public SmartUpdateFreeformLayer() {
		super();
	}
	
	public void init(EditPart editPart) {
		this.editPart = editPart;
		EditPartViewer viewer = editPart.getRoot().getViewer();
		if (viewer instanceof ScrollingGraphicalViewer) {		  	
			ScrollingGraphicalViewer graphicalViewer = (ScrollingGraphicalViewer) viewer;
			Control control = graphicalViewer.getControl();
			if (control instanceof FigureCanvas) {
				FigureCanvas figureCanvas = (FigureCanvas)graphicalViewer.getControl();	    
				updateManager = figureCanvas.getLightweightSystem().getUpdateManager();
				if (updateManager != null) 
					registerOnDeferredUpdateManager(updateManager);
			}
		}
	}
	
	/**
	 * Registers this Figures updateListener to the given
	 * updateManager.
	 * 
	 * @param updateManager The updateManager to register to.
	 */	
	protected void registerOnDeferredUpdateManager(UpdateManager updateManager) {
		if (updateManager == null)
			return;
		updateManager.removeUpdateListener(updateListener);
		updateManager.addUpdateListener(updateListener);
	}
	
	/**
	 * Overrides Figure's paint as this will always 
	 * paint all its children even if not neccessary.
	 * This implementation takes the last region notified
	 * by the UpdateManager (wich will be notified synchronously
	 * right before this paint method will be invoked 
	 * whithin UpdateManager.repairDamage) and first asks all 
	 * its children wether they do intersect this region. 
	 * If true and the child is an instance of 
	 * {@link ISmartUpdateFigure} its {@link ISmartUpdateFigure#paintRegion(Rectangle)}
	 * method will be invoked instead of the default
	 * {@link Figure#paint(Graphics)} method.
	 */
	public void paint(Graphics graphics) {
		
		if (notifiedDamage == null) {
			// no update notification was done 
			// before this call
			// do default painting (all children)
			super.paint(graphics);
			return;
		}
		
		// synchronous notification was done right before paint
		try
		// make sure the temporary Damage is reset in finally
		{
			for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
				IFigure child = (IFigure) iter.next();
				if (child.intersects(notifiedDamage)) {
					graphics.pushState();
					try {
						graphics.setClip(child.getBounds());
//						System.out.println("MLDC paint child: "+child+" child.getBounds() "+child.getBounds());
						if (child instanceof ISmartUpdateFigure)
							((ISmartUpdateFigure)child).paintRegion(graphics, notifiedDamage);
						else
							child.paint(graphics);
						graphics.restoreState();
					} finally
					{
						graphics.popState();
					}
				}
			}
		}
		finally
		{
			notifiedDamage = null;
		}
	}

	/**
	 * @see com.nightlabs.editor2d.figures.BufferedFreeformLayer#refresh()
	 */
	public void refresh() {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure child = (Figure) iter.next();
			if (child instanceof ISmartUpdateFigure) {
				((ISmartUpdateFigure)child).refresh();
			}
		}
	}

	/**
	 * @see com.nightlabs.editor2d.figures.BufferedFreeformLayer#refresh(org.eclipse.draw2d.Figure)
	 */
	public void refresh(IFigure figure) {
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			Figure child = (Figure) iter.next();
			if (child instanceof ISmartUpdateFigure) {
				((ISmartUpdateFigure)child).refresh(figure);
			}
		}
	}
	
}
