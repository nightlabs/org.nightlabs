/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 05.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.views;

import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.nightlabs.editor2d.AbstractEditor;
import com.nightlabs.editor2d.DrawComponent;
import com.nightlabs.editor2d.Editor;
import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.Layer;
import com.nightlabs.editor2d.MultiLayerDrawComponent;
import com.nightlabs.editor2d.command.CreateLayerCommand;
import com.nightlabs.editor2d.command.DeleteLayerCommand;
import com.nightlabs.editor2d.edit.LayerEditPart;
import com.nightlabs.editor2d.edit.MultiLayerDrawComponentEditPart;

public class LayerView 
extends ViewPart 
implements ISelectionListener
{
  public static final Logger LOGGER = Logger.getLogger(LayerView.class);
  
  public static final String ID_VIEW = LayerView.class.getName();  
  
  public static final Image EYE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/eye12.gif").createImage();
//  public static final Image DELETE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/delete16.gif").createImage();
//  public static final Image DELETE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/minus16.gif").createImage();  
  public static final Image DELETE_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/delete24.gif").createImage();  
//  public static final Image NEW_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/add16.gif").createImage();
  public static final Image NEW_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/plus16.gif").createImage();  
  public static final Image UP_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/up16.gif").createImage();
  public static final Image DOWN_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/down16.gif").createImage();
  public static final Image LOCK_ICON = ImageDescriptor.createFromFile(EditorPlugin.class,"icons/lock12.gif").createImage();
  
  protected MultiLayerDrawComponent mldc;
  protected AbstractEditor editor;
  protected Map button2Layer = new HashMap();
  protected Composite layerComposite;
  protected ScrolledComposite scrollComposite;
  protected Button buttonUp;
  protected Button buttonDown;
  protected Button buttonNew;
  protected Button buttonDelete;
  protected Color currentLayerColor;
      
  /**
   * 
   */
  public LayerView() {
    super();
  }  
  
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
   */
  public void createPartControl(Composite parent) 
  {
    if (getSite().getPage().getActiveEditor() instanceof AbstractEditor) 
    {
      LOGGER.debug("getSite().getPage().getActiveEditor() instanceof Editor!");
      editor = (AbstractEditor) getSite().getPage().getActiveEditor();
      // instead of listen to the CommandStack
      // listen to the MultiLayerDrawComponentEditPart
//      editor.getOutlineEditDomain().getCommandStack().addCommandStackListener(commandStackListener);
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
    
    // add SelectionChangeListener
    getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
    
    // Create parent layout
    GridLayout parentLayout = new GridLayout();
    GridData parentData = new GridData();
    parent.setLayout(parentLayout);
            
    // Create LayerComposite
    scrollComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
    scrollComposite.setExpandHorizontal(true);
    layerComposite = new Composite(scrollComposite, SWT.NONE);
    layerComposite.setSize(scrollComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));    
    scrollComposite.setContent(layerComposite);
            
    GridData layersData = new GridData();
    layersData.grabExcessHorizontalSpace = true;
    layersData.grabExcessVerticalSpace = true;
    layersData.horizontalAlignment = SWT.FILL;
    layersData.verticalAlignment = SWT.FILL;
    scrollComposite.setLayoutData(layersData);    
    scrollComposite.setMinSize(layerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));    
        
//    FillLayout layerFillLayout = new FillLayout();
//    layerFillLayout.type = SWT.VERTICAL;
//    layerComposite.setLayout(layerFillLayout);
    GridLayout layerGridLayout = new GridLayout();
    layerComposite.setLayout(layerGridLayout);
            
    // Create Entries    
    createTools(parent);
    refresh();
    scrollComposite.layout(true);
    
    deactivateTools(false);
  }

  private void addLayer() 
  {
		// create new Layer		
		CreateLayerCommand addLayer = new CreateLayerCommand(mldc);
		editor.getOutlineEditDomain().getCommandStack().execute(addLayer);		
  }
    
	private void createLayerEntry(Composite parent, Layer l) 
	{							
	  // create parentComposite
	  Composite parentComposite = new Composite(parent, SWT.BORDER);
		GridLayout parentLayout = new GridLayout();
		parentLayout.numColumns = 3;
		GridData parentData = new GridData();
		parentData.grabExcessHorizontalSpace = true;
		parentData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		parentData.grabExcessVerticalSpace = false;
		parentData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		parentData.grabExcessHorizontalSpace = true;
		parentComposite.setLayout(parentLayout);
		parentComposite.setLayoutData(parentData);
		
		// create Visible-Button
		Button buttonVisible = new Button(parentComposite, SWT.TOGGLE);
		buttonVisible.setSelection(!l.isVisible());
		buttonVisible.setImage(EYE_ICON);
		buttonVisible.setToolTipText(EditorPlugin.getResourceString("layer_visible"));
		buttonVisible.addSelectionListener(visibleListener);
		GridData gridDataV = new GridData();	
		gridDataV.verticalAlignment = GridData.BEGINNING;
		buttonVisible.setLayoutData(gridDataV);
		
		// create Editable-Button
		Button buttonEditble = new Button(parentComposite, SWT.TOGGLE);
		buttonEditble.setSelection(!l.isEditable());		
		buttonEditble.setImage(LOCK_ICON);
		buttonEditble.setToolTipText(EditorPlugin.getResourceString("layer_locked"));		
		buttonEditble.addSelectionListener(editableListener);		
		GridData gridDataE = new GridData();
		gridDataE.horizontalAlignment = org.eclipse.swt.layout.GridData.CENTER;
		gridDataE.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;		
		buttonEditble.setLayoutData(gridDataE);
		
		// create Name-Text		
		Text text = new Text(parentComposite, SWT.PUSH);
		text.addSelectionListener(textListener);
		text.addFocusListener(focusListener);
  	text.setEditable(true);		
		
  	// TODO: Name should already be set when the command is executed
		if (l.getName() == null)
		  text.setText(EditorPlugin.getResourceString("layer_default_name"));
		else 
		  text.setText(l.getName());
				
		GridData gridDataText = new GridData();
		gridDataText.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataText.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
		gridDataText.grabExcessHorizontalSpace = true;		
		text.setLayoutData(gridDataText);
		
		// add newLayer to button2Layer
		button2Layer.put(buttonVisible, l);
		button2Layer.put(buttonEditble, l);
		button2Layer.put(text, l);

		if (l.equals(mldc.getCurrentLayer()))
		{
		  if (currentLayerColor == null)
		    currentLayerColor = new Color(parent.getDisplay(), 155, 155, 155);
		  
		  parentComposite.setBackground(currentLayerColor);
		}			
	}
  
	private void createTools(Composite parent) 
	{	  	  	  
	  // create ParentLayout
		Composite toolsComposite = new Composite(parent, SWT.NONE);
		GridLayout parentLayout = new GridLayout();
		parentLayout.numColumns = 4;
		GridData parentData = new GridData();
		parentData.horizontalAlignment = GridData.END;		
		toolsComposite.setLayout(parentLayout);
		toolsComposite.setLayoutData(parentData);
						
		// create Buttons		
		buttonUp = new Button(toolsComposite, SWT.NONE);
		buttonUp.setText("Up");
//		buttonUp.setImage(UP_ICON);
		buttonUp.setToolTipText(EditorPlugin.getResourceString("layer_up"));
		buttonUp.addSelectionListener(upListener);
		buttonDown = new Button(toolsComposite, SWT.NONE);
		buttonDown.setText("Down");	
//		buttonDown.setImage(DOWN_ICON);
		buttonDown.setToolTipText(EditorPlugin.getResourceString("layer_down"));
		buttonDown.addSelectionListener(downListener);		
		buttonNew = new Button(toolsComposite, SWT.NONE);
		buttonNew.setText("New");
//		buttonNew.setImage(NEW_ICON);
		buttonNew.setToolTipText(EditorPlugin.getResourceString("layer_new"));		
		buttonNew.addSelectionListener(newListener);
		buttonDelete = new Button(toolsComposite, SWT.NONE);
		buttonDelete.setText("Delete");
//		buttonDelete.setImage(DELETE_ICON);
		buttonDelete.setToolTipText(EditorPlugin.getResourceString("layer_delete"));				
		buttonDelete.addSelectionListener(deleteListener);		
	}
	
  /* (non-Javadoc)
   * @see org.eclipse.ui.IWorkbenchPart#setFocus()
   */
  public void setFocus() 
  {
    
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
				} else {
					l.setVisible(false);
				}
				
				editor.getEditPartViewer().getRootEditPart().refresh();
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
				} else {
					l.setEditable(false);
				}
				editor.getEditorSite().getShell().update();
			}
			else {
				throw new IllegalStateException("There is no such Layer registered!");
			}			
		}
	};  
		
	protected SelectionListener newListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("NEW widgetSelected()");
			addLayer();
//			refresh();
		}
	};

	protected SelectionListener deleteListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("DELETE widgetSelected()");
			
		  Layer currentLayer = mldc.getCurrentLayer();
		  DeleteLayerCommand layerCommand = new DeleteLayerCommand(mldc, currentLayer);
		  editor.getOutlineEditDomain().getCommandStack().execute(layerCommand);
		  
//			int index = mldc.getDrawComponents().indexOf(mldc.getCurrentLayer());
//			mldc.getDrawComponents().remove(index);		
//
//      if (index != 0) {
//        mldc.setCurrentLayer((Layer) mldc.getDrawComponents().get(index-1));
//      } else if ( index==0 && mldc.getDrawComponents().size() > 2) {
//        mldc.setCurrentLayer((Layer) mldc.getDrawComponents().get(index+1));
//      }			
						  			
//			refresh();				
		}
	};

	protected SelectionListener upListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("UP widgetSelected()"); 
		}
	};
	
	protected SelectionListener downListener = new SelectionAdapter() 
	{ 
		public void widgetSelected(SelectionEvent e) 
		{    
		  LOGGER.debug("DOWN widgetSelected()"); 
		}
	};
		
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
    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartListener#childAdded(org.eclipse.gef.EditPart, int)
     */
    public void childAdded(EditPart child, int index) {
      if (child instanceof LayerEditPart) {
        MultiLayerDrawComponentEditPart parent = (MultiLayerDrawComponentEditPart) child.getParent();
        mldc = (MultiLayerDrawComponent) parent.getModel();
        refresh();
      }
    }

    /* (non-Javadoc)
     * @see org.eclipse.gef.EditPartListener#removingChild(org.eclipse.gef.EditPart, int)
     */
    public void removingChild(EditPart child, int index) {
      if (child instanceof LayerEditPart) {
        MultiLayerDrawComponentEditPart parent = (MultiLayerDrawComponentEditPart) child.getParent();
        mldc = (MultiLayerDrawComponent) parent.getModel();
        refresh();        
      }
    }
  };
  
//	private KeyAdapter textListener = new KeyAdapter() 
//	{
//    public void keyReleased(KeyEvent e) 
//    {
//      if (e.getSource() instanceof Text) 
//      {
//        Text text = (Text) e.getSource();
//        if (e.keyCode == 13) {
//          String layerName = text.getText();
//          LOGGER.debug("New LayerName = "+layerName);
//          Layer l = (Layer) button2Layer.get(text);
//          l.setName(layerName);
//        }        
//      }
//    }
//  }; 
	
	private void deactivateTools(boolean newButton) 
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
	
	private void activateTools() 
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
      if (layerComposite != null || !layerComposite.isDisposed())
        deactivateTools(false);
    }
    refresh();    
  }

	public void refresh()
	{		
		// TODO instead of create new layerComposite, better remove old entries
		if (scrollComposite != null || !scrollComposite.isDisposed()) 
		{
			button2Layer = new HashMap();
			
	    layerComposite = new Composite(scrollComposite, SWT.NONE);
	    layerComposite.setSize(scrollComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));    
	    scrollComposite.setContent(layerComposite);		
//	    FillLayout layerFillLayout = new FillLayout();
//	    layerFillLayout.type = SWT.VERTICAL;
//	    layerComposite.setLayout(layerFillLayout);		
	    GridLayout layerGridLayout = new GridLayout();
	    layerComposite.setLayout(layerGridLayout);		
					
			if (mldc != null) 
			{
				for (int i = mldc.getDrawComponents().size()-1; i >= 0; --i)
				{
					DrawComponent dc  = (DrawComponent) mldc.getDrawComponents().get(i);
					if (dc instanceof Layer) {
					  Layer l = (Layer) dc; 
					  createLayerEntry(layerComposite, l); 
					} else {
					  LOGGER.debug("dc NOT instanceof Layer, but instanceof "+dc.getClass());
					}
				}
				
//				if (mldc.getDrawComponents().size() <= 1)			  
//				  deactivateTools(true);			  
//				else 
//				  activateTools();
				
				scrollComposite.setMinSize(layerComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));				
				layerComposite.pack();
			}				  
		}		
	}
				
  public void dispose() 
  {
    getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);    
    super.dispose();
  }
}
