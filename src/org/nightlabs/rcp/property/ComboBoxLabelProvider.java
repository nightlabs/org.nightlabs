/**
 * <p> Project: org.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 15.03.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.rcp.property;

public class ComboBoxLabelProvider 
extends org.eclipse.ui.views.properties.ComboBoxLabelProvider 
{

  public ComboBoxLabelProvider(String[] values) {
    super(values);
  }

  public String getText(Object element) 
  {
      if (element == null)
          return ""; //$NON-NLS-1$
      
      return element.toString(); //$NON-NLS-1$
  }   
}
