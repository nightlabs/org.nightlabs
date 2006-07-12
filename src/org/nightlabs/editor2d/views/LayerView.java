/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d.views;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.composite.XComposite.LayoutMode;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.command.CreateLayerCommand;
import org.nightlabs.editor2d.command.DeleteLayerCommand;
import org.nightlabs.editor2d.command.DrawComponentReorderCommand;
import org.nightlabs.editor2d.edit.LayerEditPart;
import org.nightlabs.editor2d.edit.MultiLayerDrawComponentEditPart;
import org.nightlabs.editor2d.util.OrderUtil;

public class LayerView 
extends ViewPart 
implements ISelectionListener
{
  public static final Logger LOGGER = Logger.getLogger(LayerView.class);  
  public static final String ID_VIEW = LayerView.class.getName();  
  
  public static final Image DELETE_ICON = SharedImages.DELETE_16x16.createImage();  
  public static final Image NEW_ICON = SharedImages.ADD_16x16.createImage();
  public static final Image EYE_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Eye");    
  public static final Image EYE_INVISIBLE_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Eye-Invisible");  
  public static final Image UP_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Up");
  public static final Image DOWN_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Down");
  public static final Image LOCK_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Lock");
  public static final Image UNLOCK_ICON = SharedImages.getSharedImage(EditorPlugin.getDefault(), LayerView.class, "Unlocked");  
      
  protected MultiLayerDrawComponent mldc;
  protected AbstractEditor editor;
  protected Map button2Layer = new HashMap();
  protected Button buttonUp;
  protected Button buttonDown;
  protected Button buttonNew;
  protected Button buttonDelete;
  
  protected Color currentLayerColor; 
  protected Color getCurrentLayerColor() 
  {
  	if (currentLayerColor == null)
  		currentLayerColor = new Color(Display.getCurrent(), 232, 236, 240);  	
  	return currentLayerColor;
  }

  protected Color toolButtonColor;
  protected Color getToolButtonColor() 
  {
  	if (toolButtonColor == null)
  		toolButtonColor = new Color(Display.getCurrent(), 0, 0, 0);  	
  	return toolButtonColor;
  }
    
  public LayerView() 
  {
    super();
  }  
  
  protected void initMultiLayerDrawComponent() 
  {
    if (getSite().getPage().getActiveEditor() instanceof AbstractEditor) 
    {
      LOGGER.debug("getSite().getPage().getActiveEditor() instanceof Editor!");
      editor = (AbstractEditor) getSite().getPage().getActiveEditor();
      RootEditPart rootEditPart = editor.getOutlineGraphicalViewer().getRootEditPart();
      List children = rootEditPart.getChildren();
      if (!children.isEmpty()) {
        EditPart editPart = (EditPart) children.get(0);
        if (editPart instanceof MultiLayerDrawComponentEditPart) {
          MultiLayerDrawComponentEditPart mldcEditPart = (MultiLayerDrawComponentEditPart) editPart;
          mldcEditPart.addEditPartListener(mldcListener);
        }
      }
      mldc = editor.getMultiLayerDrawComponent();
    }  	
  }
    
	protected void init() 
	{
		// init the MultiLayerDrawComponent
  	initMultiLayerDrawComponent();  	
    // add SelectionChangeListener
    getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);		
	}
	
	protected XFormToolkit toolkit = null;
	protected XFormToolkit getToolkit() {
		return toolkit;
	}
	
	protected ScrolledForm form = null;	
	protected ScrolledForm getForm() {
		return form;
	}
		
	protected Composite layerComposite;
	
//  /**
//   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
//   */
//  public void createPartControl(Composite parent) 
//  {   	
//    init();
//            
//		toolkit = new XFormToolkit(parent.getDisplay());
//		form = getToolkit().createForm(parent);
//		form.getBody().setLayout(new GridLayout());
//		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
//               		
//		layerForm = getToolkit().createScrolledForm(form.getBody());
//		layerForm.getBody().setLayout(new GridLayout());
//		layerForm.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));	
//		
//		layerComposite = getToolkit().createComposite(layerForm.getBody(), SWT.BORDER);
//		layerComposite.setLayout(new GridLayout());
//		layerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
//		
//		toolsComposite = getToolkit().createComposite(form.getBody());
//		toolsComposite.setLayout(new GridLayout());
//		toolsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		createTools(parent);		
//		
//    refresh();    
//    deactivateTools(true);
//  }
	
  /**
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent) 
  {   	
    init();
                
    parent.setLayout(XComposite.getLayout(LayoutMode.ORDINARY_WRAPPER));    
    parent.setLayoutData(new GridData(GridData.FILL_BOTH));    
    
		toolkit = new XFormToolkit(parent.getDisplay());
//		toolkit.setCurrentMode(TOOLKIT_MODE.COMPOSITE);				
		parent.setBackground(getToolkit().getBackground());
		
		form = getToolkit().createScrolledForm(parent);
		form.setLayout(new GridLayout());
		form.setLayoutData(new GridData(GridData.FILL_BOTH));		
		form.getBody().setLayout(new GridLayout());
		form.getBody().setLayoutData(new GridData(GridData.FILL_BOTH));
				    				
		layerComposite = getToolkit().createComposite(form.getBody(), SWT.NONE);
		layerComposite.setLayout(new GridLayout());
		layerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(layerComposite);
		toolkit.paintBordersFor(form.getBody());
						
		createTools(parent);				
    refresh();    
    deactivateTools(false);
  }
  
  protected void createLayerEntry(Composite parent, Layer l) 
	{							
		int buttonStyle = SWT.TOGGLE;
				
		Composite parentComposite = getToolkit().createComposite(parent, SWT.NONE);
		parentComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		
		getToolkit().paintBordersFor(parent);
		getToolkit().paintBordersFor(parentComposite);		
		if (!parentComposite.isDisposed()) 
		{
			parentComposite.setLayout(new GridLayout(2, false));
			parentComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			// create Visible-Button
			Button buttonVisible = getToolkit().createButton(parentComposite, "", buttonStyle);			
			buttonVisible.setSelection(!l.isVisible());
			if (l.isVisible()) {
				buttonVisible.setImage(EYE_ICON);				
				buttonVisible.setToolTipText(EditorPlugin.getResourceString("layerView.buttonVisible.tooltip.invisible"));				
			}
			else {
				buttonVisible.setImage(EYE_INVISIBLE_ICON);
				buttonVisible.setToolTipText(EditorPlugin.getResourceString("layerView.buttonVisible.tooltip.visible"));				
			}			
			buttonVisible.setLayoutData(new GridData(GridData.BEGINNING));
			
			buttonVisible.addSelectionListener(visibleListener);		
			buttonVisible.addDisposeListener(visibleDisposeListener);
			
//			// create Editable-Button
//			Button buttonEditble = getToolkit().createButton(parentComposite, EditorPlugin.getResourceString("layerView.buttonLocked.text"), buttonStyle);		
//			buttonEditble.setSelection(!l.isEditable());		
////			buttonEditble.setImage(LOCK_ICON);
//			if (l.isEditable()) {
//				buttonEditble.setImage(UNLOCK_ICON);				
//				buttonEditble.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.locked"));
//			}
//			else {				
//				buttonEditble.setImage(LOCK_ICON);				
//				buttonEditble.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.unlocked"));				
//			}					
//			buttonEditble.setLayoutData(new GridData(GridData.CENTER));		
//			buttonEditble.addSelectionListener(editableListener);		
//			buttonEditble.addDisposeListener(editableDisposeListener);
			
			// create Name-Text		
			Text text = getToolkit().createText(parentComposite, EditorPlugin.getResourceString("layerView.layer.name"));
			if (l.getName() != null)
				text.setText(l.getName());		
			text.setEditable(true);
			text.setLayoutData(new GridData(GridData.FILL_BOTH));		  								
			text.addSelectionListener(textListener);
			text.addFocusListener(focusListener);
			text.addDisposeListener(textDisposeListener);
			
			// add newLayer to button2Layer
			button2Layer.put(buttonVisible, l);
//			button2Layer.put(buttonEditble, l);
			button2Layer.put(text, l);

			// set bgColor of currentLayer
			if (l.equals(mldc.getCurrentLayer())) {
//			  parentComposite.setBackground(getCurrentLayerColor());		
				text.setBackground(getCurrentLayerColor());				
			}
		}
	}
	
  protected void createTools(Composite parent) 
	{	  	  	  		
		int buttonStyle = SWT.PUSH;
		
		Composite toolsComposite = getToolkit().createComposite(parent);		
		toolsComposite.setLayout(new GridLayout(5, false));
		toolsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolsComposite.setBackground(getToolkit().getBackground());
		
		// spacer Label 
		Label spacerLabel = getToolkit().createLabel(toolsComposite, "");
		spacerLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
				
		// create Buttons		
		buttonUp = getToolkit().createButton(toolsComposite, EditorPlugin.getResourceString("layerView.buttonUp.text"), 
				buttonStyle);
//		buttonUp.setImage(UP_ICON);
		buttonUp.setToolTipText(EditorPlugin.getResourceString("layerView.buttonUp.tooltip"));
		buttonUp.addSelectionListener(upListener);
		buttonUp.addDisposeListener(upDisposeListener);
		buttonUp.setBackground(getToolButtonColor());
		
		buttonDown = getToolkit().createButton(toolsComposite, EditorPlugin.getResourceString("layerView.buttonDown.text"), 
				buttonStyle);
//		buttonDown.setImage(DOWN_ICON);
		buttonDown.setToolTipText(EditorPlugin.getResourceString("layerView.buttonDown.tooltip"));
		buttonDown.addSelectionListener(downListener);
		buttonDown.addDisposeListener(downDisposeListener);
		buttonDown.setBackground(getToolButtonColor());	
		
		buttonNew = getToolkit().createButton(toolsComposite, EditorPlugin.getResourceString("layerView.buttonNew.text"), 
				buttonStyle);
//		buttonNew.setImage(NEW_ICON);
		buttonNew.setToolTipText(EditorPlugin.getResourceString("layerView.buttonNew.tooltip"));		
		buttonNew.addSelectionListener(newListener);
		buttonNew.addDisposeListener(newDisposeListener);
		buttonNew.setBackground(getToolButtonColor());		
		
		buttonDelete = getToolkit().createButton(toolsComposite, EditorPlugin.getResourceString("layerView.buttonDelete.text"), 
				buttonStyle);
//		buttonDelete.setImage(DELETE_ICON);
		buttonDelete.setToolTipText(EditorPlugin.getResourceString("layerView.buttonDelete.tooltip"));				
		buttonDelete.addSelectionListener(deleteListener);				
		buttonDelete.addDisposeListener(deleteDisposeListener);
		buttonDelete.setBackground(getToolButtonColor());		
	}
	
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus() 
  {
  	form.setFocus();
  }

  protected FocusAdapter focusListener = new FocusAdapter() 
  {
    public void focusGained(FocusEvent e) 
    {
			Text t = (Text) e.getSource();
			if (button2Layer.containsKey(t)) 
			{
				Layer currLayer = (Layer) button2Layer.get(t);
				if (!mldc.getCurrentLayer().equals(currLayer)) {
					mldc.setCurrentLayer(currLayer);
					LOGGER.debug("Layer "+currLayer.getName()+" = CurrentLayer");
					refresh();				  
				}
			} else {
			  throw new IllegalStateException("There is no such Layer registered!");
			}      
    }
  }; 
  
  protected SelectionListener textListener = new SelectionAdapter() 
	{ 
		public void widgetDefaultSelected(SelectionEvent e) 
		{    
      if (e.getSource() instanceof Text) 
      {
        Text text = (Text) e.getSource();
        String layerName = text.getText();
        LOGGER.debug("New LayerName = "+layerName);
        Layer l = (Layer) button2Layer.get(text);
        l.setName(layerName);        
      }		  
		}
	};  
   	
	protected SelectionListener visibleListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
			LOGGER.debug("VISIBLE widgetSelected()");
			
			Button b = (Button) e.getSource();
			if (button2Layer.containsKey(b)) {
				Layer l = (Layer) button2Layer.get(b);
				if (!b.getSelection()) {
					l.setVisible(true);
					b.setImage(EYE_ICON);
					b.setToolTipText(EditorPlugin.getResourceString("layerView.buttonVisible.tooltip.invisible"));
				} else {
					l.setVisible(false);
					b.setImage(EYE_INVISIBLE_ICON);
					b.setToolTipText(EditorPlugin.getResourceString("layerView.buttonVisible.tooltip.visible"));
				}				
				updateViewer();
			}
			else {
				throw new IllegalStateException("There is no such Layer registered!");
			}			
		}
	};  

	protected SelectionListener editableListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("EDITABLE widgetSelected()");
			
			Button b = (Button) e.getSource();
			if (button2Layer.containsKey(b)) {
				Layer l = (Layer) button2Layer.get(b);
				if (b.getSelection()) {
					l.setEditable(true);
					b.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.locked"));
					b.setImage(UNLOCK_ICON);					
				} else {
					l.setEditable(false);
					b.setToolTipText(EditorPlugin.getResourceString("layerView.buttonLocked.tooltip.unlocked"));
					b.setImage(LOCK_ICON);
				}
				updateViewer();
			}
			else {
				throw new IllegalStateException("There is no such Layer registered!");
			}			
		}
	};  
		
	protected void updateViewer() 
	{
		editor.updateViewer();
//		editor.getEditPartViewer().getRootEditPart().refresh();
//		editor.getEditorSite().getShell().update();
	}
	
	protected SelectionListener newListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("NEW widgetSelected()");
//		  CreateLayerCommand addLayer = new CreateLayerCommand(mldc);
		  CreateLayerCommand addLayer = new CreateLayerCommand(mldc, editor.getModelFactory());		  
		  executeCommand(addLayer);
		}
	};

	protected SelectionListener deleteListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("DELETE widgetSelected()");
			
		  Layer currentLayer = mldc.getCurrentLayer();
		  DeleteLayerCommand layerCommand = new DeleteLayerCommand(mldc, currentLayer);
		  executeCommand(layerCommand);		  
		}
	};

	protected Layer getCurrentLayer() 
	{
		return mldc.getCurrentLayer();
	}
	
	protected SelectionListener upListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("UP widgetSelected()");
		  int oldIndex = OrderUtil.indexOf(getCurrentLayer());
		  int lastIndex = OrderUtil.getLastIndex(mldc);
		  int newIndex = oldIndex + 1;
		  if (!(lastIndex > newIndex)) {
		  	newIndex = lastIndex;
		  }
		  DrawComponentReorderCommand cmd = new DrawComponentReorderCommand(
		  		getCurrentLayer(), mldc, newIndex);
		  executeCommand(cmd);
		}
	};
	
	protected SelectionListener downListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("DOWN widgetSelected()"); 
		  int oldIndex = OrderUtil.indexOf(getCurrentLayer());
		  int firstIndex = 0;
		  int newIndex = oldIndex - 1;
		  if (!(firstIndex < newIndex)) {
		  	newIndex = firstIndex;
		  }
		  DrawComponentReorderCommand cmd = new DrawComponentReorderCommand(
		  		getCurrentLayer(), mldc, newIndex);
		  executeCommand(cmd);		  
		}
	};
		
	protected void executeCommand(Command cmd) 
	{
		editor.getOutlineEditDomain().getCommandStack().execute(cmd);
	}
	
	protected CommandStackListener commandStackListener = new CommandStackListener() 
	{
    /* (non-Javadoc)
     * @see org.eclipse.gef.commands.CommandStackListener#commandStackChanged(java.util.EventObject)
     */
    public void commandStackChanged(EventObject event) 
    {      
			editor.getEditPartViewer().getRootEditPart().refresh();
			refresh();
    }
  };

  protected EditPartListener mldcListener = new EditPartListener.Stub() 
  {
    /**
     * @see org.eclipse.gef.EditPartListener#childAdded(org.eclipse.gef.EditPart, int)
     */
    public void childAdded(EditPart child, int index) 
    {
      if (child instanceof LayerEditPart) {
        MultiLayerDrawComponentEditPart parent = (MultiLayerDrawComponentEditPart) child.getParent();
        mldc = (MultiLayerDrawComponent) parent.getModel();
        refresh();
      }
    }

    /**
     * @see org.eclipse.gef.EditPartListener#removingChild(org.eclipse.gef.EditPart, int)
     */
    public void removingChild(EditPart child, int index) 
    {
      if (child instanceof LayerEditPart) {
        MultiLayerDrawComponentEditPart parent = (MultiLayerDrawComponentEditPart) child.getParent();
        mldc = (MultiLayerDrawComponent) parent.getModel();
        refresh();        
      }
    }
  };
  	
  protected void deactivateTools(boolean newButton) 
	{
	  if (!buttonUp.isDisposed())
	    buttonUp.setEnabled(false);
	  if (!buttonDown.isDisposed())
	    buttonDown.setEnabled(false);
	  if (!buttonDelete.isDisposed())
	    buttonDelete.setEnabled(false);
	  if (!buttonNew.isDisposed())
	    buttonNew.setEnabled(newButton);
	}
	
	protected void activateTools() 
	{
	  if (!buttonUp.isDisposed()) 
	    buttonUp.setEnabled(true);
	  if (!buttonDown.isDisposed())	  
	    buttonDown.setEnabled(true);
	  if (!buttonDelete.isDisposed())	  
	    buttonDelete.setEnabled(true);
	  if (!buttonNew.isDisposed())
	    buttonNew.setEnabled(true);
	}
	
  /* (non-Javadoc)
   * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
   */
  public void selectionChanged(IWorkbenchPart part, ISelection selection) 
  {
    if (part instanceof AbstractEditor) 
    {
      this.editor = (AbstractEditor) part;
      this.mldc = editor.getMultiLayerDrawComponent();
      editor.getOutlineEditDomain().getCommandStack().removeCommandStackListener(commandStackListener);      
      editor.getOutlineEditDomain().getCommandStack().addCommandStackListener(commandStackListener);
    }    
    else if (!(getSite().getPage().getActiveEditor() instanceof AbstractEditor))
    {
      editor = null;
      mldc = null;
      deactivateTools(false);
    }
    refresh();    
  }
  
  protected void createComposites() 
  {
		if (layerComposite != null)
			layerComposite.dispose();		
		layerComposite = getToolkit().createComposite(form.getBody(), SWT.NONE);		
		layerComposite.setLayout(new GridLayout());
		layerComposite.setLayoutData(new GridData(GridData.FILL_BOTH));		
		layerComposite.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		getToolkit().paintBordersFor(form.getBody());		
  }
    				
	public void refresh()
	{			
		if (layerComposite != null || !layerComposite.isDisposed()) 		
		{						
			createComposites();
			button2Layer.clear();			
			if (mldc != null) {
				for (int i = mldc.getCurrentPage().getDrawComponents().size()-1; i >= 0; --i) {
					DrawComponent dc  = (DrawComponent) mldc.getCurrentPage().getDrawComponents().get(i);
					if (dc instanceof Layer) {
					  Layer l = (Layer) dc; 
					  createLayerEntry(layerComposite, l); 
					} else {
					  LOGGER.debug("dc NOT instanceof Layer, but instanceof "+dc.getClass());
					}
				}				
				if (mldc.getCurrentPage().getDrawComponents().size() <= 1)			  
				  deactivateTools(true);			  
				else 
				  activateTools();
				
				form.reflow(true);
			}				  
		}		
	}  
  
  public void dispose() 
  {
    getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);    
    super.dispose();
  }
  
  protected DisposeListener visibleDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(visibleListener);
		}	
	};

  protected DisposeListener editableDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(editableListener);
		}	
	};

  protected DisposeListener textDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Text t = (Text) e.getSource();
			t.removeSelectionListener(textListener);
			t.removeFocusListener(focusListener);
		}	
	};
	
  protected DisposeListener upDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(upListener);
		}	
	};
	
  protected DisposeListener downDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(downListener);
		}	
	};
	
  protected DisposeListener newDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(newListener);
		}	
	};		

  protected DisposeListener deleteDisposeListener = new DisposeListener() {	
		public void widgetDisposed(DisposeEvent e) {
			Button b = (Button) e.getSource();
			b.removeSelectionListener(newListener);
		}	
	};		
	
}
