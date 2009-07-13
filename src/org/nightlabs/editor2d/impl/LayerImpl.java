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

import java.util.Locale;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.util.NLLocale;

public class LayerImpl
extends DrawComponentContainerImpl
implements Layer
{
	private static final long serialVersionUID = 1L;

	public LayerImpl() {
		super();
	}
 
  @Override
	public String getTypeName()
  {
  	// TODO hack to avoid english name when loading files, becuase nameProvider is then not set yet
  	if (NLLocale.getDefault().getLanguage().equals(Locale.GERMAN.getLanguage()))
  			return "Ebene";
  	
  	return "Layer";
  }
  
  // does noting to avoid bounds calculation each time something is transformed in the layer
	@Override
	public void notifyChildTransform(DrawComponent child)
	{
		
	}
	
	@Override
	public Object clone(DrawComponentContainer parent)
	{
		LayerImpl layer = (LayerImpl) super.clone(parent);
		return layer;
	}
	
//	public DrawComponent clone()
//	{
//		Layer layer = new LayerImpl();
//		layer = (Layer) assign(layer);
//		layer.setVisible(isVisible());
//		layer.setEditable(isEditable());
//		return layer;
//	}
		
} //LayerImpl
