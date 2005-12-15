/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 08.11.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.custom;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

import sun.rmi.runtime.GetThreadPoolAction;

import org.nightlabs.editor2d.render.RenderModeDescriptor;
import org.nightlabs.editor2d.render.RenderModeListener;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.rcp.action.XContributionItem;
import org.nightlabs.rcp.custom.ColorCombo;
import org.nightlabs.util.Utils;

public class RenderModeContributionItem 
extends XContributionItem
{
	public static final String ID = RenderModeContributionItem.class.getName();		
	protected String initString = "RenderMode TEST";
	
	public RenderModeContributionItem(IWorkbenchPage page) 
	{
		super(ID);
		this.page = page;
		this.page.addPartListener(partListener);
	}
		
	public RenderModeContributionItem(RenderModeManager renderModeMan) 
	{		
		super(ID);		
		setRenderModeMan(renderModeMan);
	}
	
	protected IPartListener partListener = new IPartListener()
	{
		public void partActivated(IWorkbenchPart part) 
		{
		  Object adapter = part.getAdapter(RenderModeManager.class);
		  if (adapter != null && adapter instanceof RenderModeManager) {
		  	setRenderModeMan((RenderModeManager)adapter);
		  }
		}		
		public void partOpened(IWorkbenchPart part) {
			
		}	
		public void partDeactivated(IWorkbenchPart part) {
			
		}	
		public void partClosed(IWorkbenchPart part) {
			
		}	
		public void partBroughtToTop(IWorkbenchPart part) {
			
		}	
	};	
	
	protected ColorCombo combo;	
	protected ToolItem toolitem;	
	protected IWorkbenchPage page;
	
	protected Map entry2Index = new HashMap();	
	protected Map index2RenderMode = new HashMap();
	
  protected void refresh(boolean repopulateCombo) 
  {
  	if (combo == null || combo.isDisposed())
  		return;
  	
		if (renderModeMan == null) {
			combo.setEnabled(false);
//			combo.setText(initString); //$NON-NLS-1$
		} 
		else {
			if (repopulateCombo) 
			{
				combo.remove(0);
				populateMaps(renderModeMan);
				String[] entries = (String[]) Utils.collection2TypedArray(string2RenderMode.keySet(), String.class);				
				for (int i=0; i<entries.length-1; i++) {
					String entry = entries[i];
					combo.add(null, entry, i);
					entry2Index.put(entry, new Integer(i));
					index2RenderMode.put(new Integer(i), new Integer(getRenderMode(entry)));
				}
			}
			
			int currentRenderMode = renderModeMan.getCurrentRenderMode();
			String entry = getEntry(currentRenderMode);
			combo.select(getIndex(entry));
			combo.setEnabled(true);
		} 
  }	
	
  /**
   * Computes the width required by control
   * @param control The control to compute width
   * @return int The width required
   */
  protected int computeWidth(Control control) 
  {
  	int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
  	// $TODO: Windows workaround - Fixed in Eclipse 3.0 
  	// Combo is not wide enough to show all text - add enough space for another character
  	if (SWT.getPlatform().equals("win32")) //$NON-NLS-1$
  		width += FigureUtilities.getTextWidth("8", control.getFont()); //$NON-NLS-1$
  	return width;
  }  
  
  /**
   * Creates and returns the control for this contribution item
   * under the given parent composite.
   *
   * @param parent the parent composite
   * @return the new control
   */
  protected Control createControl(Composite parent) 
  {
  	combo = new ColorCombo(parent, SWT.DROP_DOWN | SWT.BORDER);
  	combo.addSelectionListener(selectionListener);  			
  	combo.addFocusListener(focusListener);
  	combo.addDisposeListener(disposeListener);
  	
  	// Initialize width of combo
  	combo.add(null, initString, 0);
  	toolitem.setWidth(computeWidth(combo));
  	refresh(true);
  	return combo;
  }  
      
	protected RenderModeManager renderModeMan;		
	public RenderModeManager getRenderModeMan() {
		return renderModeMan;
	}
	public void setRenderModeMan(RenderModeManager rm) 
	{
  	if (renderModeMan == rm)
  		return;
  	if (renderModeMan != null)
  		renderModeMan.removeRenderModeListener(renderModeListener);

  	renderModeMan = rm;
  	refresh(true);

  	if (renderModeMan != null)
  		renderModeMan.addRenderModeListener(renderModeListener);		
	}
	
	 /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method to
   * create a control under the given parent, and then creates
   * a new tool item to hold it.
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
   * 
   * @param parent The ToolBar to add the new control to
   * @param index Index
   */
  public void fill(ToolBar parent, int index) 
  {
  	toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
  	Control control = createControl(parent);
  	toolitem.setControl(control);	
  }  
  
  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method calls the <code>createControl</code> framework method.
   * Subclasses must implement <code>createControl</code> rather than
   * overriding this method.
   * 
   * @param parent The parent of the control to fill
   */
  public final void fill(Composite parent) {
  	createControl(parent);
  }

  /**
   * The control item implementation of this <code>IContributionItem</code>
   * method throws an exception since controls cannot be added to menus.
   * 
   * @param parent The menu
   * @param index Menu index
   */
  public final void fill(Menu parent, int index) {
  	Assert.isTrue(false, "Can't add a control to a menu");//$NON-NLS-1$
  } 
  
  /**
   * @see org.eclipse.jface.action.ContributionItem#dispose()
   */
  public void dispose() 
  {
  	if (partListener == null)
  		return;
  	page.removePartListener(partListener);
  	if (renderModeMan != null) {
  		renderModeMan.removeRenderModeListener(renderModeListener);
  		renderModeMan = null;
  	}
  	if (combo != null) {
    	combo = null;  		
  	}
  	partListener = null;
  }  
  
  protected Map string2RenderMode = new HashMap();
  protected Map renderMode2String = new HashMap();
  
  protected void populateMaps(RenderModeManager rmm) 
  {
  	for (Iterator it = rmm.getRenderModes().iterator(); it.hasNext(); ) 
  	{
  		int renderMode = ((Integer) it.next()).intValue();
  		RenderModeDescriptor desc = rmm.getRenderModeDescriptor(renderMode);
  		String s = null;
  		if (desc != null) {
  			s = desc.getLocalizedText();
  		} else {
  			s = "RenderMode "+renderMode;
  		}
  		string2RenderMode.put(s, new Integer(renderMode));
//  		string2RenderMode.put(new Integer(renderMode), s);
  		renderMode2String.put(new Integer(renderMode), s);
  	}
  }
  
  protected int getRenderMode(String entry) 
  {
  	Integer i = (Integer) string2RenderMode.get(entry);
  	if (i != null)
  		return i.intValue();
  	else
  		return 0;
  }
  
  protected String getEntry(int renderMode) {
  	return (String) renderMode2String.get(new Integer(renderMode));
  }
  
  protected int getIndex(String entry) 
  {
  	Integer i = (Integer) entry2Index.get(entry);
  	if (i != null)
    	return i.intValue();
  	else
  		return 0;	 
  }
  
  protected int getRenderMode(int index) 
  {
  	Integer i = (Integer)index2RenderMode.get(new Integer(index)); 
  	if (i != null)
  		return i.intValue();
  	else 
  		return 1;
  }
  
//	protected List getEntries() {
//		return new ArrayList(getRenderModeMan().getRenderModes());
//	}
//	
//	protected String getText(Object entry) 
//	{
//		RenderModeDescriptor desc = getRenderModeMan().getRenderModeDescriptor(((Integer)entry).intValue());		
//		if (desc != null) {
//			return desc.getLocalizedText();
//		}
//		return "RenderMode "+((Integer)entry).intValue();
//	}
//	
//	protected Image getImage(Object entry) 
//	{
//		RenderModeDescriptor desc = getRenderModeMan().getRenderModeDescriptor(((Integer)entry).intValue());		
//		if (desc != null) {
//			if (desc.getImage() != null) {
//				ImageData imgData = ImageUtil.convertToSWT(desc.getImage());
//				return new Image(null, imgData);				
//			}
//		}
//		return null;
//	}

	protected RenderModeListener renderModeListener = new RenderModeListener()
	{	
		public void renderModeChanges(int renderMode) {
			refresh(false);
		}	
	};
	
	protected SelectionListener selectionListener = new SelectionListener()
	{
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			int index = combo.getSelectionIndex();
			int renderMode = getRenderMode(index);
			renderModeMan.setCurrentRenderMode(renderMode);
		}	
	};
	
	protected FocusListener focusListener = new FocusListener()
	{	
		public void focusLost(FocusEvent e) {
			refresh(false);
		}	
		public void focusGained(FocusEvent e) {
			// do nothing
		}	
	};
	
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
  		combo.removeSelectionListener(selectionListener);
  		combo.removeFocusListener(focusListener);			
		}	
	};
}
