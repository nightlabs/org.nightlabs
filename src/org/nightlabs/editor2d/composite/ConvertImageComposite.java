/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/
package org.nightlabs.editor2d.composite;

import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.media.jai.ColorCube;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.LookupTableJAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.operator.ColorConvertDescriptor;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.holongate.j2d.J2DCanvas;
import org.nightlabs.base.composite.XComposite;
import org.nightlabs.base.form.XFormToolkit;
import org.nightlabs.base.form.XFormToolkit.TOOLKIT_MODE;
import org.nightlabs.base.util.RCPUtil;
import org.nightlabs.editor2d.EditorPlugin;
import org.nightlabs.editor2d.image.ColorConvertDelegate;
import org.nightlabs.editor2d.image.RenderModeMetaData;
import org.nightlabs.editor2d.j2dswt.BufferedImagePaintable;
import org.nightlabs.editor2d.render.RenderConstants;
import org.nightlabs.editor2d.util.ImageUtil;

/**
 * <p> Author: Daniel.Mazurek[AT]NightLabs[DOT]de </p>
 */
public class ConvertImageComposite 
extends XComposite 
{
	public static final Logger LOGGER = Logger.getLogger(ConvertImageComposite.class);
	
	/**
	 * @param parent
	 * @param style
	 */
	public ConvertImageComposite(Composite parent, int style, 
			BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style);
		init(bi, toolkitMode);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public ConvertImageComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, 
			BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{
		super(parent, style, layoutMode, layoutDataMode);
		init(bi, toolkitMode);		
	}
	
	protected long start = 0;
	protected int bitsPerPixel;
	protected void init(BufferedImage bi, TOOLKIT_MODE toolkitMode) 
	{ 
		start = System.currentTimeMillis();
		originalImage = bi;
		convertImage = ImageUtil.cloneImage(originalImage);
		bitsPerPixel = bi.getColorModel().getPixelSize();
		initColorModels();		
		initRenderHints();
		initDithering();
		createComposite(this);		
		addDisposeListener(disposeListener);
	}
	
	protected void initRenderHints() 
	{
		renderHints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, 
				RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		renderHints.put(RenderingHints.KEY_DITHERING, 
				RenderingHints.VALUE_DITHER_DEFAULT);
	}
	
	protected XFormToolkit toolkit = null;
	protected TOOLKIT_MODE toolkitMode = TOOLKIT_MODE.COMPOSITE;
	protected BufferedImage originalImage = null;
	public BufferedImage getOriginalImage() {
		return originalImage;
	}
	protected BufferedImage convertImage = null;	
	public BufferedImage getConvertedImage() {
		return convertImage;
	}	
//	protected RenderedImage originalImage = null;	
//	public RenderedImage getOriginalImage() {
//		return originalImage;
//	}
//	protected RenderedImage convertImage = null;	
//	public RenderedImage getConvertedImage() {
//		return convertImage;
//	}	
	
	protected ScrolledComposite originalSC = null;
	protected ScrolledComposite convertSC = null;
	protected J2DCanvas originalCanvas = null;
	protected J2DCanvas convertCanvas = null;
	protected BufferedImagePaintable originalPaintable = null;
	protected BufferedImagePaintable convertPaintable = null;
	protected Button fitImageButton = null;
	protected Button ditherButton = null;
	protected void createComposite(Composite parent) 
	{
		toolkit = new XFormToolkit(Display.getCurrent());
		toolkit.setCurrentMode(toolkitMode);
		
		Composite comp = toolkit.createComposite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(2, true));
		comp.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Group originalGroup = toolkit.createGroup(comp, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.originalImage"));
		originalGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalGroup.setLayout(new GridLayout());
		originalSC = new ScrolledComposite(originalGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		originalSC.setExpandHorizontal(true);		
		originalSC.setExpandVertical(true);
		originalSC.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalSC.setLayout(new GridLayout());
		originalPaintable = new BufferedImagePaintable(originalImage);
		originalCanvas = new J2DCanvas(originalSC, originalPaintable);
		originalCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		originalSC.setContent(originalCanvas);
		originalSC.setMinSize(originalCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		Group convertGroup = toolkit.createGroup(comp, SWT.NONE, 
				EditorPlugin.getResourceString("convertImage.group.convertImage"));
		convertGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertGroup.setLayout(new GridLayout());
		convertSC = new ScrolledComposite(convertGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		convertSC.setExpandHorizontal(true);		
		convertSC.setExpandVertical(true);		
		convertSC.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertSC.setLayout(new GridLayout());		
		convertPaintable = new BufferedImagePaintable(convertImage);
		convertCanvas = new J2DCanvas(convertSC, convertPaintable);
		convertCanvas.setLayoutData(new GridData(GridData.FILL_BOTH));
		convertSC.setContent(convertCanvas);
		convertSC.setMinSize(convertCanvas.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		
		convertCanvas.addControlListener(resizeListener);
		originalSC.getHorizontalBar().addSelectionListener(horizontalScrollListener);
		originalSC.getVerticalBar().addSelectionListener(verticalScrollListener);
		convertSC.getHorizontalBar().addSelectionListener(horizontalScrollListener);
		convertSC.getVerticalBar().addSelectionListener(verticalScrollListener);
		
		Composite optionsComp = toolkit.createXComposite(parent, SWT.NONE, 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		optionsComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		optionsComp.setLayout(new GridLayout(2, false));
		
		Label colorModeLabel = toolkit.createLabel(optionsComp, EditorPlugin.getResourceString("convertImage.colorModel.label"));
		colorModelCombo = toolkit.createCombo(optionsComp, SWT.BORDER);
		colorModelCombo.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER, GridData.VERTICAL_ALIGN_BEGINNING, true, false));
		colorModelCombo.addSelectionListener(colorModelListener);
		populateColorModelCombo();

		Label imageScaleLabel = toolkit.createLabel(optionsComp, EditorPlugin.getResourceString("convertImage.fitImage.label"));
		fitImageButton = toolkit.createButton(optionsComp, "", SWT.CHECK);
		fitImageButton.setSelection(fitImage);
		fitImageButton.addSelectionListener(fitImageListener);

		Label ditherLabel = toolkit.createLabel(optionsComp, 
				EditorPlugin.getResourceString("convertImage.ditherButton.label"));
		ditherButton = toolkit.createButton(optionsComp, "", SWT.CHECK);
		ditherButton.setSelection(dithering);
		ditherButton.addSelectionListener(ditherButtonListener);
		
		detailParent = toolkit.createXComposite(parent, SWT.NONE, 
				LayoutMode.TIGHT_WRAPPER, LayoutDataMode.GRID_DATA);
		detailParent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		selectColorMode(rgb);
		originalCanvas.repaint();
		convertCanvas.repaint();
		
		long end = System.currentTimeMillis() - start;
		LOGGER.debug("init took "+end+" ms!");
	}
	
	protected SelectionListener horizontalScrollListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			repaintCanvas();
		}	
	};
	
	protected SelectionListener verticalScrollListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			repaintCanvas();
		}	
	};
	
	protected void repaintCanvas() 
	{
		originalCanvas.repaint();
		convertCanvas.repaint();		
	}
	
	protected boolean dithering = true;
	protected SelectionListener ditherButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) {
			dithering = !dithering;
			setDithering(dithering, true);
		}	
	};
	
	protected Composite detailComp = null;
	protected Composite detailParent = null;
		
	public static String DITHER_MODE_QUALITY = "errordiffusion"; 
	public static String DITHER_MODE_SPEED = "ordereddither";
	protected String ditherMode = DITHER_MODE_QUALITY;

	protected KernelJAI ditherAlgorithm = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;	
	protected Button buttonFilterFloydSteinberg = null;
	protected Button buttonFilterJarvis = null;
	protected Button buttonFilterStucki = null;
	protected Button buttonDitherMask441 = null;
	protected Composite createDitherDetail(Composite parent) 
	{		
		ExpandableComposite ec = toolkit.createExpandableComposite(parent, SWT.NONE);
		ec.setText(EditorPlugin.getResourceString("convertImage.ditherOptions.label"));
		detailComp = ec;				
		detailComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));	
		detailComp.setLayout(new GridLayout());		
		
		Composite comp = toolkit.createGroup(detailComp, SWT.NONE, "");
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		buttonFilterFloydSteinberg = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.floydSteinberg.label"), SWT.RADIO);
		buttonFilterJarvis = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.jarvis.label"), SWT.RADIO);
		buttonFilterStucki = toolkit.createButton(comp,
				EditorPlugin.getResourceString("convertImage.ditherMode.stucki.label"), SWT.RADIO);
		buttonDitherMask441 = toolkit.createButton(comp, 
				EditorPlugin.getResourceString("convertImage.ditherMode.441.label"), SWT.RADIO);
		
		buttonFilterFloydSteinberg.setSelection(true);
		
		buttonFilterFloydSteinberg.addSelectionListener(ditherDetailButtonListener);
		buttonFilterJarvis.addSelectionListener(ditherDetailButtonListener);
		buttonFilterStucki.addSelectionListener(ditherDetailButtonListener);
		buttonDitherMask441.addSelectionListener(ditherDetailButtonListener);
		
		ec.setClient(comp);
		ec.setExpanded(true);
		ec.addExpansionListener(expansionListener);		
		detailParent.layout(true);
		
		return detailComp;
	}
		
	protected ExpansionAdapter expansionListener = new ExpansionAdapter() 
	{
		public void expansionStateChanged(ExpansionEvent e) 
		{
			// TODO: dont layout detailParent but change size of composite			
//			ConvertImageComposite.this.layout();			
			detailParent.layout(true);
		}		
	};
	
	protected SelectionListener ditherDetailButtonListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			Button b = (Button) e.getSource();
			if (b.equals(buttonFilterFloydSteinberg)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = KernelJAI.ERROR_FILTER_FLOYD_STEINBERG;
			} else if (b.equals(buttonFilterJarvis)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = KernelJAI.ERROR_FILTER_JARVIS;
			} else if (b.equals(buttonFilterStucki)) {
				ditherMode = DITHER_MODE_QUALITY;
				ditherAlgorithm = KernelJAI.ERROR_FILTER_STUCKI;
			} else if (b.equals(buttonDitherMask441)) {
				ditherMode = DITHER_MODE_SPEED; 
			} 
			else {
				throw new IllegalArgumentException("e.getSource() is an unknown object");
			}
			refresh();
		}	
	};
	
	protected ColorModel bw = null;
	protected ColorModel grey = null;
	protected ColorModel rgb = null;
	protected Combo colorModelCombo = null;
	protected Map<String, ColorModel> name2ColorModel = new HashMap<String, ColorModel>();
	protected void initColorModels() 
	{		
		bw = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY),
					new int[] {2}, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);		
		name2ColorModel.put(EditorPlugin.getResourceString("convertImage.colorModel.blackwhite"), bw);
		
		grey = ImageUtil.getColorModel(BufferedImage.TYPE_BYTE_GRAY);		
		name2ColorModel.put(EditorPlugin.getResourceString("convertImage.colorModel.grayscale"), grey);
		
		rgb = ImageUtil.getColorModel(BufferedImage.TYPE_INT_RGB); 
		name2ColorModel.put(EditorPlugin.getResourceString("convertImage.colorModel.rgb"), rgb);
		colorModel = rgb;		
	}
	
	protected void populateColorModelCombo() 
	{
		for (Iterator<String> it = name2ColorModel.keySet().iterator(); it.hasNext(); ) {
			String colorModelName = it.next();
			colorModelCombo.add(colorModelName);
		}
	}
	
	protected RenderingHints renderHints = null;		
	protected ColorModel colorModel = null;
	protected SelectionListener colorModelListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			String name = colorModelCombo.getItem(colorModelCombo.getSelectionIndex());
			colorModel = name2ColorModel.get(name);
			selectColorMode(colorModel);
		}	
	};

	protected void setDithering(boolean b, boolean refresh) 
	{				
		RCPUtil.disposeAllChildren(detailParent);
		if (b) {
			detailComp = createDitherDetail(detailParent);
			renderHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		}	else {
			renderHints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		}
		// TODO: dont layout detailParent but change size of composite
		detailParent.layout(true);
//		this.layout();
		if (refresh)
			refresh();
	}
	
	protected void selectColorMode(ColorModel cm) 
	{
		String name = "";
		for (Iterator it = name2ColorModel.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, ColorModel> entry = (Map.Entry) it.next();
			if (entry.getValue().equals(cm)) {
				name = entry.getKey();
			}
		}
		colorModelCombo.setText(name);
		
		boolean blackwhite = colorModel.equals(bw);
		ditherButton.setEnabled(blackwhite);
		setDithering(blackwhite, false);
		
		refresh();
	}
	
	protected int scaleInterpolationType = AffineTransformOp.TYPE_BILINEAR;
	protected AffineTransformOp rescaleOp = null;	
	protected float scale = 1.0f;
	protected ControlListener resizeListener = new ControlAdapter()
	{	
		public void controlResized(ControlEvent e) 
		{
			org.eclipse.swt.graphics.Rectangle canvasSize = originalCanvas.getClientArea();
			float originalWidth = originalImage.getWidth();
			float originalHeight = originalImage.getHeight();
			float newWidth = canvasSize.width;
			float newHeight = canvasSize.height;
			float scaleX = 1;
			float scaleY = 1;
			
			if (originalWidth != 0 && newWidth != 0)
				scaleX = newWidth / originalWidth;
			if (originalHeight != 0 && newWidth != 0)
				scaleY = newHeight / originalHeight;
			
			float tmpScale = Math.min(scaleX, scaleY);
			if (tmpScale != 1) {
				scale = tmpScale; 
				rescaleOp = createRescaleOp(scale);				
			}			
			LOGGER.debug("Canvas resized!");
			
			refresh();
		}	
	};	
	
	protected AffineTransformOp createRescaleOp(float scale) 
	{
		AffineTransform at = new AffineTransform();
		at.scale(scale, scale);
		LOGGER.debug("image scale factor = "+scale);
		return new AffineTransformOp(at, scaleInterpolationType);		
	}
	
	protected boolean fitImage = true;
	protected SelectionListener fitImageListener = new SelectionListener()
	{	
		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}	
		public void widgetSelected(SelectionEvent e) 
		{
			fitImage = !fitImage;
			if (fitImage)
				rescaleOp = createRescaleOp(scale);
			
			refresh();	
			layoutScrollBars();
		}	
	};
	
	protected void layoutScrollBars() 
	{
		Point size = null;
		if (!fitImage)
			size = new Point(originalImage.getWidth(), originalImage.getHeight());			
		else
			size = new Point(originalScale.getWidth(), originalScale.getHeight());			
		
		originalCanvas.computeSize(size.x, size.y);
		convertCanvas.computeSize(size.x, size.y);		
		originalSC.setMinSize(size);		
		convertSC.setMinSize(size);				
		originalCanvas.layout(true);		
		convertCanvas.layout(true);		
	}	
		
	protected List<RenderModeMetaData> renderModeMetaDatas = new LinkedList<RenderModeMetaData>();
	public List<RenderModeMetaData> getRenderModeMetaDatas() {
		return renderModeMetaDatas;
	}
	
	protected void refresh() 
	{		
		LOGGER.debug("refresh!");
		long start = System.currentTimeMillis();
				
		if (colorModel == bw) {			
			convertBlackWhite();						
		}
		else {
			if (convertImage.getType() == BufferedImage.TYPE_BYTE_BINARY)
				convertImage = ImageUtil.cloneImage(originalImage);
//			convertImage = colorConvertJAI(originalImage, colorModel, renderHints);
			RenderedImage img = colorConvertJDK(originalImage, colorModel, renderHints);
			if (img != null)
				convertImage = convertToBufferedImage(img);
		}						
		rescaleImages();
		long end = System.currentTimeMillis() - start;
		LOGGER.debug("refresh took "+end+" ms!");
	}		
			
//	private BufferedImage colorConvertJAI(BufferedImage original, ColorModel colorModel,
//			RenderingHints rh) 
//	{
//		rh.put(JAI.KEY_IMAGE_LAYOUT, 
//				getColorConvertImageLayout(colorModel, original.getWidth(), original.getHeight()));		
//		PlanarImage dst = ColorConvertDescriptor.create(original, colorModel, rh);
//		return dst.getAsBufferedImage();		
//	}	
//	
//	protected boolean isCompatible(BufferedImage bi, ColorModel cm) 
//	{
//		return cm.isCompatibleSampleModel(bi.getSampleModel());
//	}
//	
//	private ImageLayout getColorConvertImageLayout(ColorModel cm, int width, int height) 
//	{
//    ImageLayout layout = new ImageLayout(); 
//    layout.setColorModel(cm);
//    layout.setSampleModel(cm.createCompatibleSampleModel(width, height));
//    return layout;
//	}		
	
	private RenderedImage colorConvertJDK(BufferedImage original, ColorModel colorModel, 
			RenderingHints rh) 
	{
		long startTime = System.currentTimeMillis();		
		renderModeMetaData = new RenderModeMetaData();
		parameters.clear();
		parameters.put(RenderModeMetaData.KEY_BUFFERED_IMAGE_OP, new ColorConvertOp(colorModel.getColorSpace(), rh));
		renderModeMetaData.setParameters(parameters);
		renderModeMetaData.setRendererDelegateClass(ColorConvertDelegate.class);
		
		renderModeMetaDatas.clear();
		renderModeMetaDatas.add(renderModeMetaData);
		
		String renderMode = getRenderMode(colorModel); 
		try {
			RenderedImage img = renderModeMetaData.getRendererDelegate().render(renderMode, null, original, renderModeMetaData);
			long end = System.currentTimeMillis() - startTime;
			LOGGER.debug("Color Convert took "+end+" ms!");
			return img;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}		
	
	protected String getRenderMode(ColorModel cm) 
	{
		if (cm.equals(bw)) {
			return RenderConstants.BLACK_WHITE_MODE;
		} else if (cm.equals(grey)) {
			return RenderConstants.GRAY_MODE;
		} else if (cm.equals(rgb)) {
			return RenderConstants.RGB_MODE;
		}
		return null;
	}
	
	protected BufferedImage originalScale = null;
	protected BufferedImage convertScale = null;
	protected void rescaleImages() 
	{
		long start = System.currentTimeMillis();
		
		if (fitImage && (rescaleOp != null)) 		
		{
			originalScale = rescaleOp.filter(originalImage, null);			
			originalPaintable.setImage(originalScale);
			convertScale = rescaleOp.filter(convertImage, null);			
			convertPaintable.setImage(convertScale);			
		}
		else {
			originalPaintable.setImage(originalImage);
			convertPaintable.setImage(convertImage);			
		}
		repaintCanvas();
		
		long end = System.currentTimeMillis() - start;
		LOGGER.debug("rescaleImages took "+end+" ms!");
	}
							
	private void convertBlackWhite() 
	{						
		if (!dithering) {
			convertImage = convertBlackWhiteWithoutDithering(originalImage);			
			return;
		}			
		try {
			PlanarImage dst = PlanarImage.wrapRenderedImage(originalImage);		
			RenderedImage img = convertBlackWhiteWithDithering(dst, bitsPerPixel);
			if (img != null)
				convertImage = convertToBufferedImage(img);
			LOGGER.debug("Dithering worked!");
		} catch (Exception e) {
			convertImage = convertBlackWhiteWithoutDithering(originalImage);
		}
	}		
		
	private BufferedImage convertBlackWhiteWithoutDithering(BufferedImage img) 
	{
		LOGGER.debug("Dithering skipped!");					
		return convertToBufferedImage(colorConvertJDK(img, bw, renderHints));
//	return ImageUtil.cloneImage(img, BufferedImage.TYPE_BYTE_BINARY);		
	}
		
	protected RenderModeMetaData renderModeMetaData = null;
	protected Map<String, Object> parameters = new HashMap<String, Object>();	
	private RenderedImage convertBlackWhiteWithDithering(PlanarImage src, int bitsPerPixel) 
	{
		long startTime = System.currentTimeMillis();
    // Load the ParameterBlock for the dithering operation
    // and set the operation name.
    ParameterBlock pb = new ParameterBlock();
    pb.addSource(src);
    String opName = null;
    if (ditherMode.equals(DITHER_MODE_QUALITY)) 
    {
        opName = DITHER_MODE_QUALITY;
        LookupTableJAI lut = lookupTable;
        pb.add(lut);
        pb.add(ditherAlgorithm);
    } else if (ditherMode.equals(DITHER_MODE_SPEED)){
        opName = DITHER_MODE_SPEED;
        ColorCube cube = colorCube;
        pb.add(cube);
        pb.add(KernelJAI.DITHER_MASK_441);
    }
    // Create a hint containing the layout.
    RenderingHints hints = new RenderingHints(JAI.KEY_IMAGE_LAYOUT,
                                              imagelayout);    
		renderModeMetaData = new RenderModeMetaData();
		parameters.clear();
		parameters.put(RenderModeMetaData.KEY_OP_NAME, opName);
		parameters.put(RenderModeMetaData.KEY_PARAMETER_BLOCK, pb);
		parameters.put(RenderModeMetaData.KEY_RENDERING_HINTS, hints);
		renderModeMetaData.setRendererDelegateClass(ColorConvertDelegate.class);
		renderModeMetaData.setParameters(parameters);
		
		renderModeMetaDatas.clear();
		renderModeMetaDatas.add(renderModeMetaData);
		
		String renderMode = getRenderMode(colorModel); 
		try {
			RenderedImage img = renderModeMetaData.getRendererDelegate().render(renderMode, null, src, renderModeMetaData);
			long end = System.currentTimeMillis() - startTime;
			LOGGER.debug("Dithering took "+end+" ms!");
			return img;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}	
		
	protected BufferedImage convertToBufferedImage(RenderedImage img) 
	{
		return ImageUtil.convertToBufferedImage(img);
	}
	
	protected LookupTableJAI lookupTable = null;
	protected ColorCube colorCube = null;
	protected ImageLayout imagelayout = null;
	protected void initDithering() 
	{
		lookupTable = ImageUtil.getLookupTable(bitsPerPixel);
		colorCube = ImageUtil.getColorCube(bitsPerPixel);
		imagelayout = ImageUtil.get1BitDitherImageLayout(originalImage.getWidth(), originalImage.getHeight());
	}
		
	protected DisposeListener disposeListener = new DisposeListener()
	{	
		public void widgetDisposed(DisposeEvent e) 
		{
			colorCube = null;
			colorModel = null;
			convertImage = null;
			convertScale = null;
			grey = null;
			imagelayout = null;
			lookupTable = null;
			originalImage = null;
			originalScale = null;
			rgb = null;
		}	
	};
	
}