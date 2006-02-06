/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.action.IStatusLineManager;

import org.nightlabs.editor2d.edit.AbstractDrawComponentEditPart;
import org.nightlabs.editor2d.edit.LayerEditPart;
import org.nightlabs.editor2d.edit.MultiLayerDrawComponentEditPart;
import org.nightlabs.editor2d.figures.DrawComponentFigure;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.util.EditorUtil;
import org.nightlabs.editor2d.viewer.descriptor.DescriptorManager;
import org.nightlabs.editor2d.viewer.descriptor.DrawComponentDescriptor;

public class ViewerManager 
{
  public static final Logger LOGGER = Logger.getLogger(ViewerManager.class);
  
  protected ScrollingGraphicalViewer viewer = null;
  protected Viewport viewport = null;
  protected Point mousePoint = new Point();
  protected RootEditPart root = null;
  protected IStatusLineManager statusLineMan = null;
  public ViewerManager(ScrollingGraphicalViewer viewer, IStatusLineManager statusLineMan) 
  {
    super();
    this.viewer = viewer;
    this.statusLineMan = statusLineMan;
    root = viewer.getRootEditPart();
    root.addEditPartListener(rootListener);
    FigureCanvas canvas = (FigureCanvas) viewer.getControl();
    viewport = canvas.getViewport();
    initExcludeListRef();    
    conditionRef = new ConditionRef(createDefaultCondition());
    viewport.addMouseMotionListener(mouseListener);
    mousePoint = new Point();
  }
  
  protected ExcludeListRef excludeListRef;
  protected void initExcludeListRef() 
  {
    List excludeList = new ArrayList();
    LOGGER.debug("root = "+root);
    excludeList.add(root);
    excludeListRef = new ExcludeListRef(excludeList);    
  } 
  
  protected EditPartListener rootListener = new EditPartListener.Stub()
  {	
		public void removingChild(EditPart child, int index) 
		{
			excludeListRef.getExcludeList().remove(child);
			for (Iterator it = child.getChildren().iterator(); it.hasNext(); ) {
				excludeListRef.getExcludeList().remove(it.next());
			}						
		}	
		public void childAdded(EditPart child, int index) 
		{
			// exclude the ModelRoot (MultiLayerDrawComponent) and its Layers (Layer)
			excludeListRef.getExcludeList().add(child);
			for (Iterator it = child.getChildren().iterator(); it.hasNext(); ) {
				excludeListRef.getExcludeList().add(it.next());
			}			
		}	
	};
  
  protected List ignoredClasses = new ArrayList();
  public void addIgnoreTypeCollection(Collection editPartTypes) 
  {
  	if (editPartTypes == null)
  		throw new IllegalArgumentException("Param editPartTypes must not be null!");
  	
  	if (editPartTypes.isEmpty())
  		return;

  	for (Iterator it = editPartTypes.iterator(); it.hasNext(); ) {
  		Object o = it.next();
  		if (o instanceof Class) {
  			Class c = (Class) o;
  			addIgnoreType(c);
  		}
  	}
  	conditionRef.setCondition(createIgnoreCondition(ignoredClasses));  	
  }
    
  public void addIgnoreType(Class c) 
  {
		if (AbstractDrawComponentEditPart.class.isAssignableFrom(c) && !ignoredClasses.contains(c)) {
			ignoredClasses.add(c);
		} 
		conditionRef.setCondition(createIgnoreCondition(ignoredClasses));		
  }
  
  public void addExcludeCollection(Collection editParts) 
  {
  	if (editParts == null)
			throw new IllegalArgumentException("Param editParts must not be null!");
  	
  	for (Iterator it = editParts.iterator(); it.hasNext(); ) {
  		Object o = it.next();
  		if (o instanceof AbstractDrawComponentEditPart && !excludeListRef.getExcludeList().contains(o)) {
  			EditPart ep = (EditPart) o;
  			excludeListRef.getExcludeList().add(ep);
  		}
  	}  	
  }
  
  public void addExclude(AbstractDrawComponentEditPart ep) 
  {
  	if (ep == null)
			throw new IllegalArgumentException("Param ep must not be null!");
  	
  	excludeListRef.getExcludeList().add(ep);
  }
  
  public void setCondition(EditPartViewer.Conditional condition) {
  	conditionRef.setCondition(condition);
  }
  
  protected Class exclusiveClass;
  public void setExclusiveClass(Class c) {
  	exclusiveClass = c;
  }
    
  protected DescriptorManager descriptorManager = new DescriptorManager();
  public DescriptorManager getDescriptorManager() {
  	return descriptorManager;
  }
  public void setDescriptorManager(DescriptorManager descMan) {
  	this.descriptorManager = descMan;
  }
  
  protected Point relativePoint = null;
  protected AbstractDrawComponentEditPart oldPart = null;
  
//  protected MouseMotionListener mouseListener = new MouseMotionListener.Stub() 
//  {
//    public void mouseMoved(org.eclipse.draw2d.MouseEvent me) 
//    {
//    	relativePoint = new Point(me.x, me.y);
//    	mousePoint = EditorUtil.toAbsoluteWithScrollOffset(root, me.x, me.y);    	      
//      EditPart part = viewer.findObjectAtExcluding(relativePoint, excludeListRef.getExcludeList(), conditionRef.getCondition());
//      statusLineMan.setMessage(getMouseCoordinates());      
//      if (part != null) 
//      {       	
//      	if (!(part instanceof MultiLayerDrawComponentEditPart) &&
//      			!(part instanceof LayerEditPart))
//      	{
//      		if (!(part instanceof RootEditPart)) 
//      		{
//        		if (exclusiveClass == null) {
//          		if (part instanceof AbstractDrawComponentEditPart) {
//          			doRollOver((AbstractDrawComponentEditPart)part);     			
//          		}      		
//        		}
//        		else {
//        			if (exclusiveClass.equals(part.getClass())) {
//        				AbstractDrawComponentEditPart dcPart = (AbstractDrawComponentEditPart) exclusiveClass.cast(part);
//        				doRollOver(dcPart);
//        			}
//        		}      			
//      		}
//        }
//      }
//    }
//  };
  
  protected MouseMotionListener mouseListener = new MouseMotionListener.Stub() 
  {
    public void mouseMoved(org.eclipse.draw2d.MouseEvent me) 
    {
    	relativePoint = new Point(me.x, me.y);
    	mousePoint = EditorUtil.toAbsoluteWithScrollOffset(root, me.x, me.y);    	      
      EditPart part = viewer.findObjectAtExcluding(relativePoint, excludeListRef.getExcludeList(), conditionRef.getCondition());
      statusLineMan.setMessage(getMouseCoordinates());      
      if (part != null) 
      {       	
    		if (exclusiveClass == null) {
      		if (part instanceof AbstractDrawComponentEditPart) {
      			doRollOver((AbstractDrawComponentEditPart)part);     			
      		}      		
    		}
    		else {
    			if (exclusiveClass.equals(part.getClass())) {
    				AbstractDrawComponentEditPart dcPart = (AbstractDrawComponentEditPart) exclusiveClass.cast(part);
    				doRollOver(dcPart);
    			}
    		}      			
      }
    }
  };  
  
  protected String getMouseCoordinates() 
  {
  	return "MouseX = "+mousePoint.x+", MouseY = "+mousePoint.y;  	
  }

  protected IFigure getFeedbackLayer() 
  {
    if (root instanceof ScalableFreeformRootEditPart) {
    	ScalableFreeformRootEditPart rootEditPart = (ScalableFreeformRootEditPart) root;
    	IFigure feedbackLayer = rootEditPart.getLayer(ScalableFreeformRootEditPart.FEEDBACK_LAYER);
    	return feedbackLayer;
    }
    return null;
  }
  
  protected DrawComponentFigure rollOverFigure = null;  
  protected void addRollOver(AbstractDrawComponentEditPart dcPart) 
  {
    if (getFeedbackLayer() != null && dcPart != null) 
    {
    	long addStart = System.currentTimeMillis();
    	DrawComponent dc = dcPart.getDrawComponent();
//    	Rectangle figureBounds = dcPart.getFigure().getBounds();
    	if (rollOverFigure == null)
    		rollOverFigure = new DrawComponentFigure();
    	Renderer r = dc.getRenderModeManager().getRenderer(RenderConstants.ROLLOVER_MODE, dc.getClass());
    	
    	if (r != null) 
    	{
    		rollOverFigure.setRenderer(r);
    		rollOverFigure.setDrawComponent(dc);    		
//    		rollOverFigure.setBounds(figureBounds);
    		// add already calls repaint
      	getFeedbackLayer().add(rollOverFigure); 
    	}
    	long addEnd = System.currentTimeMillis() - addStart;
    	LOGGER.debug("addRollOver took "+addEnd+" ms!");
    }  	  	
  }

  protected void removeRollOver() 
  {
    if (getFeedbackLayer() != null && rollOverFigure != null) {
    	// remove already calls repaint
    	getFeedbackLayer().remove(rollOverFigure);
    }  	
  }
    
  protected void doRollOver(AbstractDrawComponentEditPart dcPart) 
  {
    if (dcPart != null) {
    	DrawComponent dc = dcPart.getDrawComponent();    	
  		descriptorManager.setDrawComponent(dc);
  		statusLineMan.setMessage(getMouseCoordinates() + ", " + descriptorManager.getEntriesAsString(false));
  		
//      removeRollOver();
//      addRollOver(dcPart);  		
    }
    
//    if (!dcPart.equals(oldPart)) {            
//			if (oldPart != null) {
//      	oldPart.getDrawComponent().setRenderMode(RenderModeManager.DEFAULT_MODE);
//      	oldPart.getFigure().repaint();
//			}						
//      dcPart.getDrawComponent().setRenderMode(RenderModeManager.ROLLOVER_MODE);
//			oldPart = dcPart;
//			dcPart.getFigure().repaint();
//			LOGGER.debug("dcPart = "+dcPart);
//    }      			  	
  }
    
  public static class ConditionRef
  {
  	private EditPartViewer.Conditional condition;
  	public ConditionRef(EditPartViewer.Conditional condition)
  	{
  		this.condition = condition;
  	}
  	public EditPartViewer.Conditional getCondition() {
			return condition;
		}
  	public void setCondition(EditPartViewer.Conditional condition) {
			this.condition = condition;
		}
  }

  public static class ExcludeListRef
  {
  	private Collection excludeList;
  	public ExcludeListRef(Collection excludeList) {
  		this.excludeList = excludeList;
  	}
		public Collection getExcludeList() {
			return excludeList;
		}
		public void setExcludeList(Collection excludeList) {
			this.excludeList = excludeList;
		}  	
  }
  
  protected ConditionRef conditionRef = null;  
  protected EditPartViewer.Conditional createIgnoreCondition(final Collection classes) 
  {
  	EditPartViewer.Conditional condition = new EditPartViewer.Conditional() 
  	{
  		public boolean evaluate(EditPart part) 
  		{
  			if (!(part instanceof AbstractDrawComponentEditPart))
  				return false;
  			
  			Class c = part.getClass();
  			if (classes.contains(c)) {
  				return false;
  			}
  			
  			return true;
  		}
		};
		return condition;
  }
  
  protected EditPartViewer.Conditional createExclusiveCondition(final Class c) 
  {
  	EditPartViewer.Conditional condition = new EditPartViewer.Conditional() 
  	{
  		public boolean evaluate(EditPart part) 
  		{
  			if (part.getClass().equals(c))
  				return true;
  			
  			return false;
  		}
  	};
  	return condition;
  }
  
  protected EditPartViewer.Conditional createDefaultCondition() 
  {
  	EditPartViewer.Conditional condition = new EditPartViewer.Conditional() 
  	{
  		public boolean evaluate(EditPart part) 
  		{
  			if (part instanceof MultiLayerDrawComponentEditPart ||
  					part instanceof LayerEditPart)
  				return false;
  			
  			if (part instanceof RootEditPart)
  				return false;
  			
  			if (part instanceof AbstractDrawComponentEditPart)
  				return true;
  			
  			return false;
  		}
  	};
  	return condition;  	
  }
   
}
