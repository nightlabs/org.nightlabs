/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.base.property.ComboBoxLabelProvider;

public class RotationPropertyDescriptor 
//extends ComboBoxPropertyDescriptor 
extends PropertyDescriptor
{
  public static final String[] defaultRotations = new String[] {"-180", "-90", "-45", "0", "45", "90", "180"};
  
  public RotationPropertyDescriptor(Object id, String displayName) 
  {
    super(id, displayName);
  }

  /**
   * The <code>ComboBoxPropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method creates and returns a new
   * <code>ComboBoxCellEditor</code>.
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new RotationCellEditor(parent, defaultRotations, SWT.NONE);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }    
  
  public ILabelProvider getLabelProvider() 
  {
    if (isLabelProviderSet())
      return super.getLabelProvider();
    else
      return new ComboBoxLabelProvider(defaultRotations);
  }  
}
