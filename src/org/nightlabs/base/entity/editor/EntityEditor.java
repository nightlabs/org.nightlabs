/* *****************************************************************************
 * JFire - it's hot - Free ERP System - http://jfire.org                       *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
 ******************************************************************************/
package org.nightlabs.base.entity.editor;

import java.util.List;

import org.eclipse.ui.PartInitException;
import org.nightlabs.base.editor.CommitableFormEditor;
import org.nightlabs.base.entity.EntityEditorRegistry;

/**
 * An abstract base class for entity editors. It provides
 * the method {@link #addPages()} using the EntityEditorRegistry
 * to add pages registered via extension point.
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class EntityEditor extends CommitableFormEditor
{
	private String editorID;
	
	public EntityEditor(String editorID)
	{
		this.editorID = editorID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
	 */
	@Override
	protected void addPages()
	{
		try {
			List<EntityEditorPageSettings> pageSettings = EntityEditorRegistry.sharedInstance().getPageSettingsOrdered(getEditorID());
			for (EntityEditorPageSettings pageSetting : pageSettings)
				addPage(pageSetting.getPageFactory().createPage(this));
		} catch (PartInitException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the editor id.
	 * @return The editor id
	 */
	public String getEditorID()
	{
		return editorID;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs()
	{
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}
}
