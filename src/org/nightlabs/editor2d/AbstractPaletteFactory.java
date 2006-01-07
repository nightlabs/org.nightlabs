/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 * Project author: Daniel Mazurek <Daniel.Mazurek [at] nightlabs [dot] org>    *
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

package org.nightlabs.editor2d;

import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite.FlyoutPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;

import org.nightlabs.base.resource.SharedImages;
import org.nightlabs.base.resource.SharedImages.ImageDimension;
import org.nightlabs.base.resource.SharedImages.ImageFormat;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.model.ModelCreationFactory;
import org.nightlabs.editor2d.tools.EditorSelectionToolEntry;
import org.nightlabs.editor2d.tools.EllipseToolEntry;
import org.nightlabs.editor2d.tools.ImageToolEntry;
import org.nightlabs.editor2d.tools.LineToolEntry;
import org.nightlabs.editor2d.tools.RectangleToolEntry;
import org.nightlabs.editor2d.tools.TextToolEntry;
import org.nightlabs.editor2d.tools.ZoomToolEntry;

public abstract class AbstractPaletteFactory 
{
  public AbstractPaletteFactory() {
    super();
  }
    
  /** Default palette size. */
  protected static final int DEFAULT_PALETTE_SIZE = 125;
  /** Preference ID used to persist the palette location. */
  protected static final String PALETTE_DOCK_LOCATION = "PaletteFactory.Location";
  /** Preference ID used to persist the palette size. */
  protected static final String PALETTE_SIZE = "PaletteFactory.Size";
  /** Preference ID used to persist the flyout palette's state. */
  protected static final String PALETTE_STATE = "PaletteFactory.State";
  
  public abstract CreationFactory getCreationFactory(Class targetClass);
  
  /**
   * Return a FlyoutPreferences instance used to save/load the preferences of a flyout palette.
   */
  public static FlyoutPreferences createPalettePreferences() 
  {
    // set default flyout palette preference values, in case the preference store
    // does not hold stored values for the given preferences
    getPreferenceStore().setDefault(PALETTE_DOCK_LOCATION, -1);
    getPreferenceStore().setDefault(PALETTE_STATE, -1);
    getPreferenceStore().setDefault(PALETTE_SIZE, DEFAULT_PALETTE_SIZE);
    
    return new FlyoutPreferences() {
      public int getDockLocation() {
        return getPreferenceStore().getInt(PALETTE_DOCK_LOCATION);
      }
      public int getPaletteState() {
        return getPreferenceStore().getInt(PALETTE_STATE);
      }
      public int getPaletteWidth() {
        return getPreferenceStore().getInt(PALETTE_SIZE);
      }
      public void setDockLocation(int location) {
        getPreferenceStore().setValue(PALETTE_DOCK_LOCATION, location);
      }
      public void setPaletteState(int state) {
        getPreferenceStore().setValue(PALETTE_STATE, state);
      }
      public void setPaletteWidth(int width) {
        getPreferenceStore().setValue(PALETTE_SIZE, width);
      }
    };
  } 
    
  /*
  * Returns the preference store for the EditorPlugin.
  * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore() 
  */
  protected static IPreferenceStore getPreferenceStore() 
 {
    return EditorPlugin.getDefault().getPreferenceStore();
 }
 
 /** Create the "Shapes" drawer. */
 protected static PaletteContainer createShapesDrawer() 
 {
  PaletteDrawer componentsDrawer = new PaletteDrawer(EditorPlugin.getResourceString("palette.group.shapes"));

  // add Rectangle Tool
  ToolEntry component = new RectangleToolEntry  
  (
    EditorPlugin.getResourceString("palette.rectangle.label"),
    EditorPlugin.getResourceString("palette.rectangle.shortdesc"), 
    RectangleDrawComponent.class,
    new ModelCreationFactory(RectangleDrawComponent.class), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/rectangle16.gif"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/rectangle24.gif")    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Rectangle"),    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Rectangle",
    		ImageDimension._24x24, ImageFormat.png)
  );
  componentsDrawer.add(component);

  // add Ellipse Tool
  component = new EllipseToolEntry
  (
    EditorPlugin.getResourceString("palette.ellipse.label"),
    EditorPlugin.getResourceString("palette.ellipse.shortdesc"), 
    EllipseDrawComponent.class,
    new ModelCreationFactory(EllipseDrawComponent.class), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/ellipse16.gif"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/ellipse24.gif")    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Ellipse"),        
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Ellipse",
    		ImageDimension._24x24, ImageFormat.png)
  );
  componentsDrawer.add(component);

  // add Line Tool
  component = new LineToolEntry
  (
    EditorPlugin.getResourceString("palette.line.label"),
    EditorPlugin.getResourceString("palette.line.shortdesc"), 
    LineDrawComponent.class,
    new ModelCreationFactory(LineDrawComponent.class), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/line16.gif"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/line24.gif")    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Line"),    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Line",
    		ImageDimension._24x24, ImageFormat.png)    
  );
  componentsDrawer.add(component);
  
  // add Text Tool
  component = new TextToolEntry
  (
    EditorPlugin.getResourceString("palette.text.label"),
    EditorPlugin.getResourceString("palette.text.shortdesc"), 
    TextDrawComponent.class,
    new ModelCreationFactory(TextDrawComponent.class), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/text16.gif"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/text24.gif")    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Text"),    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Text",
    		ImageDimension._24x24, ImageFormat.png)    
  );
  componentsDrawer.add(component);

  // add Image Tool
  component = new ImageToolEntry
  (
    EditorPlugin.getResourceString("palette.image.label"),
    EditorPlugin.getResourceString("palette.image.shortdesc"), 
    ImageDrawComponent.class,
    new ModelCreationFactory(ImageDrawComponent.class), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/image16.gif"), 
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/image24.gif")
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Image"),    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Image",
    		ImageDimension._24x24, ImageFormat.png)        
  );
  componentsDrawer.add(component);
  
  return componentsDrawer;
 } 
  
 /**
  * Creates the PaletteRoot and adds all palette elements.
  * Use this factory method to create a new palette for your graphical editor.
  * @return a new PaletteRoot
  */
 protected static PaletteRoot createPalette() 
 {
  PaletteRoot palette = new PaletteRoot();
  palette.add(createToolsGroup(palette));
  palette.add(createShapesDrawer());
  return palette;
 }
 
 /** Create the "Tools" group. */
 protected static PaletteContainer createToolsGroup(PaletteRoot palette) 
 {
  PaletteGroup toolGroup = new PaletteGroup(EditorPlugin.getResourceString("palette.group.tools"));

  // Add a selection tool to the group
  ToolEntry tool = new EditorSelectionToolEntry();
  toolGroup.add(tool);
  palette.setDefaultEntry(tool);
  
  // Add a marquee tool to the group
  toolGroup.add(new MarqueeToolEntry());

  // Add a zoom tool to the group
  toolGroup.add(new ZoomToolEntry(EditorPlugin.getResourceString("palette.zoom.label"),
    EditorPlugin.getResourceString("palette.zoom.shortdesc"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/zoom16.gif"),
//    ImageDescriptor.createFromFile(EditorPlugin.class, "icons/zoom24.gif")    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Zoom"),    
    SharedImages.getSharedImageDescriptor(EditorPlugin.getDefault(), AbstractPaletteFactory.class, "Zoom",
    		ImageDimension._24x24, ImageFormat.png)
  ));
  
  // Add a (unnamed) separator to the group
  toolGroup.add(new PaletteSeparator());

  return toolGroup;
 }
 
}
