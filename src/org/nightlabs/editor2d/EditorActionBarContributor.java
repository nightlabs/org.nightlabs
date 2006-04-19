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
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.editor2d.actions.EditShapeAction;
import org.nightlabs.editor2d.actions.RepaintAction;
import org.nightlabs.editor2d.actions.RepaintRetargetAction;
import org.nightlabs.editor2d.actions.RotateAction;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneDown;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneUp;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalBack;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalFront;
import org.nightlabs.editor2d.actions.preferences.ShowFigureToolTipAction;
import org.nightlabs.editor2d.actions.preferences.ShowStatusLineAction;
import org.nightlabs.editor2d.actions.zoom.ZoomAllAction;
import org.nightlabs.editor2d.actions.zoom.ZoomAllRetargetAction;
import org.nightlabs.editor2d.actions.zoom.ZoomPageAction;
import org.nightlabs.editor2d.actions.zoom.ZoomPageRetargetAction;
import org.nightlabs.editor2d.actions.zoom.ZoomSelectionAction;
import org.nightlabs.editor2d.actions.zoom.ZoomSelectionRetargetSelection;
import org.nightlabs.editor2d.custom.EditorZoomComboContributionItem;
import org.nightlabs.editor2d.print.EditorPrintAction;
import org.nightlabs.editor2d.print.EditorPrintPreviewAction;
import org.nightlabs.editor2d.print.EditorPrintSetupAction;
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
  	addRetargetAction(new ZoomPageRetargetAction());
  	  	
  	addRetargetAction(new RetargetAction(ShowFigureToolTipAction.ID, 
  			EditorPlugin.getResourceString("action.showFigureToolTip.text"), IAction.AS_CHECK_BOX));
  	
  	addRetargetAction(new RetargetAction(ShowStatusLineAction.ID,
  			EditorPlugin.getResourceString("action.statusLine.text"), IAction.AS_CHECK_BOX));
  	
  	addRetargetAction(new RepaintRetargetAction());
  	  	
  	addRetargetAction(new RetargetAction(EditorPrintAction.ID, 
  			EditorPlugin.getResourceString("action.print.text")));  	
  	addRetargetAction(new RetargetAction(EditorPrintPreviewAction.ID, 
  			EditorPlugin.getResourceString("action.printPreview.text")));  	  	
  	addRetargetAction(new RetargetAction(EditorPrintSetupAction.ID, 
  			EditorPlugin.getResourceString("action.printPageSetup.text")));  	
  	
//	addRetargetAction(new DirectEditRetargetAction());   
//	addRetargetAction(new ViewerRetargetAction());  	
  }

  public static final String ID_VIEW_MENU = IWorkbenchActionConstants.M_VIEW;
  public static final String ID_EDIT_MENU = IWorkbenchActionConstants.M_EDIT;
  public static final String ID_FILE_MENU = IWorkbenchActionConstants.M_FILE;
  
  /**
   * @see org.eclipse.ui.part.EditorActionBarContributor#contributeToMenu(IMenuManager)
   */
  public void contributeToMenu(IMenuManager menubar) 
  {
  	super.contributeToMenu(menubar);  	
  	 
  	IContributionItem fm = RCPUtil.getMenuItem(ID_FILE_MENU, menubar);
  	if (fm instanceof IMenuManager)  
  		fileMenu = (IMenuManager) fm;
  	if (fileMenu != null) 
  	{
  		IContributionItem printMenu = RCPUtil.getMenuItem(EditorPrintAction.ID, fileMenu);
  		if (printMenu == null)
  			fileMenu.insertBefore(ActionFactory.QUIT.getId(), getAction(EditorPrintAction.ID));
  		
			fileMenu.insertAfter(EditorPrintAction.ID, getAction(EditorPrintPreviewAction.ID));
  		fileMenu.insertAfter(EditorPrintPreviewAction.ID, getAction(EditorPrintSetupAction.ID));
    	fileMenu.insertAfter(EditorPrintSetupAction.ID, new Separator());  				  		
  	}
  	  	
  	editMenu = new MenuManager(EditorPlugin.getResourceString("menu.edit"), ID_EDIT_MENU);
  	editMenu.add(getAction(ActionFactory.UNDO.getId()));
  	editMenu.add(getAction(ActionFactory.REDO.getId()));
  	
  	editMenu.add(new Separator());
  	editMenu.add(getAction(GEFActionConstants.ALIGN_LEFT));
  	editMenu.add(getAction(GEFActionConstants.ALIGN_CENTER));
  	editMenu.add(getAction(GEFActionConstants.ALIGN_RIGHT));
  	
  	editMenu.add(new Separator());
  	editMenu.add(getAction(GEFActionConstants.ALIGN_TOP));
  	editMenu.add(getAction(GEFActionConstants.ALIGN_MIDDLE));
  	editMenu.add(getAction(GEFActionConstants.ALIGN_BOTTOM));
  	
  	editMenu.add(new Separator());	
  	editMenu.add(getAction(GEFActionConstants.MATCH_WIDTH));
  	editMenu.add(getAction(GEFActionConstants.MATCH_HEIGHT));  	
  	  	
  	menubar.insertAfter(IWorkbenchActionConstants.M_FILE, editMenu);  	
  	
  	viewMenu = new MenuManager(EditorPlugin.getResourceString("menu.view"), ID_VIEW_MENU);
  	viewMenu.add(getAction(GEFActionConstants.ZOOM_IN));
  	viewMenu.add(getAction(GEFActionConstants.ZOOM_OUT));
  	viewMenu.add(getAction(ZoomAllAction.ID));
  	viewMenu.add(getAction(ZoomSelectionAction.ID));
  	viewMenu.add(getAction(ZoomPageAction.ID));
  	
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_RULER_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_GRID_VISIBILITY));
  	viewMenu.add(getAction(GEFActionConstants.TOGGLE_SNAP_TO_GEOMETRY));
  	  	
  	viewMenu.add(new Separator());
  	viewMenu.add(getAction(ShowFigureToolTipAction.ID));
  	viewMenu.add(getAction(ShowStatusLineAction.ID));
  	
  	menubar.insertAfter(ID_EDIT_MENU, viewMenu);
  }
    
  protected IMenuManager viewMenu = null;
  protected IMenuManager getViewMenu() {
  	return viewMenu;
  }
  
  protected IMenuManager editMenu = null;
  protected IMenuManager getEditMenu() {
  	return editMenu;
  }
  
  protected IMenuManager fileMenu = null;
  protected IMenuManager getFileMenu() {
  	return fileMenu;
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
  	tbm.add(getAction(ZoomPageAction.ID));
  	
  	tbm.add(new Separator());	
  	RenderModeManager renderMan = RendererRegistry.sharedInstance().getRenderModeManager();
  	if (renderMan.getRenderModes().size() > 1)
  		tbm.add(new RenderModeContributionItem(getPage()));  	  		
  	
  	tbm.add(new Separator());
  	tbm.add(getAction(RepaintAction.ID));
//  	tbm.add(getAction(ViewerAction.ID));
  }  
    
  /**
   * @see org.eclipse.gef.ui.actions.ActionBarContributor#declareGlobalActionKeys()
   */
  protected void declareGlobalActionKeys() 
  {
  	addGlobalActionKey(ActionFactory.PRINT.getId());
  	addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
  	addGlobalActionKey(ActionFactory.PASTE.getId());    
  	addGlobalActionKey(ActionFactory.COPY.getId());  	
  	addGlobalActionKey(GEFActionConstants.ZOOM_IN);
  	addGlobalActionKey(GEFActionConstants.ZOOM_OUT);
  	addGlobalActionKey(ActionFactory.SAVE.getId());  	
  	addGlobalActionKey(ActionFactory.SAVE_AS.getId());
  	addGlobalActionKey(ActionFactory.UNDO.getId());
  	addGlobalActionKey(ActionFactory.REDO.getId());

  	addGlobalActionKey(ZoomAllAction.ID);
  	addGlobalActionKey(ZoomSelectionAction.ID);
  	addGlobalActionKey(ZoomPageAction.ID);
  	addGlobalActionKey(RotateAction.ID);
  	addGlobalActionKey(EditShapeAction.ID);
  	addGlobalActionKey(ChangeOrderOneUp.ID);
  	addGlobalActionKey(ChangeOrderOneDown.ID);
  	addGlobalActionKey(ChangeOrderToLocalBack.ID);
  	addGlobalActionKey(ChangeOrderToLocalFront.ID);  	
  }

	public void dispose() 
	{
		super.dispose();
		
		// TODO: workaround because printActions are not removed automaticly
		IContributionItem fm = RCPUtil.getMenuItem(ID_FILE_MENU, getActionBars().getMenuManager());
		if (fm instanceof IMenuManager) {
			IMenuManager fileMenu = (IMenuManager) fm;
			removeActionMenu(EditorPrintAction.ID, fileMenu);			
			removeActionMenu(EditorPrintPreviewAction.ID, fileMenu);
			removeActionMenu(EditorPrintSetupAction.ID, fileMenu);
		}		
	}
  
	protected void removeActionMenu(String actionID, IMenuManager menuMan) 
	{
		IContributionItem ci = RCPUtil.getMenuItem(actionID, menuMan);
		if (ci != null) {
			menuMan.remove(ci);
		}
	}
	 
}
