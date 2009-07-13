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

package org.nightlabs.editor2d.render;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.render.j2d.J2DBaseRenderer;
import org.nightlabs.editor2d.render.j2d.J2DImageDefaultRenderer;
import org.nightlabs.editor2d.render.j2d.J2DPageRenderer;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;
import org.nightlabs.editor2d.render.j2d.J2DShapeDefaultRenderer;


public class RenderModeManager
{
	//	/**
	//	 * LOG4J logger used by this class
	//	 */
	//	private static final Logger logger = Logger.getLogger(RenderModeManager.class);

	public static final String DEFAULT_MODE = "default";
	public static final String ROLLOVER_MODE = "rollover";

	public RenderModeManager() {
		super();
		initModes();
	}

	/**
	 *
	 * initiates the standard render modes, which are always available
	 */
	protected void initModes()
	{
		if (mode2Class2Renderer == null)
			mode2Class2Renderer = new HashMap<String, Map<String, Renderer>>();

		Renderer r = addRenderer(DEFAULT_MODE, ShapeDrawComponent.class.getName());
		r.addRenderContext(new J2DShapeDefaultRenderer());
		r = addRenderer(DEFAULT_MODE, ImageDrawComponent.class.getName());
		r.addRenderContext(new J2DImageDefaultRenderer());
		r = addRenderer(DEFAULT_MODE, DrawComponentContainer.class.getName());
		r.addRenderContext(new J2DBaseRenderer());
		r = addRenderer(DEFAULT_MODE, PageDrawComponent.class.getName());
		r.addRenderContext(new J2DPageRenderer());

	}

	private Map<String, Map<String, Renderer>> mode2Class2Renderer = null;

	/**
	 * adds / registers a new renderMode, for the given Class with the
	 * and returns the renderer
	 *
	 * @param mode the renderMode to add
	 * @param dcClass the DrawComponent Class to link the Renderer with
	 * @param r the Renderer for the given Mode
	 *
	 */
	//  protected Renderer addRenderer(String mode, Class dcClass)
	protected Renderer addRenderer(String mode, String dcClass)
	{
		if (dcClass == null)
			throw new IllegalArgumentException("Param dcClass must not be null!");

		//  	Map<Class, Renderer> class2Renderer = mode2Class2Renderer.get(mode);
		Map<String, Renderer> class2Renderer = mode2Class2Renderer.get(mode);
		if (class2Renderer == null) {
			//  		class2Renderer = new HashMap<Class, Renderer>();
			class2Renderer = new HashMap<String, Renderer>();
			mode2Class2Renderer.put(mode, class2Renderer);
		}
		Renderer renderer = class2Renderer.get(dcClass);
		if (renderer == null) {
			renderer = new BaseRenderer();
			class2Renderer.put(dcClass, renderer);
		}
		return renderer;
	}

	/**
	 *
	 * @param mode the RenderMode
	 * @param dcClass the DrawComponent Class
	 * @return the Renderer for the given class and Render Mode
	 * if no special Renderer is registered for the given class, the
	 * defaultRenderer for this class is taken.
	 */
	//  public Renderer getRenderer(String mode, Class dcClass)
	public Renderer getRenderer(String mode, String dcClass)
	{
		if (mode2Class2Renderer.containsKey(mode)) {
			//      Map<Class, Renderer> class2Renderer = mode2Class2Renderer.get(mode);
			Map<String, Renderer> class2Renderer = mode2Class2Renderer.get(mode);
			Renderer r = class2Renderer.get(dcClass);
			if (r != null)
				return r;
			else {
				r = checkIfRendererForSuperInterfaceExists(class2Renderer, dcClass);
				if (r != null)
					return r;
			}
		}
		return getDefaultRenderer(dcClass);
	}

	protected Renderer checkIfRendererForSuperInterfaceExists(
			//  		Map<Class, Renderer> class2Renderer, Class dcClass)
			Map<String, Renderer> class2Renderer, String dcClassString)
	{
		try {
			Class<?> dcClass = Class.forName(dcClassString);
			// first check for interfaces
			Class<?>[] interfaces = dcClass.getInterfaces();
			for (Class<?> clazz : interfaces) {
				if (clazz.isAssignableFrom(dcClass)) {
					Renderer r = class2Renderer.get(clazz.getName());
					if (r != null)
						return r;
					else
						continue;
				}
			}
			// afterwards check for superClasses
			Class<?> superClass = dcClass.getSuperclass();
			while (superClass != null && !superClass.equals(Object.class)) {
				// Check if a registered Class is a maybe superclass of the given class
				//      	for (Iterator<Class> it = class2Renderer.keySet().iterator(); it.hasNext(); ) {
				for (Iterator<String> it = class2Renderer.keySet().iterator(); it.hasNext(); ) {
					String className = it.next();
					Class<?> c = Class.forName(className);
					if (c.isAssignableFrom(dcClass)) {
						Renderer r = class2Renderer.get(c.getName());
						if (r != null)
							return r;
					}
				}
				superClass = superClass.getSuperclass();
			}
		} catch (Exception e) {
			// do nothing
		}
		return null;
	}

	/**
	 * @return the registered Renderer for the given Class with the
	 * RenderMode DEFAULT_MODE, which should be always available
	 */
	//  public Renderer getDefaultRenderer(Class dcClass)
	public Renderer getDefaultRenderer(String dcClass)
	{
		if (mode2Class2Renderer.containsKey(DEFAULT_MODE))
		{
			//      Map<Class, Renderer> class2Renderer = mode2Class2Renderer.get(DEFAULT_MODE);
			Map<String, Renderer> class2Renderer = mode2Class2Renderer.get(DEFAULT_MODE);
			if (class2Renderer != null) {
				Renderer r = class2Renderer.get(dcClass);
				if (r != null)
					return r;
				r = checkIfRendererForSuperInterfaceExists(class2Renderer, dcClass);
				if (r != null)
					return r;
			}
		}
		return null;
	}

	private String currentRenderMode = DEFAULT_MODE;

	/**
	 *
	 * @return the current (last set) render Mode
	 */
	public String getCurrentRenderMode() {
		return currentRenderMode;
	}

	/**
	 *
	 * @param currentRenderMode the RenderMode to set to currentRenderMode
	 */
	public void setCurrentRenderMode(String currentRenderMode) {
		this.currentRenderMode = currentRenderMode;
		fireRenderModeChanged();
	}

	private Set<RenderModeListener> renderModeListeners = new HashSet<RenderModeListener>();
	//	protected Set<RenderModeListener> getRenderModeListeners()
	//	{
	//		if (renderModeListeners == null)
	//			renderModeListeners = new HashSet<RenderModeListener>();
	//
	//		return renderModeListeners;
	//	}

	/**
	 *
	 * @param rml the RenderModeListener to add
	 * @see org.nightlabs.editor2d.render.RenderModeListener
	 */
	public void addRenderModeListener(RenderModeListener rml)
	{
		renderModeListeners.add(rml);
	}

	/**
	 *
	 * @param rml the RenderModeListener to remove
	 * @see org.nightlabs.editor2d.render.RenderModeListener
	 */
	public void removeRenderModeListener(RenderModeListener rml)
	{
		renderModeListeners.remove(rml);
	}

	protected void fireRenderModeChanged()
	{
		for (Iterator<RenderModeListener> it = renderModeListeners.iterator(); it.hasNext(); ) {
			RenderModeListener rml = it.next();
			rml.renderModeChanges(getCurrentRenderMode());
		}
	}

	private Set<RenderContextListener> renderContextListeners = null;
	protected Set<RenderContextListener> getRenderContextListeners()
	{
		if (renderContextListeners == null)
			renderContextListeners = new HashSet<RenderContextListener>();

		return renderContextListeners;
	}

	public void addRenderContextListener(RenderContextListener rcl) {
		getRenderContextListeners().add(rcl);
	}

	public void removeRenderContextListener(RenderContextListener rcl) {
		getRenderContextListeners().remove(rcl);
	}

	protected void fireRenderContextChanged()
	{
		for (Iterator<RenderContextListener> it = getRenderContextListeners().iterator(); it.hasNext(); ) {
			RenderContextListener rcl = it.next();
			rcl.renderContextChanged(currentRenderContextType);
		}
	}

	/**
	 *
	 * @return all registered Render-Modes
	 */
	public Set<String> getRenderModes()
	{
		return mode2Class2Renderer.keySet();
	}

	private Map<String, RenderModeDescriptor> renderMode2Descriptor = null;
	protected Map<String, RenderModeDescriptor> getRenderMode2Descriptor()
	{
		if (renderMode2Descriptor == null)
			renderMode2Descriptor = new HashMap<String, RenderModeDescriptor>();

		return renderMode2Descriptor;
	}

	/**
	 *
	 * @param desc The RenderModeDescriptor for the RenderMode to register
	 * @param c the DrawComponent Class to link the Renderer with
	 * @param r the Renderer for the given RenderMode
	 *
	 * @see org.nightlabs.editor2d.render.Renderer
	 */
	//	public Renderer addRenderModeDescriptor(RenderModeDescriptor desc, Class c)
	public Renderer addRenderModeDescriptor(RenderModeDescriptor desc, String c)
	{
		if (desc == null)
			throw new IllegalArgumentException("Param desc must not be null");

		Renderer renderer = addRenderer(desc.getRenderMode(), c);
		getRenderMode2Descriptor().put(desc.getRenderMode(), desc);
		return renderer;
	}

	/**
	 *
	 * @param renderMode the RenderMode
	 * @return the RenderModeDescriptor for the given renderMode if it was registered,
	 * return null if no RenderModeDescriptor is registered
	 */
	public RenderModeDescriptor getRenderModeDescriptor(String renderMode)
	{
		return getRenderMode2Descriptor().get(renderMode);
	}

	/**
	 * @param renderMode the renderMode to get all Renderers for
	 * @return a Collection with all Renderers for the given renderMode
	 */
	public Collection<Renderer> getRenderers(String renderMode)
	{
		Set<String> classes = getRenderClasses();
		Collection<Renderer> renderer = new ArrayList<Renderer>(classes.size());
		for (Iterator<String> it = classes.iterator(); it.hasNext(); ) {
			String c = it.next();
			Renderer r = getRenderer(renderMode, c);
			renderer.add(r);
		}
		return renderer;
	}

	public Set<String> getRenderClasses()
	{
		Set<String> renderClasses = new HashSet<String>();
		for (Iterator<Map.Entry<String, Map<String, Renderer>>> it = mode2Class2Renderer.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Map<String, Renderer>> entry = it.next();
			Map<String, Renderer> class2Renderer = entry.getValue();
			renderClasses.addAll(class2Renderer.keySet());
		}
		return renderClasses;
	}

	public Collection<Renderer> getCurrentRenderers() {
		//		Map<Class, Renderer> renderers = mode2Class2Renderer.get(currentRenderMode);
		Map<String, Renderer> renderers = mode2Class2Renderer.get(currentRenderMode);
		if (renderers != null)
			return renderers.values();
		return Collections.emptySet();
	}

	private String currentRenderContextType = J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D;
	public String getCurrentRenderContextType() {
		return currentRenderContextType;
	}

	public void setCurrentRenderContextType(String renderContextType)
	{
		this.currentRenderContextType = renderContextType;
		for (Iterator<Map.Entry<String, Map<String, Renderer>>> it = mode2Class2Renderer.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Map<String, Renderer>> modeEntry = it.next();
			Map<String, Renderer> class2Renderer = modeEntry.getValue();
			for (Iterator<Map.Entry<String, Renderer>> itClass = class2Renderer.entrySet().iterator(); itClass.hasNext(); ) {
				Map.Entry<String, Renderer> classEntry = itClass.next();
				Renderer renderer = classEntry.getValue();
				//				RenderContext rc = renderer.getRenderContext(currentRenderContextType);
				renderer.getRenderContext(currentRenderContextType);
			}
		}
		fireRenderContextChanged();
	}

}
