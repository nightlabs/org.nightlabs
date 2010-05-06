/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.util.bean.propertyeditor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.nightlabs.util.bean.BeanUtil;
import org.nightlabs.util.bean.DefaultPropertyEditorContext;
import org.nightlabs.util.bean.PropertyEditorMan;

/**
 * Encapsulate a Component object used to configure a Beans property value.
 * There are 3 possibilities:<p>
 * 1. The property supports a custom editor. In this case, the editor UI
 *    simply behaves as a container for the custom editor.
 * 2. The property is paintable. In this case, the editor UI creates a
 *    PaintableComponent which will delegate painting to the property editor.
 * 3. The property is text based. In this case, the editor UI creates a
 *    text field for the property.
 */
public class PropertyEditorUI
extends JPanel
{
	private static final long serialVersionUID = 1L;

	private boolean differentValues = false;
	public boolean isDifferentValues() {
		return differentValues;
	}

	private boolean saveAllValues = false;
	public boolean isSaveAllValues() {
		return saveAllValues;
	}
	public void setSaveAllValues(boolean b) {
		saveAllValues = b;
	}

	private boolean readOnly = false;
	public boolean isReadOnly() {
		return readOnly;
	}

	private Object firstBean;
	private Object firstValue;

  /*
   * Methods to access the beans property
   */
  private Method getMethod, setMethod;

  /** Property editor */
  private PropertyEditor editor;
	public PropertyEditor getPropertyEditor() {
		return editor;
	}

  /** Propertye editor type */
  private int editorType;

  /** Component displaying the value */
  private Component component;
  public Component getComponent() {
  	return component;
  }

  private String propertyName;
  public String getPropertyName() {
  	return propertyName;
  }

	/**
	 * @return the property name for a given set method
	 * @param methodName method name. If the name is not the one of a set or get method
	 *        name, the result is not guaranteed.
	 */
	public String getPropertyName(String methodName)
	{
		return BeanUtil.getPropertyName(methodName, false);
	}

  /*
   * Editor types
   */
  static final int EDITOR_CUSTOM = 0;
  static final int EDITOR_PAINTABLE = 1;
  static final int EDITOR_STRING = 2;

  public PropertyEditorUI(PropertyDescriptor pd)
	{
//  	this.editor = PropertyEditorManager.findEditor(pd.getPropertyType());
  	this.editor = PropertyEditorMan.getPropertyEditor(DefaultPropertyEditorContext.class, pd.getPropertyType());
  	this.getMethod = pd.getReadMethod();
  	this.setMethod = pd.getWriteMethod();

  	this.propertyName = pd.getName();

  	init();
  }

  /**
   * @param editor the property editor for which a UI is provided.
   * @param getMethod method to use to get the property value from a beans
   * @param setMethod method to use to save the property value into a beans
   */
  public PropertyEditorUI(PropertyEditor editor, Method getMethod, Method setMethod)
  {
    if(editor==null || getMethod==null)
      throw new IllegalArgumentException();

    this.editor = editor;
    this.getMethod = getMethod;
    this.setMethod = setMethod;

		this.propertyName = getPropertyName(getMethod.getName());

		init();
  }

  private void init()
  {
    if(editor.supportsCustomEditor())
      editorType = EDITOR_CUSTOM;
    else if(editor.isPaintable())
      editorType = EDITOR_PAINTABLE;
    else
      editorType = EDITOR_STRING;

    if (setMethod == null)
    	readOnly = true;

    switch(editorType){
    case EDITOR_CUSTOM:
      component = editor.getCustomEditor();
      if (readOnly)
        component.setEnabled(false);
      break;
    case EDITOR_PAINTABLE:
      component = new PaintableEditor(editor);
      if (readOnly)
        component.setEnabled(false);
      break;
    case EDITOR_STRING:
      component = new JTextField();
      if (readOnly)
        component.setEnabled(false);
      break;
    }

    setLayout(new BorderLayout());
    add(component);
  }

  public int getEditorType(){
    return editorType;
  }

  /**
   * @param beans the Vector of all Beans which have this Property
   * but only the property of one bean will be displayed by this editor.
   */
  public void load(List<? extends Object> beans)
  throws PropertyEditorUIException
	{
  	// Iterate through all beans
  	for (int i=0; i<beans.size(); i++)
  	{
  		Object bean = beans.get(i);

  		try
			{
  			Object value = getMethod.invoke(bean, (Object) null);

  			// if this is the first bean only set the Value
  			// once for the firstBean
  			if (firstValue == null)
  			{
  				firstValue = value;
  				firstBean = bean;
  				editor.setValue(firstValue);

    			if(editorType==EDITOR_STRING){
    				((JTextField)component).setText(editor.getAsText());
    			}

    			component.repaint();
  			}
  			// if there exist different values for some beans
  			// set differentValues true
  			else if (!firstValue.equals(value))
  				differentValues = true;

  		}catch(IllegalAccessException e1){
  			throw new PropertyEditorUIException(e1);
  		}catch(IllegalArgumentException e2){
  			throw new PropertyEditorUIException(e2);
  		}catch(InvocationTargetException e3){
  			throw new PropertyEditorUIException(e3);
  		}
  	} // for (int i=0; i<beans.size(); i++)

  }

  /**
   * @param bean object whose property is to be displayed by this editor.
   */
  public void load(Object bean)
  throws PropertyEditorUIException
  {
    try
		{
      Object value = getMethod.invoke(bean, (Object) null);
    	if (firstValue == null)
    	{
    		firstValue = value;
      	firstBean = bean;
      	editor.setValue(firstValue);

        if(editorType==EDITOR_STRING){
          ((JTextField)component).setText(editor.getAsText());
        }

        component.repaint();
    	}
    }catch(IllegalAccessException e1){
    	throw new PropertyEditorUIException(e1);
    }catch(IllegalArgumentException e2){
    	throw new PropertyEditorUIException(e2);
    }catch(InvocationTargetException e3){
    	throw new PropertyEditorUIException(e3);
    }
  }

  /**
   * @param beans the Vector of Beans which should save the Value
   * of this editor
   */
  public void save(List<? extends Object> beans)
	throws PropertyEditorUIException
	{
  	boolean commitValue = false;
  	if(editorType==EDITOR_STRING)
  		editor.setAsText(((JTextField)component).getText());

  	Object value = editor.getValue();

  	if (firstValue == null)
  		throw new IllegalStateException("you must first load values before you can save!");

  	if (firstValue.equals(value))
  		commitValue = false;
  	else
  		commitValue = true;

  	try
		{
  		if (commitValue)
  		{
      	if (saveAllValues)
      	{
  	  		for (Iterator<? extends Object> it = beans.iterator(); it.hasNext(); )
  	  		{
  	  			Object bean = it.next();
  	  			if (setMethod != null)
  	  			{
  	  				setMethod.invoke(bean, new Object[]{value});
  	  			}
  	  		}
      	} // if (saveAllValues)
      	else
      	{
      		if (setMethod != null)
      			setMethod.invoke(firstBean, new Object[]{value});
      	}
  		}
  	}catch(IllegalAccessException e1){
  		throw new PropertyEditorUIException(e1);
  	}catch(IllegalArgumentException e2){
  		throw new PropertyEditorUIException(e2);
  	}catch(InvocationTargetException e3){
  		throw new PropertyEditorUIException(e3);
  	}
  }

  /**
   * @param bean object into which the property value displayed
   *        in UI component should be saved.
   */
  public void save(Object bean)
  throws PropertyEditorUIException
	{
    if(editorType==EDITOR_STRING)
      editor.setAsText(((JTextField)component).getText());

    Object value = editor.getValue();

    boolean commitValue = false;

  	if (firstValue == null)
  		throw new IllegalStateException("you must first load values before you can save!");

  	if (firstValue.equals(value))
  		commitValue = false;
  	else
  		commitValue = true;

    try
		{
    	if (commitValue)
    	{
        if (setMethod != null)
        {
          setMethod.invoke(bean, new Object[]{value});
        }
    	}
    }catch(IllegalAccessException e1){
    	throw new PropertyEditorUIException(e1);
    }catch(IllegalArgumentException e2){
    	throw new PropertyEditorUIException(e2);
    }catch(InvocationTargetException e3){
    	throw new PropertyEditorUIException(e3);
    }
  }
}
