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
import java.awt.geom.Point2D;
import java.util.Locale;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.util.GeomUtil;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.util.NLLocale;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 */
public class PageDrawComponentImpl
extends DrawComponentContainerImpl
implements PageDrawComponent
{
	public PageDrawComponentImpl() { }

	private Layer currentLayer = null;
	public Layer getCurrentLayer() {
		return currentLayer;
	}
	public void setCurrentLayer(Layer newLayer) {
		Layer oldCurrentLayer = currentLayer;
		primSetCurrentLayer(newLayer);
		firePropertyChange(PROP_CURRENT_LAYER, oldCurrentLayer, currentLayer);
	}
	protected void primSetCurrentLayer(Layer layer) {
		this.currentLayer = layer;
	}

	@Override
	public Class<? extends DrawComponent> getRenderModeClass() {
		return PageDrawComponent.class;
	}

	@Override
	public String getTypeName() {
		// TODO hack to avoid english name when loading files, because nameProvider is then not set yet
		if (NLLocale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage()))
			return "Seite";

		return "Page";
	}

	private int orientation = ORIENTATION_VERTICAL;

	@Override
	public int getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(int orientation)
	{
		int oldOrientation = this.orientation;
		this.orientation = orientation;
		Rectangle oldBounds = new Rectangle(getPageBounds());
		pageBounds.width = oldBounds.height;
		pageBounds.height = oldBounds.width;
		firePropertyChange(PROP_ORIENTATION, oldOrientation, orientation);
	}

	private Rectangle pageBounds = new Rectangle();

	@Override
	public Rectangle getPageBounds() {
		return pageBounds;
	}

	@Override
	public void setPageBounds(Rectangle pageBounds) {
		Rectangle oldPageBounds = new Rectangle(pageBounds);
		primSetPageBounds(pageBounds);
		firePropertyChange(PROP_PAGE_BOUNDS, oldPageBounds, pageBounds);
	}

	protected void primSetPageBounds(Rectangle pageBounds) {
		this.pageBounds = pageBounds;
	}

	private boolean showPageBounds = true;

	@Override
	public boolean isShowPageBounds() {
		return showPageBounds;
	}

	@Override
	public void setShowPageBounds(boolean showPageBounds) {
		this.showPageBounds = showPageBounds;
		firePropertyChange(PROP_SHOW_PAGE_BOUNDS, !showPageBounds, showPageBounds);
	}

	@Override
	public boolean canContainDrawComponent(Class<? extends DrawComponent> classOrInterface) {
		// a page cannot contain other pages
		if (PageDrawComponent.class.isAssignableFrom(classOrInterface))
			return false;

		return true;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.editor2d.impl.DrawComponentContainerImpl#resolutionChanged(org.nightlabs.i18n.unit.resolution.Resolution, org.nightlabs.i18n.unit.resolution.Resolution)
	 */
	@Override
	public void resolutionChanged(Resolution oldResolution, Resolution newResolution)
	{
		Point2D scale = getResolutionScale(oldResolution, newResolution);
		pageBounds = GeomUtil.scaleRect(pageBounds, scale.getX(), scale.getY(), false);
		super.resolutionChanged(oldResolution, newResolution);
	}

}
