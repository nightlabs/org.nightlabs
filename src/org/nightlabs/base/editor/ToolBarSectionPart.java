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
package org.nightlabs.base.editor;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 */
public class ToolBarSectionPart 
extends MessageSectionPart 
{
	
	private ToolBar toolBar;
	private ToolBarManager toolBarManager;

	public ToolBarSectionPart(IFormPage page, Composite parent, int style, String title) {
		super(page, parent, style, title);
		toolBar = new ToolBar(getSection(), SWT.NONE);
		toolBarManager = new ToolBarManager(toolBar);
		getSection().setTextClient(toolBar);
		toolBarManager.update(true);
	}

	/**
	 * 
	 * @return
	 */
	public ToolBarManager getToolBarManager() {
		return toolBarManager;
	}

	/**
	 * This should be called after contributing to the ToolBarManager ({@link #getToolBarManager()})
	 */
	public void udpateToolBarManager() {
		toolBarManager.update(true);
	}
	
}
