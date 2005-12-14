/**
 * <p> Project: com.nightlabs.editor2d </p>
 * <p> Copyright: Copyright (c) 2004 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 02.11.2004 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.editor2d.properties;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableTree;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import com.nightlabs.editor2d.util.J2DUtil;


public class AWTColorCellEditor 
extends DialogCellEditor
{
  /*
	 * The default extent in pixels.
	 */
	private static final int DEFAULT_EXTENT= 16;
	
	/**
	 * Gap between between image and text in pixels.
	 */	 	
	private static final int GAP = 6;

	/**
	 * The composite widget containing the color and RGB label widgets
	 */
	private Composite composite;

	/**
	 * The label widget showing the current color.
	 */
	private Label colorLabel;

	/**
	 * The label widget showing the RGB values.
	 */
	private Label rgbLabel;

	/**
	 * The image.
	 */
	private Image image;
	
	/**
	 * Internal class for laying out this cell editor.
	 */
	private class ColorCellLayout extends Layout {
		public Point computeSize(
			Composite editor, 
			int wHint, 
			int hHint, 
			boolean force) {
			if (wHint != SWT.DEFAULT && hHint != SWT.DEFAULT)
				return new Point(wHint, hHint);
			Point colorSize = colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			Point rgbSize = rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			return new Point(
				colorSize.x + GAP + rgbSize.x, 
				Math.max(colorSize.y, rgbSize.y)); 
		}
		public void layout(Composite editor, boolean force) {
			Rectangle bounds = editor.getClientArea();
			Point colorSize = colorLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			Point rgbSize = rgbLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, force);
			int ty = (bounds.height - rgbSize.y) / 2;
			if (ty < 0)
				ty = 0;
			colorLabel.setBounds(-1, 0, colorSize.x, colorSize.y);
			rgbLabel.setBounds(
				colorSize.x + GAP - 1, 
				ty, 
				bounds.width - colorSize.x - GAP, 
				bounds.height); 
		}
	}
	
/**
 * Creates a new color cell editor parented under the given control.
 * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
 * validator.
 *
 * @param parent the parent control
 */
public AWTColorCellEditor(Composite parent) {
	this(parent, SWT.NONE);
}

/**
 * Creates a new color cell editor parented under the given control.
 * The cell editor value is black (<code>RGB(0,0,0)</code>) initially, and has no 
 * validator.
 *
 * @param parent the parent control
 * @param style the style bits
 * @since 2.1
 */
public AWTColorCellEditor(Composite parent, int style) {
	super(parent, style);
//	doSetValue(new java.awt.Color(0,0,0));	  
}

/**
 * Creates and returns the color image data for the given control
 * and RGB value. The image's size is either the control's item extent 
 * or the cell editor's default extent, which is 16 pixels square.
 *
 * @param w the control
 * @param color the color
 */
private ImageData createColorImage(Control w, RGB color) {

	GC gc = new GC(w);
	FontMetrics fm = gc.getFontMetrics();
	int size = fm.getAscent();
	gc.dispose();

	int indent = 6;
	int extent = DEFAULT_EXTENT;
	if (w instanceof Table)
		extent = ((Table) w).getItemHeight() - 1;
	else if (w instanceof Tree)
		extent = ((Tree) w).getItemHeight() - 1;
	else if (w instanceof TableTree)
		extent = ((TableTree) w).getItemHeight() - 1;

	if (size > extent)
		size = extent;

	int width = indent + size;
	int height = extent;

	int xoffset = indent;
	int yoffset = (height - size) / 2;

	RGB black = new RGB(0, 0, 0);
	PaletteData dataPalette = new PaletteData(new RGB[] {black, black, color});
	ImageData data = new ImageData(width, height, 4, dataPalette);
	data.transparentPixel = 0;

	int end = size - 1;
	for (int y = 0; y < size; y++) {
		for (int x = 0; x < size; x++) {
			if (x == 0 || y == 0 || x == end || y == end)
				data.setPixel(x + xoffset, y + yoffset, 1);
			else
				data.setPixel(x + xoffset, y + yoffset, 2);
		}
	}

	return data;
}

/* (non-Javadoc)
 * Method declared on DialogCellEditor.
 */
protected Control createContents(Composite cell) 
{
  // TODO: remove colorLabel, replaced by AWTColorLabelProvider
	Color bg = cell.getBackground();
	composite = new Composite(cell, getStyle());
	composite.setBackground(bg);
	composite.setLayout(new ColorCellLayout());
	colorLabel = new Label(composite, SWT.LEFT);
	colorLabel.setBackground(bg);
	rgbLabel = new Label(composite, SWT.LEFT);
	rgbLabel.setBackground(bg);
	rgbLabel.setFont(cell.getFont());
	return composite;
}

/* (non-Javadoc)
 * Method declared on CellEditor.
 */
public void dispose() {
	if (image != null) {
		image.dispose();
		image = null;
	}
	super.dispose();
}

/* (non-Javadoc)
 * Method declared on DialogCellEditor.
 */
protected Object openDialogBox(Control cellEditorWindow) {
	ColorDialog dialog = new ColorDialog(cellEditorWindow.getShell());
	Object value = getValue();
	if (value != null) {
	  if (value instanceof String) {
	    value = new java.awt.Color(0,0,0);
	  }
	  if (value instanceof java.awt.Color) {
	    value = J2DUtil.toRGB((java.awt.Color)value);
	  }
	  dialog.setRGB((RGB) value);	  
	}		
	value = dialog.open(); 
	RGB rgb = dialog.getRGB();
	java.awt.Color awtColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
	return awtColor;
}

	/* (non-Javadoc)
	 * Method declared on DialogCellEditor.
	 */
	protected void updateContents(Object value) 
	{
	  if (value != null) {
		  System.out.println("value = instanceof "+value.getClass());	    
	  }

	  if (value instanceof String) {
	    value = new java.awt.Color(0,0,0);
	  }
	  
	//	RGB rgb = (RGB) value;
	  if (value == null) {
	    value = new java.awt.Color(0,0,0);
	  }
	  	  
	  RGB rgb = J2DUtil.toRGB((java.awt.Color)value);
		// XXX: We don't have a value the first time this method is called".
		if (rgb == null) {
			rgb = new RGB(0, 0, 0);
		}
		// XXX: Workaround for 1FMQ0P3: SWT:ALL - TableItem.setImage doesn't work if using the identical image."
		if (image != null)
			image.dispose();
	
		ImageData id = createColorImage(colorLabel.getParent().getParent(), rgb);
		ImageData mask = id.getTransparencyMask();
		image = new Image(colorLabel.getDisplay(), id, mask);
		colorLabel.setImage(image);
	
		rgbLabel.setText("(" + rgb.red + "," + rgb.green + "," + rgb.blue + ")");//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	}
}

