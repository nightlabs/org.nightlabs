/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d;

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
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

import com.nightlabs.editor2d.actions.ZoomAllAction;
import com.nightlabs.editor2d.actions.ZoomAllRetargetAction;
import com.nightlabs.editor2d.actions.ZoomSelectionAction;
import com.nightlabs.editor2d.actions.ZoomSelectionRetargetSelection;
import com.nightlabs.editor2d.actions.viewer.ViewerAction;
import com.nightlabs.editor2d.actions.viewer.ViewerRetargetAction;
import com.nightlabs.editor2d.custom.EditorZoomComboContributionItem;
import com.nightlabs.editor2d.custom.RenderModeContributionItem;


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
  	addRetargetAction(new ViewerRetargetAction());
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

  	tbm.add(getAction(GEFActionConstants.ZOOM_IN));
  	tbm.add(getAction(GEFActionConstants.ZOOM_OUT));
  	
  	tbm.add(getAction(ZoomAllAction.ID));
  	tbm.add(getAction(ZoomSelectionAction.ID));
  	
  	tbm.add(new RenderModeContributionItem(getPage()));
  	
  	tbm.add(getAction(ViewerAction.ID));
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
