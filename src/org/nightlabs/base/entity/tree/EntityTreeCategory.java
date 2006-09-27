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
package org.nightlabs.base.entity.tree;

import org.eclipse.core.commands.common.EventManager;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * An abstract default implementation for an entity tree category.
 * To create a new entity tree category, one should inherit this
 * class.
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public abstract class EntityTreeCategory extends EventManager implements IEntityTreeCategory
{
	/**
	 * The id of this category.
	 */
	String id;
	
	/**
	 * The name of this category.
	 */
	String name;
	
	/**
	 * The editor id for this category.
	 */
	String editorID;
	
	/**
	 * The icon for this category and the default
	 * icon for entries in this category.
	 */
	Image icon;
	
	/**
	 * The index hint for this category telling 
	 * the system where to position this category
	 * in the entity tree.
	 */
	int indexHint;

	/**
	 * Default constructor.
	 */
	public EntityTreeCategory()
	{
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement, java.lang.String, java.lang.Object)
	 */
	public void setInitializationData(IConfigurationElement element, String propertyName, Object data) throws CoreException
	{
		id = element.getAttribute("id");
		name = element.getAttribute("name");
		editorID = element.getAttribute("editorID");
		String iconStr = element.getAttribute("icon");
		if(iconStr != null)
			icon = AbstractUIPlugin.imageDescriptorFromPlugin(element.getDeclaringExtension().getNamespaceIdentifier(), iconStr).createImage();
//			icon = ImageDescriptor.createFromURL(BaseAdminPlugin.getDefault().getBundle().getEntry(iconStr)).createImage();
		else
			icon = null;
		String indexHintStr = element.getAttribute("indexHint");
		if(indexHintStr != null)
			indexHint = Integer.parseInt(indexHintStr);
		else
			indexHint = Integer.MAX_VALUE / 2;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#addEntityTreeCategoryChangeListener(org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategoryChangeListener)
	 */
	public void addEntityTreeCategoryChangeListener(IEntityTreeCategoryChangeListener listener)
	{
		addListenerObject(listener);
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#removeEntityTreeCategoryChangeListener(org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategoryChangeListener)
	 */
	public void removeEntityTreeCategoryChangeListener(IEntityTreeCategoryChangeListener listener)
	{
		removeListenerObject(listener);
	}

	/**
	 * Returns the count of listeners attached to this category
	 * @return The count of listeners attached to this category
	 */
	protected int getListenerCount() {
		return getListeners().length;
	}
	
	/**
	 * Fire a category change event. This will notify listeners
	 * about content and structure changes within this category.
	 */
	protected void fireEntityTreeCategoryChange() 
	{
	  Object[] array = getListeners();
	  for (int nX = 0; nX < array.length; nX++) {
	    final IEntityTreeCategoryChangeListener l = (IEntityTreeCategoryChangeListener) array[nX];
	    l.categoryChanged(EntityTreeCategory.this);
	  }
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(IEntityTreeCategory o)
	{
		return getIndexHint() - o.getIndexHint();
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getId()
	 */
	public String getId()
	{
		return id;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getEditorID()
	 */
	public String getEditorID()
	{
		return editorID;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getIcon()
	 */
	public Image getImage()
	{
		return icon;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getIndexHint()
	 */
	public int getIndexHint()
	{
		return indexHint;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.IEntityTreeCategory#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#getIcon(java.lang.Object)
	 */
	public Image getImage(Object o)
	{
		return getImage();
	}
	
	/* (non-Javadoc)
	 * @see org.nightlabs.jfire.base.admin.entityeditor.IEntityTreeCategory#getText(java.lang.Object)
	 */
	public String getText(Object o)
	{
		return o.toString();
	}
}
