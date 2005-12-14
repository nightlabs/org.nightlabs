
/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.property;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import org.nightlabs.util.IAssignable;

public class AssignablePropertyDescriptor 
extends PropertyDescriptor
{
	protected Collection assignables = null;
	
  /**
   * Creates a new property descriptor with the given id and display name for the given Collection
   * of assignables
   * @see IAssignable
   */
  public AssignablePropertyDescriptor(Object id, String displayName, Collection assignables) 
  {
  	super(id, displayName);
  	this.assignables = assignables;
  }
    
  /**
   * @return a <code>AssignableLabelProvider</code> 
   */
  public ILabelProvider getLabelProvider() 
  {
  	return new AssignableLabelProvider(assignables);
  }    
  
  /**
   * The <code>AssignablePropertyDescriptor</code> implementation of this 
   * <code>IPropertyDescriptor</code> method creates and returns a new
   * <code>AssignableCellEditor</code>.
   * <p>
   * The editor is configured with the current validator if there is one.
   * </p>
   */
  public CellEditor createPropertyEditor(Composite parent) 
  {
    CellEditor editor = new AssignableCellEditor(assignables, parent, SWT.READ_ONLY);
    if (getValidator() != null)
      editor.setValidator(getValidator());
    return editor;
  }  
  
  public static TreeMap createID2Assignables(Collection assignables) 
	{
  	TreeMap id2Assignable = new TreeMap();
		for (Iterator it = assignables.iterator(); it.hasNext(); ) {
			IAssignable assignable = (IAssignable) it.next();
			id2Assignable.put(new Integer(assignable.getId()), assignable);
		}
		return id2Assignable;
	}
}
