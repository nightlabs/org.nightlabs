/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.12.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.tools;

import org.apache.log4j.Logger;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;

import org.nightlabs.editor2d.request.EditorCreateRequest;
import org.nightlabs.editor2d.request.EditorRequestConstants;


public abstract class EditorCreationTool 
extends CreationTool 
//extends TargetingTool
implements EditorRequestConstants
{
  protected SnapToHelper helper;
  
  public static final Logger LOGGER = Logger.getLogger(EditorCreationTool.class);
    
  /**
   * @param aFactory
   */
  public EditorCreationTool(CreationFactory aFactory) {
    super(aFactory);
  }

  /**
   * Creates a {@link EditorCreateRequest} and sets this tool's factory on the request.
   * @see org.eclipse.gef.tools.TargetingTool#createTargetRequest()
   */
  protected Request createTargetRequest() 
  {
  	EditorCreateRequest request = new EditorCreateRequest();
  	request.setFactory(getFactory());
  	return request;
  }
  
  protected Point getRealLocation() 
  {
    Point p = getLocation();
    Point realLocation;
    
    EditPartViewer view = getCurrentViewer();
    if (view instanceof ScrollingGraphicalViewer) 
    {
      ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) view;
      FigureCanvas canvas = (FigureCanvas) viewer.getControl();
      Viewport viewport = canvas.getViewport();
      Point viewLocation = viewport.getViewLocation();
      realLocation = p.getTranslated(viewLocation);                    
      return realLocation;      
    }
    return p;
  }
  
  protected EditorCreateRequest getEditorCreateRequest() {
  	return (EditorCreateRequest)getTargetRequest();
  }  
    
  /**
   * @see org.eclipse.gef.tools.AbstractTool#getDebugName()
   */
  protected String getDebugName() {
  	return "EditorCreation Tool";//$NON-NLS-1$
  }   
}


