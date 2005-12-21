package org.nightlabs.base.action;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public abstract class AbstractContributionItem 
extends XContributionItem 
{
	public AbstractContributionItem() 
	{
		super();
		init();
	}

	public AbstractContributionItem(boolean fillToolBar, boolean fillCoolBar,
			boolean fillMenuBar, boolean fillComposite) 
	{
		super();
		init();
		this.fillCoolBar = fillCoolBar;
		this.fillToolBar = fillToolBar;
		this.fillMenuBar = fillMenuBar;
		this.fillComposite = fillComposite;
	}
		
	protected abstract Control createControl(Composite parent);
	protected abstract String initName(); 
	protected abstract String initID();
	
	protected boolean fillToolBar = true;
	protected boolean fillCoolBar = true;
	protected boolean fillMenuBar = true;
	protected boolean fillComposite = true;
	
	protected boolean toolBarFilled = false;
	protected boolean coolBarFilled = false;
	protected boolean compositeFilled = false;
	
	protected String name = null;
	public String getName() {
		return name;
	}
	
	protected void init() 
	{
		name = initName();
		setId(initID());
	}
		
	protected void setSize() 
	{
		if (fillToolBar && toolBarFilled) 
			getToolItem().setWidth(computeWidth(getControl()));
		
		if (fillCoolBar && coolBarFilled)
			getCoolItem().setSize(computeWidth(getControl()), computeHeight(getControl()));
		
		if (fillComposite && compositeFilled)
			getControl().setSize(computeWidth(getControl()), computeHeight(getControl()));
	}
	
  protected int computeWidth(Control control) 
  {
  	int width = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
  	return width;
  }  	
	
  protected int computeHeight(Control control) 
  {
  	int height = control.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).y;
  	return height;
  }    
  
	public void fill(Composite parent) 
	{
		if (fillComposite) {
			control = createControl(parent);
			compositeFilled = true;
			setSize();
		}		
	}

	protected Control control = null;
	public Control getControl() {
		return control;
	}
	
	public void fill(CoolBar parent, int index) 
	{
		if (fillCoolBar) {
			CoolItem coolItem = new CoolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
			coolItem.setControl(control);
			coolBarFilled = true;
			setSize();
		}
	}

	protected CoolItem coolItem = null;
	public CoolItem getCoolItem() {
		return coolItem;
	}
	
	public void fill(Menu menu, int index) 
	{
		if (fillMenuBar) {
			menuItem = new MenuItem(menu, SWT.NONE);
			menuItem.setText(name);			
		}
	}

	public MenuItem menuItem = null;
	protected MenuItem getMenuItem() {
		return menuItem;
	}
	
	public void fill(ToolBar parent, int index) 
	{
		if (fillToolBar) {
			toolItem = new ToolItem(parent, SWT.SEPARATOR, index);
			control = createControl(parent);
	  	toolItem.setControl(control);
	  	toolBarFilled = true;
	  	setSize();
		}
	}
	
	protected ToolItem toolItem = null;
	public ToolItem getToolItem() {
		return toolItem;
	}

}
