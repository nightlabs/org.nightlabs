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

package org.nightlabs.editor2d;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.palette.PaletteRoot;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.edit.GraphicalEditPartFactory;
import org.nightlabs.editor2d.edit.tree.TreePartFactory;
import org.nightlabs.editor2d.outline.filter.FilterNameProvider;
import org.nightlabs.editor2d.util.ModelUtil;


public class Editor  
extends AbstractEditor
{
  protected EditPartFactory editPartFactory;
  public EditPartFactory getEditPartFactory() 
  {
    if (editPartFactory == null)
      editPartFactory = new GraphicalEditPartFactory();
    
    return editPartFactory;
  }  
    
  protected EditPartFactory outlineEditPartFactory;
  public EditPartFactory getOutlineEditPartFactory() 
  {
    if (outlineEditPartFactory == null)
      outlineEditPartFactory = new TreePartFactory(getFilterManager());
    
    return outlineEditPartFactory;
  }   
  
  protected PaletteRoot palette; 
  public PaletteRoot getPaletteRoot() 
  {
    if (palette == null)
      palette = EditorPaletteFactory.createPalette();

    return palette;
  }  
  
  protected ContextMenuProvider contextMenuProvider;
  public ContextMenuProvider getContextMenuProvider() 
  {
    if (contextMenuProvider == null)
      return new EditorContextMenuProvider(getGraphicalViewer(), getActionRegistry());
    
    return contextMenuProvider;
  }
      
  protected FilterNameProvider getFilterNameProvider() 
  {
		return new ModelUtil();
	}

	public MultiLayerDrawComponent createMultiLayerDrawComponent() 
  {
    MultiLayerDrawComponent mldc = Editor2DFactory.eINSTANCE.createMultiLayerDrawComponent();
    return mldc;
  }
}
  
