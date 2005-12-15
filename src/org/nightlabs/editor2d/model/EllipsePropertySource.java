package org.nightlabs.editor2d.model;

import java.util.List;

import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.properties.IntPropertyDescriptor;

public class EllipsePropertySource 
extends ShapeDrawComponentPropertySource 
{
	public EllipsePropertySource(EllipseDrawComponent ellipse) {
		super(ellipse);
	}
	
	protected EllipseDrawComponent getEllipseDrawComponent() {
		return (EllipseDrawComponent) drawComponent;
	}
	
	protected List createPropertyDescriptors() 
	{
		List descriptors = super.createPropertyDescriptors();
		// Start Angle
		descriptors.add(new IntPropertyDescriptor(EllipseDrawComponent.PROP_START_ANGLE,
				EditorPlugin.getResourceString("property.startangle.label")));
		// End Angle
		descriptors.add(new IntPropertyDescriptor(EllipseDrawComponent.PROP_END_ANGLE,
				EditorPlugin.getResourceString("property.endangle.label")));
		
		return descriptors;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
			getEllipseDrawComponent().setStartAngle(((Integer)value).intValue());
		}
		else if (id.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
			getEllipseDrawComponent().setEndAngle(((Integer)value).intValue());
		}
	}		
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#getPropertyValue(java.lang.Object)
	 */
	public Object getPropertyValue(Object id) 
	{
		Object o = super.getPropertyValue(id);
		if (o != null) 
			return o;
		else 
		{
			if (id.equals(EllipseDrawComponent.PROP_START_ANGLE)) {
				return new Integer(getEllipseDrawComponent().getStartAngle());
			}
			else if (id.equals(EllipseDrawComponent.PROP_END_ANGLE)) {
				return new Integer(getEllipseDrawComponent().getEndAngle());
			}
			
			return null;			
		}
	}		
}
