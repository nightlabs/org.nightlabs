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
package org.nightlabs.editor2d.properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.dialog.ConvertImageDialog;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ImageColorConversionPropertyCellEditor 
extends DialogCellEditor 
{

	/**
	 * @param imageDC the ImageDrawComponent to edit
	 * @param parent the parent composite
	 */
	public ImageColorConversionPropertyCellEditor(ImageDrawComponent imageDC, Composite parent) {
		super(parent);
		this.imageDC = imageDC;		
	}

	/**
	 * @param imageDC the ImageDrawComponent to edit
	 * @param parent the parent composite
	 * @param style the style
	 */
	public ImageColorConversionPropertyCellEditor(ImageDrawComponent imageDC, Composite parent, int style) {
		super(parent, style);
		this.imageDC = imageDC;
	}

	protected ConvertImageDialog imageDialog = null;
	protected ImageDrawComponent imageDC = null;
	
	/**
	 * @see org.eclipse.jface.viewers.DialogCellEditor#openDialogBox(org.eclipse.swt.widgets.Control)
	 */
	@Override
	protected Object openDialogBox(Control cellEditorWindow) 
	{
		imageDialog = new ConvertImageDialog(cellEditorWindow.getShell(), imageDC.getOriginalImage());
		int returnCode = imageDialog.open();
		if (returnCode == Dialog.OK) {
			return imageDialog.getConvertImageComposite().getRenderModeMetaDatas();
		}
		return null;
	}

}
