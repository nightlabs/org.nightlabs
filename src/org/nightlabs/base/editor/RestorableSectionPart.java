/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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
package org.nightlabs.base.editor;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

/**
 * A section part with the ability to set it undirty.
 * 
 * @version $Revision$
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class RestorableSectionPart extends SectionPart
{
	/**
	 * Create an instance of this section part and add
	 * it to a managed form.
	 * @param section The section this part is for
	 * @param managedForm The managed form where this
	 * 		section part should be added or <code>null</code>
	 * 		if the section part sould not be added to
	 * 		a managed form. 
	 */
	public RestorableSectionPart(Section section, IManagedForm managedForm)
	{
		super(section);
		if(managedForm != null)
			managedForm.addPart(this);
	}
	
	/**
	 * Mark this section part undirty. Eclipse
	 * SectionPart lacks this feature, so here is
	 * a workaround.
	 */
	public void markUndirty()
	{
		// set dirty = false
		super.commit(false);
		getManagedForm().dirtyStateChanged();
	}
}
