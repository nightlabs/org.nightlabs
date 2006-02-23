/**
 * <p> Project: org.nightlabs.ipanema.sun </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 10.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package org.nightlabs.editor2d.composite;

import org.eclipse.gef.internal.InternalImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.j2dswt.DrawComponentPaintable;

public class PreviewComposite 
extends Composite
{
	protected DrawComponent drawComponent;	
	public DrawComponent getDrawComponent() {
		return drawComponent;
	}
	public void setDrawComponent(DrawComponent drawComponent) {
		this.drawComponent = drawComponent;
	}

	public PreviewComposite(Composite parent, int style, DrawComponent dc) 
	{
		this(parent, style, dc, true, true);
	}

	public PreviewComposite(Composite parent, int style, DrawComponent dc, boolean zoomButtons,
			boolean resizeCanvas) 
	{
		super(parent, style);
		this.drawComponent = dc;
		this.zoomButtons = zoomButtons;
		setLayout(new GridLayout());
		setLayoutData(new GridData(GridData.FILL_BOTH));
		createCanvas(this);		
	}
	
	protected boolean zoomButtons = false;
	protected boolean resizeCanvas = true;
	protected DrawComponentPaintable paintable;
	protected Button zoomInButton;
	protected Button zoomOutButton;
	protected Label zoomLabel;
	
	protected J2DCanvas canvas;
	public J2DCanvas getCanvas() {
		return canvas;
	}
	
//	protected J2DCanvas createCanvas(Composite parent) 
//	{
//		paintable = new DrawComponentPaintable(getDrawComponent());
//		Group group = new Group(parent, SWT.NONE);
//		group.setLayoutData(new GridData(GridData.FILL_BOTH));
//		group.setText(EditorPlugin.getResourceString("preview.group.title"));
//		group.setLayout(new GridLayout(1, true));
//		
//		Composite buttonBar = new XComposite(group, SWT.NONE);
//		buttonBar.setLayout(new GridLayout(5, false)); 
//		buttonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		
//		Composite beginComp = new XComposite(buttonBar, SWT.NONE);
//		beginComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		zoomInButton = new Button(buttonBar, SWT.PUSH);
//		zoomInButton.setImage(InternalImages.DESC_ZOOM_IN.createImage());
//		zoomInButton.addSelectionListener(zoomListener);
//		zoomLabel = new Label(buttonBar, SWT.NONE);
//		updateZoomLabel();
//		zoomOutButton = new Button(buttonBar, SWT.PUSH);
//		zoomOutButton.setImage(InternalImages.DESC_ZOOM_OUT.createImage());
//		zoomOutButton.addSelectionListener(zoomListener);
//		Composite endComp = new XComposite(buttonBar, SWT.NONE);
//		endComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//						
//		canvas = new J2DCanvas(group, SWT.BORDER, paintable);
//		canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
//		canvas.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
//		canvas.repaint();
//		return canvas;
//	}
	
	protected J2DCanvas createCanvas(Composite parent) 
	{
		paintable = new DrawComponentPaintable(getDrawComponent());
		if (zoomButtons) {
			Group group = new Group(parent, SWT.NONE);
			group.setLayoutData(new GridData(GridData.FILL_BOTH));
			group.setText(EditorPlugin.getResourceString("preview.group.title"));
			group.setLayout(new GridLayout(1, true));
			
			Composite buttonBar = new XComposite(group, SWT.NONE);
			buttonBar.setLayout(new GridLayout(5, false)); 
			buttonBar.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Composite beginComp = new XComposite(buttonBar, SWT.NONE);
			beginComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			zoomInButton = new Button(buttonBar, SWT.PUSH);
			zoomInButton.setImage(InternalImages.DESC_ZOOM_IN.createImage());
			zoomInButton.addSelectionListener(zoomListener);
			zoomLabel = new Label(buttonBar, SWT.NONE);
			updateZoomLabel();
			zoomOutButton = new Button(buttonBar, SWT.PUSH);
			zoomOutButton.setImage(InternalImages.DESC_ZOOM_OUT.createImage());
			zoomOutButton.addSelectionListener(zoomListener);
			Composite endComp = new XComposite(buttonBar, SWT.NONE);
			endComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
							
			canvas = new J2DCanvas(group, SWT.BORDER, paintable);
			if (resizeCanvas)
				canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			canvas.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
			canvas.repaint();
			return canvas;			
		}
		else {
			canvas = new J2DCanvas(parent, SWT.BORDER, paintable);
			if (resizeCanvas)
				canvas.setLayoutData(new GridData(GridData.FILL_BOTH));
			canvas.setBackground(new Color(parent.getDisplay(), 255, 255, 255));
			canvas.repaint();
			return canvas;						
		}
	}	
		
	protected double zoom = 1;
	protected double zoomStep = 0.2d;
	protected SelectionListener zoomListener = new SelectionListener(){	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			if (e.getSource().equals(zoomInButton)) {
				zoom += zoomStep;	
				applyZoom(zoom);
				updateZoomLabel();
				updateCanvas();
			}
			else if (e.getSource().equals(zoomOutButton)) {
				zoom -= zoomStep;
				applyZoom(zoom);
				updateZoomLabel();				
				updateCanvas();				
			}
		}	
	};
	
	protected void updateZoomLabel() {
		double z = Math.floor(zoom * 100);
		zoomLabel.setText(z + "%");
	}	
	
	public void updateCanvas() 
	{
		if (canvas != null) {
//			paintable = new DrawComponentPaintable(getSectionDrawComponent());
			paintable = new DrawComponentPaintable(getDrawComponent());
			canvas.setPaintable(paintable);
			canvas.repaint();			
		}
	}
	
	public void applyZoom(double zoomFactor) {
		canvas.getPaintableManager().setScale(zoomFactor);
	}	
	
}
