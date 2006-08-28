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
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.DrillDownAdapter;

/**
 * Implementations of this interface can appear in
 * an implementation of an entity tree.
 * 
 * @see EntityTreeCategory
 * 
 * @version $Revision: 4430 $ - $Date: 2006-08-20 17:18:07 +0000 (Sun, 20 Aug 2006) $
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
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
	 * Retrun a <em>new</em> {@link ITreeContentProvider} to use with this category.
	 * 
	 * The top level entries of an entity tree will always be the categories themselves.
	 * The {@link ITreeContentProvider} returned here is used to determine 
	 * the elements of a category and their children. 
	 * 
	 * The returned provider's {@link IStructuredContentProvider#getElements(Object)} 
	 * method can be called with the parent {@link IEntityTreeCategory} 
	 * as inputElement. This is when to obtain the categories top-level children.
	 *  
	 * Note that {@link IStructuredContentProvider#getElements(Object)} 
	 * can also be called with an inputElement equal to an object returned 
	 * in the getElements() or getChildren() methods. This is  when a {@link DrillDownAdapter}
	 * is used. The implementation of getElements will have to check this 
	 * in order to support such adapters. Best practise will be to return 
	 * a list of node-elements when the input is a category and
	 * delegate to the node-objects in other cases.
	 * 
	 * The {@link ITreeContentProvider#getParent(Object)} is also used when {@link DrillDownAdapter}s
	 * are used and should return the category when called with an element
	 * returned in {@link IStructuredContentProvider#getElements(Object)}.
	 *  
	 * @return A <em>new</em> {@link ITreeContentProvider} to use with this category.
	 */
	ITreeContentProvider getContentProvider();

	/**
	 * Return a <em>new</em> {@link ITableLabelProvider} to use with this category.
	 * 
	 * The icons and labels of an entity tree will be determined by the category itself
	 * (see {@link #getImage()}) and {@link #getName()}. The {@link ITableLabelProvider}
	 * returned here is used to determine labels and icons for the elements of a 
	 * category and their children.
	 * 
	 * @return A <em>new</em> {@link ITableLabelProvider} to use with this category.
	 */
	ITableLabelProvider getLabelProvider();

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
