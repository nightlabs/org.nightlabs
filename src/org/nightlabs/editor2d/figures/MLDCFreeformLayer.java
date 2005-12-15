/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.figures;

import java.util.Map;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.UpdateListener;
import org.eclipse.draw2d.UpdateManager;
import org.eclipse.draw2d.geometry.Rectangle;

import org.nightlabs.editor2d.edit.MultiLayerDrawComponentEditPart;
import org.nightlabs.editor2d.util.EditorUtil;


public class MLDCFreeformLayer 
extends FreeformLayer 
implements UpdateListener
{
  public MLDCFreeformLayer() {
    super();    
  }
    
  public void registerOnDeferredUpdateManager(UpdateManager updateManager) {
    if (updateManager == null)
      return;
    updateManager.removeUpdateListener(this);
    updateManager.addUpdateListener(this);
  }
    
  private MultiLayerDrawComponentEditPart mldcEditPart;    
	public void setMldcEditPart(MultiLayerDrawComponentEditPart mldcEditPart) {
	  this.mldcEditPart = mldcEditPart;
	}
	
  private Rectangle notifiedDamage;
  
  public void notifyPainting(Rectangle damage, Map dirtyRegions) {
  	if (mldcEditPart == null)
  		return;
    notifiedDamage = damage;
    notifiedDamage = EditorUtil.toAbsolute(mldcEditPart, damage);
//    System.out.println("MLDC notify Painting called with "+damage);
  }
  
  public void notifyValidating() {
  }
      
//  public void paint(Graphics graphics) {
//  	super.paint(graphics);
//    System.out.println("MLDC paint called with ");
//    if (notifiedDamage == null) {
//      super.paint(graphics);
//      return;
//    }
//    
////    System.out.println("MLDC notifiedDamage != null");
//    for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
//      IFigure child = (IFigure) iter.next();
//      if (child.intersects(notifiedDamage)) {
//        graphics.pushState();
//        try {
//          graphics.setClip(child.getBounds());
////          System.out.println("MLDC paint child: "+child+" child.getBounds() "+child.getBounds());
//          child.paint(graphics);
//          graphics.restoreState();
//        } finally
//        {
//          graphics.popState();
//        }
//      }
//    }
//    notifiedDamage = null;
//  }
 }
