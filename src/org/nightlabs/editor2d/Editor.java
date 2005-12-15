/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d;

import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.palette.PaletteRoot;

import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.MultiLayerDrawComponent;
import org.nightlabs.editor2d.edit.GraphicalEditPartFactory;
import org.nightlabs.editor2d.edit.tree.TreePartFactory;
import org.nightlabs.editor2d.outline.filter.FilterNameProvider;
import org.nightlabs.editor2d.util.ModelUtil;


public class Editor  
extends AbstractEditor
{
//  protected IModelManager modelManager;    
//  public IModelManager getModelManager() 
//  {
//    if (modelManager == null)
//      modelManager = new DrawComponentModelManager();
//    
//    return modelManager;
//  }
  
  protected EditPartFactory editPartFactory;
  public EditPartFactory getEditPartFactory() 
  {
    if (editPartFactory == null)
      editPartFactory = new GraphicalEditPartFactory();
    
    return editPartFactory;
  }  
    
  protected EditPartFactory outlineEditPartFactory;
  public EditPartFactory getOutlineEditPartFactory() 
  {
    if (outlineEditPartFactory == null)
      outlineEditPartFactory = new TreePartFactory(getFilterManager());
    
    return outlineEditPartFactory;
  }   
  
  protected PaletteRoot palette; 
  public PaletteRoot getPaletteRoot() 
  {
    if (palette == null)
      palette = EditorPaletteFactory.createPalette();

    return palette;
  }  
  
  protected ContextMenuProvider contextMenuProvider;
  public ContextMenuProvider getContextMenuProvider() 
  {
    if (contextMenuProvider == null)
      return new EditorContextMenuProvider(getGraphicalViewer(), getActionRegistry());
    
    return contextMenuProvider;
  }
      
  protected FilterNameProvider getFilterNameProvider() 
  {
		return new ModelUtil();
	}

	public MultiLayerDrawComponent createMultiLayerDrawComponent() 
  {
    MultiLayerDrawComponent mldc = Editor2DFactory.eINSTANCE.createMultiLayerDrawComponent();
    return mldc;
  }
}
  
