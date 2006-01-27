/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import org.nightlabs.base.NLBasePlugin;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;

/**
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class RCPUtil
{
	static {
		Display.getDefault().addFilter(SWT.KeyDown, new Listener() {
			public void handleEvent(Event event)
			{
				switch (event.keyCode) {
					case SWT.CTRL:
						keyDown_Ctrl = true;
						break;
					case SWT.SHIFT:
						keyDown_Shift = true;
						break;
				}
			}
		});
		Display.getDefault().addFilter(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event)
			{
				switch (event.keyCode) {
					case SWT.CTRL:
						keyDown_Ctrl = false;
						break;
					case SWT.SHIFT:
						keyDown_Shift = false;
						break;
				}
			}
		});
	}

	public static final int KEY_CTRL = SWT.CTRL;
	public static final int KEY_SHIFT = SWT.CTRL;

	private static boolean keyDown_Ctrl = false;
	private static boolean keyDown_Shift = false;

	public static boolean isKeyDown(int key)
	{
		switch (key) {
			case SWT.CTRL:
				return keyDown_Ctrl;
			case SWT.SHIFT:
				return keyDown_Shift;
			default:
				throw new IllegalArgumentException("Unknown key! Use one of the KEY_* constants!");
		}
	}

	/**
	 * Recursively sets the enabled flag for the given Composite and all its children.
	 * 
	 * @param comp The parent control
	 * @param enabled The enabled flag to set
	 */
	public static void setControlEnabledRecursive(Composite comp, boolean enabled) {
		comp.setEnabled(enabled);
		Control[] children = comp.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Composite)
				setControlEnabledRecursive((Composite)children[i], enabled);
		}
	}

	/**
	 * Sets the given button selected if it is contained in the given parent Composite,
	 * and deselects all other buttons in this Composite
	 * 
	 * @param parent the parent Composite
	 * @param button a Button with style Param SWT.TOGGLE or SWT.RADIO which should be selected
	 */
	public static void setButtonSelected(Composite parent, Button button) 
	{
	  button.setSelection(true);
	  Control[] children = parent.getChildren();
	  for (int i = 0; i < children.length; i++) {
	    if (!children[i].equals(button)) {
	      if ( (((children[i].getStyle() & SWT.TOGGLE) != 0) || ((children[i].getStyle() & SWT.RADIO) != 0))  
	          && (children[i] instanceof Button) ) 
	      {
	        ((Button)children[i]).setSelection(false);
	      }
	    } 
	  }
	}
	
	/**
	 * Returns wether the ViewPart with the given id is currently visble in
	 * one of the pages of the active Workbench window. Will also return 
	 * true when the page-book containing this view is minimized.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static boolean isViewVisible(String viewID) {
//		IWorkbenchPage[] pages = Workbench.getInstance().getActiveWorkbenchWindow().getPages();
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart part = pages[i].findView(viewID);
			if (part != null) {
				return isPartVisible(part);
			}
		}
		return false;
	}
	
	/**
	 * Show/Hide all ViewActions of the given View.
	 * 
	 * @param view The View which ViewActions should be shown/hidden
	 * @param visible true to show all Actions, fals to hide them 
	 */
	public static void setViewActionsVisible(IViewPart view, boolean visible) {
		IToolBarManager toolBarManager = view.getViewSite().getActionBars().getToolBarManager();
		IMenuManager menuManager = view.getViewSite().getActionBars().getMenuManager();
		IContributionItem[] tItems = toolBarManager.getItems();
		for (int i = 0; i < tItems.length; i++) {
			tItems[i].setVisible(visible);
			tItems[i].update();
		}
		IContributionItem[] mItems = menuManager.getItems();
		for (int i = 0; i < mItems.length; i++) {
			mItems[i].setVisible(visible);
			mItems[i].update();
		}
		toolBarManager.update(true);
		menuManager.update(true);
	}

	/**
	 * Returns wether the given IWorkbenchPart is currently visble in
	 * one of the pages of the active Workbench window. Will also return 
	 * true when the page-book containing this view is minimized.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static boolean isPartVisible(IWorkbenchPart part) {
		boolean visible = false;
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			if (part != null)
				if (pages[i].isPartVisible(part)){
					visible = true;
				}
		}		
		return visible;
	}
	
	/**
	 * Shows the view with the given viewID in all workbench-pages
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static IWorkbenchPart showView(String viewID) {
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart view = null;
			try { view = pages[0].showView(viewID); } catch (PartInitException e) { throw new RuntimeException(e); }
			if (view != null) 
				return view;
		}
		return null;
	}
	
	/**
	 * Shows the view with the given viewID and 
	 * gives it focus.
	 * 
	 * @param viewID The id of the view to be queried
	 * @return Wether the view is visible
	 */
	public static IWorkbenchPart activateView(String viewID) {
		IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
		for (int i = 0; i < pages.length; i++) {
			IWorkbenchPart view = null;
			try { view = pages[0].showView(viewID); } catch (PartInitException e) { throw new RuntimeException(e); }
			if (view != null) {
				pages[0].activate(view);
				return view;
			}
		}
		return null;
	}

	/**
	 * @deprecated Use {@link #getActiveWorkbenchShell()} instead!
	 */
	public static Shell getWorkbenchShell() {
		return getActiveWorkbenchShell();
	}

	/**
	 * Returns the active WorkbenchWindow's shell.
	 * @return The active WorkbenchWindow's shell.
	 */
	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getShell();
	}

	/**
	 * Returns the active WorkbenchWindow's active page.
	 * @return The active WorkbenchWindow's active page.
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	/**
	 * @deprecated Use {@link #getActiveWorkbenchPage()} instead!
	 */
	public static IWorkbenchPage getWorkbenchPage() {
		return getActiveWorkbenchPage();
	}

	/**
	 * Returns the active WorkbenchWindow's active page.
	 * @return The active WorkbenchWindow's active page.
	 */
	public static IWorkbenchPage getActiveWorkbenchPage() {
		IWorkbenchWindow window = getActiveWorkbenchWindow();
		return window == null ? null : window.getActivePage();
	}

	public static int showErrorDialog(String message, int buttonStyle) 
	{
		MessageBox errorDialog = new MessageBox(getWorkbenchShell(), SWT.ICON_ERROR | buttonStyle);
		errorDialog.setMessage(message);
		errorDialog.setText(NLBasePlugin.getResourceString("action.openfile.error.title"));
		return errorDialog.open();				
	}
	
	public static int showErrorDialog(String message) 
	{
		return showErrorDialog(message, SWT.OK);
	}	
	
	public static int showConfirmOverwriteDialog(String fileName) 
	{
		MessageBox errorDialog = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_INFORMATION | SWT.OK | SWT.CANCEL);
		errorDialog.setMessage(
				NLBasePlugin.getResourceString("action.openfile.confirmoverwrite.message1")
				+ " " + fileName + " " +
				NLBasePlugin.getResourceString("action.openfile.confirmoverwrite.message2"));
		errorDialog.setText(NLBasePlugin.getResourceString("action.openfile.confirmoverwrite.title"));
		return errorDialog.open();	  	
	}
	
	public static void disposeAllChildren(Composite comp) 
	{
		if (comp != null) 
		{
			if (!comp.isDisposed()) {
				Control[] children = comp.getChildren();
				for (int i=0; i<children.length; i++) {
					Control c = children[i];
					c.dispose();
				}				
			}
		}
	}
  
	/**
	 * Opens an editor with the given input and editorID and returns it.
	 * 
	 * @param input The editors input
	 * @param editorID The editors id 
	 * @return The editor opened
	 * @throws PartInitException
	 */
	public static IEditorPart openEditor(IEditorInput input, String editorID) 
	throws PartInitException 
	{
		return getActiveWorkbenchPage().openEditor(input, editorID);
	}
  
	/**
	 * Finds the editor for the given input in the workbench's 
	 * active workbenchpage. Returns null if no editor for
	 * the given input was found.
	 *
	 * @param input The input for which to search a currently open editor. 
	 */
	public static IEditorPart findEditor(IEditorInput input) {
		return getActiveWorkbenchPage().findEditor(input);
	}

	/**
	 * Finds the view with the given id in the workbench's
	 * active workbenchpage. Returns null if no view for the
	 * given viewID was found.
	 *
	 * @param viewID The id of the view.
	 */
	public static IViewPart findView(String viewID)
	{
		return getActiveWorkbenchPage().findView(viewID);
	}

	/**
	 * 
	 * @param bounds the bounds of a Control
	 * @return the Point which determines the Location so that the given Bounds are
	 * centered on the screen 
	 */
	public static Point getCenterPosition(Rectangle bounds) 
	{
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = 0;
		int y = 0;
		if (bounds.width < screenSize.getWidth())
			x = (((int)screenSize.getWidth()) - bounds.width) / 2; 
		if (bounds.height < screenSize.getHeight())
			y = (((int)screenSize.getHeight()) - bounds.height) / 2;
		
		return new Point(x,y);
	}

	/**
	 * Adds all known perspectives as perspective shortcuts to the given
	 * layout.
	 */
	public static void addAllPerspectiveShortcuts(IPageLayout layout)
	{
		IPerspectiveDescriptor[] perspectives = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives();
		for (int i = 0; i < perspectives.length; i++)
			layout.addPerspectiveShortcut(perspectives[i].getId());
//		IConfigurationElement[] configPerspectives =  Platform.getExtensionRegistry().getConfigurationElementsFor("org.eclipse.ui.perspectives");
//		for(int i = 0; i < configPerspectives.length; i++)
//			layout.addPerspectiveShortcut(configPerspectives[i].getAttribute("id"));
	}
	
	/**
	 * 
	 * @param layout the GridLayout to apply the given LayoutMode to 
	 * @param layoutMode the LayoutMode to apply
	 * @see LayoutMode
	 * @return the given GridLayout with the applied LayoutMode
	 */
	public static GridLayout applyLayout(GridLayout layout, LayoutMode layoutMode) 
	{
		if (layoutMode == LayoutMode.TIGHT_WRAPPER) 
		{
			layout.horizontalSpacing = 0;
			layout.verticalSpacing = 0;
			layout.marginHeight = 0;
			layout.marginWidth = 0;

			layout.marginLeft = 0;
			layout.marginRight = 0;
			layout.marginTop = 0;
			layout.marginBottom = 0;
		}
		return layout;
	}
	
	/**
	 *  
	 * @param comp the Composite to set the Form Border for
	 * @see FormToolkit#paintBordersFor(Composite)
	 */
	public static void setFormBorder(Composite comp) 
	{
		comp.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
	}
}
