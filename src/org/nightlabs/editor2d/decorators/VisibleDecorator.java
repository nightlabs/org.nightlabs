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
package org.nightlabs.editor2d.decorators;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.edit.tree.DrawComponentTreeEditPart;
import org.nightlabs.editor2d.edit.tree.VisibleCompositeImage;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class VisibleDecorator 
extends LabelProvider 
implements ILightweightLabelDecorator 
{

	public VisibleDecorator() {

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) 
	{
		if(element instanceof DrawComponentTreeEditPart) 
		{	
			// TODO: manually update the image of the DrawComponentTreeEditPart and call refresh
			ImageDescriptor invisibleImage = SharedImages.getSharedImageDescriptor(
					EditorPlugin.getDefault(), 
					VisibleCompositeImage.class, "", ImageDimension._8x8, ImageFormat.gif);
			
			decoration.addOverlay(invisibleImage);
//			decoration.addPrefix("[Sample]");
			decoration.addSuffix("{invisible}");
		}
	}

	public static VisibleDecorator getVisibleDecorator()
	{
		IDecoratorManager decoratorManager = WorkbenchPlugin.getDefault().getWorkbench().getDecoratorManager();
  
	  if (decoratorManager.getEnabled("org.nightlabs.editor2d.decorators.VisibleDecorator")) {
	  	return (VisibleDecorator) decoratorManager.getLightweightLabelDecorator("org.nightlabs.editor2d.decorators.VisibleDecorator");
	  }
	  return null;
	}
	
	public void refresh(Object part) {
		this.fireLabelProviderChanged(new LabelProviderChangedEvent(this,part));
	}

}
