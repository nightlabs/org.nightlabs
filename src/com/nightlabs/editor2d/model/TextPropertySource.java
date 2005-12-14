package com.nightlabs.editor2d.model;

import java.util.List;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.nightlabs.editor2d.EditorPlugin;
import com.nightlabs.editor2d.TextDrawComponent;
import com.nightlabs.editor2d.properties.CheckboxPropertyDescriptor;
import com.nightlabs.editor2d.properties.FontNamePropertyDescriptor;
import com.nightlabs.editor2d.properties.IntPropertyDescriptor;

public class TextPropertySource 
extends ShapeDrawComponentPropertySource 
{
	public TextPropertySource(TextDrawComponent text) {
		super(text);
	}
	
	protected TextDrawComponent getTextDrawComponent() {
		return (TextDrawComponent) drawComponent;
	}
	
	public static final String CATEGORY_FONT = EditorPlugin.getResourceString("property.category.text");	
		
	protected List createPropertyDescriptors() 
	{
		List descriptors = super.createPropertyDescriptors();
		
		// Font Name
		PropertyDescriptor desc = new FontNamePropertyDescriptor(TextDrawComponent.PROP_FONT_NAME,
				EditorPlugin.getResourceString("property.fontname.label"));
		desc.setCategory(CATEGORY_FONT);
		descriptors.add(desc);
		
		// Font Size
//		desc = new FontSizePropertyDescriptor(TextDrawComponent.PROP_FONT_SIZE,
//				EditorPlugin.getResourceString("property.fontsize.label"));
//		desc.setCategory(CATEGORY_FONT);
//		descriptors.add(desc);
		desc = new IntPropertyDescriptor(TextDrawComponent.PROP_FONT_SIZE,
				EditorPlugin.getResourceString("property.fontsize.label"));
		desc.setCategory(CATEGORY_FONT);
		descriptors.add(desc);
		
		// Bold
		desc = new CheckboxPropertyDescriptor(TextDrawComponent.PROP_BOLD,
				EditorPlugin.getResourceString("property.bold.label"));
		desc.setCategory(CATEGORY_FONT);
		descriptors.add(desc);
		
		// Italic
		desc = new CheckboxPropertyDescriptor(TextDrawComponent.PROP_ITALIC,
				EditorPlugin.getResourceString("property.italic.label"));
		desc.setCategory(CATEGORY_FONT);
		descriptors.add(desc);
		
		// Text
		desc = new TextPropertyDescriptor(TextDrawComponent.PROP_TEXT,
				EditorPlugin.getResourceString("property.text.label"));
		desc.setCategory(CATEGORY_FONT);
		descriptors.add(desc);
		
		return descriptors;
	}	
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.views.properties.IPropertySource#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	public void setPropertyValue(Object id, Object value) 
	{
		super.setPropertyValue(id, value);
		
		if (id.equals(TextDrawComponent.PROP_FONT_NAME)) {
			getTextDrawComponent().setFontName((String)value);
		}
		else if (id.equals(TextDrawComponent.PROP_FONT_SIZE)) {
			getTextDrawComponent().setFontSize(((Integer)value).intValue());
		}
		else if (id.equals(TextDrawComponent.PROP_BOLD)) {
			getTextDrawComponent().setBold(((Boolean)value).booleanValue());
		}
		else if (id.equals(TextDrawComponent.PROP_ITALIC)) {
			getTextDrawComponent().setItalic(((Boolean)value).booleanValue());
		}
		else if (id.equals(TextDrawComponent.PROP_TEXT)) {
			getTextDrawComponent().setText((String)value);
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
			if (id.equals(TextDrawComponent.PROP_FONT_NAME)) {
				return getTextDrawComponent().getFontName();
			}
			else if (id.equals(TextDrawComponent.PROP_FONT_SIZE)) {
				return new Integer(getTextDrawComponent().getFontSize());
			}
			else if (id.equals(TextDrawComponent.PROP_BOLD)) {
				return new Boolean(getTextDrawComponent().isBold());
			}
			else if (id.equals(TextDrawComponent.PROP_ITALIC)) {
				return new Boolean(getTextDrawComponent().isItalic());
			}
			else if (id.equals(TextDrawComponent.PROP_TEXT)) {
				return getTextDrawComponent().getText();
			}

			return null;			
		}
	}			
}
