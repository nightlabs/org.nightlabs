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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.nightlabs.base.app.DefaultActionBuilder.ActionBarItem;

/**
 * 
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de, <p>
 *
 */
public class DefaultWorkbenchWindowAdvisor 
extends WorkbenchWindowAdvisor 
{
	/**
	 * @param configurer
	 */
	public DefaultWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer, String applicationTitle) 
	{
		super(configurer);
		this.applicationName = applicationTitle;
	}
	protected String applicationName = "Application";

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		Collection<ActionBarItem> menuBarItems = new HashSet<ActionBarItem>();
		menuBarItems.add(ActionBarItem.New);
		menuBarItems.add(ActionBarItem.Open);
		menuBarItems.add(ActionBarItem.RecentFiles);
		menuBarItems.add(ActionBarItem.Save);
		menuBarItems.add(ActionBarItem.Perspectives);
		menuBarItems.add(ActionBarItem.Views);
		menuBarItems.add(ActionBarItem.Preferences);
		menuBarItems.add(ActionBarItem.Close);		
		menuBarItems.add(ActionBarItem.CloseAll);
		return new DefaultActionBuilder(configurer, menuBarItems, null);				
	}

	public void preWindowOpen() 
	{
		super.preWindowOpen();
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setTitle(applicationName);
		configurer.setInitialSize(getScreenSize());
		configurer.setShowMenuBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowCoolBar(true);
		configurer.setShowPerspectiveBar(true);
		configurer.setShowProgressIndicator(true);
	}			

	protected Point getScreenSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new Point(screenSize.width, screenSize.height);
	}
}
