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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

/**
 * A generic base class for editor inputs using a
 * JDO Object ID.
 *
 * @version $Revision$ - $Date$
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class JDOObjectEditorInput<JDOObjectID> implements IEditorInput
{
	/**
	 * The JDO Object ID.
	 */
	JDOObjectID jdoObjectID;
	
	/**
	 * Create an instance of this editor input for a
	 * JDO Object ID.
	 * @param jdoObjectID The JDO Object ID
	 */
	public JDOObjectEditorInput(JDOObjectID jdoObjectID)
	{
		this.jdoObjectID = jdoObjectID;
	}
	
	/**
	 * Get the JDO Object ID
	 * @return The JDO Object ID
	 */
	public JDOObjectID getJDOObjectID()
	{
		return jdoObjectID;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists()
	{
		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText()
	{
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class arg0)
	{
		return null;
	}
	
	/**
	 * Check for equality using the JDO Object ID
	 * equals method.
	 * @param other The object to check. 
	 */
	@Override
	public boolean equals(Object other)
	{
		return 
				other != null && 
				other instanceof JDOObjectEditorInput && 
				((JDOObjectEditorInput)other).jdoObjectID.equals(this.jdoObjectID);
	}
}
