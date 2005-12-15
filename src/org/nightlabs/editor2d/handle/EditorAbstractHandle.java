/**
 * <p> Project: org.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 14.04.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.handle;

import org.eclipse.draw2d.Locator;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.handles.AbstractHandle;
import org.eclipse.swt.graphics.Cursor;


public abstract class EditorAbstractHandle 
extends AbstractHandle 
{
  /**
   * The default size for the RotateCenterHandle.
   */
  protected static final int DEFAULT_HANDLE_SIZE = 7;

  {
    init();
  }  
  
  protected void init() {
    setPreferredSize(new Dimension(DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
    setSize(getPreferredSize());
  }   
  
  public void setBounds(Rectangle rect) {
    super.setBounds(new Rectangle(rect.x, rect.y, DEFAULT_HANDLE_SIZE, DEFAULT_HANDLE_SIZE));
  }  
  
  /**
   * 
   */
  public EditorAbstractHandle() 
  {
    super();
    init();    
  }
  
  /**
   * @param owner
   * @param loc
   */
  public EditorAbstractHandle(GraphicalEditPart owner, Locator loc) {
    super(owner, loc);
    init();
  }

  /**
   * @param owner
   * @param loc
   * @param c
   */
  public EditorAbstractHandle(GraphicalEditPart owner, Locator loc, Cursor c) {
    super(owner, loc, c);
    init();
  }

  /* (non-Javadoc)
   * @see org.eclipse.gef.handles.AbstractHandle#createDragTracker()
   */
  protected abstract DragTracker createDragTracker();

}
