/**
 * 
 */
package org.nightlabs.base.form;

import org.apache.log4j.Logger;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.nightlabs.base.toolkit.AbstractToolkit;

/**
 * Used to work around the problem with the {@link FormToolkit}, which recursively overrides the
 * menu! see {@link FormToolkit#adapt(Composite)} <br> <br>
 * This is now changed so that the menu of a child is only set if there was none before.
 * 
 * <p> This should only be a temporary solution. We will have to look at the Manifest Editor, 
 * for example, to see how to omit this workaround!</p>
 * 
 * @author Marius Heinzmann [marius<at>NightLabs<dot>de]
 */
public class NightlabsFormsToolkit extends AbstractToolkit 
{
	private static final Logger logger = Logger.getLogger(NightlabsFormsToolkit.class);
	
	public NightlabsFormsToolkit(Display display) {
		super(new FormColors(display));
	}

	public NightlabsFormsToolkit(FormColors colors) {
		super(colors);
	}

	@Override
	public void adapt(Composite composite) {
		composite.setBackground(getColors().getBackground());
		composite.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				((Control) e.widget).setFocus();
			}
		});
		// only propagate the menu down the tree, if the child has none yet
		// otherwise all menus set in the lower parts of the tree are overridden
		if (composite.getMenu() == null)
			composite.setMenu(composite.getParent().getMenu());
	}
			
	@Override
	protected void paintTableBorder(Control control, GC gc, Color color) {
		Rectangle b = control.getBounds();
		gc.setForeground(color);
		gc.drawRectangle(b.x -2, b.y -2, b.width+3, b.height+3);
//		gc.drawRectangle(b.x -1, b.y -1, b.width+1, b.height+1);
	}

	@Override
	protected void paintTextBorder(Control child, GC gc, Color color) {
		Rectangle b = child.getBounds();
		gc.setForeground(color);
//		gc.drawRectangle(b.x -1, b.y -1, b.width+1, b.height+1);
		gc.drawRectangle(b.x -2, b.y -2, b.width+4, b.height+4);
//		gc.drawRectangle(b.x - 1, b.y - 2, b.width + 1,	b.height + 3);
	}
	
	@Override
	protected boolean checkAdditionalTypesForBorders(Control control) {
		return false;
	}

	protected static final int minBorderSpace = 3;
	
	/**
	 * This method ensures that there is a big enough inset to draw the border around.
	 * @param child the Control which is getting a border and the insets are checked / enforced for.
	 */
	@Override
	protected void checkPrerequisites(Control child) {
		Composite parent = child.getParent();
		Layout layout = parent.getLayout();
		if (layout instanceof GridLayout) {
			GridLayout gridLayout = (GridLayout) layout;
			if (gridLayout.marginHeight < minBorderSpace)				gridLayout.marginHeight = minBorderSpace;
			if (gridLayout.marginWidth < minBorderSpace)				gridLayout.marginWidth = minBorderSpace;
			if (gridLayout.horizontalSpacing < minBorderSpace) 	gridLayout.horizontalSpacing = minBorderSpace;
			if (gridLayout.verticalSpacing < minBorderSpace) 		gridLayout.verticalSpacing = minBorderSpace;
		}
		else if (layout instanceof RowLayout) {
			RowLayout rowLayout = (RowLayout) layout;
			if (rowLayout.spacing < minBorderSpace)							rowLayout.spacing = minBorderSpace;
			if (rowLayout.marginHeight < minBorderSpace)				rowLayout.marginHeight = minBorderSpace;
			if (rowLayout.marginWidth < minBorderSpace)					rowLayout.marginWidth = minBorderSpace;
		}
		else if (layout instanceof FillLayout) {
			FillLayout fillLayout = (FillLayout) layout;
			if (fillLayout.spacing < minBorderSpace)						fillLayout.spacing = minBorderSpace;
			if (fillLayout.marginHeight < minBorderSpace)				fillLayout.marginHeight = minBorderSpace;
			if (fillLayout.marginWidth < minBorderSpace)				fillLayout.marginWidth = minBorderSpace;
		}
		else if (layout instanceof ColumnLayout) {
			ColumnLayout columnLayout = (ColumnLayout) layout;
			if (columnLayout.horizontalSpacing < minBorderSpace)	columnLayout.horizontalSpacing = minBorderSpace;
			if (columnLayout.verticalSpacing < minBorderSpace) 		columnLayout.verticalSpacing = minBorderSpace;
			if (columnLayout.leftMargin < minBorderSpace)					columnLayout.leftMargin = minBorderSpace;
			if (columnLayout.rightMargin < minBorderSpace)				columnLayout.rightMargin = minBorderSpace;
			if (columnLayout.topMargin < minBorderSpace)					columnLayout.topMargin = minBorderSpace;
			if (columnLayout.bottomMargin < minBorderSpace)				columnLayout.bottomMargin = minBorderSpace;
		}
		else if (layout instanceof TableWrapLayout) {
			TableWrapLayout wrapLayout = (TableWrapLayout) layout;
			if (wrapLayout.horizontalSpacing < minBorderSpace)	wrapLayout.horizontalSpacing = minBorderSpace;
			if (wrapLayout.verticalSpacing < minBorderSpace) 		wrapLayout.verticalSpacing = minBorderSpace;
			if (wrapLayout.leftMargin < minBorderSpace)					wrapLayout.leftMargin = minBorderSpace;
			if (wrapLayout.rightMargin < minBorderSpace)				wrapLayout.rightMargin = minBorderSpace;
			if (wrapLayout.topMargin < minBorderSpace)					wrapLayout.topMargin = minBorderSpace;
			if (wrapLayout.bottomMargin < minBorderSpace)				wrapLayout.bottomMargin = minBorderSpace;
		}
		else {
			logger.warn("The encountered layout is unkown, the margins can therefore not be set correctly"+
					" to be able to draw a flat looking border around the given Control:"+child);
		}
	}
}
