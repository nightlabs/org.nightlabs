/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
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

package org.nightlabs.base.app;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.action.INewFileAction;
import org.nightlabs.base.action.NewFileRegistry;
import org.nightlabs.base.action.OpenFileAction;
import org.nightlabs.base.action.ReOpenFileAction;
import org.nightlabs.base.config.RecentFileCfMod;
import org.nightlabs.config.Config;
import org.nightlabs.config.ConfigException;

/**
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de, <p>
 * <p>		 		 Alex[AT]NightLabs[DOT]de</p>
 */
public class DefaultActionBuilder 
extends ActionBarAdvisor 
{		
	private IActionBarConfigurer configurer;
	
	// File-Menu
	private IMenuManager newMenu;
	private IMenuManager recentFilesMenu;
//	private ActionFactory.IWorkbenchAction newAction;
//	private ActionFactory.IWorkbenchAction closeAction;
//	private ActionFactory.IWorkbenchAction closeAllAction;
	private ActionFactory.IWorkbenchAction saveAction;
	private ActionFactory.IWorkbenchAction saveAsAction;
//	private ActionFactory.IWorkbenchAction printAction;
//	private ActionFactory.IWorkbenchAction importAction;
//	private ActionFactory.IWorkbenchAction exportAction;
//	private ActionFactory.IWorkbenchAction propertiesAction;
	private ActionFactory.IWorkbenchAction quitAction;
	private OpenFileAction openAction;
	
	// Help-Menu
	private ActionFactory.IWorkbenchAction introAction; 
	private ActionFactory.IWorkbenchAction helpAction;
	private ActionFactory.IWorkbenchAction updateAction;
	private ActionFactory.IWorkbenchAction aboutAction;
	
	// Window-Menu
	private IContributionItem openPerspectiveMenu;
	private IContributionItem showViewMenu;	
	private ActionFactory.IWorkbenchAction preferencesAction;
			
	public DefaultActionBuilder(IActionBarConfigurer configurer) 
	{		
		this(configurer, true, true, true, true, true, true, true, true);
	}	
	
	public DefaultActionBuilder(IActionBarConfigurer configurer, boolean showNewMenu, boolean showOpenMenu, 
			boolean showRecentFilesMenu, boolean showSaveMenu, boolean showPerspectivesMenu, boolean showViewsMenu, 
			boolean showPreferencesMenu, boolean showHelpMenu) 
	{
		this(configurer, showNewMenu, showOpenMenu, showRecentFilesMenu, showSaveMenu, 
				showPerspectivesMenu, showViewsMenu, showPreferencesMenu, showHelpMenu, true, true);		
	}	

	public DefaultActionBuilder(IActionBarConfigurer configurer, boolean showNewMenu, boolean showOpenMenu, 
			boolean showRecentFilesMenu, boolean showSaveMenu, boolean showPerspectivesMenu, boolean showViewsMenu, 
			boolean showPreferencesMenu, boolean showHelpMenu, boolean showIntro, boolean showUpdate) 
	{
		super(configurer);
		this.showNewMenu = showNewMenu;
		this.showOpenMenu = showOpenMenu;
		this.showRecentFilesMenu = showRecentFilesMenu;
		this.showSaveMenu = showSaveMenu;
		this.showPerspectivesMenu = showPerspectivesMenu;
		this.showViewsMenu = showViewsMenu;
		this.showPreferencesMenu = showPreferencesMenu;
		this.showHelpMenu = showHelpMenu;
		this.showIntro = showIntro;
		this.showUpdate = showUpdate;
		initRecentFileConfig();
	}	
		
	protected boolean showNewMenu = false;
	protected boolean showOpenMenu = false;
	protected boolean showRecentFilesMenu = false;
	protected boolean showSaveMenu = false;
	protected boolean showPerspectivesMenu = false;
	protected boolean showViewsMenu = false;
	protected boolean showPreferencesMenu = false;
	protected boolean showHelpMenu = false;
	protected boolean showIntro = false;
	protected boolean showUpdate = false;
	
	protected void initRecentFileConfig() 
	{
		try {
			fileHistory = (RecentFileCfMod) Config.sharedInstance().createConfigModule(RecentFileCfMod.class);
		} catch (ConfigException e) {
			throw new RuntimeException(e);
		}				
	}
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#makeActions(org.eclipse.ui.IWorkbenchWindow)
	 */
	protected void makeActions(IWorkbenchWindow window) 
	{
		// File-Menu
		if (showNewMenu) {
			newMenu = new MenuManager(NLBasePlugin.getResourceString("menu.new.label"), ActionFactory.NEW.getId());
//		newMenu.add((ActionFactory.NEW.create(window)));
			newMenu.add(new GroupMarker(ActionFactory.NEW.getId()));			
		}
		
		if (showOpenMenu) 
			openAction = new OpenFileAction();
		
		if (showRecentFilesMenu) {
			openAction.addPropertyChangeListener(historyFileListener);
			recentFilesMenu = new MenuManager(NLBasePlugin.getResourceString("menu.openRecentFiles.label"), NLWorkbenchActionConstants.M_RECENT_FILES);
			recentFilesMenu.add(new GroupMarker(IWorkbenchActionConstants.HISTORY_GROUP));			
		}
		
//		closeAction = ActionFactory.CLOSE.create(window);
//		closeAllAction = ActionFactory.CLOSE_ALL.create(window);
		
		if (showSaveMenu) {
			saveAction = ActionFactory.SAVE.create(window);
			saveAsAction = ActionFactory.SAVE_AS.create(window);				
		}
//		printAction = ActionFactory.PRINT.create(window);
//		importAction = ActionFactory.IMPORT.create(window);
//		exportAction = ActionFactory.EXPORT.create(window);
//		propertiesAction = ActionFactory.PROPERTIES.create(window);
		quitAction = ActionFactory.QUIT.create(window);
		
		// Window-Menu
		if (showPerspectivesMenu)
			openPerspectiveMenu = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);
		if (showViewsMenu) 
			showViewMenu = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
		if (showPreferencesMenu)
			preferencesAction = ActionFactory.PREFERENCES.create(window);
				
		// Help-Menu		
		if (showHelpMenu)
			helpAction = ActionFactory.HELP_CONTENTS.create(window);
		if (showIntro)
			introAction = ActionFactory.INTRO.create(window);
		if (showUpdate)
			// TODO: find out how to hook updateAction
			
		aboutAction = ActionFactory.ABOUT.create(window); 
		
		if (saveAction != null)
			getActionBarConfigurer().registerGlobalAction(saveAction);
		if (saveAsAction != null)
			getActionBarConfigurer().registerGlobalAction(saveAsAction);
		if (quitAction != null)
			getActionBarConfigurer().registerGlobalAction(quitAction);
		if (openAction != null)
			getActionBarConfigurer().registerGlobalAction(openAction);
		if (introAction != null)
			getActionBarConfigurer().registerGlobalAction(introAction);
		if (helpAction != null)
			getActionBarConfigurer().registerGlobalAction(helpAction);
		if (updateAction != null)
			getActionBarConfigurer().registerGlobalAction(updateAction);		
	}

	protected IMenuManager fileMenu = null;
	public IMenuManager getFileMenu() {
		return fileMenu;
	}
	
	protected IMenuManager windowMenu = null;
	public IMenuManager getWindowMenu() {
		return windowMenu;
	}
	
	protected IMenuManager helpMenu = null;
	public IMenuManager getHelpMenu() {
		return helpMenu;
	}
	
	/**
	 * @see org.eclipse.ui.application.ActionBarAdvisor#fillMenuBar(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillMenuBar(IMenuManager menuBar) 
	{		
	  // File-Menu
		fileMenu = new MenuManager(NLBasePlugin.getResourceString("menu.file.label"), 
				IWorkbenchActionConstants.M_FILE); 
		menuBar.add(fileMenu);

		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		if (showNewMenu) {
			fileMenu.add(newMenu);
			createNewEntries(newMenu);			
		}
		if (showOpenMenu)
			fileMenu.add(openAction); 

		if (showRecentFilesMenu) {
	    fileMenu.add(recentFilesMenu);
			historyFileMenuManager = recentFilesMenu;
			createHistoryEntries(historyFileMenuManager);			
		}
		fileMenu.add(new Separator());
		
//		fileMenu.add(closeAction);
//		fileMenu.add(closeAllAction);
    
		if (showSaveMenu) {
	    fileMenu.add(new GroupMarker(IWorkbenchActionConstants.SAVE_GROUP));
			fileMenu.add(saveAction);
			fileMenu.add(saveAsAction);
			fileMenu.add(new Separator());			
		}
		
//		fileMenu.add(printAction);
//		fileMenu.add(new GroupMarker(IWorkbenchActionConstants.IMPORT_EXT));
//		fileMenu.add(importAction);
//		fileMenu.add(exportAction);
//		fileMenu.add(new Separator());
				
		fileMenu.add(quitAction);
    fileMenu.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));		
		
    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    
    // Window-Menu
		windowMenu = new MenuManager(NLBasePlugin.getResourceString("menu.window.label"), 
				IWorkbenchActionConstants.M_WINDOW);
		menuBar.add(windowMenu);		
		
		// Perspective-SubMenu
		if (showPerspectivesMenu) {
			MenuManager openPerspectiveMenuMgr = new MenuManager(NLBasePlugin.getResourceString("menu.openPerspective.label"), 
					NLWorkbenchActionConstants.M_PERSPECTIVES);
			openPerspectiveMenuMgr.add(openPerspectiveMenu);
			windowMenu.add(openPerspectiveMenuMgr);					
		}
		
		// View-SubMenu
		if (showViewsMenu) {
			MenuManager showViewMenuMgr = new MenuManager(NLBasePlugin.getResourceString("menu.showView.label"), 
					NLWorkbenchActionConstants.M_VIEWS);
			showViewMenuMgr.add(showViewMenu);
			windowMenu.add(showViewMenuMgr);		
			windowMenu.add(new Separator());			
		}
		
		if (showPreferencesMenu)
			windowMenu.add(preferencesAction);
		
		// Help-Menu
		helpMenu = new MenuManager(NLBasePlugin.getResourceString("menu.help.label"), 
				IWorkbenchActionConstants.M_HELP);
		menuBar.add(helpMenu);
//		helpMenu.add(introAction);
		if (showHelpMenu) {
			helpMenu.add(helpAction);
			helpMenu.add(new Separator());
		}
		if (showIntro) {
			helpMenu.add(introAction);		
			helpMenu.add(new Separator());			
		}
		
		helpMenu.add(aboutAction);
	}

	public void fillCoolBar(ICoolBarManager coolBar) 
	{
	}
	
	public void dispose() 
	{
	  	aboutAction.dispose();
//	  	closeAction.dispose();
//	  	closeAllAction.dispose();
//	  	exportAction.dispose();
	  	if (showHelpMenu)
	  		helpAction.dispose();
//	  	importAction.dispose();
	  	if (showIntro)
	  		introAction.dispose();
//	  	newAction.dispose();

	  	if (showPreferencesMenu)
	  		preferencesAction.dispose();
//	  	printAction.dispose();
//	  	propertiesAction.dispose();
	    quitAction.dispose();
	    if (showSaveMenu) {
		    saveAction.dispose();
		    saveAsAction.dispose();	    	    	
	    }
	}
	
	protected RecentFileCfMod fileHistory;
	protected IMenuManager historyFileMenuManager;	
	protected int historyEntries = 0;
	protected int maxHistoryLength = 0;
	protected String firstHistoryID = null; 
	protected String lastHistoryID = null;

	// is notifyed if a file has been opend or created
	protected PropertyChangeListener historyFileListener = new PropertyChangeListener() {	
		public void propertyChange(PropertyChangeEvent arg0) {
			if (arg0.getPropertyName().equals(OpenFileAction.HISTORY_FILE_ADDED)) {
				String fileName = (String) arg0.getNewValue();
				addHistoryFile(historyFileMenuManager, fileName, false);
			}
		}	
	}; 
		
	protected void addHistoryFile(IMenuManager menuMan, String fileName, boolean append) 
	{
		ReOpenFileAction action = new ReOpenFileAction(fileName);
		if (firstHistoryID == null) {
			firstHistoryID = action.getId();
			menuMan.add(action);				
		}
		else 
		{
			if (!append) {
				menuMan.insertBefore(firstHistoryID, action);
				firstHistoryID = action.getId();				
			}
			else
				menuMan.add(action);
		}
		
		historyEntries++;
		
		if (maxHistoryLength == historyEntries)
			lastHistoryID = action.getId();
		
		if (maxHistoryLength < historyEntries) 
		{
			menuMan.remove(lastHistoryID);
			if (!fileHistory.getRecentFileNames().contains(fileName))
				fileHistory.getRecentFileNames().add(fileName);
			
			for (int i=0; i<fileHistory.getRecentFileNames().size()-maxHistoryLength; i++) {
				fileHistory.getRecentFileNames().remove(i);				
			}				
		}
	}
	
	/**
	 * creates the MenuEntries of all previous opened files
	 * @param menuMan The IMenuManager to which the entries should be added
	 */
	protected void createHistoryEntries(IMenuManager menuMan) 
	{
		if (fileHistory != null) {
			List fileNames = fileHistory.getRecentFileNames();
			maxHistoryLength = fileHistory.getMaxHistoryLength();
			if (fileNames.size() != 0) {
				for (int i=fileNames.size()-1; i!=0; i--) {
					String fileName = (String) fileNames.get(i);
					addHistoryFile(menuMan, fileName, true);
				}											
			}
		}			
	}	
		
	/**
	 * adds entries registered in the {@link NewFileRegistry}-ExtensionPoint
	 * to the given {@link MenuManager}
	 * 
	 * @param menuMan the IMenuManager to add new entries to
	 */
	protected void createNewEntries(IMenuManager menuMan)
	{
		NewFileRegistry newFileRegistry = NewFileRegistry.sharedInstance(); 
		Map categoryID2Actions = newFileRegistry.getCategory2Actions();
		List defaultActions = new ArrayList();
		for (Iterator it = categoryID2Actions.keySet().iterator(); it.hasNext(); ) 
		{
			String categoryID = (String) it.next();
			List actions = (List) categoryID2Actions.get(categoryID);
			for (Iterator itActions = actions.iterator(); itActions.hasNext(); ) {
				INewFileAction action = (INewFileAction) itActions.next();			
				if (categoryID.equals(NewFileRegistry.DEFAULT_CATEGORY)) {
					defaultActions.add(action);
				}
				else {
					String categoryName = newFileRegistry.getCategoryName(categoryID);
					if (categoryName != null && !categoryName.equals("")) {					
						IMenuManager categoryMenu = new MenuManager(categoryName);
						categoryMenu.add(action);
						menuMan.add(categoryMenu);
					}
				}							
			}
		}
		for (Iterator itDefault = defaultActions.iterator(); itDefault.hasNext(); ) {
			menuMan.add((IAction)itDefault.next());
		}
	}
	
}
