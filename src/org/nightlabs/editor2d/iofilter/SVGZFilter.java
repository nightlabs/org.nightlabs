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
package org.nightlabs.editor2d.iofilter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Daniel.Mazurek <at> NightLabs <dot> de
 *
 */
public class SVGZFilter
extends AbstractSVGFilter
{
	public static final String FILE_EXTENSION = "svgz";
	
	public SVGZFilter() {
		super();
	}

	@Override
	protected String initFileExtension() {
		return FILE_EXTENSION;
	}

	@Override
	protected String initDescription() {
		return "SVGZ File Filter";
	}
	
	@Override
	protected String initName() {
		return "SVGZ File Filter";
	}
	
	@Override
	protected void writeSVG(OutputStream out)
	throws IOException
	{
		GZIPOutputStream zipStream = new GZIPOutputStream(out);
		super.writeSVG(zipStream);
		zipStream.finish();
		zipStream.close();
		out.close();
	}
	
//	protected SVGGeneratorContext createSVGGeneratorContext()
//	{
//		SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(getDocument());
//		ctx.setStyleHandler(new EditorStyleSheetHandler(getPainter()));
//    try {
//			ctx.setImageHandler(new ImageHandlerPNGEncoder(getImageDir(), null));
//		} catch (SVGGraphics2DIOException e) {
//			ctx.setImageHandler(new ImageHandlerBase64Encoder());
//		}
//		return ctx;
//	}
//
//	protected String getImageDir()
//	{
//		try {
//			return getImageDirFile().getCanonicalPath();
//		} catch (IOException ioe) {
//			throw new RuntimeException(ioe);
//		}
//	}
//
//	protected File getImageDirFile()
//	throws IOException
//	{
//		File f = new File(Utils.getTempDir(), "tmpSVGImages");
//		f.mkdir();
//		return f;
//	}
	
}
