/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.06.2005 </p>
 * <p> Author: Daniel Mazurek </p>
 **/
package com.nightlabs.editor2d.figures;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;


public class DummyFreeformLayer 
extends FreeformLayer 
implements BufferedFreeformLayer
{  
	public boolean intersects(Rectangle rect) 
	{
		System.out.println("DummyFreeformLayer.intersects :"+rect);
		return super.intersects(rect);
	}
	
	/* (non-Javadoc)
	 * @see com.nightlabs.editor2d.figures.BufferedFreeformLayer#refresh()
	 */
	public void refresh() {
	}
	
	/* (non-Javadoc)
	 * @see com.nightlabs.editor2d.figures.BufferedFreeformLayer#refresh(org.eclipse.draw2d.Figure)
	 */
	public void refresh(IFigure figure) {
	}
	
	/* (non-Javadoc)
	 * @see com.nightlabs.editor2d.figures.BufferedFreeformLayer#init(org.eclipse.gef.EditPart)
	 */
	public void init(EditPart editPart) {
	}
}
