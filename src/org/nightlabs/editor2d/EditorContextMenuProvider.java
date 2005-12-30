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

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionFactory;

import org.nightlabs.editor2d.actions.EditShapeAction;
import org.nightlabs.editor2d.actions.NormalSelectionAction;
import org.nightlabs.editor2d.actions.ResetRotationCenterAction;
import org.nightlabs.editor2d.actions.RotateAction;
import org.nightlabs.editor2d.actions.SelectAllWithSameName;
import org.nightlabs.editor2d.actions.ShowDefaultRenderAction;


public class EditorContextMenuProvider 
extends ContextMenuProvider
{
  public static final String CONTEXT_MENU_ID = "org.nightlabs.editor2d.outline.contextmenu";
  
  /** The editor's action registry. */
  private ActionRegistry actionRegistry;
  	
  /**
   * Instantiate a new menu context provider for the specified EditPartViewer 
   * and ActionRegistry.
   * @param viewer	the editor's graphical viewer
   * @param registry	the editor's action registry
   * @throws IllegalArgumentException if registry is <tt>null</tt>. 
   */
  public EditorContextMenuProvider(EditPartViewer viewer, ActionRegistry registry) {
  	super(viewer);
  	if (registry == null) {
  		throw new IllegalArgumentException();
  	}
  	actionRegistry = registry;
  }

  /**
   * Called when the context menu is about to show. Actions, 
   * whose state is enabled, will appear in the context menu.
   * @see org.eclipse.gef.ContextMenuProvider#buildContextMenu(org.eclipse.jface.action.IMenuManager)
   */
  public void buildContextMenu(IMenuManager manager) 
  {
  	// Add standard action groups to the menu
  	GEFActionConstants.addStandardActionGroups(manager);
  	
  	IAction action;
  	
  	action = getActionRegistry().getAction(ActionFactory.UNDO.getId());
  	manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

  	action = getActionRegistry().getAction(ActionFactory.REDO.getId());
  	manager.appendToGroup(GEFActionConstants.GROUP_UNDO, action);

  	action = getActionRegistry().getAction(ActionFactory.PASTE.getId());
  	if (action.isEnabled())
  		manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

  	action = getActionRegistry().getAction(ActionFactory.DELETE.getId());
  	if (action.isEnabled())
  		manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);

  	action = getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT);
  	if (action.isEnabled())
  		manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
  	
  	// View SubMenu
  	MenuManager viewsubmenu = new MenuManager(EditorPlugin.getResourceString("menu_view"));  	 
  	buildViewSubMenu(viewsubmenu);  	
  	if (!viewsubmenu.isEmpty())
  		manager.appendToGroup(GEFActionConstants.GROUP_REST, viewsubmenu);
  	
  	// Edit SubMenu
  	MenuManager editsubmenu = new MenuManager(EditorPlugin.getResourceString("menu.edit"));
  	buildEditSubMenu(editsubmenu);  	
  	if (!editsubmenu.isEmpty())
  		manager.appendToGroup(GEFActionConstants.GROUP_REST, editsubmenu);
  	
  	// Alignment SubMenu
  	MenuManager alignSubMenu = new MenuManager(EditorPlugin.getResourceString("menu.align"));
  	buildAlignSubMenu(alignSubMenu);
  	if (!alignSubMenu.isEmpty())
  		manager.appendToGroup(GEFActionConstants.GROUP_REST, alignSubMenu);

  	// Mark SubMenu
  	MenuManager markSubMenu = new MenuManager(EditorPlugin.getResourceString("menu.mark"));
  	buildMarkSubMenu(markSubMenu);
  	if (!markSubMenu.isEmpty())
  		manager.appendToGroup(GEFActionConstants.GROUP_REST, markSubMenu);  	
  	
//  	action = getActionRegistry().getAction(ActionFactory.SAVE.getId());
//  	manager.appendToGroup(GEFActionConstants.GROUP_SAVE, action);
  }

  protected IAction getAction(String actionId) {
  	return actionRegistry.getAction(actionId);
  }

  protected ActionRegistry getActionRegistry() {
  	return actionRegistry;
  }

  protected void setActionRegistry(ActionRegistry registry) {
  	actionRegistry = registry;
  }  
    
  protected void buildViewSubMenu(MenuManager menuMan) 
  {
  	IAction action = getActionRegistry().getAction(ShowDefaultRenderAction.ID);
  	if (action.isEnabled())
  	  menuMan.add(action);    
  }
  
  protected void buildEditSubMenu(MenuManager menuMan)
  {  	
  	// Edit Shape Action
  	IAction action = getActionRegistry().getAction(EditShapeAction.ID);
  	if (action.isEnabled())
  	  menuMan.add(action);

  	// Rotate Action
  	action = getActionRegistry().getAction(RotateAction.ID);
  	if (action.isEnabled())
  	  menuMan.add(action);
  	  	
  	// Normal Selection Action
  	action = getActionRegistry().getAction(NormalSelectionAction.ID);
  	if (action.isEnabled())
  	  menuMan.add(action);
  	
  	// Reset Rotation Center Action
  	action = getActionRegistry().getAction(ResetRotationCenterAction.ID);
//  	if (action.isEnabled())
//  	  manager.appendToGroup(GEFActionConstants.GROUP_EDIT, action);
  	menuMan.add(action);  	  	
  }
  
  protected void buildAlignSubMenu(MenuManager menuMan) 
  {
  	IAction action = getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT);
  	if (action.isEnabled())
  	  menuMan.add(action);

  	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER);
  	if (action.isEnabled())
  	  menuMan.add(action);

  	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT);
  	if (action.isEnabled())
  	  menuMan.add(action);
  		
  	menuMan.add(new Separator());
  	
  	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP);
  	if (action.isEnabled())
  	  menuMan.add(action);

  	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE);
  	if (action.isEnabled())
  	  menuMan.add(action);

  	action = getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM);
  	if (action.isEnabled())
  	  menuMan.add(action);    
  }
  
  protected void buildMarkSubMenu(MenuManager menuMan)
  {
    IAction action;
    action = getActionRegistry().getAction(SelectAllWithSameName.ID);
    menuMan.add(action);    
  }  
}
