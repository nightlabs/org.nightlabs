/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 23.02.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.custom;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;


public class EditorZoomComboContributionItem 
//extends ZoomComboContributionItem 
extends ContributionItem
implements ZoomListener
{
  protected Combo combo;
  protected String[] initStrings;
  protected ToolItem toolitem;
  protected ZoomManager zoomManager;
  protected IPartService service;
  protected IPartListener partListener;
  
  public double[] zoomLevels = {.01, .05, .1, .25, .5, .75, 1.0, 1.5, 2.0, 2.5, 3, 4, 5};
  
  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   */
  public EditorZoomComboContributionItem(IPartService partService) 
  {
  	this(partService, "8888%");//$NON-NLS-1$
  }

  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   * @param initString the initial string displayed in the combo
   */
  public EditorZoomComboContributionItem(IPartService partService, String initString) 
  {
  	this(partService, new String[] {initString});
  }
  
  /**
   * Constructor for ComboToolItem.
   * @param partService used to add a PartListener
   * @param initStrings the initial string displayed in the combo
   */
  public EditorZoomComboContributionItem(IPartService partService, String[] initStrings) 
  {
  	super(GEFActionConstants.ZOOM_TOOLBAR_WIDGET);
//    super(partService, initStrings);
  	this.initStrings = initStrings;
  	service = partService;
  	Assert.isNotNull(partService);
  	partService.addPartListener(partListener = new IPartListener() {
  		public void partActivated(IWorkbenchPart part) 
  		{
  		  Object adapter = part.getAdapter(ZoomManager.class);
  		  if (adapter != null && adapter instanceof ZoomManager) 
  		  {
    			setZoomManager((ZoomManager)adapter);  
    			getZoomManager().setZoomLevels(zoomLevels);
//    			getZoomManager().setZoomAnimationStyle(ZoomManager.ANIMATE_ZOOM_IN_OUT);
    			refresh(true);  		    
  		  }
  		}
  		public void partBroughtToTop(IWorkbenchPart p) { }
  		public void partClosed(IWorkbenchPart p) { }
  		public void partDeactivated(IWorkbenchPart p) { }
  		public void partOpened(IWorkbenchPart p) { }
  	});
  }
  
  protected void refresh(boolean repopulateCombo) 
  {
  	if (combo == null || combo.isDisposed())
  		return;
  	//$TODO GTK workaround
  	try {
  		if (zoomManager == null) {
  			combo.setEnabled(false);
  			combo.setText(""); //$NON-NLS-1$
  		} else {
  			if (repopulateCombo) {
  				combo.setItems(getZoomManager().getZoomLevelsAsText());
  			}
  			String zoom = getZoomManager().getZoomAsText();
  			int index = combo.indexOf(zoom);
  			if (index != -1)
  				combo.select(index);
  			else
  				combo.setText(zoom);
  			combo.setEnabled(true);
  		}
  	} catch (SWTException exception) {
  		if (!SWT.getPlatform().equals("gtk")) //$NON-NLS-1$
  			throw exception;
  	}
  }
  
  /**
   * Returns the zoomManager.
   * @return ZoomManager
   */
  public ZoomManager getZoomManager() 
  {
  	return zoomManager;
  }

  /**
   * Sets the ZoomManager
   * @param zm The ZoomManager
   */
  public void setZoomManager(ZoomManager zm) 
  {
  	if (zoomManager == zm)
  		return;
  	if (zoomManager != null)
  		zoomManager.removeZoomListener(this);

  	zoomManager = zm;
  	refresh(true);

  	if (zoomManager != null)
  		zoomManager.addZoomListener(this);
  }
  
  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(SelectionEvent)
   */
  private void handleWidgetDefaultSelected(SelectionEvent event) {
  	handleWidgetSelected(event);
  }

  /**
   * @see org.eclipse.swt.events.SelectionListener#widgetSelected(SelectionEvent)
   */
  private void handleWidgetSelected(SelectionEvent event) {
  	// TODO: GTK Workaround (fixed in 3.0) - see SWT bug #44345
  	if (zoomManager != null)
  		if (combo.getSelectionIndex() >= 0)
  			zoomManager.setZoomAsText(combo.getItem(combo.getSelectionIndex()));
  		else 
  		{
  			zoomManager.setZoomAsText(combo.getText());  		  
  		}
  	/*
  	 * There are several cases where invoking setZoomAsText (above) will not result in
  	 * zoomChanged being fired (the method below), such as when the user types "asdf" as
  	 * the zoom level and hits enter, or when they type in 1%, which is below the minimum
  	 * limit, and the current zoom is already at the minimum level.  Hence, there is no 
  	 * guarantee that refresh() will always be invoked.  But we need to invoke it to clear
  	 * out the invalid text and show the current zoom level.  Hence, an (often redundant)
  	 * invocation to refresh() is made below.
  	 */
  	refresh(false);
  }

  /**
   * @see ZoomListener#zoomChanged(double)
   */
  public void zoomChanged(double zoom) {
  	refresh(false);
  }
  
  /**
   * Computes the width required by control
   * @param control The control to compute width
   * @return int The width required
   */
  protected int computeWidth(Control control) {
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
  protected Control createControl(Composite parent) {
  	combo = new Combo(parent, SWT.DROP_DOWN);
  	combo.addSelectionListener(new SelectionListener() {
  		public void widgetSelected(SelectionEvent e) {
  			handleWidgetSelected(e);
  		}
  		public void widgetDefaultSelected(SelectionEvent e) {
  			handleWidgetDefaultSelected(e);
  		}
  	});
  	combo.addFocusListener(new FocusListener() {
  		public void focusGained(FocusEvent e) {
  			// do nothing
  		}
  		public void focusLost(FocusEvent e) {
  			refresh(false);
  		}
  	});
  	
  	// Initialize width of combo
  	combo.setItems(initStrings);
  	toolitem.setWidth(computeWidth(combo));
  	refresh(true);
  	return combo;
  }

  /**
   * @see org.eclipse.jface.action.ContributionItem#dispose()
   */
  public void dispose() {
  	if (partListener == null)
  		return;
  	service.removePartListener(partListener);
  	if (zoomManager != null) {
  		zoomManager.removeZoomListener(this);
  		zoomManager = null;
  	}
  	combo = null;
  	partListener = null;
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
  public void fill(ToolBar parent, int index) {
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
}
