/**
 * <p> Project: com.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 27.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.property;

import java.util.Collection;
import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.nightlabs.rcp.util.ImageUtil;
import com.nightlabs.util.IAssignable;
import com.nightlabs.util.IColorAssignable;

public class AssignableLabelProvider 
extends LabelProvider
{
	public AssignableLabelProvider(Collection assignables) 
	{
		this.id2Assignable = AssignablePropertyDescriptor.createID2Assignables(assignables);
	}
	
	protected Map id2Assignable;
	
  public String getText(Object element) 
  {
    if (element == null)
        return ""; //$NON-NLS-1$

    if (element instanceof Integer) {
    	Integer id = (Integer) element;
    	IAssignable assignable = (IAssignable) id2Assignable.get(id);
    	if (assignable != null)
    		return assignable.getName();
    }

    return ""; //$NON-NLS-1$
  }
  
  public Image getImage(Object element) 
  {
  	if (element instanceof Integer) {
    	Integer id = (Integer) element;
    	IAssignable assignable = (IAssignable) id2Assignable.get(id);
    	if (assignable != null) {
      	if (assignable instanceof IColorAssignable) {
      		IColorAssignable colorAssignable = (IColorAssignable) assignable;
      		return ImageUtil.createColorImage(colorAssignable.getColor());
      	}    		
    	}
  	}
  	return null;
 	} 
 
}
