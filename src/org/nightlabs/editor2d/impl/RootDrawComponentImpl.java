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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.NameProvider;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.render.RenderModeListener;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;
import org.nightlabs.util.CollectionUtil;
import org.nightlabs.util.NLLocale;

public class RootDrawComponentImpl
extends DrawComponentContainerImpl
implements RootDrawComponent
{
	private static final long serialVersionUID = 1L;
//	private static final Logger logger = Logger.getLogger(RootDrawComponentImpl.class);

	private boolean gridEnabled = GRID_ENABLED_DEFAULT;
	private boolean rulersEnabled = RULERS_ENABLED_DEFAULT;
	private boolean snapToGeometry = SNAP_TO_GEOMETRY_DEFAULT;
	private double zoom = ZOOM_DEFAULT;
	private long lastID = LAST_ID_DEFAULT;
	//  private transient Map<Class, List<DrawComponent>> class2DrawComponents = null;
//	private transient Map<Class<? extends DrawComponent>, List<DrawComponent>> class2DrawComponents = null;
	private EditorRuler leftRuler = null;
	private EditorRuler topRuler = null;

	public RootDrawComponentImpl()
	{
		// TODO: check if this is unnecessary
		setRenderModeManager(new RenderModeManager());
	}

	@Override
	public Layer getCurrentLayer() {
		return currentPage.getCurrentLayer();
	}

	@Override
	public void setCurrentLayer(Layer layer)
	{
		currentPage.setCurrentLayer(layer);
	}

	private Resolution resolution = new ResolutionImpl(IResolutionUnit.dpiUnit, 72);

	@Override
	public Resolution getResolution() {
		return resolution;
	}

	protected void primSetResolution(Resolution resolution)
	{
		Resolution oldResolution = this.resolution;
		this.resolution = resolution;
		modelUnit = null;
		// TODO uncomment once working
//		resolutionChanged(oldResolution, resolution);
	}

	@Override
	public void setResolution(Resolution resolution)
	{
		Resolution oldResolution = this.resolution;
		primSetResolution(resolution);
		firePropertyChange(PROP_RESOLUTION, oldResolution, resolution);
	}

	private DotUnit modelUnit = null;

	@Override
	public DotUnit getModelUnit() {
		if (modelUnit == null) {
			modelUnit = new DotUnit(getResolution());
		}
		return modelUnit;
	}

	private PageDrawComponent currentPage = null;

	@Override
	public PageDrawComponent getCurrentPage() {
		return currentPage;
	}

	@Override
	public void setCurrentPage(PageDrawComponent page)
	{
		PageDrawComponent oldPage = currentPage;
		currentPage = page;
		firePropertyChange(PROP_CURRENT_PAGE, oldPage, currentPage);
	}

	@Override
	public boolean isGridEnabled() {
		return gridEnabled;
	}

	@Override
	public void setGridEnabled(boolean newGridEnabled) {
		gridEnabled = newGridEnabled;
	}

	@Override
	public boolean isRulersEnabled() {
		return rulersEnabled;
	}

	@Override
	public void setRulersEnabled(boolean newRulersEnabled) {
		rulersEnabled = newRulersEnabled;
	}

	@Override
	public boolean isSnapToGeometry() {
		return snapToGeometry;
	}

	@Override
	public void setSnapToGeometry(boolean newSnapToGeometry) {
		snapToGeometry = newSnapToGeometry;
	}

	@Override
	public double getZoom() {
		return zoom;
	}

	@Override
	public void setZoom(double newZoom) {
		zoom = newZoom;
	}

	@Override
	public EditorRuler getLeftRuler() {
		return leftRuler;
	}

	@Override
	public void setLeftRuler(EditorRuler newLeftRuler) {
		leftRuler = newLeftRuler;
	}

	@Override
	public EditorRuler getTopRuler() {
		return topRuler;
	}

	@Override
	public void setTopRuler(EditorRuler newTopRuler) {
		topRuler = newTopRuler;
	}

	@Override
	public long getLastID() {
		return lastID;
	}

	@Override
	public void setLastID(long newLastID) {
		lastID = newLastID;
	}

	//	public Map<Class<? extends DrawComponent>, List<? extends DrawComponent>> getClass2DrawComponents()

//	/**
//	 * This <code>Map</code> serves as a cache for the {@link #getClass2DrawComponents()} method in order
//	 * to quickly know all extended classes and implemented interfaces of any concrete class that was already
//	 * processed. This way, for every concrete class the hierarchy needs to be processed only once.
//	 *
//	 * @see #getClassesAndInterfaces(Class)
//	 */
//	private static Map<Class<?>, Set<Class<?>>> concreteClass2ClassesAndInterfaces = new HashMap<Class<?>, Set<Class<?>>>();
//
//	/**
//	 * Get all classes that are extended by a given class including the
//	 * given class itself as well as all interfaces implemented by all of these classes.
//	 * An instance of the given <code>concreteClass</code> can thus be casted into
//	 * every one of the returned classes/interfaces.
//	 *
//	 * @param concreteClass the class for which to get the hierarchy.
//	 * @return all extended classes and implemented interfaces.
//	 */
//	protected static Set<Class<?>> getClassesAndInterfaces(Class<?> concreteClass)
//	{
//		synchronized (concreteClass2ClassesAndInterfaces) {
//			Set<Class<?>> res = concreteClass2ClassesAndInterfaces.get(concreteClass);
//			if (res == null) {
//				Class<?> c = concreteClass;
//				while (c != null) {
//					res.add(c);
//					for (Class<?> iface : c.getInterfaces())
//						res.add(iface);
//
//					c = c.getSuperclass();
//				}
//				res = Collections.unmodifiableSet(res);
//				concreteClass2ClassesAndInterfaces.put(concreteClass, res);
//			}
//			return res;
//		}
//	}

//	@Override
//	public Map<Class<? extends DrawComponent>, List<DrawComponent>> getClass2DrawComponents()
//	{
//		if (class2DrawComponents == null) {
//			Map<Class<? extends DrawComponent>, List<DrawComponent>> c2dc = new HashMap<Class<? extends DrawComponent>, List<DrawComponent>>();
//
//
//			class2DrawComponents =
//		}
//
//		return class2DrawComponents;
//	}

	//	public void setClass2DrawComponents(Map<Class, List<DrawComponent>> newClass2DrawComponents) {
	//		class2DrawComponents = newClass2DrawComponents;
	//	}
	//	public <T extends DrawComponent> void setClass2DrawComponents(Map<Class<T>, List<T>> newClass2DrawComponents) {
	//		class2DrawComponents = newClass2DrawComponents;
	//	}

	protected Map<Long, DrawComponent> getId2DrawComponent()
	{
		if (id2DrawComponent == null) {
			Map<Long, DrawComponent> id2dc = new HashMap<Long, DrawComponent>();

			// The id2DrawComponent is transient and therefore is null
			// after e.g. Util.cloneSerializable(...). Additionally, the clone()
			// method below sets it to null in the clone. Thus we need to re-index
			// all contents.
			// Marco.

			id2dc.put(this.getId(), this);
			populateId2DrawComponentMap(id2dc, getDrawComponents());
			id2DrawComponent = id2dc;
		}

		return id2DrawComponent;
	}

	private static void populateId2DrawComponentMap(Map<Long, DrawComponent> map, Collection<DrawComponent> drawComponents)
	{
		for (DrawComponent drawComponent : drawComponents) {
			Long drawComponentId = drawComponent.getId();
			if (!map.containsKey(drawComponentId)) {
				map.put(drawComponentId, drawComponent);

				if (drawComponent instanceof DrawComponentContainer)
					populateId2DrawComponentMap(map, ((DrawComponentContainer)drawComponent).getDrawComponents());
			}
		}
	}

//	@Override
//	public void setId2DrawComponent(Map<Long, DrawComponent> newId2DrawComponent) {
//		id2DrawComponent = newId2DrawComponent;
//	}

	@Override
	public long nextID() {
		return ++lastID;
	}

	@Override
	public DrawComponent getDrawComponent(long id) {
		return getId2DrawComponent().get(id);
	}

	@Override
	public void registerDrawComponent(DrawComponent drawComponent)
	{
		if (drawComponent == null)
			throw new IllegalArgumentException("Param drawComponent must not be null!");

		// only count up ID if the drawComponent have no ID yet
		if (drawComponent.getId() == DrawComponent.ID_DEFAULT)
			drawComponent.setId(nextID());

		Class<? extends DrawComponent> dcClass = drawComponent.getClass();

		if (!getId2DrawComponent().containsKey(drawComponent.getId())) {
			// if cache fields are initialized, update them (rather than evicting these fields)
			if (drawComponentClasses != null)
				drawComponentClasses.add(dcClass);

			if (findDrawComponentsCacheMap != null) {
				for (Map.Entry<Class<?>, Collection<DrawComponent>> me : findDrawComponentsCacheMap.entrySet()) {
					Class<?> clazz = me.getKey();
					if (clazz.isAssignableFrom(dcClass))
						me.getValue().add(drawComponent);
				}
			}

			// and register the new drawComponent in our main map (id => drawComponentInstance)
			getId2DrawComponent().put(drawComponent.getId(), drawComponent);
		} // if (!getId2DrawComponent().containsKey(drawComponent.getId())) {

//		List<DrawComponent> typeList = null;
//		if (!getClass2DrawComponents().containsKey(dcClass))
//		{
//			typeList = new ArrayList<DrawComponent>();
//			typeList.add(drawComponent);
//			getClass2DrawComponents().put(dcClass, typeList);
//			firePropertyChange(TYPE_ADDED, null, dcClass);
//		}
//		else
//		{
//			typeList = getClass2DrawComponents().get(dcClass);
//			if (!typeList.contains(drawComponent))
//				typeList.add(drawComponent);
//		}

		if (drawComponent.getName() == "") {
			Collection<? extends DrawComponent> typeList = findDrawComponents(dcClass);
			// TODO this auto-naming strategy might cause duplicates when elements have been deleted, because in this case, the size of the typeList has shrunk. Need better strategy! Marco.

			if (getNameProvider() != null)
				drawComponent.setName(getNameProvider().getTypeName(dcClass)+ " " + typeList.size());
			else
				drawComponent.setName(drawComponent.getTypeName() + " " + typeList.size());
		}

		drawComponent.setRenderMode(getRenderModeManager().getCurrentRenderMode());
	}

	@Override
	public void unregisterDrawComponent(DrawComponent drawComponent)
	{
		if (drawComponent == null)
			throw new IllegalArgumentException("Param drawComponent must not be null");

		unregisterDrawComponent(drawComponent.getId());
	}

	@Override
	public void unregisterDrawComponent(long id)
	{
		DrawComponent dc = getId2DrawComponent().remove(id);
		if (dc == null)
			throw new IllegalArgumentException("there is no drawComponent registered with the id "+id);

		if (findDrawComponentsCacheMap != null) {
			Class<? extends DrawComponent> dcClass = dc.getClass();

			for (Map.Entry<Class<?>, Collection<DrawComponent>> me : findDrawComponentsCacheMap.entrySet()) {
				Class<?> clazz = me.getKey();
				if (clazz.isAssignableFrom(dcClass))
					me.getValue().remove(dc);
			}
		}

		// We don't easily know whether it was the last instance of this type, hence we simply invalidate the cache (clear the field).
		// I don't think it is necessary to update the cache intelligently. But if the performance is slow, we might later add
		// reference counting (convert the Set to a Map<Class<? extends DrawComponent>, Long> drawComponentClass2ReferenceCount).
		// Marco.
		drawComponentClasses = null;

//		Class<? extends DrawComponent> dcClass = dc.getClass();
//		List<DrawComponent> typeCollection = getClass2DrawComponents().get(dcClass);
//		if (typeCollection != null)
//			typeCollection.remove(dc);
//		else
//			logger.warn("No type collection registered for Class "+dcClass);
	}

	@Override
	public String toString()
	{
		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (gridEnabled: ");
		result.append(gridEnabled);
		result.append(", rulersEnabled: ");
		result.append(rulersEnabled);
		result.append(", snapToGeometry: ");
		result.append(snapToGeometry);
		result.append(", zoom: ");
		result.append(zoom);
		result.append(", lastID: ");
		result.append(lastID);
//		result.append(", class2DrawComponents: ");
//		result.append(class2DrawComponents);
//		result.append(", id2DrawComponent: ");
//		result.append(id2DrawComponent);
		result.append(')');
		return result.toString();
	}

	private transient Map<Long, DrawComponent> id2DrawComponent = null;

//	@SuppressWarnings("unchecked")
//	public <T extends DrawComponent> List<T> getDrawComponents(Class<T> type)	{
//		List<T> dcs = (List<T>) getClass2DrawComponents().get(type);
//		if (dcs != null) {
//			return Collections.unmodifiableList(dcs);
//		}
//
//		// FIXME this doesn't put anything back into the map? what's the policy of the class2DrawComponents map? shouldn't it put the generated list into it?
//		// hmmm... looks more like a fallback, in case the type isn't managed by the map at all
//		// in this case, it probably shouldn't be put into the Map, because it would never be updated
//		// once it has been added. Ask Daniel!
//		// Marco.
//
//		// TODO after some discussion with Daniel, we came to the conclusion that probably
//		// some code from the filter(s) should be moved here. additionally, this
//		// might better be implemented as is below but with the policy that the map is
//		// a lazily initialized cache. i.e. a new object being added must clear the
//		// appropriate cache parts (or even the complete cache).
//		dcs = new ArrayList<T>();
//		for (Map.Entry<Class<? extends DrawComponent>, List<DrawComponent>> entry : getClass2DrawComponents().entrySet()) {
//			Class<? extends DrawComponent> clazz = entry.getKey();
//			//			if (clazz.isAssignableFrom(type)) {
//			if (type.isAssignableFrom(clazz)) {
//				dcs.addAll((List<T>)entry.getValue());
//			}
//		}
//
//		return Collections.unmodifiableList(dcs);
//	}

	private transient RenderModeListener renderModeListener = createRenderModeListener();

	private RenderModeListener createRenderModeListener() {
		return new RenderModeListener() {
			public void renderModeChanges(String renderMode)
			{
				setRenderMode(renderMode);
			}
		};
	}

	@Override
	public void setRenderModeManager(RenderModeManager man)
	{
		if (renderModeManager != null)
			renderModeManager.removeRenderModeListener(renderModeListener);

		this.renderModeManager = man;
		renderModeManager.addRenderModeListener(renderModeListener);
		super.setRenderModeManager(man);
	}

	/**
	 * initialize all transient fields which require not default initialization
	 * @see Serializable
	 */
	private void readObject(java.io.ObjectInputStream in)
	throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		if (renderModeManager == null)
			renderModeManager = new RenderModeManager();

		if (renderModeListener == null)
			renderModeListener = createRenderModeListener();

		if (id2DrawComponent == null) {
			id2DrawComponent = new HashMap<Long, DrawComponent>();
			populateId2DrawComponentMapRecursively(this);
		}
	}

	private void populateId2DrawComponentMapRecursively(DrawComponent drawComponent)
	{
		id2DrawComponent.put(drawComponent.getId(), drawComponent);

		if (drawComponent instanceof DrawComponentContainer)
			for (DrawComponent child : ((DrawComponentContainer)drawComponent).getDrawComponents())
				populateId2DrawComponentMapRecursively(child);
	}

	@Override
	public void dispose()
	{
		super.dispose();
		findDrawComponentsCacheMap = null;
		id2DrawComponent = null;
		if (renderModeManager != null)
			renderModeManager.removeRenderModeListener(renderModeListener);
		leftRuler = null;
		topRuler = null;
	}

	/**
	 * the languageID used when for the name {@link I18nText}s when adding new drawComponents
	 * by default this is <code>NLLocale.getDefault().getLanguage()</code>
	 */
	private transient String languageId = NLLocale.getDefault().getLanguage();

	/**
	 * the languageID is formated like in java.util.Locale (DE, EN, ...)
	 *
	 *@return the current languageID of the RootDrawComponent
	 */
	public String getLanguageID() {
		return languageId;
	}

	/**
	 * sets the languageID and fires a PropertyChange with the PropertyName
	 * {@link RootDrawComponent#PROP_LANGUAGE_ID}
	 *
	 * @param newLanguageId the languageID to set
	 */
	public void setLanguageID(String newLanguageId) {
		String oldLanguageId = languageId;
		this.languageId = newLanguageId;
		firePropertyChange(PROP_LANGUAGE_ID, oldLanguageId, languageId);
	}

	private transient NameProvider nameProvider = null;
	public NameProvider getNameProvider() {
		return nameProvider;
	}

	public void setNameProvider(NameProvider nameProvider) {
		this.nameProvider = nameProvider;
	}

	@Override
	public Object clone(DrawComponentContainer parent) {
		RootDrawComponentImpl clone = (RootDrawComponentImpl) super.clone(parent);
//		clone.class2DrawComponents = null; // index will be rebuilt ... or not - seems to be broken code.
		clone.id2DrawComponent = null;
		clone.findDrawComponentsCacheMap = null;
		clone.drawComponentClasses = null;
		if (this.drawComponentClasses != null)
			clone.drawComponentClasses = new HashSet<Class<? extends DrawComponent>>(this.drawComponentClasses);

		// TODO what to do with the other fields - e.g. the rulers? After a short talk with Daniel, we decided that the rulers should be cloned. We didn't talk about the other fields, yet. Marco.
		return clone;
	}

	private transient Map<Class<?>, Collection<DrawComponent>> findDrawComponentsCacheMap = null;

	@Override
	public <T extends DrawComponent> Collection<T> findDrawComponents(Class<T> type)
	{
		if (findDrawComponentsCacheMap == null)
			findDrawComponentsCacheMap = new HashMap<Class<?>, Collection<DrawComponent>>();

		Collection<T> res = CollectionUtil.castCollection(findDrawComponentsCacheMap.get(type));
		if (res == null) {
			res = new ArrayList<T>(super.findDrawComponents(type)); // returns a read-only collection, but we want to be able to update our cache => copying
			Collection<DrawComponent> cachedRes = CollectionUtil.castCollection(res);
			findDrawComponentsCacheMap.put(type, cachedRes);
		}
		return Collections.unmodifiableCollection(res);
	}

	private transient Set<Class<? extends DrawComponent>> drawComponentClasses = null;

	@Override
	public Set<Class<? extends DrawComponent>> getDrawComponentClasses() {
		if (drawComponentClasses == null) {
			Set<Class<? extends DrawComponent>> s = new HashSet<Class<? extends DrawComponent>>();
			for (DrawComponent drawComponent : getId2DrawComponent().values()) {
				s.add(drawComponent.getClass());
			}
			drawComponentClasses = s;
		}

		return Collections.unmodifiableSet(drawComponentClasses);
	}

	@Override
	public boolean canContainDrawComponent(Class<? extends DrawComponent> classOrInterface)
	{
		// a RootDrawComponent currently does not support to contain other RootDrawComponents
		if (RootDrawComponent.class.isAssignableFrom(classOrInterface))
			return false;

		return true;
	}
}
