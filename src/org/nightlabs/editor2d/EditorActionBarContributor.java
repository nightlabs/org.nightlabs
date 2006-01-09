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

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.internal.GEFMessages;
import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.AlignmentRetargetAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightRetargetAction;
import org.eclipse.gef.ui.actions.MatchWidthRetargetAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.actions.ZoomAllAction;
import org.nightlabs.editor2d.actions.ZoomAllRetargetAction;
import org.nightlabs.editor2d.actions.ZoomSelectionAction;
import org.nightlabs.editor2d.actions.ZoomSelectionRetargetSelection;
import org.nightlabs.editor2d.custom.EditorZoomComboContributionItem;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.viewer.action.RenderModeContributionItem;
import org.nightlabs.editor2d.viewer.render.RendererRegistry;


public class EditorActionBarContributor 
extends ActionBarContributor 
{

  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#buildActions()
   */
  protected void buildActions() 
  {
  	addRetargetAction(new UndoRetargetAction());
  	addRetargetAction(new RedoRetargetAction());
  	addRetargetAction(new DeleteRetargetAction());
  	  	
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.LEFT));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.CENTER));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.RIGHT));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.TOP));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.MIDDLE));
  	addRetargetAction(new AlignmentRetargetAction(PositionConstants.BOTTOM));
  	  	
  	addRetargetAction(new MatchWidthRetargetAction());
  	addRetargetAction(new MatchHeightRetargetAction());
  	
  	addRetargetAction(new RetargetAction(
  			GEFActionConstants.TOGGLE_RULER_VISIBILITY, 
  			GEFMessages.ToggleRulerVisibility_Label, IAction.AS_CHECK_BOX));
  	
  	addRetargetAction(new RetargetAction(
  			GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY, 
  			GEFMessages.ToggleSnapToGeometry_Label, IAction.AS_CHECK_BOX));

  	addRetargetAction(new RetargetAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY, 
  			GEFMessages.ToggleGrid_Label, IAction.AS_CHECK_BOX));

  	addRetargetAction(new ZoomInRetargetAction());
  	addRetargetAction(new ZoomOutRetargetAction());  	
  	addRetargetAction(new ZoomAllRetargetAction());
  	addRetargetAction(new ZoomSelectionRetargetSelection());
//  	addRetargetAction(new DirectEditRetargetAction());   
//  	addRetargetAction(new ViewerRetargetAction());
  }

  public static final String ID_VIEW_MENU = "menu view";
  /**
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
   */
  public void contributeToMenu(IMenuManager menubar) 
  {
  	super.contributeToMenu(menubar);  	
//  	IMenuManager fileMenu = (IMenuManager) menubar.find(IWorkbenchActionConstants.M_FILE);  	
  	MenuManager viewMenu = new MenuManager(EditorPlugin.getResourceString("menu_view"), ID_VIEW_MENU);
  	viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
  	viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(GEFActionConstants.MATCH_WIDTH));
  	viewMenu.add(getAction(GEFActionConstants.MATCH_HEIGHT));
  	menubar.insertAfter(IWorkbenchActionConstants.M_FILE, viewMenu);
  }
    
  /**
   * Add actions to the given toolbar.
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToToolBar(org.eclipse.jface.action.IToolBarManager)
   */
  public void contributeToToolBar(IToolBarManager tbm) 
  {
  	super.contributeToToolBar(tbm);
  	tbm.add(getAction(ActionFactory.UNDO.getId()));
  	tbm.add(getAction(ActionFactory.REDO.getId()));
  	  	  	
  	tbm.add(new Separator());
  	tbm.add(getAction(GEFActionConstants.ALIGN_LEFT));
  	tbm.add(getAction(GEFActionConstants.ALIGN_CENTER));
  	tbm.add(getAction(GEFActionConstants.ALIGN_RIGHT));
  	tbm.add(new Separator());
  	tbm.add(getAction(GEFActionConstants.ALIGN_TOP));
  	tbm.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
  	tbm.add(getAction(GEFActionConstants.ALIGN_BOTTOM));
  	
  	tbm.add(new Separator());	
  	tbm.add(getAction(GEFActionConstants.MATCH_WIDTH));
  	tbm.add(getAction(GEFActionConstants.MATCH_HEIGHT));
  	
  	tbm.add(new Separator());	
  	String[] zoomStrings = new String[] {	ZoomManager.FIT_ALL, 
  											ZoomManager.FIT_HEIGHT, 
  											ZoomManager.FIT_WIDTH	};
  	  	
//  	tbm.add(new ZoomComboContributionItem(getPage(), zoomStrings));
  	tbm.add(new EditorZoomComboContributionItem(getPage(), zoomStrings));

  	IAction zoomInAction = getAction(GEFActionConstants.ZOOM_IN);
  	zoomInAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
  			EditorPlugin.getDefault(), EditorActionBarContributor.class, "ZoomIn"));
  	tbm.add(zoomInAction);

  	IAction zoomOutAction = getAction(GEFActionConstants.ZOOM_OUT);
  	zoomOutAction.setImageDescriptor(SharedImages.getSharedImageDescriptor(
  			EditorPlugin.getDefault(), EditorActionBarContributor.class, "ZoomOut"));  	
  	tbm.add(zoomOutAction);
  	
  	tbm.add(getAction(ZoomAllAction.ID));
  	tbm.add(getAction(ZoomSelectionAction.ID));
  	
  	RenderModeManager renderMan = RendererRegistry.sharedInstance().getRenderModeManager();
  	if (renderMan.getRenderModes().size() > 1)
  		tbm.add(new RenderModeContributionItem(getPage()));  	
  	
//  	tbm.add(getAction(ViewerAction.ID));
  }  
    
  /* (non-Javadoc)
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
   */
  protected void declareGlobalActionKeys() 
  {
  	addGlobalActionKey(ActionFactory.PRINT.getId());
  	addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
  	addGlobalActionKey(ActionFactory.PASTE.getId());    
  	addGlobalActionKey(ActionFactory.COPY.getId());  	
  }

}
