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
package org.nightlabs.editor2d;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.nightlabs.editor2d.views.LayerView;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class EditorPerspective 
implements IPerspectiveFactory 
{
	public static final String ID_PERSPECTIVE = EditorPerspective.class.getName();
	
	/**
	 * 
	 */
	public EditorPerspective() {
		super();
	}

	/**
	 * @see org.eclipse.ui.IPerspectiveFactory#createInitialLayout(org.eclipse.ui.IPageLayout)
	 */
	public void createInitialLayout(IPageLayout layout) 
	{
    layout.setEditorAreaVisible(true);
    String editorArea = layout.getEditorArea();
    layout.addView(IPageLayout.ID_EDITOR_AREA, IPageLayout.TOP, IPageLayout.RATIO_MAX, editorArea);
    
    IFolderLayout rightTop = layout.createFolder("rightTop", IPageLayout.RIGHT, 0.75f, editorArea);
    rightTop.addView(IPageLayout.ID_PROP_SHEET);
    IFolderLayout rightMiddle = layout.createFolder("rightMiddle", IPageLayout.BOTTOM, 0.33f, "rightTop");
    rightMiddle.addView(IPageLayout.ID_OUTLINE);    
    IFolderLayout rightBottom = layout.createFolder("rightBottom", IPageLayout.BOTTOM, 0.5f, "rightMiddle");
    rightBottom.addView(LayerView.ID_VIEW);
               
    layout.addPerspectiveShortcut(ID_PERSPECTIVE);      		
	}

}
