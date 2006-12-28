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

package org.nightlabs.editor2d;

import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.J2DScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.MatchHeightAction;
import org.eclipse.gef.ui.actions.MatchWidthAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.gef.ui.parts.J2DGraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.properties.UndoablePropertySheetEntry;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IKeyBindingService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.nightlabs.base.i18n.ResolutionUnitEP;
import org.nightlabs.base.i18n.UnitRegistryEP;
import org.nightlabs.base.io.FileEditorInput;
import org.nightlabs.base.io.IOFilterRegistry;
import org.nightlabs.base.io.IOFilterUIInformationProvider;
import org.nightlabs.base.io.InformationProviderWizard;
import org.nightlabs.base.language.LanguageManager;
import org.nightlabs.base.print.page.PredefinedPageEP;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.base.wizard.DynamicPathWizardDialog;
import org.nightlabs.base.wizard.IWizardHop;
import org.nightlabs.editor2d.actions.DeleteAction;
import org.nightlabs.editor2d.actions.EditShapeAction;
import org.nightlabs.editor2d.actions.NormalSelectionAction;
import org.nightlabs.editor2d.actions.RepaintAction;
import org.nightlabs.editor2d.actions.ResetRotationCenterAction;
import org.nightlabs.editor2d.actions.RotateAction;
import org.nightlabs.editor2d.actions.SelectAllWithSameName;
import org.nightlabs.editor2d.actions.ShowDefaultRenderAction;
import org.nightlabs.editor2d.actions.copy.CloneAction;
import org.nightlabs.editor2d.actions.copy.CopyAction;
import org.nightlabs.editor2d.actions.copy.CutAction;
import org.nightlabs.editor2d.actions.copy.PasteAction;
import org.nightlabs.editor2d.actions.group.GroupAction;
import org.nightlabs.editor2d.actions.group.UnGroupAction;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneDown;
import org.nightlabs.editor2d.actions.order.ChangeOrderOneUp;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalBack;
import org.nightlabs.editor2d.actions.order.ChangeOrderToLocalFront;
import org.nightlabs.editor2d.actions.preferences.ShowFigureToolTipAction;
import org.nightlabs.editor2d.actions.preferences.ShowStatusLineAction;
import org.nightlabs.editor2d.actions.shape.ConvertToShapeAction;
import org.nightlabs.editor2d.actions.shape.ShapeExclusiveOrAction;
import org.nightlabs.editor2d.actions.shape.ShapeIntersectAction;
import org.nightlabs.editor2d.actions.shape.ShapeSubtractAction;
import org.nightlabs.editor2d.actions.shape.ShapeUnionAction;
import org.nightlabs.editor2d.actions.zoom.ZoomAllAction;
import org.nightlabs.editor2d.actions.zoom.ZoomPageAction;
import org.nightlabs.editor2d.actions.zoom.ZoomSelectionAction;
import org.nightlabs.editor2d.edit.MultiLayerDrawComponentEditPart;
import org.nightlabs.editor2d.figures.BufferedFreeformLayer;
import org.nightlabs.editor2d.impl.LayerImpl;
import org.nightlabs.editor2d.outline.EditorOutlinePage;
import org.nightlabs.editor2d.outline.filter.FilterManager;
import org.nightlabs.editor2d.outline.filter.NameProvider;
import org.nightlabs.editor2d.preferences.Preferences;
import org.nightlabs.editor2d.print.EditorPrintAction;
import org.nightlabs.editor2d.print.EditorPrintSetupAction;
import org.nightlabs.editor2d.properties.EditorPropertyPage;
import org.nightlabs.editor2d.properties.UnitManager;
import org.nightlabs.editor2d.render.RenderModeManager;
import org.nightlabs.editor2d.rulers.EditorRulerProvider;
import org.nightlabs.editor2d.unit.DotUnit;
import org.nightlabs.editor2d.unit.UnitConstants;
import org.nightlabs.editor2d.viewer.descriptor.DescriptorManager;
import org.nightlabs.editor2d.viewer.render.RendererRegistry;
import org.nightlabs.i18n.unit.IUnit;
import org.nightlabs.i18n.unit.UnitRegistry;
import org.nightlabs.i18n.unit.resolution.IResolutionUnit;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;
import org.nightlabs.i18n.unit.resolution.ResolutionUnitRegistry;
import org.nightlabs.io.AbstractIOFilterWithProgress;
import org.nightlabs.io.IOFilter;
import org.nightlabs.io.IOFilterInformationProvider;
import org.nightlabs.io.IOFilterMan;
import org.nightlabs.io.IOFilterWithProgress;
import org.nightlabs.io.WriteException;
import org.nightlabs.print.page.IPredefinedPage;
import org.nightlabs.print.page.PredefinedPageRegistry;
import org.nightlabs.print.page.PredefinedPageUtil;


public abstract class AbstractEditor 
extends J2DGraphicalEditorWithFlyoutPalette
//extends GraphicalEditorWithFlyoutPalette
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(Editor.class);

	private boolean savePreviouslyNeeded = false;
	private RulerComposite rulerComp;

	private TreeViewer treeViewer;    
	private EditorOutlinePage outlinePage;
	private boolean editorSaving = false;

	protected void closeEditor(boolean save) 
	{
		getSite().getPage().closeEditor(AbstractEditor.this, save);      
		System.gc();			
	}

	public Object getModel() {
		return getMultiLayerDrawComponent();
	}

	private UnitManager unitManager = null;
	public UnitManager getUnitManager() 
	{
		if (unitManager == null) 
		{
			// TODO: update dotunit if resolution changes
			Collection<IUnit> units = UnitRegistryEP.sharedInstance().getUnitRegistry().getUnits(
					UnitConstants.UNIT_CONTEXT_EDITOR2D, true);
			unitManager = new UnitManager(new HashSet<IUnit>(units), 
					getMultiLayerDrawComponent().getModelUnit());
		}
		return unitManager;
	}

	protected abstract MultiLayerDrawComponent createMultiLayerDrawComponent();    
	private MultiLayerDrawComponent mldc = null;  
	public MultiLayerDrawComponent getMultiLayerDrawComponent() 
	{
		if (mldc == null) {
			mldc = createMultiLayerDrawComponent();
		}      
		return mldc;
	}

	protected abstract EditPartFactory createEditPartFactory(); 
	private EditPartFactory editPartFactory = null;
	public EditPartFactory getEditPartFactory() 
	{
		if (editPartFactory == null)
			editPartFactory = createEditPartFactory();;

		return editPartFactory;
	}      

	protected abstract EditPartFactory createOutlineEditPartFactory();
	private EditPartFactory outlineEditPartFactory = null;
	public EditPartFactory getOutlineEditPartFactory() 
	{
		if (outlineEditPartFactory == null)
			outlineEditPartFactory = createOutlineEditPartFactory();

		return outlineEditPartFactory;
	}     

	protected abstract ContextMenuProvider createContextMenuProvider();    
	private ContextMenuProvider contextMenuProvider = null;
	public ContextMenuProvider getContextMenuProvider() 
	{
		if (contextMenuProvider == null)
			contextMenuProvider = createContextMenuProvider();

		return contextMenuProvider;
	}    

	private PaletteRoot paletteRoot = null;
	public PaletteRoot getPaletteRoot() 
	{
		if (paletteRoot == null) {
			paletteRoot = getPaletteFactory().createPalette();
		}
		return paletteRoot;
	}

	/** KeyHandler with common bindings for both the Outline View and the Editor. */
	private KeyHandler sharedKeyHandler;

	private RenderModeManager renderMan = null;
	public RenderModeManager getRenderModeManager() {
		return renderMan;
	}     

	private FilterManager filterMan = null;
	public FilterManager getFilterManager() {
		return filterMan;
	}

	protected abstract NameProvider createNameProvider();

	private NameProvider nameProvider = null;
	public NameProvider getFilterNameProvider() {
		if (nameProvider == null)
			nameProvider = createNameProvider();
		return nameProvider;
	}    

	private IOFilterMan ioFilterMan;
	public IOFilterMan getIOFilterMan() 
	{
		if (ioFilterMan == null)
			ioFilterMan = IOFilterRegistry.sharedInstance().getIOFilterMan();

		return ioFilterMan;
	}

	private LanguageManager langMan;
	public LanguageManager getLanguageManager() 
	{
		if (langMan == null)
			langMan = new LanguageManager();

		return langMan;
	}

	/** Create a new Editor instance. This is called by the Workspace. */
	public AbstractEditor() 
	{
		init();
//		setEditDomain(new DefaultEditDomain(this));            
		filterMan = new FilterManager(getFilterNameProvider()); 
//		initJ2DRegistry();
	}
	
	protected void init() {

	}
	
	protected DefaultEditDomain getEditDomain() 
	{
		 if (super.getEditDomain() == null) {
			 setEditDomain(new DefaultEditDomain(this));
		 }
		 return super.getEditDomain();
	}
	
//	protected void initJ2DRegistry() 
//	{
//	Map hints = new HashMap();
//	hints.put(J2DGraphics.KEY_FIXED_LINEWIDTH, true);
//	hints.put(J2DGraphics.KEY_USE_JAVA2D, true);
//	J2DRegistry.setHints(hints);    	    	
//	}

	protected MultiLayerDrawComponent load(IOFilter ioFilter, InputStream input) 
	{      
		if (ioFilter != null) 
		{
			try 
			{
				MultiLayerDrawComponent mldc = (MultiLayerDrawComponent) ioFilter.read(input);
				mldc.setRenderModeManager(getRenderModeManager());
				return mldc;        	
			} 
			catch (IOException e) {
				throw new RuntimeException("There occured an Error while reading with IOFilter "+ioFilter+" from InpuStream "+input, e);
			}          
		}
		return null;
	}
	
	protected void load(FileEditorInput fileInput, IProgressMonitor monitor) 
	{
		IOFilter ioFilter = getIOFilterMan().getIOFilter(fileInput.getFile());
		if (ioFilter != null) 
		{
			try 
			{    		
				if (ioFilter instanceof IOFilterWithProgress) 
				{	    			
					IOFilterWithProgress progressFilter = (IOFilterWithProgress) ioFilter;
					progressFilter.addPropertyChangeListener(progressListener);	    			
					monitor.beginTask(EditorPlugin.getResourceString("resource.load") +" "+ fileInput.getName(), progressFilter.getTotalWork());
					mldc = load(ioFilter, new FileInputStream(fileInput.getFile()));
					progressFilter.removePropertyChangeListener(progressListener);
					return;
				}
				else
					monitor.beginTask(EditorPlugin.getResourceString("resource.load") +" "+ fileInput.getName(), 2);	    			
				mldc = load(ioFilter, new FileInputStream(fileInput.getFile()));
				return;
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			} finally {
				monitor.done();
			}
		}
	}

	protected void load(FileEditorInput fileInput) 
	{    	
		final FileEditorInput input = fileInput;
		IOFilter ioFilter = getIOFilterMan().getIOFilter(fileInput.getFile());
		prepareInformationProvider(fileInput.getFile(), ioFilter, false);
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{			
			public void run(IProgressMonitor monitor) 
			throws InvocationTargetException, InterruptedException 
			{
				try {
					load(input, monitor);      
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}					
			}			
		};

		try {
			getProgressMonitor().run(false, false, runnable);
			setPartName(input.getName());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}    	    	
	}
		
	private ScalableFreeformRootEditPart rootEditPart;
	public ScalableFreeformRootEditPart getRootEditPart() 
	{
		if (rootEditPart == null)
			rootEditPart = new J2DScalableFreeformRootEditPart();

		return rootEditPart;
	}

	private ViewerManager viewerManager;
	public ViewerManager getViewerManager() {
		return viewerManager;
	}

	private DescriptorManager descriptorManager;
	public DescriptorManager getDescriptorManager() {
		return descriptorManager;
	}

	protected void configureGraphicalViewer() 
	{
		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)getGraphicalViewer();

		List zoomLevels = new ArrayList(3);
		zoomLevels.add(ZoomManager.FIT_ALL);
		zoomLevels.add(ZoomManager.FIT_WIDTH);
		zoomLevels.add(ZoomManager.FIT_HEIGHT);
		getRootEditPart().getZoomManager().setZoomLevelContributions(zoomLevels);

		viewer.setRootEditPart(getRootEditPart());

		viewer.setEditPartFactory(getEditPartFactory());
		ContextMenuProvider provider = getContextMenuProvider();
		viewer.setContextMenu(provider);
		getSite().registerContextMenu("org.nightlabs.editor2d.contextmenu", //$NON-NLS-1$
				provider, viewer);
		viewer.setKeyHandler(new EditorViewerKeyHandler(viewer)
		.setParent(getCommonKeyHandler()));

		loadProperties();

		// Actions
		IAction showRulers = new ToggleRulerVisibilityAction(getGraphicalViewer());
		getActionRegistry().registerAction(showRulers);

		IAction snapAction = new ToggleSnapToGeometryAction(getGraphicalViewer());
		getActionRegistry().registerAction(snapAction);

		IAction showGrid = new ToggleGridAction(getGraphicalViewer());
		getActionRegistry().registerAction(showGrid);

		Listener listener = new Listener() {
			public void handleEvent(Event event) {
				handleActivationChanged(event);
			}
		};
		getGraphicalControl().addListener(SWT.Activate, listener);
		getGraphicalControl().addListener(SWT.Deactivate, listener);  

//		// TODO Workaround to fix grey bg in editor
//		getGraphicalControl().setBackground(new Color(null, 255, 255, 255));

		// ViewerManager
		viewerManager = new ViewerManager(viewer, getEditorSite().getActionBars().getStatusLineManager());
		configureViewerManager();  

		getGraphicalControl().addControlListener(resizeListener);
//		getCommandStack().addCommandStackEventListener(commandStackListener);
	}

	// should solve redraw problems when resizing the viewer
	private ControlListener resizeListener = new ControlAdapter(){		
		public void controlResized(ControlEvent e) {
			updateViewer();
			logger.debug("Control resized!");
		}		
	};
			
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
	 */    
	public void commandStackChanged(EventObject event) 
	{
		if (isDirty()){
			if (!savePreviouslyNeeded()) {
				setSavePreviouslyNeeded(true);
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		}
		else {
			setSavePreviouslyNeeded(false);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		super.commandStackChanged(event);
		// should solve redraw problems when undoing things
		updateViewer();
	}  

	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
	 */
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
//				// create a drag source listener for this palette viewer
//				// together with an appropriate transfer drop target listener, this will enable
//				// model element creation by dragging a CombinatedTemplateCreationEntries 
//				// from the palette into the editor
//				// @see ShapesEditor#createTransferDropTargetListener()
//				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
			}
		};
	}

	public Object getAdapter(Class type)
	{
		if (type == IContentOutlinePage.class) {
			treeViewer = new TreeViewer();
			outlinePage = new EditorOutlinePage(this, treeViewer);        
			return outlinePage;
		}
		
		if (type == ZoomManager.class) {
			return getZoomManager();
		}			
		
		if (type == org.eclipse.ui.views.properties.IPropertySheetPage.class) {
			PropertySheetPage page = new EditorPropertyPage(getUnitManager());    		
			page.setRootEntry(new UndoablePropertySheetEntry(getCommandStack()));
			return page;
		}

		if (type == RenderModeManager.class)
			return getRenderModeManager();

		if (type == MultiLayerDrawComponent.class)
			return getMultiLayerDrawComponent();

		return super.getAdapter(type);
	}

	protected Control getGraphicalControl() {
		return rulerComp;
	}

	private ZoomManager zoomManager = null;
	protected ZoomManager getZoomManager() 
	{
		if (zoomManager == null && getGraphicalViewer() != null) 
		{
			Object zoomAdapter = getGraphicalViewer().getProperty(ZoomManager.class.toString());
		  if (zoomAdapter != null && zoomAdapter instanceof ZoomManager) {
  			zoomManager = ((ZoomManager)zoomAdapter);  
		  }			
		}
		return zoomManager;		
	}
	
	public void doSave(IProgressMonitor monitor) 
	{
		try {      	
			FileEditorInput input = (FileEditorInput) getEditorInput();
			if (!input.isSaved()) {
				doSaveAs();
				return;
			}

			File file = input.getFile();
			String inputName = input.getName();
			logger.debug("inputName = "+inputName);

			if (file.exists() 
					|| org.eclipse.jface.dialogs.MessageDialogWithToggle.openConfirm(getSite().getShell(),
							EditorPlugin.getResourceString("resource.create.file"),
							EditorPlugin.getResourceString("resource.fileNotExists.1")
							+ " "
							+ file.getName() 
							+ " "
							+ EditorPlugin.getResourceString("resource.fileNotExists.2")))
			{
				editorSaving = true;
				saveProperties();
				save(file, monitor);
				getCommandStack().markSaveLocation();
			}
		}
		catch (WriteException e){
			throw new RuntimeException(e);      	
		}   
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.ISaveablePart#doSaveAs()
	 */
	public void doSaveAs() 
	{
		performSaveAs();      
	}

	/**
	 * Returns the KeyHandler with common bindings for both the Outline and Graphical Views.
	 * For example, delete is a common action.
	 */
	public KeyHandler getCommonKeyHandler() 
	{
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();

			// Add key and action pairs to sharedKeyHandler
			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.DEL, 127, 0),
					getActionRegistry().getAction(ActionFactory.DELETE.getId()));
			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
			// TODO: register more common keys        
		}
		return sharedKeyHandler;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPalettePreferences()
	 */
	protected FlyoutPreferences getPalettePreferences() {
		return getPaletteFactory().createPalettePreferences();
	}

	protected abstract AbstractPaletteFactory createPaletteFactory();   

	private AbstractPaletteFactory paletteFactory = null;
	public AbstractPaletteFactory getPaletteFactory() 
	{
		if (paletteFactory == null) {
			paletteFactory = createPaletteFactory();
		}
		return paletteFactory;
	}

	protected void handleActivationChanged(Event event) 
	{
		IAction copy = null;
		if (event.type == SWT.Deactivate)
			copy = getActionRegistry().getAction(ActionFactory.COPY.getId());
		if (getEditorSite().getActionBars().getGlobalActionHandler(ActionFactory.COPY.getId()) 
				!= copy) {
			getEditorSite().getActionBars().setGlobalActionHandler(
					ActionFactory.COPY.getId(), copy);
			getEditorSite().getActionBars().updateActionBars();
		}
	}  

	private Color bgColor = new Color(null, 255, 255, 255);
	public Color getBackgroundColor() {
		return bgColor;
	}
	public void setBackgroundColor(Color bgColor) {
		this.bgColor = bgColor;
		getGraphicalViewer().getControl().setBackground(bgColor);
	}
	
	/**
	 * Set up the editor's inital content (after creation).
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	protected void initializeGraphicalViewer() 
	{
		GraphicalViewer graphicalViewer = getGraphicalViewer();
		graphicalViewer.setContents(getModel()); // set the contents of this editor

		setBackgroundColor(bgColor);
		
		// DescriptorManager
		descriptorManager = new DescriptorManager();
		configureDescriptorManager();      
		if (getModelRootEditPart() != null) {
			getModelRootEditPart().setDescriptorManager(getDescriptorManager());
		} else {
			logger.debug("DescriptorManager for MultiLayerDrawComponentEditPart not set, because it is null!");
		}
		viewerManager.setDescriptorManager(getDescriptorManager());
		
		// FilterManager
		configureFilterManager();      
	}

	protected void initializeActionRegistry() 
	{
		super.initializeActionRegistry();

		// TODO: find out why global keyBindings not work only on base of Extension-Points
		// (org.eclipse.ui.bindings + commands) nor on EditorActionBarContributor.declareGlobalActionKeys()  
		IKeyBindingService keyBindingService = getSite().getKeyBindingService();
		for (Iterator<IAction> it = getActionRegistry().getActions(); it.hasNext(); ) {
			keyBindingService.registerAction(it.next());
		} 	
	}     

	protected void configureFilterManager() 
	{
		Map class2DrawComponents = getMultiLayerDrawComponent().getClass2DrawComponents();
		for (Iterator it = class2DrawComponents.keySet().iterator(); it.hasNext(); ) {
			Class c = (Class) it.next();
			getFilterManager().addFilter(c);
		}
		getMultiLayerDrawComponent().addPropertyChangeListener(getFilterManager().getTypeListener());    		
		getFilterManager().ignoreClass(LayerImpl.class);    	
	}

	/**
	 * By Default this Method does nothing, but Inheritans can override this Method to define 
	 * excluded EditParts, ignored classes or an exclusive class
	 * 
	 * @see org.nightlabs.editor2d.ViewerManager
	 */
	protected void configureViewerManager()
	{

	}

	/**
	 * By Default this Method does nothing, but Inheritans can override this Method to add 
	 * Descriptors for special classes to the DescriptorManager
	 * 
	 * @see DescriptorManager#addDescriptor(org.nightlabs.editor2d.viewer.descriptor.IDrawComponentDescriptor, Class) 
	 */
	protected void configureDescriptorManager() 
	{

	}

	protected EditorActionBarContributor getEditorActionBarContributor() 
	{
		if (getEditorSite().getActionBarContributor() != null && 
				getEditorSite().getActionBarContributor() instanceof EditorActionBarContributor) 
		{
			return (EditorActionBarContributor) getEditorSite().getActionBarContributor();			
		}
		return null;
	}
	
	protected void createActions() 
	{
		super.createActions();
		ActionRegistry registry = getActionRegistry();
		IAction action;

		// Match Actions
		action = new MatchWidthAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new MatchHeightAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

//		action = new DirectEditAction((IWorkbenchPart)this);
//		registry.registerAction(action);
//		getSelectionActions().add(action.getId());

		// Alignment Actions
		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.LEFT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.RIGHT);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.TOP);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.BOTTOM);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.CENTER);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new AlignmentAction((IWorkbenchPart)this, PositionConstants.MIDDLE);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// ZoomIn
		action = new ZoomInAction(getRootEditPart().getZoomManager());
		registry.registerAction(action);      
		getSite().getKeyBindingService().registerAction(action);

		// ZoomOut
		action = new ZoomOutAction(getRootEditPart().getZoomManager());
		registry.registerAction(action);      
		getSite().getKeyBindingService().registerAction(action);

		// Zoom All
		action = new ZoomAllAction(getRootEditPart().getZoomManager());
		registry.registerAction(action);      

		// Zoom Selection
		action = new ZoomSelectionAction(this);      
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Zoom Page
		action = new ZoomPageAction(this, getRootEditPart().getZoomManager());      
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

		// Edit Shape Action
		action = new EditShapeAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Rotate Action
		action = new RotateAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Normal Selection Action
		action = new NormalSelectionAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());   

		// Reset Rotation Center Action
		action = new ResetRotationCenterAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Show Default View (Renderer) Action
		action = new ShowDefaultRenderAction(this);
		registry.registerAction(action);

		// Select all with same name
		action = new SelectAllWithSameName(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());    

		// Clone (Duplicate) Action
		action = new CloneAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());   

		// Order Actions
		action = new ChangeOrderToLocalFront(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());    

		action = new ChangeOrderToLocalBack(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());    

		action = new ChangeOrderOneDown(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());    

		action = new ChangeOrderOneUp(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());    

		// Paste Action
		PasteAction pasteAction = new PasteAction(this);
		registry.registerAction(pasteAction);
		getPropertyActions().add(pasteAction.getId());
		getSite().getKeyBindingService().registerAction(pasteAction);

		// Cut Action
		CutAction cutAction = new CutAction(this);
		registry.registerAction(cutAction);
		getSelectionActions().add(cutAction.getId());
		cutAction.addPropertyChangeListener(pasteAction.cutListener);
		getSite().getKeyBindingService().registerAction(cutAction);      

		// Copy Action
		CopyAction copyAction = new CopyAction(this);
		registry.registerAction(copyAction);
		getSelectionActions().add(copyAction.getId());  
		copyAction.addPropertyChangeListener(pasteAction.copyListener);
		getSite().getKeyBindingService().registerAction(copyAction);      

		// Tooltip Preference 
		action = new ShowFigureToolTipAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

		// Status Line Preference
		action = new ShowStatusLineAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

		// Repaint Action
		action = new RepaintAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

//		// Test Viewer Action
//		action = new ViewerAction(this);
//		registry.registerAction(action);

		// Print Action
		action = new EditorPrintAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

		// Print Preview Action
//		action = new EditorPrintPreviewAction(this);
//		registry.registerAction(action);
//		getPropertyActions().add(action.getId());

		// Print Page Setup Action
		action = new EditorPrintSetupAction(this);
		registry.registerAction(action);
		getPropertyActions().add(action.getId());

		// Group Action
		action = new GroupAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// UnGroup Action
		action = new UnGroupAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Convert To Shape
		action = new ConvertToShapeAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		// Shape Union
		action = new ShapeUnionAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());            

		// Shape Intersection
		action = new ShapeIntersectAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());            

		// Shape Subtract
		action = new ShapeSubtractAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());            

		// Shape Exclusive Or
		action = new ShapeExclusiveOrAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());     

		// Delete
		action = new DeleteAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());      
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#createGraphicalViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected void createGraphicalViewer(Composite parent) 
	{
		long start = System.currentTimeMillis();
		rulerComp = new RulerComposite(parent, SWT.NONE);		
		super.createGraphicalViewer(rulerComp);
		rulerComp.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
		if (logger.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - start;
			logger.debug("createGraphicalViewer took "+duration+" ms!");
		}
	}  

	public FigureCanvas getEditor(){
		return (FigureCanvas)getGraphicalViewer().getControl();
	}

	public boolean isDirty() {
		return isSaveOnCloseNeeded();
	}

	public boolean isSaveOnCloseNeeded() {
		return getCommandStack().isDirty();
	}

	protected void loadProperties() 
	{
		// Ruler properties
		EditorRuler ruler = getMultiLayerDrawComponent().getLeftRuler();
		RulerProvider provider = null;
		if (ruler != null) {
			provider = new EditorRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER, provider);
		ruler = getMultiLayerDrawComponent().getTopRuler();
		provider = null;
		if (ruler != null) {
			provider = new EditorRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_RULER_VISIBILITY, 
				new Boolean(getMultiLayerDrawComponent().isRulersEnabled()));

		// Snap to Geometry property
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED, 
				new Boolean(getMultiLayerDrawComponent().isSnapToGeometry()));

		// Grid properties
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, 
				new Boolean(getMultiLayerDrawComponent().isGridEnabled()));
		// We keep grid visibility and enablement in sync
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, 
				new Boolean(getMultiLayerDrawComponent().isGridEnabled()));

		// Zoom
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
		.getProperty(ZoomManager.class.toString());
		if (manager != null)
			manager.setZoom(getMultiLayerDrawComponent().getZoom());      	            
	}

	/**
	 * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}

	protected boolean savePreviouslyNeeded() {
		return savePreviouslyNeeded;
	}

	protected void saveProperties() 
	{
		getMultiLayerDrawComponent().setRulersEnabled(((Boolean)getGraphicalViewer()
				.getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY)).booleanValue());
		getMultiLayerDrawComponent().setGridEnabled(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
		getMultiLayerDrawComponent().setSnapToGeometry(((Boolean)getGraphicalViewer()
				.getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
		ZoomManager manager = (ZoomManager)getGraphicalViewer()
		.getProperty(ZoomManager.class.toString());
		if (manager != null)
			getMultiLayerDrawComponent().setZoom(manager.getZoom());
	}

	protected void setSavePreviouslyNeeded(boolean value) {
		savePreviouslyNeeded = value;
	}  

	protected boolean performSaveAs() 
	{
		FileDialog dialog = new FileDialog(getSite().getWorkbenchWindow().getShell(), SWT.SAVE);
		String inputFileName = getEditorInput().getName();
		dialog.setFileName(inputFileName);      
//		String[] fileExtensions = getIOFilterMan().getAvailableFileExtensionsAsStrings();      
		String[] fileExtensions = getIOFilterMan().getWriteFileExtensions(true);
		if (fileExtensions != null)
			dialog.setFilterExtensions(fileExtensions);

		String fullPath = dialog.open();
		// Cancel pressed
		if (fullPath == null)
			return false;

		final File file = new File(fullPath);  

		if (!file.exists()) {
			save(file);
		}
		else {
			int returnVal = RCPUtil.showConfirmOverwriteDialog(file.getName());
			if (returnVal == SWT.OK)
				save(file);
			else 
				return false;
		}
		try {
			getCommandStack().markSaveLocation();
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		return true;
	}

	private PropertyChangeListener progressListener = new PropertyChangeListener()
	{
		public void propertyChange(PropertyChangeEvent evt) 
		{
			Object newValue = evt.getNewValue();
			String propertyName = evt.getPropertyName();
			if (propertyName.equals(AbstractIOFilterWithProgress.PROGRESS_CHANGED)) {
				int work = ((Integer)newValue).intValue();
				getProgressMonitor().getProgressMonitor().internalWorked(work); 
			}
			else if (propertyName.equals(AbstractIOFilterWithProgress.SUBTASK_FINISHED)) {
				String subTaskName = (String) newValue;
				getProgressMonitor().getProgressMonitor().subTask(subTaskName);  				
			}
		}			
	};

	private ProgressMonitorDialog progressMonitor; 
	protected ProgressMonitorDialog getProgressMonitor() 
	{
		if (progressMonitor == null) {
			progressMonitor = new ProgressMonitorDialog(getSite().getWorkbenchWindow().getShell());    		
		}
		return progressMonitor;
	}

	protected void prepareInformationProvider(File f, IOFilter ioFilter, boolean write) 
	{
		if (ioFilter != null) 
		{
			IOFilterInformationProvider ip = ioFilter.getInformationProvider();
			if (ip != null) 
			{
				URL url;
				try {
					url = f.toURL();
					ip.setURL(url);	
					if (logger.isDebugEnabled())
						logger.debug("url = "+url);
						
					if (ip instanceof IOFilterUIInformationProvider) 
					{
						IOFilterUIInformationProvider uiip = (IOFilterUIInformationProvider) ip;
						IWizardHop hop = null;
						if (write)
							hop = uiip.getWizardHopForWrite();
						else
							hop = uiip.getWizardHopForRead();
						if (hop != null) 
						{
							InformationProviderWizard wizard = new InformationProviderWizard(url);
							wizard.addDynamicWizardPage(hop.getEntryPage());
							DynamicPathWizardDialog dialog = new DynamicPathWizardDialog(wizard);
							int returnCode = dialog.open(); 
							if (returnCode == DynamicPathWizardDialog.OK) {
								
							}
						}
					}										
				} catch (MalformedURLException e) {
					logger.warn("fileInput.getFile() "+f+" could not be transformed into ULR", e);
					logger.warn("prepartion of IOFilterInformationProvider for IOFilter "+ioFilter.getName().getText(Locale.getDefault().getLanguage()) +"failed!");
				}
			}
		}		
	}
	
	protected void save(File f) 
	{
		final File file = f;   
		IOFilter ioFilter = getIOFilterMan().getIOFilter(file); 
		prepareInformationProvider(file, ioFilter, true);
		IRunnableWithProgress runnable = new IRunnableWithProgress()
		{			
			public void run(IProgressMonitor monitor) 
			throws InvocationTargetException, InterruptedException 
			{
				saveProperties();
				try {
					save(file, monitor);      
				} 
				catch (Exception e) {
					throw new RuntimeException(e);
				}					
			}			
		};

		try {
			getProgressMonitor().run(false, false, runnable);
			setPartName(file.getName());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}    	
	}    

	/**
	 * Saves the Model under the specified path.
	 * 
	 * @param file the file to save
	 * @param progressMonitor The ProgressMonitor to show the save Progress
	 */
	protected void save(File file, IProgressMonitor progressMonitor)
	throws WriteException
	{
		if (null == progressMonitor)
			progressMonitor = new NullProgressMonitor();

		// use IOFilterMan
		IOFilter ioFilter = getIOFilterMan().getIOFilter(file);      
		if (ioFilter != null) 
		{
			try {
				if (ioFilter instanceof IOFilterWithProgress) {      		
					IOFilterWithProgress progressFilter = (IOFilterWithProgress) ioFilter;
					progressMonitor.beginTask(EditorPlugin.getResourceString("resource.save") +" "+ file.getName(), progressFilter.getTotalWork());      		
					progressFilter.addPropertyChangeListener(progressListener);      		
					saveFile(file, progressFilter, progressMonitor);
					progressFilter.removePropertyChangeListener(progressListener); 
				}
				else {
					progressMonitor.beginTask(EditorPlugin.getResourceString("resource.save") +" "+ file.getName(), 2);      		
					saveFile(file, ioFilter, progressMonitor);      		
				}      		
			} finally {
				progressMonitor.done();
			}
		}
	}     

	protected void saveFile(File file, IOFilter ioFilter, IProgressMonitor monitor) 
	throws WriteException
	{
		try {        	
			String fileName = file.getCanonicalPath();
			logger.info("Save File "+fileName);
			FileOutputStream fos = new FileOutputStream(fileName);          
			ioFilter.write(getMultiLayerDrawComponent(), fos);      		
		} catch (Exception e) {
//			throw new WriteException(file, "an error occured while writing", e);
			throw new RuntimeException(e);
		}    	
	}

	protected void setInput(IEditorInput input)
	{ 
		long start = System.currentTimeMillis();
		super.setInput(input);
		renderMan = RendererRegistry.sharedInstance().getRenderModeManager();

		if (input instanceof FileEditorInput) {
			FileEditorInput fileInput = (FileEditorInput) input; 
			mldc = getMultiLayerDrawComponent();        
			if (!fileInput.isSaved()) {
				initialzePage();
			} else {        	
				load(fileInput);        	
			}            	      	
		} 
		else
			initialzePage();

		mldc.setRenderModeManager(getRenderModeManager());      
		getMultiLayerDrawComponent().setLanguageId(getLanguageManager().getCurrentLanguageID());
		getUnitManager().setCurrentUnit(getMultiLayerDrawComponent().getModelUnit());
		if (logger.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - start;
			logger.debug("setInput() took "+duration+" ms!");
		}
	}

	protected void initialzePage() 
	{
		logger.debug("initialize Page!");
		mldc = getMultiLayerDrawComponent();
		loadAdditional();

		String pageID = Preferences.getPreferenceStore().getString(
				Preferences.PREF_PREDEFINED_PAGE_ID);
		IPredefinedPage defaultPage = getPredefinedPageRegistry().getPage(pageID);
		IUnit pageUnit = defaultPage.getUnit();    	
		String resolutionUnitID = Preferences.getPreferenceStore().getString(
				Preferences.PREF_STANDARD_RESOLUTION_UNIT_ID);
		IResolutionUnit resUnit = getResolutionUnitRegistry().getResolutionUnit(resolutionUnitID);
		Resolution resolution = new ResolutionImpl(resUnit, 
				Preferences.getPreferenceStore().getDouble(Preferences.PREF_DOCUMENT_RESOLUTION)); 
		
		DotUnit dotUnit = (DotUnit) getUnitRegistry().getUnit(DotUnit.UNIT_ID);
		if (dotUnit == null) {
			dotUnit = new DotUnit(resolution);
			getUnitRegistry().addUnit(dotUnit, UnitConstants.UNIT_CONTEXT_EDITOR2D);			
		}
		else 
			dotUnit.setResolution(resolution);    	

		String unitID = Preferences.getPreferenceStore().getString(Preferences.PREF_STANDARD_UNIT_ID);
		getUnitManager().setCurrentUnit(getUnitRegistry().getUnit(unitID));
		
		Rectangle pageBounds = PredefinedPageUtil.getPageBounds(dotUnit, defaultPage);
		getMultiLayerDrawComponent().setResolution(resolution);  	  	
		getMultiLayerDrawComponent().getCurrentPage().setPageBounds(pageBounds); 	  	
	}	
	
	protected ResolutionUnitRegistry getResolutionUnitRegistry() {
		return ResolutionUnitEP.sharedInstance().getResolutionUnitRegistry();
	}
	
	protected UnitRegistry getUnitRegistry() {
		return UnitRegistryEP.sharedInstance().getUnitRegistry();
	}
	
	protected PredefinedPageRegistry getPredefinedPageRegistry() {
		return PredefinedPageEP.sharedInstance().getPageRegistry();
	}
	
	protected void loadAdditional() {
		if (!editorSaving) {
			if (getGraphicalViewer() != null) {
				getGraphicalViewer().setContents(getMultiLayerDrawComponent());
				loadProperties();
			}        
		}    	
	}

	public EditPartViewer getEditPartViewer() {
		return (EditPartViewer) getGraphicalViewer();
	}

	public void updateViewer() 
	{
		refreshBuffer();    	
		getGraphicalViewer().getControl().redraw();
		logger.debug("updateViewer!");
	}

	protected void refreshBuffer() 
	{
		MultiLayerDrawComponentEditPart mldcEditPart = getModelRootEditPart();
		if (mldcEditPart != null) {
			BufferedFreeformLayer buffer = mldcEditPart.getBufferedFreeformLayer();
			if (buffer != null) {
//				LOGGER.debug("Buffer refreshed!");
				buffer.refresh();
			}			    		
		}
	}

	private MultiLayerDrawComponentEditPart mldcEditPart = null;
	protected MultiLayerDrawComponentEditPart getModelRootEditPart() 
	{
		if (getRootEditPart().getChildren().size() == 1) {
			EditPart editPart = (EditPart) getRootEditPart().getChildren().get(0);
			if (editPart != null) {
				if (editPart instanceof MultiLayerDrawComponentEditPart) {
					return mldcEditPart = (MultiLayerDrawComponentEditPart) editPart;
				}
			}
		}    		
		return mldcEditPart;
	}

//	**************** BEGIN public Methods for EditorOutlinePage ******************** 
	public GraphicalViewer getOutlineGraphicalViewer() {
		return getGraphicalViewer();
	}

	public SelectionSynchronizer getOutlineSelectionSynchronizer() {
		return getSelectionSynchronizer();
	}

	public DefaultEditDomain getOutlineEditDomain() {
		return getEditDomain();
	}

	public ActionRegistry getOutlineActionRegistry() {
		return getActionRegistry();
	}
//	**************** END public Methods for EditorOutlinePage **********************  

	public void dispose() 
	{
		super.dispose();
		
		getGraphicalControl().removeControlListener(resizeListener);
		
		mldc.dispose();
		mldc = null;
		
		if (outlinePage != null)
			outlinePage.dispose();
		outlinePage = null;
		
		if (rootEditPart != null)
			rootEditPart.deactivate();
		rootEditPart = null;
		
		if (mldcEditPart != null)
			mldcEditPart.deactivate();
		mldcEditPart = null;
				
		if (rulerComp != null)
			rulerComp.dispose();		
		rulerComp = null;
		
		if (contextMenuProvider != null)
			contextMenuProvider.dispose();
		contextMenuProvider = null;
		
		treeViewer = null;
		viewerManager = null;
		editPartFactory = null;
		paletteRoot = null;    

		freeMemory();
	}

	protected void freeMemory() 
	{
		Runtime runTime = Runtime.getRuntime();
		long maxMemory = runTime.maxMemory();
		long freeMemory = runTime.freeMemory();
		long totalMemory = runTime.totalMemory();
		long usedMemory = totalMemory - freeMemory;
		long startTime = System.currentTimeMillis();

		double mb = 1024 * 1024;
		logger.debug("Max Memory BEFORE GC   = "+(maxMemory / mb)+" MB");
		logger.debug("Total Memory BEFORE GC = "+(totalMemory / mb)+" MB");
		logger.debug("Used Memory BEFORE GC  = "+(usedMemory / mb)+" MB");
		logger.debug("Free Memory BEFORE GC  = "+(freeMemory / mb)+" MB");    
		logger.debug("GC Begin!");

		runTime.gc();
		long newTime = System.currentTimeMillis() - startTime;
		
		logger.debug("GC took "+newTime+" ms");
		
		freeMemory = runTime.freeMemory();
		totalMemory = runTime.totalMemory();
		usedMemory = totalMemory - freeMemory;
		
		logger.debug("Total Memory AFTER GC = "+(totalMemory / mb)+" MB");
		logger.debug("Used Memory AFTER GC  = "+(usedMemory / mb)+" MB");
		logger.debug("Free Memory AFTER GC  = "+(freeMemory / mb)+" MB");    
		logger.debug("");  	
	}	

	private Editor2DFactory factory = null;
	public Editor2DFactory getModelFactory() 
	{
		if (factory == null) {
			factory = createModelFactory();
		}
		return factory;
	}

	protected abstract Editor2DFactory createModelFactory();		
}