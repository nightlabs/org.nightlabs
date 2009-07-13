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

package org.nightlabs.editor2d.impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.DrawComponentContainer;
import org.nightlabs.editor2d.Editor2DFactory;
import org.nightlabs.editor2d.EditorGuide;
import org.nightlabs.editor2d.EditorRuler;
import org.nightlabs.editor2d.EllipseDrawComponent;
import org.nightlabs.editor2d.GroupDrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.Layer;
import org.nightlabs.editor2d.LineDrawComponent;
import org.nightlabs.editor2d.PageDrawComponent;
import org.nightlabs.editor2d.RectangleDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.ShapeDrawComponent;
import org.nightlabs.editor2d.TextDrawComponent;
import org.nightlabs.editor2d.j2d.GeneralShape;
import org.nightlabs.i18n.unit.resolution.Resolution;
import org.nightlabs.i18n.unit.resolution.ResolutionImpl;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * An implementation of the <b>Editor2DFactory</b>.
 * 
 */
public class Editor2DFactoryImpl
implements Editor2DFactory
{
	/**
	 * Creates an instance of the factory.
	 */
  public Editor2DFactoryImpl() {
		super();
	}
  
  public RootDrawComponent createRootDrawComponent(boolean validate)
  {
		RootDrawComponentImpl rootDrawComponent = new RootDrawComponentImpl();
		if (validate)
			validateRoot(rootDrawComponent);
		return rootDrawComponent;
	}

  public Layer createLayer() {
		return new LayerImpl();
	}
    
  public static final int UNIT_PIXELS = 2;
  public static final int UNIT_CENTIMETER = 1;
  public void validateRoot(RootDrawComponent root)
  {
    if (root == null)
      throw new IllegalStateException("Param root must not be null!");
    
    // create Factory
    Editor2DFactory factory = this;
    
    // check Pages
    if (root.getCurrentPage() == null)
    {
    	PageDrawComponent page = factory.createPageDrawComponent();
    	root.addDrawComponent(page);
    	root.setCurrentPage(page);
    }
    
    // check Layers
    if (root.getCurrentLayer() == null)
    {
      Layer l = factory.createLayer();
      root.getCurrentPage().addDrawComponent(l);
      root.getCurrentPage().setCurrentLayer(l);
    }
    
    // check Rulers
    if (root.getLeftRuler() == null)
    {
      EditorRuler leftRuler = factory.createEditorRuler();
      leftRuler.setHorizontal(false);
      leftRuler.setUnit(UNIT_PIXELS);
      root.setLeftRuler(leftRuler);
    }
    
    if (root.getTopRuler() == null)
    {
      EditorRuler topRuler = factory.createEditorRuler();
      topRuler.setHorizontal(true);
      topRuler.setUnit(UNIT_PIXELS);
      root.setTopRuler(topRuler);
    }
  }
  
  public PageDrawComponent createPageDrawComponent()
  {
  	PageDrawComponent page = new PageDrawComponentImpl();
  	return page;
  }

  public PageDrawComponent createPageDrawComponent(RootDrawComponent parent)
  {
  	PageDrawComponent page = new PageDrawComponentImpl();
  	page.setParent(parent);
  	Layer layer = createLayer();
  	layer.setParent(page);
  	page.setCurrentLayer(layer);
  	page.addDrawComponent(layer);
  	parent.setCurrentPage(page);
  	return page;
  }
    
  public RectangleDrawComponent createRectangleDrawComponent() {
		return new RectangleDrawComponentImpl();
	}

  public EllipseDrawComponent createEllipseDrawComponent() {
		return new EllipseDrawComponentImpl();
	}

  public TextDrawComponent createTextDrawComponent() {
		return new TextDrawComponentImpl();
	}

  public TextDrawComponent createTextDrawComponent(String text, String fontName,
  		int fontSize, int fontStyle, int x, int y, DrawComponentContainer parent)
  {
  	return new TextDrawComponentImpl(text, fontName, fontSize, fontStyle,
  			x, y, parent);
  }
  
  public TextDrawComponent createTextDrawComponent(String text, Font font, int x, int y,
  		DrawComponentContainer parent)
  {
  	return new TextDrawComponentImpl(text, font, x, y, parent);
  }
  
  public LineDrawComponent createLineDrawComponent() {
		return new LineDrawComponentImpl();
	}

  public EditorGuide createEditorGuide() {
		return new EditorGuideImpl();
	}

  public EditorRuler createEditorRuler() {
		return new EditorRulerImpl();
	}

  public ImageDrawComponent createImageDrawComponent() {
  	return new ImageDrawComponentImpl();
	}

  public Resolution createResolution() {
  	return new ResolutionImpl();
  }

  public GroupDrawComponent createGroupDrawComponent() {
  	return new GroupDrawComponentImpl();
  }
  
  public Color createColorFromString(String initialValue)
  {
    StringBuffer sb = new StringBuffer(initialValue);
    Integer integerRed = new Integer(sb.substring(2,5));
    Integer integerGreen = new Integer(sb.substring(8,11));
    Integer integerBlue = new Integer(sb.substring(14,17));
    Integer integerAlpha = new Integer(sb.substring(20,23));
    Color c = new Color(integerRed.intValue(), integerGreen.intValue(), integerBlue.intValue(), integerAlpha.intValue());
    return c;
  }

  public String convertColorToString(Object instanceValue)
  {
    java.awt.Color color = (java.awt.Color) instanceValue;
    StringBuffer sb = new StringBuffer();
    
    sb.append("R:");
    sb.append(convertColorValue(color.getRed()));
    sb.append(",");
    sb.append("G:");
    sb.append(convertColorValue(color.getGreen()));
    sb.append(",");
    sb.append("B:");
    sb.append(convertColorValue(color.getBlue()));
    sb.append(",");
    sb.append("A:");
    sb.append(convertColorValue(color.getAlpha()));
    return sb.toString();
  }

  protected String convertColorValue(int rgbVal)
  {
    StringBuffer sb = new StringBuffer(String.valueOf(rgbVal));
    int length = sb.length();
    if (length == 2) {
      sb.insert(0, 0);
    } else if (length == 1) {
      sb.insert(0, 0);
      sb.insert(1, 0);
    } else if (length == 0) {
      sb.insert(0, 0);
      sb.insert(1, 0);
      sb.insert(2, 0);
    } else if (length > 3) {
      throw new IllegalArgumentException("Param rgbVal must not be > 3!");
    }
    return sb.toString();
  }
    
  public GeneralShape createGeneralShapeFromString(String initialValue)
  {
    StringTokenizer st = new StringTokenizer(initialValue);
    int type = 0;
    GeneralShape gp = new GeneralShape();
    while (st.hasMoreTokens())
    {
      StringBuffer sb = new StringBuffer(st.nextToken());
      if (sb.substring(0,1).equals("T")) {
        type = new Integer(sb.substring(1,2)).intValue();
      }
      
      switch (type)
      {
      	case (PathIterator.SEG_MOVETO):
      	  float mx1 = new Double(st.nextToken()).floatValue();
      		float my1 = new Double(st.nextToken()).floatValue();
      		gp.moveTo(mx1,my1);
      		break;
      	case (PathIterator.SEG_LINETO):
      	  float lx1 = new Double(st.nextToken()).floatValue();
      		float ly1 = new Double(st.nextToken()).floatValue();
      		gp.lineTo(lx1,ly1);
      		break;
      	case (PathIterator.SEG_QUADTO):
      	  float qx1 = new Double(st.nextToken()).floatValue();
      		float qy1 = new Double(st.nextToken()).floatValue();
      	  float qx2 = new Double(st.nextToken()).floatValue();
      		float qy2 = new Double(st.nextToken()).floatValue();
      		gp.quadTo(qx1,qy1,qx2,qy2);
      		break;
      	case (PathIterator.SEG_CUBICTO):
      	  float cx1 = new Double(st.nextToken()).floatValue();
      		float cy1 = new Double(st.nextToken()).floatValue();
      	  float cx2 = new Double(st.nextToken()).floatValue();
      		float cy2 = new Double(st.nextToken()).floatValue();
      	  float cx3 = new Double(st.nextToken()).floatValue();
      		float cy3 = new Double(st.nextToken()).floatValue();
      		gp.curveTo(cx1,cy1,cx2,cy2,cx3,cy3);
      		break;
      	case (PathIterator.SEG_CLOSE):
      	  gp.closePath();
      		break;
      }
    }
    return gp;
  }


  public String convertGeneralShapeToString(Object instanceValue)
  {
    GeneralShape gp =(GeneralShape) instanceValue;
    double[] coords = new double[6];
    StringBuffer sb = new StringBuffer();
    for (PathIterator pi = gp.getPathIterator(null); !pi.isDone(); pi.next())
    {
      int segType = pi.currentSegment(coords);
      sb.append("T");
      sb.append(segType);
      sb.append(" ");
      switch (segType)
      {
      	case (PathIterator.SEG_MOVETO):
      		writeDouble(coords[0], sb);
      		writeDouble(coords[1], sb);
      	  break;
      	case (PathIterator.SEG_LINETO):
	    		writeDouble(coords[0], sb);
	    		writeDouble(coords[1], sb);
      	  break;
      	case (PathIterator.SEG_QUADTO):
	    		writeDouble(coords[0], sb);
	    		writeDouble(coords[1], sb);
	    		writeDouble(coords[2], sb);
	    		writeDouble(coords[3], sb);
      	  break;
      	case (PathIterator.SEG_CUBICTO):
	    		writeDouble(coords[0], sb);
	    		writeDouble(coords[1], sb);
	    		writeDouble(coords[2], sb);
	    		writeDouble(coords[3], sb);
	    		writeDouble(coords[4], sb);
	    		writeDouble(coords[5], sb);
      	  break;
      	case (PathIterator.SEG_CLOSE):
      	  break;
      }
    }
    return sb.toString();
  }
    
  protected final static String atSeperator = ", ";

  public AffineTransform createAffineTransformFromString(String initialValue)
  {
    StringTokenizer st = new StringTokenizer(initialValue, atSeperator);
    double[] matrix = new double[6];
    int matrixLength = 6;
    for (int i=0; i<matrixLength; i++)
    {
      matrix[i] = new Double(st.nextToken()).doubleValue();
    }
    return new AffineTransform(matrix);
  }
  
  /**
   * 
   * converts the AffineTransform Matrix into a String, similar to AffineTransform.toString()
   */
  public String convertAffineTransformToString(Object instanceValue)
  {
    AffineTransform at = (AffineTransform) instanceValue;
    StringBuffer sb = new StringBuffer();
    double[] matrix = new double[6];
    at.getMatrix(matrix);
    for (int i=0; i<matrix.length; i++)
    {
      sb.append(matrix[i]);
      if (i!=matrix.length-1)
        sb.append(atSeperator);
    }
    return sb.toString();
  }

  public BufferedImage createBufferedImageFromString(String initialValue)
  {
    byte[] imageData = createByteArrayFromString(initialValue);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
    JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(inputStream);
    BufferedImage image = null;
    try {
      image = decoder.decodeAsBufferedImage();
    } catch (Exception e) {
    	throw new RuntimeException(e);
    }
    return image;
  }

  public String convertBufferedImageToString(Object instanceValue)
  {
		BufferedImage image = (BufferedImage) instanceValue;
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
		try {
      encoder.encode(image);
    } catch (Exception e) {
    	throw new RuntimeException(e);
    }
		byte[] imageData = outputStream.toByteArray();
		String imageString = convertByteArrayToString(imageData);
		return imageString;
  }
  
  protected static final char [] HEX_DIGITS =
  { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  
  protected String convertByteArrayToString(byte[] instanceValue)
  {
    if (instanceValue == null)
    {
      return null;
    }
    else
    {
      byte [] bytes = instanceValue;
      char [] result = new char[2 * bytes.length];
      for (int i = 0, j = 0; i < bytes.length; ++i)
      {
        int high = (bytes[i] >> 4) & 0xF;
        int low = bytes[i] & 0xF;
        result[j++] = HEX_DIGITS[high];
        result[j++] = HEX_DIGITS[low];
      }
      return new String(result);
    }
  }
  
  protected byte[] createByteArrayFromString(String initialValue)
  {
    if (initialValue == null)
    {
      return null;
    }

    int size = initialValue.length();
    int limit = (size + 1) / 2;
    byte [] result = new byte[limit];
    if (size % 2 != 0)
    {
      result[--limit] = hexCharToByte(initialValue.charAt(size - 1));
    }
    
    for (int i = 0, j = 0; i < limit; ++i)
    {
      byte high = hexCharToByte(initialValue.charAt(j++));
      byte low = hexCharToByte(initialValue.charAt(j++));
      result[i] = (byte)(high << 4 | low);
    }
    return result;
  }
  
  protected static byte hexCharToByte(char character)
  {
    switch (character)
    {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      {
        return (byte)(character - '0');
      }
      case 'a':
      case 'b':
      case 'c':
      case 'd':
      case 'e':
      case 'f':
      {
        return (byte)(character - 'a' + 10);
      }
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
      {
        return (byte)(character - 'A' + 10);
      }
      default:
      {
        throw new NumberFormatException("Invalid hexadecimal");
      }
    }
  }
  
  protected String writeDouble(double d, StringBuffer sb)
  {
    sb.append(d);
    sb.append(" ");
    return sb.toString();
  }

  private Set<Class<?>> supportedClasses = null;
	public Set<Class<?>> getSupportedClass()
	{
		if (supportedClasses == null) {
			supportedClasses = new HashSet<Class<? extends Object>>();
			supportedClasses.addAll(getSupportedDrawComponentClasses());
			supportedClasses.add(EditorGuide.class);
			supportedClasses.add(EditorRuler.class);
			supportedClasses.add(Resolution.class);
		}
		return supportedClasses;
	}

	private Set<Class<? extends DrawComponent>> supportedDrawComponentClasses = null;
	public Set<Class<? extends DrawComponent>> getSupportedDrawComponentClasses()
	{
		if (supportedDrawComponentClasses == null) {
			supportedDrawComponentClasses = new HashSet<Class<? extends DrawComponent>>();
			supportedDrawComponentClasses.add(EllipseDrawComponent.class);
			supportedDrawComponentClasses.add(RectangleDrawComponent.class);
			supportedDrawComponentClasses.add(LineDrawComponent.class);
			supportedDrawComponentClasses.add(ShapeDrawComponent.class);
			supportedDrawComponentClasses.add(GroupDrawComponent.class);
			supportedDrawComponentClasses.add(RootDrawComponent.class);
			supportedDrawComponentClasses.add(Layer.class);
			supportedDrawComponentClasses.add(PageDrawComponent.class);
			supportedDrawComponentClasses.add(TextDrawComponent.class);
			supportedDrawComponentClasses.add(ImageDrawComponent.class);
		}
		return supportedDrawComponentClasses;
	}
	
	public Object createObject(Class<?> clazz)
	{
		if (clazz.isAssignableFrom(EditorGuide.class))
				return createEditorGuide();
		if (clazz.isAssignableFrom(EditorRuler.class))
			return createEditorRuler();
		if (clazz.isAssignableFrom(Resolution.class))
			return createResolution();
		
		return createDrawComponent(clazz);
	}
    
	public DrawComponent createDrawComponent(Class<?> clazz)
	{
		if (clazz.isAssignableFrom(EllipseDrawComponent.class))
			return createEllipseDrawComponent();
		if (clazz.isAssignableFrom(RectangleDrawComponent.class))
			return createRectangleDrawComponent();
		if (clazz.isAssignableFrom(LineDrawComponent.class))
			return createLineDrawComponent();
		if (clazz.isAssignableFrom(TextDrawComponent.class))
			return createTextDrawComponent();
		if (clazz.isAssignableFrom(GroupDrawComponent.class))
			return createGroupDrawComponent();
		if (clazz.isAssignableFrom(RootDrawComponent.class))
//			return createRootDrawComponent();
			return createRootDrawComponent(true);
		if (clazz.isAssignableFrom(ImageDrawComponent.class))
			return createImageDrawComponent();
		if (clazz.isAssignableFrom(Layer.class))
			return createLayer();
		if (clazz.isAssignableFrom(PageDrawComponent.class))
			return createPageDrawComponent();
		
		return null;
	}
} //Editor2DFactoryImpl
