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
package org.nightlabs.editor2d.iofilter.svg;

import java.util.Iterator;
import java.util.Map;

import org.apache.batik.svggen.DefaultStyleHandler;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.nightlabs.editor2d.DrawComponent;
import org.w3c.dom.Element;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class EditorStyleSheetHandler
extends DefaultStyleHandler
{
//	private static final Logger logger = Logger.getLogger(EditorStyleSheetHandler.class);
	
  public EditorStyleSheetHandler(SVGPainter painter) {
  	painter.addPaintListener(paintListener);
  }
  
  private DrawComponent currentDrawComponent;
  private DrawComponentPaintListener paintListener = new DrawComponentPaintListener()
  {
		public void drawComponentPainted(DrawComponent dc) {
			currentDrawComponent = dc;
		}
	};

//	private Element lastElement = null;
//	private String lastID = "id";
//	private String attrID = "id";
//
//  private Pattern strokePattern = null;
//  private Pattern getStrokePattern()
//  {
//  	if (strokePattern == null) {
//  		strokePattern = Pattern.compile("stroke:([^;]*);");
//  	}
//  	return strokePattern;
//  }
//
//  public void setStyle(Element element, Map styleMap, SVGGeneratorContext generatorContext)
//  {
//    String tagName = element.getTagName();
//    StringBuffer sb = new StringBuffer();
//    // collect all style attributes and concat them to one string
//    for (Iterator it = styleMap.entrySet().iterator(); it.hasNext(); )
//    {
//    	Map.Entry entry = (Map.Entry) it.next();
//    	String styleName = (String) entry.getKey();
//    	String value = (String) entry.getValue();
//    	if (element.getAttributeNS(null, styleName).length() == 0) {
//    		if (appliesTo(styleName, tagName)) {
//    			sb.append(styleName);
//    			sb.append(":");
//    			sb.append(value);
//    			sb.append(";");
//    		}
//    	}
//    }
//    // set the style attribute
//    element.setAttribute(SVG_STYLE_ATTRIBUTE, sb.toString());
//
//    // get the id from the current painted drawComponent and set it as attribute
//    String id = attrID;
//    if (currentDrawComponent != null) {
//    	id = ""+currentDrawComponent.getId();
//    }
//    element.setAttribute(attrID, id);
//    logger.debug("element "+element.getNodeName()+" set Attribute "+attrID+" "+id);
//    logger.debug("lastID = "+lastID);
//
//    // check if this is the second entry for the same id
//  	if (element.getAttribute(attrID).equals(lastID))
//  	{
//  		logger.debug("element.getAttribute("+attrID+") = "+element.getAttribute(attrID));
//  		String currentStyleValue = element.getAttribute(SVG_STYLE_ATTRIBUTE);
//  		logger.debug("currentStyleValue = "+currentStyleValue);
//  		if (lastElement != null)
//  		{
//  			String lastStyleValue = lastElement.getAttribute(SVG_STYLE_ATTRIBUTE);
//  			logger.debug("lastStyleValue = "+lastStyleValue);
//
//  			String currentStrokeValue = null;
//  			Matcher currentMatcher = getStrokePattern().matcher(currentStyleValue);
//  			// a current stroke style could be found
//  			if (currentMatcher.find()) {
//  				// e.g. stroke:none
//  				currentStrokeValue = currentMatcher.group(1);
//  				logger.debug("currentStrokeValue = "+currentStrokeValue);
//  			}
//  			// no current stroke style could be found
//  			else {
//
//  			}
//				String lastStrokeValue = "";
//				// none
//				Matcher lastMatcher = getStrokePattern().matcher(lastStyleValue);
//				// a stroke style could be found in the last element
//				if (lastMatcher.find())
//				{
//					// e.g. stroke:black
//  				lastStrokeValue = lastMatcher.group(1);
//  				logger.debug("lastStrokeValue = "+lastStrokeValue);
//
//  				String newCompleteStyleValue = lastStyleValue;
//  				// an old stroke value was set
//  				if (currentStrokeValue != null) {
//  					newCompleteStyleValue = lastMatcher.replaceFirst(currentStrokeValue);
//    				logger.debug("newCompleteStyleValue = "+newCompleteStyleValue);
//  				}
//  				// no old stroke value was set
//  				else {
//  					newCompleteStyleValue = lastStyleValue + "stroke:" + lastStrokeValue;
//  				}
//  				lastElement.setAttribute(SVG_STYLE_ATTRIBUTE, newCompleteStyleValue);
//				}
//				logger.debug("lastElement.getAttribute("+SVG_STYLE_ATTRIBUTE+") = "+lastElement.getAttribute(SVG_STYLE_ATTRIBUTE));
//  		}
//			// remove the second entry with the same id
////			Node removedNode = element.getParentNode().removeChild(element);
////			logger.debug("removedNode name = "+removedNode.getNodeName());
////			logger.debug("removedNode value = "+removedNode.getNodeValue());
//  	}
//    lastID = id;
//    lastElement = element;
//  }
    
  @SuppressWarnings("unchecked")
	@Override
	public void setStyle(Element element, Map styleMap, SVGGeneratorContext generatorContext)
  {
    String tagName = element.getTagName();
    Iterator iter = styleMap.keySet().iterator();
    String styleName = null;
    while (iter.hasNext()) {
      styleName = (String)iter.next();
      if (element.getAttributeNS(null, styleName).length() == 0){
        if (appliesTo(styleName, tagName)) {
          element.setAttributeNS(null, styleName, (String)styleMap.get(styleName));
        }
      }
    }
    String id = "id";
    if (currentDrawComponent != null) {
    	id = ""+currentDrawComponent.getId();
    }
    element.setAttribute("id", id);
    // not supported by BATIK DOM Implementation org.apache.batik.dom.GenericElementNS
//    element.setIdAttribute(id, true);
  }
	
//  private static void logElement(Element element)
//  {
//    if (logger.isDebugEnabled()) {
//    	logger.debug("element.getTagName() = "+element.getTagName());
//    	logger.debug("element.getNodeName() = "+element.getNodeName());
//    	logger.debug("element.getAttributes().getLength() = "+element.getAttributes().getLength());
//    	for (int i=0; i<element.getAttributes().getLength(); i++) {
//    		Node n = element.getAttributes().item(i);
//    		logger.debug("element attribute "+i+" nodeName = "+n.getNodeName());
//    		logger.debug("element attribute "+i+" value = "+n.getNodeValue());
//    		logger.debug("element attribute "+i+" type = "+n.getNodeType());
//    	}
//    }
//  }
}
