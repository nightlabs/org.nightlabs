/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 26.10.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.model;

import org.eclipse.gef.requests.CreationFactory;

import com.nightlabs.editor2d.DrawComponentContainer;
import com.nightlabs.editor2d.Editor2DFactory;
import com.nightlabs.editor2d.EditorGuide;
import com.nightlabs.editor2d.EditorRuler;
import com.nightlabs.editor2d.EllipseDrawComponent;
import com.nightlabs.editor2d.ImageDrawComponent;
import com.nightlabs.editor2d.Layer;
import com.nightlabs.editor2d.LineDrawComponent;
import com.nightlabs.editor2d.MultiLayerDrawComponent;
import com.nightlabs.editor2d.RectangleDrawComponent;
import com.nightlabs.editor2d.TextDrawComponent;

public class ModelCreationFactory 
implements CreationFactory
{
	protected Class targetClass;
	
	public ModelCreationFactory( Class targetClass ) {
		this.targetClass = targetClass;
	}
	
	/* 
	 * @see org.eclipse.gef.requests.CreationFactory#getNewObject()
	 */
	public Object getNewObject()
	{
//		Map registry = EPackage.Registry.INSTANCE;
//		String editor2DURI = Editor2DPackage.eNS_URI;
//		Editor2DPackage editor2DPackage =
//		(Editor2DPackage) registry.get(editor2DURI);
//		Editor2DFactory factory = editor2DPackage.getEditor2DFactory();
	  
	  Editor2DFactory factory = Editor2DFactory.eINSTANCE;
	  	  
		Object result = null;
			
		if( targetClass.equals(RectangleDrawComponent.class)) {
			result = factory.createRectangleDrawComponent();
		}
		else if( targetClass.equals(Layer.class)) {
			result = factory.createLayer();
		}
		else if( targetClass.equals(EllipseDrawComponent.class)) {
			result = factory.createEllipseDrawComponent();
		}
		else if( targetClass.equals(MultiLayerDrawComponent.class)) {
		  result = factory.createMultiLayerDrawComponent();
		}
		else if (targetClass.equals(DrawComponentContainer.class)) {
		  result = factory.createDrawComponentContainer();
		}
		else if( targetClass.equals(EditorGuide.class)) {
		  result = factory.createEditorGuide();
		}
		else if ( targetClass.equals(EditorRuler.class)) {
		  result = factory.createEditorRuler();
		}
		else if ( targetClass.equals(LineDrawComponent.class)) {
		  result = factory.createLineDrawComponent();
		}
    else if ( targetClass.equals(TextDrawComponent.class)) {
      result = factory.createTextDrawComponent();
    }
    else if ( targetClass.equals(ImageDrawComponent.class)) {
      result = factory.createImageDrawComponent();
    }
			
		return result;
	}

	/* 
	 * @see org.eclipse.gef.requests.CreationFactory#getObjectType()
	 */
	public Object getObjectType()
	{
		return targetClass;
	}
}

