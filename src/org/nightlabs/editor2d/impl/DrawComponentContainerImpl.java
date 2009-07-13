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

package org.nightlabs.editor2d.impl;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.render.BaseRenderer;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.render.Renderer;
import org.nightlabs.editor2d.render.j2d.J2DBaseRenderer;
import org.nightlabs.editor2d.util.EditorModelUtil;
import org.nightlabs.i18n.unit.resolution.Resolution;

/**
 * The Base Implementation of the Interface {@link DrawComponentContainer}
 *
 * <p>
 * Author: Daniel.Mazurek[AT]NightLabs[DOT]de
 * </p>
 */
public class DrawComponentContainerImpl
extends DrawComponentImpl
implements DrawComponentContainer
{
	private static final Logger logger = Logger.getLogger(DrawComponentContainerImpl.class);

	/**
	 * Instances of {@link DrawComponent} where some of them might be
	 * {@link DrawComponentContainer}.
	 */
	private List<DrawComponent> drawComponents = null;

	public DrawComponentContainerImpl() { }

	/**
	 * @return Returns instances of {@link DrawComponent}. Some of them are
	 *         instances of {@link DrawComponentContainer}. This method never
	 *         returns <tt>null</tt>.
	 * @see DrawComponentContainer#getDrawComponents()
	 */
	public List<DrawComponent> getDrawComponents() {
		return Collections.unmodifiableList(primGetDrawComponents());
	}

	protected List<DrawComponent> primGetDrawComponents() {
		if (drawComponents == null)
			drawComponents = new ArrayList<DrawComponent>();

		return drawComponents;
	}

	/**
	 * adds a DrawComponent to the DrawComponent-List and fires a PropertyChange
	 * with the PropertyName {@link DrawComponentContainer#CHILD_ADDED}
	 *
	 * Furthermore it sets the Parent for the drawComponent to this
	 * DrawComponentContainer and calls
	 * {@link DrawComponent#getRoot().registerDrawComponent(drawComponent)} to
	 * register it at the RootDrawComponent, e.g. to receive an unique id
	 * and sets the RenderModeManager. The cached bounds are also reset.
	 *
	 * @see DrawComponentContainer#addDrawComponent(DrawComponent)
	 */
	public void addDrawComponent(DrawComponent drawComponent) {
		if (drawComponent == null)
			throw new IllegalArgumentException("Param drawComponent must NOT be null!");
		addDrawComponent(drawComponent, getLastIndex() + 1);
	}

	/**
	 * adds a DrawComponent at the given index in the DrawComponent-List
	 * {@link DrawComponentContainer#getDrawComponents()} and fires a
	 * PropertyChange with the PropertyName
	 * {@link DrawComponentContainer#CHILD_ADDED}
	 *
	 * Furthermore it sets the Parent for the drawComponent to this
	 * DrawComponentContainer and calls
	 * {@link RootDrawComponent.registerDrawComponent(drawComponent)} to
	 * register it at the RootDrawComponent, e.g. to receive an unique id
	 * and sets the RenderModeManager. The cached bounds are also reset.
	 *
	 * always use this Method to add a DrawComponent, never add it directly to the
	 * drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param drawComponent
	 *          the DrawComponent to add
	 * @param index
	 *          the index in the DrawComponent-List
	 * @see #addDrawComponent(DrawComponent)
	 * @see RootDrawComponent#registerDrawComponent(DrawComponent)
	 */
	public void addDrawComponent(DrawComponent drawComponent, int index) {
		if (drawComponent == null)
			throw new IllegalArgumentException("Param drawComponent must NOT be null!");

		List<DrawComponent> oldDrawComponents = new ArrayList<DrawComponent>(getDrawComponents());
		primAddDrawComponent(drawComponent, index);
		firePropertyChange(CHILD_ADDED, oldDrawComponents, drawComponents);
	}

	protected void assertCanAddDrawComponent(DrawComponent drawComponent)
	{
		long start = System.currentTimeMillis();

		Class<? extends DrawComponent> clazz = drawComponent.getClass();
		if (!canContainDrawComponent(clazz))
			throw new IllegalArgumentException("this.canContainDrawComponent(drawComponent) returned false for this=" + this + " drawComponent=" + drawComponent);

		if (drawComponent instanceof DrawComponentContainer) {
			for (DrawComponent child : ((DrawComponentContainer)drawComponent).getDrawComponents())
				assertCanAddDrawComponent(child);
		}

		DrawComponentContainer parent = getParent();
		if (parent != null && parent instanceof DrawComponentContainerImpl)
			((DrawComponentContainerImpl)parent).assertCanAddDrawComponent(drawComponent);

		long duration = System.currentTimeMillis() - start;
		if (logger.isDebugEnabled())
			logger.debug("assertCanAddDrawComponent: took " + duration + " msec");
		else if (duration > 100)
			logger.warn("assertCanAddDrawComponent: took longer than 100 msec: duration = " + duration + " msec");;
	}

	protected void primAddDrawComponent(DrawComponent drawComponent, int index) {
		if (index < 0)
			throw new IllegalArgumentException("Param index " + index + " must not be < 0");

		assertCanAddDrawComponent(drawComponent);

		if (index > getLastIndex())
			primGetDrawComponents().add(drawComponent);
		else
			primGetDrawComponents().add(index, drawComponent);

		drawComponent.setParent(this);
		getRoot().registerDrawComponent(drawComponent);
		drawComponent.setRenderModeManager(getRoot().getRenderModeManager());
		clearBoundsAndNotifyParent();
	}

	protected int getLastIndex() {
		if (getDrawComponents().size() == 0)
			return 0;

		return getDrawComponents().size() - 1;
	}

	public void addDrawComponents(Collection<DrawComponent> drawComponents) {
		if (drawComponents == null)
			throw new IllegalArgumentException("Param drawComponents must NOT be null!");

		addDrawComponents(drawComponents, getLastIndex() + 1);
	}

	public void addDrawComponents(Collection<DrawComponent> drawComponents, int index) {
		if (drawComponents == null)
			throw new IllegalArgumentException("Param drawComponents must NOT be null!");

		List<DrawComponent> oldDrawComponents = new ArrayList<DrawComponent>(getDrawComponents());
		primAddDrawComponents(drawComponents, index);
		firePropertyChange(CHILD_ADDED, oldDrawComponents, getDrawComponents());
	}

	protected void primAddDrawComponents(Collection<DrawComponent> drawComponents, int index) {
		for (DrawComponent drawComponent : drawComponents) {
			primAddDrawComponent(drawComponent, index);
			index++;
		}
	}

	/**
	 * removes the given DrawComponent at the given index from the container and
	 * fires a PropertyChange with the PropertyName
	 * {@link DrawComponentContainer#CHILD_REMOVED}
	 *
	 * This Method unregisters the drawComponent from the RootDrawComponent
	 * and removes it from the drawComponents-List
	 *
	 * always use this Method to remove a DrawComponent, never remove it directly
	 * from the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param drawComponent
	 *          the DrawComponent to remove
	 * @see RootDrawComponent#unregisterDrawComponent(DrawComponent
	 *      drawComponent)
	 * @see DrawComponentContainer#removeDrawComponent(DrawComponent)
	 */
	public void removeDrawComponent(DrawComponent drawComponent) {
		List<DrawComponent> oldDrawComponents = new ArrayList<DrawComponent>(getDrawComponents());
		primRemoveDrawComponent(drawComponent);
		firePropertyChange(CHILD_REMOVED, oldDrawComponents, drawComponents);
	}

	protected void primRemoveDrawComponent(DrawComponent drawComponent) {
		getRoot().unregisterDrawComponent(drawComponent);
		primGetDrawComponents().remove(drawComponent);
		clearBoundsAndNotifyParent();
	}

	public void removeDrawComponents(Collection<DrawComponent> drawComponents) {
		List<DrawComponent> oldDrawComponents = new ArrayList<DrawComponent>(getDrawComponents());
		primRemoveDrawComponents(drawComponents);
		firePropertyChange(CHILD_REMOVED, oldDrawComponents, drawComponents);
	}

	protected void primRemoveDrawComponents(Collection<DrawComponent> drawComponents) {
		for (DrawComponent drawComponent : drawComponents) {
			primRemoveDrawComponent(drawComponent);
		}
	}

	/**
	 * removes the given DrawComponent at the given index from the container and
	 * fires a PropertyChange with the PropertyName
	 * {@link DrawComponentContainer#CHILD_REMOVED}
	 *
	 * This Method unregisters the drawComponent from the RootDrawComponent
	 * and removes it from the drawComponents-List
	 *
	 * always use this Method to remove a DrawComponent, never remove it directly
	 * from the drawComponents-List, because this skips the registration in the
	 * RootDrawComponent
	 *
	 * @param index
	 *          the index to remove
	 * @see RootDrawComponent#unregisterDrawComponent(long)
	 * @see DrawComponentContainer#removeDrawComponent(int)
	 */
	public void removeDrawComponent(int index) {
		List<DrawComponent> oldDrawComponents = new ArrayList<DrawComponent>(getDrawComponents());
		primRemoveDrawComponent(index);
		firePropertyChange(CHILD_REMOVED, oldDrawComponents, drawComponents);
	}

	protected void primRemoveDrawComponent(int index) {
		DrawComponent dc = getDrawComponents().get(index);
		long idToRemove = dc.getId();
		getRoot().unregisterDrawComponent(idToRemove);
		getDrawComponents().remove(index);
		clearBoundsAndNotifyParent();
	}

	/**
	 * @return the RootDrawComponent which is the Root Component for all
	 *         DrawComponents
	 * @see DrawComponent#getRoot()
	 */
	@Override
	public RootDrawComponent getRoot() {
		DrawComponentContainer parent = getParent();
		if (parent == null) {
			if (this instanceof RootDrawComponent)
				return (RootDrawComponent) this;
			else
				throw new IllegalStateException(
						"Member parent may not be null for DrawComponentContainer "
								+ this.toString());
		}

		if (parent instanceof RootDrawComponent) {
			return (RootDrawComponent) parent;
		}

		while (!(parent instanceof RootDrawComponent)) {
			parent = parent.getParent();
		}

		return (RootDrawComponent) parent;
	}

	/**
	 * @return the Bounds which corresponds to the bounds of all contained
	 *         children bounds
	 * @see DrawComponent#getBounds()
	 */
	@Override
	public Rectangle getBounds() {
		if (bounds != null) {
			return bounds;
		}
		else {
			Rectangle oldBounds = bounds;

			int minX = Integer.MAX_VALUE;
			int minY = Integer.MAX_VALUE;
			int maxX = Integer.MIN_VALUE;
			int maxY = Integer.MIN_VALUE;

			if (getDrawComponents().isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("getBounds() returned EMPTY bounds for DrawComponentContainer "+this);
				}
				return new Rectangle(0, 0, 0, 0);
			}

			for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
				DrawComponent dc = it.next();
				Rectangle bounds = dc.getBounds();
				minX = Math.min((int) bounds.getMinX(), minX);
				minY = Math.min((int) bounds.getMinY(), minY);
				maxX = Math.max((int) bounds.getMaxX(), maxX);
				maxY = Math.max((int) bounds.getMaxY(), maxY);
			}
			bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
			x = minX;
			y = minY;
			width = maxX - minX;
			height = maxY - minY;

			firePropertyChange(PROP_BOUNDS, oldBounds, bounds);
			return bounds;
		}
	}

	@Override
	public void setRenderModeManager(RenderModeManager man) {
		super.setRenderModeManager(man);
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent dc = it.next();
			dc.setRenderModeManager(getRenderModeManager());
		}
	}

	@Override
	public void setRenderMode(String mode) {
		super.setRenderMode(mode);
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent dc = it.next();
			dc.setRenderMode(mode);
		}
	}

	@Override
	public Class<? extends DrawComponent> getRenderModeClass() {
		return DrawComponentContainer.class;
	}

	public void notifyChildTransform(DrawComponent child) {
		clearBounds();
		// logger.debug("DrawComponent changed DrawComponentContainer BOUNDS!");
	}

	@Override
	public String getTypeName() {
		return "DrawComponentContainer";
	}

	/**
	 * The implementation of this method in {@link DrawComponentContainerImpl}
	 * returns always <code>true</code>. Override it in order to declare what your subclass
	 * accepts.
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 */
	public boolean canContainDrawComponent(Class<? extends DrawComponent> classOrInterface) {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends DrawComponent> Collection<T> findDrawComponents(Class<T> type) {
		List<T> res = new ArrayList<T>();

		if (drawComponents != null) {
			for (Iterator<DrawComponent> it = drawComponents.iterator(); it.hasNext();) {
				DrawComponent dc = it.next();
				DrawComponentContainer dcc = dc instanceof DrawComponentContainer ? (DrawComponentContainer) dc : null;

				if (type.isInstance(dc))
					res.add((T) dc);

				if (dcc != null && dcc.canContainDrawComponent(type))
					res.addAll(dcc.findDrawComponents(type));
			} // for (Iterator it = drawComponents.iterator(); it.hasNext(); ) {
		} // if (drawComponents != null) {

		return Collections.unmodifiableCollection(res);
	}

	@Override
	public void transform(AffineTransform at, boolean fromParent) {
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent dc = it.next();
			dc.transform(at, fromParent);
		}
		super.transform(at, fromParent);
	}

	@Override
	public Object clone(DrawComponentContainer parent)
	{
		DrawComponentContainerImpl container = (DrawComponentContainerImpl) super.clone(parent);
		container.drawComponents = new ArrayList<DrawComponent>(getDrawComponents().size());

		// clone all children and add them to the drawComponents-List
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent child = it.next();
			child.clone(container); // the child-clone registers itself in the given container
		}
		return container;
	}

	@Override
	public void setRotationCenterPosition(RotationCenterPositions rotationCenterPosition) {
		super.setRotationCenterPosition(rotationCenterPosition);
		for (DrawComponent drawComponent : getDrawComponents()) {
			drawComponent.setRotationCenterPosition(rotationCenterPosition);
		}
	}

	@Override
	protected Renderer initDefaultRenderer() {
		Renderer r = new BaseRenderer();
		r.addRenderContext(new J2DBaseRenderer());
		return r;
	}

	@Override
	protected void primSetRotation(double newRotation) {
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent dc = it.next();
			double degreesToRotate = EditorModelUtil.calcDiffRotation(newRotation, getRotation());
			dc.setRotationMember(degreesToRotate + dc.getRotation());
		}
		super.primSetRotation(newRotation);
	}

	@Override
	public void dispose() {
		super.dispose();
		for (DrawComponent dc : drawComponents) {
			dc.dispose();
		}
	}

	@Override
	protected void primSetTemplate(boolean template) {
		for (Iterator<DrawComponent> it = getDrawComponents().iterator(); it.hasNext();) {
			DrawComponent dc = it.next();
			dc.setTemplate(template);
		}
		super.primSetTemplate(template);

		for (int i = 0; i < templateExcludeClasses.length; i++) {
			Class<? extends DrawComponent> c = templateExcludeClasses[i];
			if (c.isAssignableFrom(this.getClass())) {
				super.primSetTemplate(false);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected transient Class<? extends DrawComponent>[] templateExcludeClasses = new Class[] {
			RootDrawComponent.class, PageDrawComponent.class, Layer.class
	};

	@Override
	public void clearBounds(boolean renew) {
		for (DrawComponent child : getDrawComponents()) {
			child.clearBounds(renew);
		}
		super.clearBounds(renew);
	}

	@Override
	public void resolutionChanged(Resolution oldResolution, Resolution newResolution)
	{
		for (DrawComponent child : primGetDrawComponents()) {
			child.resolutionChanged(oldResolution, newResolution);
		}
		clearBounds(true);
	}
}
