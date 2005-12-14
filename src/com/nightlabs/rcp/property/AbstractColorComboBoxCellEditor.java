/**
 * <p> Project: com.nightlabs.gui </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.07.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.property;

import java.text.MessageFormat;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;

import com.nightlabs.rcp.custom.ColorCombo;

public abstract class AbstractColorComboBoxCellEditor 
extends CellEditor
{
		public AbstractColorComboBoxCellEditor(Composite parent, int style) {
			super(parent, style);
//			populateComboBoxItems();
		}
		
	  /**
	   * the ColorCombo which will display 
	   */
		protected ColorCombo comboBox;
		public ColorCombo getComboBox() {
			return comboBox;
		}
		
	  /**
	   * The zero-based index of the selected item.
	   */
	  protected int selection;	
		
	  /**
	   * Creates a ColorCombo and adds some listener to it
	   */
	  protected Control createControl(Composite parent) 
	  {	
		  comboBox = new ColorCombo(parent, getStyle());
//	  	comboBox = new Combo(parent, getStyle());
		  comboBox.setFont(parent.getFont());
		
		  comboBox.addKeyListener(new KeyAdapter() {
		      // hook key pressed - see PR 14201  
		      public void keyPressed(KeyEvent e) {
		          keyReleaseOccured(e);
		      }
		  });
		
		  comboBox.addSelectionListener(new SelectionAdapter() {
		      public void widgetDefaultSelected(SelectionEvent event) {
		          applyEditorValueAndDeactivate();
		      }
		
		      public void widgetSelected(SelectionEvent event) {
		          selection = comboBox.getSelectionIndex();
		      }
		  });
		
		  comboBox.addTraverseListener(new TraverseListener() {
		      public void keyTraversed(TraverseEvent e) {
		          if (e.detail == SWT.TRAVERSE_ESCAPE
		                  || e.detail == SWT.TRAVERSE_RETURN) {
		              e.doit = false;
		          }
		      }
		  });
		
		  comboBox.addFocusListener(new FocusAdapter() {
		      public void focusLost(FocusEvent e) {
		      	AbstractColorComboBoxCellEditor.this.focusLost();
		      }
		  });
		  return comboBox;
		}

	  /**
	   * sets the focus to the ComboBox
	   */
	  protected void doSetFocus() {
	    if (comboBox != null) {
	      comboBox.setFocus();    	
	    }
	  }
	  
	  /**
	   * @see org.eclipse.jface.viewers.CellEditor#focusLost()
	   */
	  protected void focusLost() 
	  {
	    if (isActivated()) {
	      applyEditorValueAndDeactivate();
	    }
	  }  
	  
	  /**
	   * Applies the currently selected value and deactiavates the cell editor
	   */
	  protected void applyEditorValueAndDeactivate() 
	  {
	    //  must set the selection before getting value
	    selection = comboBox.getSelectionIndex();
	    Object newValue = doGetValue();
	    markDirty();
	    boolean isValid = isCorrect(newValue);
	    setValueValid(isValid);
	    if (!isValid) {
	      // try to insert the current value into the error message.
	      setErrorMessage(MessageFormat.format(getErrorMessage(),
	        new Object[] { getComboBox().getItem(selection)}));
	    }
	    fireApplyEditorValue();
	    deactivate();
	  }	
	  
	  /**
	   * The <code>AssignableCellEditor</code> implementation of
	   * this <code>CellEditor</code> framework method sets the 
	   * minimum width of the cell.  The minimum width is 10 characters
	   * if <code>comboBox</code> is not <code>null</code> or <code>disposed</code>
	   * eles it is 60 pixels to make sure the arrow button and some text is visible.
	   * The list of CCombo will be wide enough to show its longest item.
	   */
	  public LayoutData getLayoutData() 
	  {
	    LayoutData layoutData = super.getLayoutData();
	    if ((comboBox == null) || comboBox.isDisposed())
	      layoutData.minimumWidth = 60;
	    else {
	      // make the comboBox 10 characters wide
	      GC gc = new GC(comboBox);
	      layoutData.minimumWidth = (gc.getFontMetrics()
	              .getAverageCharWidth() * 10) + 10;
	      gc.dispose();
	    }
	    return layoutData;
	  }	  
	  
	  protected Object oldValue;
	  protected void doSetValue(Object value) 
	  {
	    oldValue = value;
	    if (value instanceof String) 
	    {
	      String string = (String) value;
	      for (int i=0; i<getComboBox().getItemCount(); i++) {
	        TableItem item = getComboBox().getItem(i);
	        if (item.getText().equals(string)) {
	          getComboBox().select(i);
	          break;
	        }          
	      }
	    }
	  }
	  
		/**
		 * This Method should be called in the constructor of a subClass after having initalized
		 * everything which is needed (maybe also in this Method)
		 *
		 */
	  protected abstract void populateComboBoxItems();
	  
	  /**
	   * @return the value which is expected (usally you return not a String but
	   * another kind of value which is normally mapped in a subClass)
	   */  
		protected abstract Object getReturnValue();
		
		/**
		 * checks if something is selected ( = the selectionIndex != -1) and if so
		 * the value of getReturnValue() is returned
		 */
		protected Object doGetValue() 
		{
			if (getComboBox().getSelectionIndex() != -1)
				return getReturnValue();			
			else
				return oldValue;
		}
		
//		/**
//		 * @return a TableItem[] which contains all items which are displayed in the ComboBox
//		 */
//		protected abstract TableItem[] getItems();

	}

