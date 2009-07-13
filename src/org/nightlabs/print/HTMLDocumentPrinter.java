/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.print;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.print.PrintServiceLookup;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.text.View;

/**
 * HTMLDocumentPrinter is still experimental. It uses a JEditorPane to
 * render html documents and then lets the component itself print to
 * the printers {@link Graphics} object.
 * 
 * Unfortunately JEditorPane has its problems with complex html sites
 * including divs and styles.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class HTMLDocumentPrinter implements DocumentPrinter {

	public static class OffScreenJTextPane extends JEditorPane implements Printable {
		private static final long serialVersionUID = 1L;
		
		
		private int currentPage;
		private double pageEndY;
		private double pageStartY;
		// TODO: Think of possibility to configure external engines (Maybe simply by a properties string that will be set by reflection to the printerInterface)
		private boolean scaleToWidth = false;
		
		public OffScreenJTextPane() {
			super();
			setEditable(false);
		}

		public void initPrinting(File htmlFile)
		throws MalformedURLException, IOException
		{
			setPage(htmlFile.toURL());
			resetPrintPosTracker();
		}
		
		private void resetPrintPosTracker() {
			currentPage = -1;
			pageEndY = 0;
			pageStartY = 0;
		}
		

		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			double scale = 1.0;
			Graphics2D graphics2D;
			View rootView;
			graphics2D = (Graphics2D) graphics;
			// Set the size to the printJob page format width and infinite in height
			setSize((int) pageFormat.getImageableWidth(),Integer.MAX_VALUE);
			validate();
			
			rootView = getUI().getRootView(this);

			if (scaleToWidth &&
				(getMinimumSize().getWidth() > pageFormat.getImageableWidth()))
			{
				scale = pageFormat.getImageableWidth() / getMinimumSize().getWidth();
				graphics2D.scale(scale,scale);
			}

			graphics2D.setClip(
					(int) (pageFormat.getImageableX()/scale),
					(int) (pageFormat.getImageableY()/scale),
					(int) (pageFormat.getImageableWidth()/scale),
					(int) (pageFormat.getImageableHeight()/scale)
				);

			if (pageIndex > currentPage) {
				currentPage = pageIndex;
				pageStartY += pageEndY;
				pageEndY = graphics2D.getClipBounds().getHeight();
			}

			graphics2D.translate(graphics2D.getClipBounds().getX(), graphics2D.getClipBounds().getY());
			
			Rectangle currentViewBounds = new Rectangle(
					0,
					(int) -pageStartY,
					(int) (getMinimumSize().getWidth()),
					(int) (getPreferredSize().getHeight())
				);
			
			if (printView(graphics2D, currentViewBounds, rootView))
				return Printable.PAGE_EXISTS;
			else
				return Printable.NO_SUCH_PAGE;
//			if (pageIndex >= 1) {
//			return Printable.NO_SUCH_PAGE;
//			}

//			Graphics2D g2 = (Graphics2D)graphics;

//			// translate the printer graphics context
//			// into the imageable area
//			g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
//			g2.drawString("Test String", 100, 100);
////			g2.setColor(Color.black);
//			paint(g2);
//			return Printable.PAGE_EXISTS;
		}

		protected boolean printView(Graphics2D graphics2D, Shape viewBounds, View view) {
			boolean pageExists = false;
			Rectangle clipBounds = graphics2D.getClipBounds();
			Shape childBounds;
			View childView;

			if (view.getViewCount() > 0) {
				for (int i = 0; i < view.getViewCount(); i++) {
					childBounds = view.getChildAllocation(i, viewBounds);
					if (childBounds != null) {
						childView = view.getView(i);
						if (printView(graphics2D, childBounds, childView)) {
							pageExists = true;
						}
					}
				}
			}
			else {
				if (viewBounds.getBounds().getMaxY() >= clipBounds.getY()) {
					pageExists = true;
					if ((viewBounds.getBounds().getHeight() > clipBounds.getHeight()) &&
							(viewBounds.intersects(clipBounds))) {
						view.paint(graphics2D, clipBounds);
					} else {
						if (viewBounds.getBounds().getY() >= clipBounds.getY()) {
							if (viewBounds.getBounds().getMaxY() <= clipBounds.getMaxY()) {
								view.paint(graphics2D,viewBounds);
							} else {
								if (viewBounds.getBounds().getY() < pageEndY) {
									pageEndY = viewBounds.getBounds().getY();
								}
							}
						}
					}
				}
			}
			return pageExists;
		}

	}

	private OffScreenJTextPane pane;
	
	/**
	 * 
	 */
	public HTMLDocumentPrinter() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.print.DocumentPrinter#printDocument(java.io.File)
	 */
	public void printDocument(File file) throws PrinterException {
		if (pane == null) {
			pane = new OffScreenJTextPane();
			Frame frame = new Frame();
			frame.setLayout(new BorderLayout());

			JScrollPane editorScrollPane = new JScrollPane(pane);
			editorScrollPane.setVerticalScrollBarPolicy(
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			editorScrollPane.setPreferredSize(new Dimension(250, 145));
			editorScrollPane.setMinimumSize(new Dimension(10, 10));

			frame.add(editorScrollPane, BorderLayout.CENTER);
			frame.setSize(400, 600);
			frame.setVisible(true);
		}
		try {
			pane.initPrinting(file);
		} catch (Exception e) {
			throw new PrinterException("Failed loading HTML "+e.getMessage());
		}
//		pane.setVisible(true);

		
		PrinterJob printerJob = PrinterJob.getPrinterJob();
		if (printerConfiguration.getPrintServiceName() != null)
			printerJob.setPrintService(PrintUtil.lookupPrintService(printerConfiguration.getPrintServiceName()));
		else
			printerJob.setPrintService(PrintServiceLookup.lookupDefaultPrintService());
		if (printerConfiguration.getPageFormat() != null)
			printerJob.setPrintable(pane, printerConfiguration.getPageFormat());
		else
			printerJob.setPrintable(pane);
//		printerJob.print();
	}

	private PrinterConfiguration printerConfiguration;

	/* (non-Javadoc)
	 * @see org.nightlabs.print.PrinterInterface#configure(org.nightlabs.print.PrinterConfiguration)
	 */
	public void configure(PrinterConfiguration printerConfiguration)
	throws PrinterException {
		this.printerConfiguration = printerConfiguration;
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.print.PrinterInterface#getConfiguration()
	 */
	public PrinterConfiguration getConfiguration() {
		return printerConfiguration;
	}

}
