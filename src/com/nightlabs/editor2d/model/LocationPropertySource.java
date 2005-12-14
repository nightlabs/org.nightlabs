package com.nightlabs.editor2d.model;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.nightlabs.editor2d.EditorPlugin;

public class LocationPropertySource 
implements IPropertySource 
{
	public static String ID_XPOS = "xPos"; //$NON-NLS-1$
	public static String ID_YPOS = "yPos"; //$NON-NLS-1$
	protected static IPropertyDescriptor[] descriptors;

	static{
		PropertyDescriptor xProp =
			new TextPropertyDescriptor(ID_XPOS,
				EditorPlugin.getResourceString("property.xposition.label"));
		xProp.setValidator(NumberCellEditorValidator.getSharedInstance());
		PropertyDescriptor yProp = 
			new TextPropertyDescriptor(ID_YPOS,
					EditorPlugin.getResourceString("property.yposition.label"));
		yProp.setValidator(NumberCellEditorValidator.getSharedInstance());
		descriptors = new IPropertyDescriptor[] {xProp, yProp};
	}

	protected Point point = null;

	public LocationPropertySource(Point point){
		this.point = point.getCopy();
	}

	public Object getEditableValue(){
		return point.getCopy();
	}

	public IPropertyDescriptor[] getPropertyDescriptors(){
		return descriptors;
	}

	public Object getPropertyValue(Object propName){
		if(ID_XPOS.equals(propName)){
			return new String(new Integer(point.x).toString());
		}
		if(ID_YPOS.equals(propName)){
			return new String(new Integer(point.y).toString());
		}
		return null;
	}

	public boolean isPropertySet(Object propName){
		return ID_XPOS.equals(propName) || ID_YPOS.equals(propName);
	}

	public void resetPropertyValue(Object propName){}

	public void setPropertyValue(Object propName, Object value){
		if(ID_XPOS.equals(propName)){
			Integer newInt = new Integer((String)value);
			point.x = newInt.intValue();
		}
		if(ID_YPOS.equals(propName)){
			Integer newInt = new Integer((String)value);
			point.y = newInt.intValue();
		}
	}

	public String toString(){
		return new String("["+point.x+","+point.y+"]");//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}
}
