/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d;

import java.util.Collection;
import java.util.List;

/**
 * This is the base interface for all DrawComponents which
 * are capable for holding children which are also of the type
 * {@link DrawComponent}, e.g. Groups
 *
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public interface DrawComponentContainer
extends DrawComponent
{
	/**
	 * The PropertyName which is fired when a child has been added
	 */
	public static final String CHILD_ADDED = "Child Added";

	/**
	 * The PropertyName which is fired when a child has been removed
	 */
	public static final String CHILD_REMOVED = "Child Removed";

	/**
	 * adds a DrawComponent to the container
	 * Implementations of this class should to the following:
	 *
	 * set the parent of the given DrawComponent to this,
	 * registers the DrawComponent in the RootDrawComponent,
	 * adds it to the drawComponent-List and fires a PropertyChange with the
	 * the propertyName CHILD_ADDED.
	 *
	 * always use this Method to add a DrawComponent, never add it directly
	 * to the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param drawComponent the DrawComponent to add
	 * @see RootDrawComponent#registerDrawComponent(DrawComponent)
	 */
	void addDrawComponent(DrawComponent drawComponent);

	/**
	 * removes the given DrawComponent from the container
	 * Implementations of this class should to the following:
	 *
	 * This Method unregisters the drawComponent from the RootDrawComponent
	 * and removes it from the drawComponents-List
	 *
	 * always use this Method to remove a DrawComponent, never remove it directly
	 * from the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param drawComponent the DrawComponent to remove
	 * @see RootDrawComponent#unregisterDrawComponent(DrawComponent)
	 */
	void removeDrawComponent(DrawComponent drawComponent);

	/**
	 * adds a DrawComponent at the given index
	 * Implementations of this class should to the following:
	 *
	 * set the parent of the given DrawComponent to this,
	 * registers the DrawComponent in the RootDrawComponent,
	 * adds it to the drawComponent-List and fires a PropertyChange with the
	 * the propertyName CHILD_ADDED.
	 *
	 * always use this Method to add a DrawComponent, never add it directly
	 * to the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param drawComponent the DrawComponent to add
	 * @param index the index in the DrawComponent-List
	 * @see #addDrawComponent(DrawComponent drawComponent)
	 * @see RootDrawComponent#registerDrawComponent(DrawComponent)
	 */
	void addDrawComponent(DrawComponent drawComponent, int index);

	/**
	 * removes the given DrawComponent at the given index from the container
	 * Implementations of this class should to the following:
	 *
	 * This Method unregisters the drawComponent from the RootDrawComponent
	 * and removes it from the drawComponents-List
	 *
	 * always use this Method to remove a DrawComponent, never remove it directly
	 * from the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param index the DrawComponent index to remove
	 * @see RootDrawComponent#unregisterDrawComponent(long)
	 */
	void removeDrawComponent(int index);

	/**
	 * This Method should be called from children of the container, when they were
	 * transformed, to notify the parent that a bounds change of the container
	 * might have occured
	 *
	 * @param child the Child-DrawComponent of this container which calls this Methods
	 */
	void notifyChildTransform(DrawComponent child);

	/**
	 *
	 * @return a List of all drawComponents for this DrawComponentContainer which have
	 * been added yet
	 * @see DrawComponentContainer#addDrawComponent(DrawComponent)
	 */
	List<DrawComponent> getDrawComponents();

//	/**
//	 *
//	 * @param drawComponents the List of DrawComponents to set for this DrawComponentContainer
//	 */
//	void setDrawComponents(List drawComponents);

//	/**
//	 * Does this object support to contain other instances of the same class. For example,
//	 * a {@link PageDrawComponent} does normally not support to contain other instances of
//	 * {@link PageDrawComponent}. This information is used in {@link #findDrawComponents(Class)}
//	 * for performance optimization. For example, it is not necessary to iterate all contents of
//	 * a page in order to find other pages, because the contract of a page defines that no other
//	 * page can be inserted into a page.
//	 * <p>
//	 * The given <code>classOrInterface</code> might be either the implementation class or the
//	 * "primary" interface or any other class or super-class. Note, that even if your implementation
//	 * does not support nesting, you might have to return <code>true</code>, if the
//	 * </p>
//	 * <p>
//	 * If your implementation of {@link DrawComponentContainer} does not know whether instances
//	 * of the same type can be nested into it (directly or indirectly), it should return
//	 * <code>true</code>. This way it is guaranteed that potentially nested
//	 * instances are found.
//	 * </p>
//	 *
//	 * @param classOrInterface a class or interface that specifies this <code>DrawComponentContainer</code>.
//	 * @return <code>true</code> if instances of the specified class can contain other instances of the same class.
//	 */
//	boolean isNestingSameClassInstancesSupported(Class<? extends DrawComponentContainer> classOrInterface);

	/**
	 * Does this object support to contain instances of the specified class or interface. For example,
	 * a {@link PageDrawComponent} does not support to contain other instances of
	 * {@link PageDrawComponent}. This information is used in {@link #findDrawComponents(Class)}
	 * for performance optimization. For example, it is not necessary to iterate all contents of
	 * a page in order to find other pages, because the contract of a page defines that no other
	 * page can be inserted into a page.
	 * <p>
	 * If your implementation of {@link DrawComponentContainer} does not know whether instances
	 * of the specified type can be nested into it (directly or indirectly), it should return
	 * <code>true</code>. This way it is guaranteed that potentially nested instances are found.
	 * </p>
	 * <p>
	 * Note, that there is no way to enforce this rule with acceptable performance. Therefore
	 * it might happen - in incorrectly implemented code using your Editor2D-data-model -
	 * that objects are nested which should not be supported.
	 * </p>
	 *
	 * @param classOrInterface a class or interface that specifies this <code>DrawComponentContainer</code>.
	 * @return <code>true</code> if instances of the specified class can contain other instances of the same class.
	 */
	boolean canContainDrawComponent(Class<? extends DrawComponent> classOrInterface);

//	/**
//	 * This method searches recursively in all child-{@link DrawComponent}s and collects
//	 * all instances that either implement a given interface, are or extend a given class.
//	 *
//	 * @param type A class or an interface which the searched <tt>DrawComponent</tt>s must match.
//	 * @param canSelfPackage If <tt>false</tt>, it is assumed that the given class does not contain
//	 *		itself and the search in this part of the tree is interrupted. If <tt>true</tt>, it will
//	 *		search in a found instance of the given type for the given type - means the whole tree will
//	 *		be scanned.
//	 * @return A <tt>Collection</tt> with instances of the given <tt>type</tt>.
//	 */
//	<T extends DrawComponent> Collection<T> findDrawComponents(Class<T> type, boolean canSelfPackage);

	/**
	 * Convenience method which calls {@link #findDrawComponents(Class, boolean)} with
	 * <tt>canSelfPackage == false</tt>.
	 */
	<T extends DrawComponent> Collection<T> findDrawComponents(Class<T> type);

	/**
	 * adds the given Collection of DrawComponents at the given index
	 *
	 * @param drawComponents the DrawComponents to add
	 * @param index the index in the drawOrder where the given drawComponents should be added
	 */
	void addDrawComponents(Collection<DrawComponent> drawComponents, int index);

	/**
	 * adds the given Collection of DrawComponents at the end of the drawOrder
	 *
	 * @param drawComponents the DrawComponents to add
	 */
	void addDrawComponents(Collection<DrawComponent> drawComponents);

	/**
	 * removes the given Collection of DrawComponents
	 *
	 * @param drawComponents the DrawComponents to remove
	 */
	void removeDrawComponents(Collection<DrawComponent> drawComponents);
} // DrawComponentContainer
