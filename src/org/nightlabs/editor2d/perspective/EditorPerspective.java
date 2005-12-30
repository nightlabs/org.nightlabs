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

package org.nightlabs.editor2d.perspective;

import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import org.nightlabs.editor2d.views.LayerView;

public class EditorPerspective 
implements IPerspectiveFactory 
{
  public static final String ID_PERSPECTIVE = EditorPerspective.class.getName();
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
   */
  public void createInitialLayout(IPageLayout layout) 
  {    
//    layout.setEditorAreaVisible(true);
//    String editorArea = layout.getEditorArea();    
//    layout.addView(IPageLayout.ID_EDITOR_AREA, IPageLayout.TOP, IPageLayout.RATIO_MAX, IPageLayout.ID_EDITOR_AREA);    
//    layout.addView(IPageLayout.ID_RES_NAV, IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
//    layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
//    layout.addView(IPageLayout.ID_PROP_SHEET, IPageLayout.BOTTOM, 0.25f, IPageLayout.ID_EDITOR_AREA);
//    layout.addView(LayerView.ID_VIEW, IPageLayout.RIGHT, 0.25f, IPageLayout.ID_EDITOR_AREA);
//    layout.addView(PaletteView.ID, IPageLayout.RIGHT, 0.25f, IPageLayout.ID_EDITOR_AREA);
//    layout.addPerspectiveShortcut(ID_PERSPECTIVE);
    
    layout.setEditorAreaVisible(true);
    String editorArea = layout.getEditorArea();
    layout.addView(IPageLayout.ID_EDITOR_AREA, IPageLayout.TOP, IPageLayout.RATIO_MAX, editorArea);
    
    IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.15f, editorArea);
    topLeft.addView(IPageLayout.ID_RES_NAV);
    IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.5f, "topLeft");
    bottomLeft.addView(IPageLayout.ID_OUTLINE);
//    bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
    IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);
    bottom.addView(IPageLayout.ID_PROP_SHEET);
    IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.75f, editorArea);
    topRight.addView(PaletteView.ID);
    IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.5f, "topRight");
    bottomRight.addView(LayerView.ID_VIEW);  
    bottomRight.addView("org.eclipse.gef.ui.stackview.CommandStackInspector");
           
    layout.addPerspectiveShortcut(ID_PERSPECTIVE);  
  }
}
