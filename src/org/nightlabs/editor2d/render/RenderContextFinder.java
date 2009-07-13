/**
 * 
 */
package org.nightlabs.editor2d.render;

import org.nightlabs.editor2d.DrawComponent;

/**
 * @author Daniel Mazurek - daniel [at] nightlabs [dot] de
 *
 */
public class RenderContextFinder<GraphicsType>
{
	public void paintRenderContext(GraphicsType g, DrawComponent dc,
			Renderer r, String renderContextType)
  {
		if (r != null && dc != null && g != null) {
			RenderContext<GraphicsType> rc = r.getRenderContext(renderContextType);
			if (rc != null)
				rc.paint(dc, g);
			else {
//				r = dc.getRenderModeManager().getDefaultRenderer(dc.getClass());
				r = dc.getRenderModeManager().getDefaultRenderer(dc.getClass().getName());
				if (r != null)
					rc = r.getRenderContext(renderContextType);
				if (rc != null)
					rc.paint(dc, g);
				else {
					checkForSuperRenderer(g, dc, dc.getRenderModeManager(), renderContextType);
				}
			}
		}
  }
	
	private void checkForSuperRenderer(GraphicsType g, DrawComponent dc,
			RenderModeManager rm, String renderContextType)
	{
	 	// first check for interfaces
		Class<? extends DrawComponent> dcClass = dc.getClass();
    Class<?>[] interfaces = dcClass.getInterfaces();
    for (Class<?> clazz : interfaces) {
			if (clazz.isAssignableFrom(dcClass)) {
//				Renderer r = rm.getRenderer(dc.getRenderMode(), clazz);
				RenderContext<GraphicsType> rc = null;
				Renderer r = rm.getRenderer(dc.getRenderMode(), clazz.getName());
				if (r != null)
					rc = r.getRenderContext(renderContextType);
				if (rc != null) {
					rc.paint(dc, g);
					break;
				}
			}
		}
    // check interfaces for superClasses
    Class<?> superClass = dcClass.getSuperclass();
    if (superClass == null)
    	return;
    
    Class<?>[] superInterfaces = superClass.getInterfaces();
    while (!superClass.equals(Object.class)) {
      for (Class<?> clazz : superInterfaces) {
  			if (clazz.isAssignableFrom(dcClass)) {
//  				Renderer r = rm.getRenderer(dc.getRenderMode(), clazz);
  				Renderer r = rm.getRenderer(dc.getRenderMode(), clazz.getName());
  				if (r != null) {
    				RenderContext<GraphicsType> rc = r.getRenderContext(renderContextType);
    				if (rc != null) {
    					rc.paint(dc, g);
    					break;
    				}
  				}
  			}
      }
    	superClass = superClass.getSuperclass();
    	superInterfaces = superClass.getInterfaces();
    }
	}
	
}
