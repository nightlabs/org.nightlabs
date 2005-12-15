/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureListener;
import org.eclipse.draw2d.FreeformFigure;
import org.eclipse.draw2d.FreeformListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;


public class LayerFreeformHelper 
implements FreeformListener
{
  public static final Logger LOGGER = Logger.getLogger(LayerFreeformHelper.class);
  
  class ChildTracker implements FigureListener {
  	public void figureMoved(IFigure source) {
  		invalidate();
  	}
  }

  private FreeformFigure host;
  private Rectangle freeformExtent;
  private FigureListener figureListener = new ChildTracker();

  LayerFreeformHelper(FreeformFigure host) {
  	this.host = host;
  }

  public Rectangle getFreeformExtent() 
  {
  	if (freeformExtent != null)
  		return freeformExtent;
  	Rectangle r;
  	List children = host.getChildren();
  	for (int i = 0; i < children.size(); i++) {
  		IFigure child = (IFigure)children.get(i);
//  		if (child instanceof OversizedBufferFreeformLayer)
//  		  r = child.getBounds();
//  		else if (child instanceof FreeformFigure)
//  			r = ((FreeformFigure) child).getFreeformExtent();
  		if (child instanceof FreeformFigure)
  			r = ((FreeformFigure) child).getFreeformExtent();
  		else
  			r = child.getBounds();
  		if (freeformExtent == null)
  			freeformExtent = r.getCopy();
  		else
  			freeformExtent.union(r);
  	}
  	Insets insets = host.getInsets();
  	if (freeformExtent == null)
  		freeformExtent = new Rectangle(0, 0, insets.getWidth(), insets.getHeight());
  	else {
  		host.translateToParent(freeformExtent);
  		freeformExtent.expand(insets);
  	}
//  	System.out.println("New extent calculated for " + host + " = " + freeformExtent);
  	return freeformExtent;
  }

  public void hookChild(IFigure child) 
  {
//    if (child instanceof OversizedBufferFreeformLayer) {
//      return;
//    }      
  	invalidate();  	
  	if (child instanceof FreeformFigure)
  		((FreeformFigure)child).addFreeformListener(this);
  	else
  		child.addFigureListener(figureListener);
  }

  void invalidate() 
  {
  	freeformExtent = null;
  	host.fireExtentChanged();
  	if (host.getParent() != null)
  	  if (host.getParent() instanceof MLDCFreeformLayer) {
  	    // Do nothing
//  	    LOGGER.debug("host.getParent() instanceof MLDCFreeformLayer!");
  	  } else {
    		host.getParent().revalidate();
  	  }
  	else
  	  if (host instanceof OversizedBufferFreeformLayer) {
  	    // Do nothing
//  	    LOGGER.debug("host.getParent() instanceof OversizedBufferFreeformLayer!");
  	  }
  	  else {
    		host.revalidate();  	    
  	  }
    
//  	freeformExtent = null;
//  	host.fireExtentChanged();
//  	if (host.getParent() != null)  	  
//  		host.getParent().revalidate();
//  	else
//  		host.revalidate();
  }

  public void notifyFreeformExtentChanged() {
  	//A childs freeform extent has changed, therefore this extent must be recalculated
  	invalidate();
  }

  public void setFreeformBounds(Rectangle bounds) {
  	host.setBounds(bounds);
  	bounds = bounds.getCopy();
  	host.translateFromParent(bounds);
  	List children = host.getChildren();
  	for (int i = 0; i < children.size(); i++) {
  		IFigure child = (IFigure)children.get(i);
  		if (child instanceof FreeformFigure)
  			((FreeformFigure) child).setFreeformBounds(bounds);
  	}
  }

  public void unhookChild(IFigure child) {
  	invalidate();
  	if (child instanceof FreeformFigure)
  		((FreeformFigure)child).removeFreeformListener(this);
  	else
  		child.removeFigureListener(figureListener);
  }
}
