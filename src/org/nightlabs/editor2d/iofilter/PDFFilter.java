/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
 * Copyright (C) 2004-2006 NightLabs - http://NightLabs.org                    *
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
package org.nightlabs.editor2d.iofilter;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.render.j2d.J2DRenderContext;
import org.nightlabs.io.AbstractSingleFileExtensionIOFilter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Daniel.Mazurek [at] NightLabs [dot] de
 *
 */
public class PDFFilter
extends AbstractSingleFileExtensionIOFilter
{
	private static final Logger logger = Logger.getLogger(PDFFilter.class);
	
	@Override
	protected String initDescription() {
		return "PDF Filter";
	}

	@Override
	protected String initFileExtension() {
		return "pdf";
	}

	@Override
	protected String initName() {
		return "PDF Filter";
	}

	@Override
	protected boolean supportsRead() {
		return false;
	}

	@Override
	protected boolean supportsWrite() {
		return true;
	}

	public Object read(InputStream in) throws IOException {
		return null;
	}

	public void write(Object o, OutputStream out) throws IOException
	{
		RootDrawComponent root = (RootDrawComponent) o;
    Document document = new Document();
    try {
      PdfWriter writer = PdfWriter.getInstance(document, out);
      document.open();
//      DefaultFontMapper mapper = new DefaultFontMapper();
//      FontFactory.registerDirectories();
//      mapper.insertDirectory("c:\\windows\\fonts");
      
    	Rectangle bounds = root.getBounds();
//    	Rectangle bounds = root.getCurrentPage().getBounds();
//	    Rectangle bounds = root.getCurrentPage().getPageBounds();
	    double width = PageSize.A4.width();
	    double height = PageSize.A4.height();
			double dcWidth = bounds.width;
			double dcHeight = bounds.height;
      
      int w = (int)dcWidth;
      int h = (int)dcHeight;
			      
      logger.debug("root.getBounds() = "+root.getBounds());
      logger.debug("width = "+w);
      logger.debug("height = "+h);
     
      PdfContentByte cb = writer.getDirectContent();
      Graphics2D g2d = cb.createGraphicsShapes(PageSize.A4.width(), PageSize.A4.height());
      
  		double scaleX = width / dcWidth;
  		double scaleY = height / dcHeight;

  		int translateX = -bounds.x;
  		int translateY = -bounds.y;

  		double scale = Math.min(scaleX, scaleY);
  		g2d.scale(scale, scale);
  		g2d.translate(translateX, translateY);
  		
      J2DRenderContext j2drc = (J2DRenderContext) root.getRenderer().getRenderContext(J2DRenderContext.RENDER_CONTEXT_TYPE_JAVA2D);
      
      j2drc.paint(root, g2d);
//      g2d.setPaint(Color.BLACK);
//      g2d.fillRect(0, 0, 200, 200);
      
      g2d.dispose();
    }
    catch(DocumentException de) {
        System.err.println(de.getMessage());
    }
    // step 5: we close the document
    document.close();
	}

}
