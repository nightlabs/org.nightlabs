package org.nightlabs.rcp.app;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import org.nightlabs.base.NLBasePlugin;

public class DefaultActionBuilder 
extends ActionBarAdvisor 
{
	public DefaultActionBuilder(IActionBarConfigurer arg0) {
		super(arg0);
	}

	// File-Menu
	private ActionFactory.IWorkbenchAction quitAction;
	
	// Window-Menu
	private ActionFactory.IWorkbenchAction preferencesAction;
	
	// Help-Menu 
	private ActionFactory.IWorkbenchAction aboutAction;		
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	protected void makeActions(IWorkbenchWindow window) 
	{
		// File-Menu
		quitAction = ActionFactory.QUIT.create(window);
		
		// Window-Menu
//		openPerspectiveMenu = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
//		showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(window);		
		preferencesAction = ActionFactory.PREFERENCES.create(window);

		// Help-Menu	
		aboutAction = ActionFactory.ABOUT.create(window); 		
	}
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenuBar(IMenuManager menuBar) 
	{
	  // File-Menu
		fileMenu = new MenuManager(
				NLBasePlugin.getResourceString("menu.file.label"), 
				IWorkbenchActionConstants.M_FILE);
		menuBar.add(fileMenu);
		
		fileMenu.add(quitAction);
    fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));		
		
    // Window-Menu
		windowMenu = new MenuManager(
				NLBasePlugin.getResourceString("menu.window.label"), 
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);		
		
		// Perspective-SubMenu
//		MenuManager openPerspectiveMenuMgr = new MenuManager(
//				DionysosPlugin.getResourceString("menu.openPerspective.label"), 
//				ID_MENU_PERSPECTIVE);
//		
//		openPerspectiveMenuMgr.add(openPerspectiveMenu);
//		windowMenu.add(openPerspectiveMenuMgr);		
		
		// View-SubMenu
//		MenuManager showViewMenuMgr = new MenuManager(
//				DionysosPlugin.getResourceString("menu.showView.label"),
//				ID_MENU_VIEWS);
//		showViewMenuMgr.add(showViewMenu);
//		windowMenu.add(showViewMenuMgr);		
//		windowMenu.add(new Separator());
		
		windowMenu.add(preferencesAction);
    
		// Help-Menu
		helpMenu = new MenuManager(
				NLBasePlugin.getResourceString("menu.help.label"), 
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);		 
		helpMenu.add(aboutAction);		
	}
 	
	
	public void dispose() 
	{
    quitAction.dispose();		    
	  aboutAction.dispose();
	  preferencesAction.dispose();
	}
	
	protected MenuManager fileMenu = null;
	public MenuManager getFileMenu() {
		return fileMenu;
	}
	
	protected MenuManager windowMenu = null;
	public MenuManager getWindowMenu() {
		return windowMenu;
	}
	
	protected MenuManager helpMenu = null;
	public MenuManager getHelpMenu() {
		return helpMenu;
	}

}
