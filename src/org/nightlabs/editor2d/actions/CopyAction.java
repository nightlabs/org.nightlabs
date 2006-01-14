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
package org.nightlabs.editor2d.actions;

import java.util.List;

import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.swt.SWT;
import org.eclipse.ui.actions.ActionFactory;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class CopyAction 
extends AbstractEditorSelectionAction 
{
//	public static final String ID = CopyAction.class.getName();
	public static final String ID = ActionFactory.COPY.getId();
	
	public static final String PROP_COPY_TO_CLIPBOARD = "Added Content to Clipboard";	
	public static final Object EMPTY_CLIPBOARD_CONTENT = new Object();
	
	/**
	 * @param editor
	 * @param style
	 */
	public CopyAction(AbstractEditor editor, int style) {
		super(editor, style);
	}

	/**
	 * @param editor
	 */
	public CopyAction(AbstractEditor editor) {
		super(editor);
	}

  protected void init() 
  {
  	super.init();
  	setText(EditorPlugin.getResourceString("action.copy.text"));
  	setToolTipText(EditorPlugin.getResourceString("action.copy.tooltip"));
  	setId(ID);
  	setActionDefinitionId(ID);
  	setAccelerator(SWT.CTRL | 'C');
  } 
	
///**
//*@return true, if objects are selected
//*/
//protected boolean calculateEnabled() {
//	return !getSelectedObjects().isEmpty();
//}

	/**
	*@return true, if objects are selected, except the RootEditPart or LayerEditParts
	*/
	protected boolean calculateEnabled() {
		return !getDefaultSelection(false).isEmpty();
	}
	
//	/**
//	 * clones all selected DrawComponents and adds them to the {@link Clipboard} 
//	 *  
//	 */
//	public void run() 
//	{
//		List dcs = getSelection(DrawComponent.class, true);
//		Clipboard clipboard = Clipboard.getDefault();
//		Object oldContent = clipboard.getContents();
//		clipboard.setContents(EMPTY_CLIPBOARD_CONTENT);
//		List clones = new ArrayList(dcs.size());				
//		for (Iterator it = dcs.iterator(); it.hasNext(); ) {
//			DrawComponent dc = (DrawComponent) it.next();
//			DrawComponent clone = dc.clone();
//			clone.setName(clone.getName() + getCopyString());
//			clones.add(clone);
//		}
//		
//		clipboard.setContents(clones);
//		firePropertyChange(PROP_COPY_TO_CLIPBOARD, oldContent, clones);
//	}	

//protected String getCopyString() 
//{
//	return " ("+EditorPlugin.getResourceString("action.clone.text")+")";
//}	
	
	/**
	 * adds all selected DrawComponents to the {@link Clipboard} 
	 *  
	 */
	public void run() 
	{
		List dcs = getSelection(DrawComponent.class, true);
		Clipboard clipboard = Clipboard.getDefault();
		Object oldContent = clipboard.getContents();
		clipboard.setContents(dcs);
		firePropertyChange(PROP_COPY_TO_CLIPBOARD, oldContent, dcs);
	}	
				
}
