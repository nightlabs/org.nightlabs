/**
 *
 */
package org.nightlabs.editor2d.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.util.List;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.print.DrawComponentPrintable.PrintConstant;

/**
 * @author daniel[at]nightlabs[dot]de
 *
 */
public class DrawComponentPageable implements Pageable {

	private static final Logger logger = Logger.getLogger(DrawComponentPageable.class);

	private DrawComponentPrintable printable;
	private PageFormat printerPageFormat;

	public DrawComponentPageable(PageFormat printerPageFormat, List<? extends DrawComponent>
		drawComponents, PrintConstant printConstant, boolean showPageBounds)
	{
		printable = new DrawComponentPrintable(drawComponents, printConstant, showPageBounds);
		this.printerPageFormat = printerPageFormat;
	}

	/* (non-Javadoc)
	 * @see java.awt.print.Pageable#getNumberOfPages()
	 */
	@Override
	public int getNumberOfPages() {
		return printable.getTotalAmountOfPages();
	}

	 public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException
	 {
			PageFormat pageFormat = new PageFormat();
			Paper paper = new Paper();

			double paperWidth = printerPageFormat.getPaper().getWidth();
			double paperHeight = printerPageFormat.getPaper().getHeight();
			int orientation = printable.getOrientation(pageIndex);

//			if (printerPageFormat.getOrientation() == PageFormat.PORTRAIT &&
//				orientation != PageFormat.PORTRAIT)
//			{
//				double d = paperWidth;
//				paperWidth = paperHeight;
//				paperHeight = d;
//			}

			paper.setSize(paperWidth, paperHeight);

			// Sets the width and height of the Paper object to the measures of the default page (see above).
			// Sets the orientation of the PageFormat object to the one of the currently considered PDF page.
			if (logger.isDebugEnabled()) {
				logger.debug("pageFormat orientation = "+orientation);
			}
			// Sets the imageable area (the area in which printing occurs) of the Paper object according to the
			// measures of the default page. The imageable area of the paper begins directly at the top-left point.
			paper.setImageableArea(0, 0, paperWidth, paperHeight);

			pageFormat.setPaper(paper);
			pageFormat.setOrientation(orientation);

			if (logger.isDebugEnabled()) {
				logger.debug("getPageFormat: width " + pageFormat.getWidth());
				logger.debug("getPageFormat: height " + pageFormat.getHeight());
				logger.debug("getPageFormat: imageableX " + pageFormat.getImageableX());
				logger.debug("getPageFormat: imageableY " + pageFormat.getImageableY());
				logger.debug("getPageFormat: imageableWidth " + pageFormat.getImageableWidth());
				logger.debug("getPageFormat: imageableHeight " + pageFormat.getImageableHeight());
			}

			return pageFormat;
     }
	/* (non-Javadoc)
	 * @see java.awt.print.Pageable#getPrintable(int)
	 */
	@Override
	public Printable getPrintable(int pageIndex)
	throws IndexOutOfBoundsException
	{
		return printable;
	}

}
