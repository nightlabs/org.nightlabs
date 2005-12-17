/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 24.05.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.J2DLightweightSystem;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.J2DScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.PageBook;
import org.holongate.j2d.J2DCanvas;

import org.nightlabs.base.language.LanguageChangeEvent;
import org.nightlabs.base.language.LanguageChangeListener;
import org.nightlabs.base.language.LanguageChooser;
import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.EditorContextMenuProvider;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.dnd.EditorTemplateTransferDropTargetListener;
import org.nightlabs.editor2d.j2dswt.MLDCPaintable;
import org.nightlabs.editor2d.outline.filter.FilterManager;
import org.nightlabs.editor2d.properties.NameLanguageManager;


public class EditorOutlinePage 
extends ContentOutlinePage 
implements IAdaptable 
{
	public static final Logger LOGGER = Logger.getLogger(EditorOutlinePage.class.getName()); 
	
  static final int ID_OUTLINE  = 0;
  static final int ID_OVERVIEW = 1;
  static final int ID_FILTER 	 = 3;
  
  protected AbstractEditor editor;
  protected FilterManager filterMan;  
  protected IAction showFilterAction;
  
  protected PageBook pageBook;
  protected Control outline;
  protected IAction showOutlineAction; 
  protected IAction showOverviewAction;
  protected IAction filterOutlineAction;
  protected Thumbnail thumbnail;
  protected DisposeListener disposeListener;
  
  protected Canvas overview;  
  
  protected LanguageChooser langMan;
  
  public EditorOutlinePage(AbstractEditor editor, EditPartViewer viewer){
    super(viewer);
    this.editor = editor;
    this.filterMan = editor.getFilterManager();
    filterMan.addPropertyChangeListener(filterListener);
    langMan = NameLanguageManager.sharedInstance();
    langMan.addLanguageChangeListener(langListener);
  }
  
	protected LanguageChangeListener langListener = new LanguageChangeListener(){	
		public void languageChanged(LanguageChangeEvent event) {			
//			getViewer().getContents().refresh();
//			getViewer().getControl().update();
//			outline.update();
		}	
	};  
  
  public void init(IPageSite pageSite) {
    super.init(pageSite);
    ActionRegistry registry = editor.getOutlineActionRegistry();
    IActionBars bars = pageSite.getActionBars();
    String id = ActionFactory.UNDO.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    id = ActionFactory.REDO.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    id = ActionFactory.DELETE.getId();
    bars.setGlobalActionHandler(id, registry.getAction(id));
    bars.updateActionBars();
  }

  protected void configureOutlineViewer()
  {
    getViewer().setEditDomain(editor.getOutlineEditDomain());
    getViewer().setEditPartFactory(editor.getOutlineEditPartFactory());
    ContextMenuProvider provider = editor.getContextMenuProvider();      
    getViewer().setContextMenu(provider);
    // TODO: ContextMenu ID Problem      
    getSite().registerContextMenu(
      EditorContextMenuProvider.CONTEXT_MENU_ID, //$NON-NLS-1$
      provider, getSite().getSelectionProvider());
    getViewer().setKeyHandler(editor.getCommonKeyHandler());
    getViewer().addDropTargetListener(
      new EditorTemplateTransferDropTargetListener(getViewer()));
    IToolBarManager tbm = getSite().getActionBars().getToolBarManager();
    
    IMenuManager menuMan = getSite().getActionBars().getMenuManager();
    createFilterEntries(menuMan);    
    
    // Show Outline
    showOutlineAction = new Action() {
      public void run() {
        showPage(ID_OUTLINE);
      }
    };
    showOutlineAction.setImageDescriptor(ImageDescriptor.createFromFile(
      EditorPlugin.class,"icons/outline.gif")); //$NON-NLS-1$
    tbm.add(showOutlineAction);
    
    // Show Overview
    showOverviewAction = new Action() {
      public void run() {
        showPage(ID_OVERVIEW);
      }
    };
    showOverviewAction.setImageDescriptor(ImageDescriptor.createFromFile(
      EditorPlugin.class,"icons/overview.gif")); //$NON-NLS-1$
    tbm.add(showOverviewAction);
        
    showPage(ID_OUTLINE);        
  }
    
  protected IAction createFilterAction(final Class c) 
  {
		IAction filterAction = new Action() {
      public void run() {
        filterMan.setFilter(c);
      }  			
		};
		filterAction.setText(filterMan.getTypeName(c));		
		return filterAction;
  }
  
  protected void createFilterEntries(IMenuManager menuMan) 
  {
		IAction filterNoneAction = new Action() {
      public void run() {
        filterMan.setAllFiltersAvailable();
      }  			
		};
		filterNoneAction.setText(EditorPlugin.getResourceString("action.filter.none"));
		menuMan.add(filterNoneAction);
		
  	for (Iterator it = filterMan.getAllFilters().iterator(); it.hasNext(); ) 
  	{
  		Class c = (Class) it.next();
			IAction filterAction = createFilterAction(c);
			menuMan.add(filterAction);
  	}  	
  }
  
  public void createControl(Composite parent){
    pageBook = new PageBook(parent, SWT.NONE);
    outline = getViewer().createControl(pageBook);
    // TODO J2DCanvas maybe this is only TreeOutline 
//    overview = new Canvas(pageBook, SWT.NONE);
    
//    overview = new J2DCanvas(pageBook, SWT.NONE, new J2DSamplePaintable("TestMessage"));    
    overview = new J2DCanvas(pageBook, SWT.NONE, new MLDCPaintable(editor.getMultiLayerDrawComponent()));    
    pageBook.showPage(outline);
    configureOutlineViewer();
    hookOutlineViewer();
    initializeOutlineViewer();    
  }
  
  public void dispose(){
    unhookOutlineViewer();
    if (thumbnail != null) {
      thumbnail.deactivate();
      thumbnail = null;
    }
    super.dispose();
//    outlinePage = null;
  }
  
  public Object getAdapter(Class type) {
    if (type == ZoomManager.class)
      return editor.getOutlineGraphicalViewer().getProperty(ZoomManager.class.toString());
    return null;
  }

  public Control getControl() {
    return pageBook;
  }

  protected void hookOutlineViewer(){
    editor.getOutlineSelectionSynchronizer().addViewer(getViewer());
  }

  protected void initializeOutlineViewer(){
    setContents(editor.getMultiLayerDrawComponent());
  }
  
//  protected void initializeOverview() 
//  {
//    LightweightSystem lws = new LightweightSystem(overview);
//    RootEditPart rep = editor.getOutlineGraphicalViewer().getRootEditPart();
//    if (rep instanceof ScalableFreeformRootEditPart) {
//      ScalableFreeformRootEditPart root = (ScalableFreeformRootEditPart)rep;
//      thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());
//      thumbnail.setBorder(new MarginBorder(3));
//      thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
//      lws.setContents(thumbnail);
//      disposeListener = new DisposeListener() {
//        public void widgetDisposed(DisposeEvent e) {
//          if (thumbnail != null) {
//            thumbnail.deactivate();
//            thumbnail = null;
//          }
//        }
//      };
//      editor.getEditor().addDisposeListener(disposeListener);
//    }
//  }
  protected void initializeOverview() 
  {
    LightweightSystem lws = new J2DLightweightSystem(overview);
//  	LightweightSystem lws = new J2DLightweightSystem();
    RootEditPart rep = editor.getOutlineGraphicalViewer().getRootEditPart();
    if (rep instanceof J2DScalableFreeformRootEditPart) {
    	J2DScalableFreeformRootEditPart root = (J2DScalableFreeformRootEditPart)rep;
//      thumbnail = new J2DScrollableThumbnail((Viewport)root.getFigure());
      thumbnail = new ScrollableThumbnail((Viewport)root.getFigure());    	
      thumbnail.setBorder(new MarginBorder(3));
      thumbnail.setSource(root.getLayer(LayerConstants.PRINTABLE_LAYERS));
      lws.setContents(thumbnail);
      disposeListener = new DisposeListener() {
        public void widgetDisposed(DisposeEvent e) {
          if (thumbnail != null) {
            thumbnail.deactivate();
            thumbnail = null;
          }
        }
      };
      editor.getEditor().addDisposeListener(disposeListener);
    }
  }
  
  public void setContents(Object contents) {
    getViewer().setContents(contents);
  }
  
  protected void showPage(int id) {
    if (id == ID_OUTLINE) {
      showOutlineAction.setChecked(true);
      showOverviewAction.setChecked(false);
      pageBook.showPage(outline);
      if (thumbnail != null)
        thumbnail.setVisible(false);
    } 
    else if (id == ID_OVERVIEW) {
      if (thumbnail == null)
        initializeOverview();
      showOutlineAction.setChecked(false);
      showOverviewAction.setChecked(true);
      pageBook.showPage(overview);
      thumbnail.setVisible(true);
    }
  }
  
  protected void unhookOutlineViewer(){
    editor.getOutlineSelectionSynchronizer().removeViewer(getViewer());
    if (disposeListener != null && editor.getEditor() != null && !editor.getEditor().isDisposed())
      editor.getEditor().removeDisposeListener(disposeListener);
  }
 
  protected PropertyChangeListener filterListener = new PropertyChangeListener() 
  {	
		public void propertyChange(PropertyChangeEvent pce)
		{
			if (pce.getPropertyName().equals(FilterManager.FILTER_CHANGED)) 
			{
				getViewer().setContents(editor.getMultiLayerDrawComponent());
			}	
			else if (pce.getPropertyName().equals(FilterManager.FILTER_ADDED)) 
			{				
				Object o = pce.getNewValue();
				if (o instanceof Class) {
					IMenuManager menuMan = getSite().getActionBars().getMenuManager();					
					Class c = (Class) o;
					menuMan.add(createFilterAction(c));
					getViewer().setContents(editor.getMultiLayerDrawComponent());					
				}
			}
			else if (pce.getPropertyName().equals(FilterManager.FILTERS_ADDED)) 
			{				
				Object o = pce.getNewValue();
				if (o instanceof Collection) {
					IMenuManager menuMan = getSite().getActionBars().getMenuManager();					
					Collection classes = (Collection) o;
					for (Iterator it = classes.iterator(); it.hasNext(); ) {
						Class c = (Class) it.next(); 
						menuMan.add(createFilterAction(c));						
					}
					getViewer().setContents(editor.getMultiLayerDrawComponent());					
				}
			}			
		}	
	};
	
}
