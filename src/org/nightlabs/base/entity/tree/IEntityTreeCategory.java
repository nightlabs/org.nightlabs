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

import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;

/**
 * Implementations of this interface can appear in
 * the system administration entity tree.
 * 
 * @see EntityTreeCategory
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public interface IEntityTreeCategory extends IExecutableExtension, Comparable<IEntityTreeCategory>
{
	/**
	 * Get the ID of this category.
	 * @return The ID of this category
	 */
	String getId();
	
	/**
	 * Get the name of this category. This will be
	 * used as label in the entity tree.
	 * @return The name of this category
	 */
	String getName();
	
	/**
	 * Get an image for for this category. This will be
	 * used as icon in the entity tree. Entries in
	 * this category will also use this icon as default.
	 * @return An image for for this category
	 */
	Image getImage();
	
	/**
	 * Get the ID of an editor to be opened for entries
	 * in this category.
	 * @return The ID of an editor to be opened for entries
	 * 		in this category
	 */
	String getEditorID();
	
	/**
	 * Get the index hint for this category telling 
	 * the system where to position this category
	 * in the entity tree.
	 * This is only a hint.
	 * @return An index for display order of this category
	 */
	int getIndexHint();
	
	/**
	 * Get all entries in this category. This method
	 * will be called by the tree content provider.
	 * @return All entries in this category.
	 */
	Object[] getChildren();
	
	/**
	 * Get the name of an entry in this category. This method
	 * will be called by the tree label provider.
	 * @see #getChildren()
	 * @param o An entry in this category.
	 * @return The name of an entry in this category.
	 */
	String getText(Object o);

	/**
	 * Get the an image for an entry in this category. This method
	 * will be called by the tree label provider.
	 * @see #getChildren()
	 * @param o An entry in this category.
	 * @return An image for an entry in this category.
	 */
	Image getImage(Object o);

	/**
	 * Add a listener for changes in this category.
	 * Listeners will be notified about content and
	 * structure changes within the category.
	 * @param listener The listener to add
	 */
	void addEntityTreeCategoryChangeListener(IEntityTreeCategoryChangeListener listener);

	/**
	 * Remove a listener for changes in this category.
	 * @param listener The listener to remove
	 */
	void removeEntityTreeCategoryChangeListener(IEntityTreeCategoryChangeListener listener);
	
	/**
	 * Get an editor input object for an entry in this category.
	 * @see #getChildren()
	 * @param o An entry in this category.
	 * @return An editor input object for an entry in this category
	 */
	IEditorInput getEditorInput(Object o);
}
