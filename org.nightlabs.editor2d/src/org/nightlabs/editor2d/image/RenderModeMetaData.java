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
package org.nightlabs.editor2d.image;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.util.Util;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class RenderModeMetaData
implements Cloneable // , Serializable
{
	private String rendererDelegateClassName;
	private transient Class<?> rendererDelegateClass = null;
	private transient ImageRendererDelegate rendererDelegate = null;

	/**
	 *
	 * @return the Class with the name of {@link RenderModeMetaData#getRendererDelegateClassName()}
	 * @throws ClassNotFoundException if the no class with the name of {@link RenderModeMetaData#getRendererDelegateClassName()}
	 * could be found
	 *
	 */
	public Class<?> getRendererDelegateClass()
	throws ClassNotFoundException
	{
		if (rendererDelegateClass == null) {
			rendererDelegateClass = Class.forName(rendererDelegateClassName);
		}
		return rendererDelegateClass;
	}

	/**
	 *
	 * @return the {@link ImageRendererDelegate} for rendering
	 */
	public ImageRendererDelegate getRendererDelegate()
	throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (rendererDelegate == null) {
			rendererDelegate = (ImageRendererDelegate) getRendererDelegateClass().newInstance();
		}
		return rendererDelegate;
	}

	/**
	 *
	 * @return the name of the renderDelegateClass
	 */
	public String getRendererDelegateClassName() {
		return rendererDelegateClassName;
	}

	/**
	 *
	 * @param rendererDelegateClassName the name of the {@link ImageRendererDelegate}-Class to set
	 */
	public void setRendererDelegateClassName(String rendererDelegateClassName) {
		this.rendererDelegateClassName = rendererDelegateClassName;
		this.rendererDelegateClass = null;
		this.rendererDelegate = null;
		this.renderModeMetaDataKey = null;
	}

	/**
	 *
	 * @param rendererDelegateClass the {@link Class} fo the {@link ImageRendererDelegate} to set
	 */
	public void setRendererDelegateClass(Class<? extends ImageRendererDelegate> rendererDelegateClass) {
		this.rendererDelegateClass = rendererDelegateClass;
		this.rendererDelegateClassName = rendererDelegateClass.getName();
		this.rendererDelegate = null;
		this.renderModeMetaDataKey = null;
	}

	private Map<String, Object> parameters = null;
	/**
	 *
	 * @return a Map which contains all necessary parameters
	 */
	public Map<String, Object> getParameters()
	{
		if (parameters == null) {
			parameters = new HashMap<String, Object>();
		}
		return parameters;
	}

	public void setParameters(Map<String, Object> params) {
		this.parameters = params;
		this.renderModeMetaDataKey = null;
	}

	private Set<String> supportedRenderModes = new HashSet<String>();
	/**
	 *
	 * @return a {@link Set} which contains all supported renderModes
	 */
	public Set<String> getSupportedRenderModes() {
		return supportedRenderModes;
	}
	/**
	 *
	 * @param renderModes the supported renderModes to set
	 */
	public void setSupportedRenderModes(Set<String> renderModes) {
		this.supportedRenderModes = renderModes;
		this.renderModeMetaDataKey = null;
	}

	private String id = null;
	/**
	 * returns the unique id of the {@link RenderModeMetaData} to identify it
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * sets the unique id of the RenderModeMetaData,
	 * if you create another {@link RenderModeMetaData} with the same id,
	 * and adds {@link ImageDrawComponent#addRenderModeMetaData(RenderModeMetaData)} it to an
	 * {@link ImageDrawComponent} the previously added will be removed
	 *
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
		this.renderModeMetaDataKey = null;
	}

	/**
	 * creates a RenderModeMetaData
	 *
	 * @param id the unique id, if you create another {@link RenderModeMetaData} with the same id,
	 * and adds {@link ImageDrawComponent#addRenderModeMetaData(RenderModeMetaData)} it to an
	 * {@link ImageDrawComponent} the previously added will be removed
	 * @param supportedRenderModes a {@link Set} of supported RenderModes for this RenderModeMetaData
	 * @param renderDelegateClassName the name of the {@link ImageRendererDelegate}-Class, which will be initiated when necessary
	 * @param parameters a {@link Map} of all necessary parameters for the {@link ImageRendererDelegate}
	 * @param defaultMode determines if this RenderModeMetaData is a defaultMode, which should not be serialized or not
	 */
	public RenderModeMetaData(String id, Set<String> supportedRenderModes,
			String renderDelegateClassName, Map<String, Object> parameters, boolean defaultMode)
	{
		this.id = id;
		this.supportedRenderModes = supportedRenderModes;
		this.rendererDelegateClassName = renderDelegateClassName;
		this.parameters = parameters;
		this.defaultMode = defaultMode;
	}

	@Override
	/**
	 * clones the RenderModeMetaData
	 * @return a deep clone of the RenderModeMetaData
	 */
	public Object clone()
	{
//		RenderModeMetaData clone = new RenderModeMetaData(getId(), getSupportedRenderModes(),
//				getRendererDelegateClassName(), getParameters(), defaultMode);

		RenderModeMetaData clone;
		try {
			clone = (RenderModeMetaData) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e); // should never happen.
		}
		clone.renderModeMetaDataKey = null;

		if (this.parameters != null)
			clone.parameters = new HashMap<String, Object>(this.parameters); // we don't currently need a deep copy of the objects in the map (it's currently only Strings in both key + value)

		return clone;
	}

	private boolean defaultMode = false;
	/**
	 *
	 * @return if this RenderModeMetaData is a defaultMode which is used for every {@link ImageDrawComponent}
	 * and should therefore be serialized or not
	 */
	public boolean isDefaultMode() {
		return defaultMode;
	}
	/**
	 *
	 * @param defaultMode determines if this RenderModeMetaData is a defaultMode
	 * which is used for every {@link ImageDrawComponent}
	 * and should therefore be serialized or not
	 */
	public void setDefaultMode(boolean defaultMode) {
		this.defaultMode = defaultMode;
		this.renderModeMetaDataKey = null;
	}

	private transient String renderModeMetaDataKey;

	/**
	 * Get a unique hash of this instance which takes all relevant properties
	 * into account. Unlike <code>hashCode()</code>, it is never dependent on objects' memory
	 * addresses (thus stable over multiple sessions).
	 * It is guaranteed that 2 instances of this class have the same unique hash, if they lead
	 * to the same way of rendering.
	 *
	 * @return
	 */
	public String getRenderModeMetaDataKey()
	{
		if (renderModeMetaDataKey == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(rendererDelegateClassName);

			if (parameters != null && !parameters.isEmpty()) {
				sb.append("-");
				LinkedList<Object> paramList = new LinkedList<Object>();
				SortedSet<String> sortedKeys = new TreeSet<String>(parameters.keySet());
				for (String key : sortedKeys) {
					paramList.add(key);
					paramList.add(parameters.get(key));
				}
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				try {
					ObjectOutputStream objOut = new ObjectOutputStream(out);
					objOut.writeObject(paramList);
					objOut.close();
					sb.append(
							Util.encodeHexStr(Util.hash(out.toByteArray(), Util.HASH_ALGORITHM_MD5))
					);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			renderModeMetaDataKey = sb.toString();
		}
		return renderModeMetaDataKey;
	}
}
