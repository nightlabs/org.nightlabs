/**
 * 
 */
package org.nightlabs.base.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

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
public class NightlabsFormsToolkit extends FormToolkit {

	protected class BorderPainter implements PaintListener {
		
		public void paintControl(PaintEvent event) {
			Composite composite = (Composite) event.widget;
			Control[] children = composite.getChildren();
			for (int i = 0; i < children.length; i++) {
				Control c = children[i];
				boolean inactiveBorder = false;
				boolean textBorder = false;
				if (!c.isVisible())
					continue;
				/*
				if (c.getEnabled() == false && !(c instanceof CCombo))
					continue;
					*/
				if (c instanceof Hyperlink)
					continue;
				Object flag = c.getData(KEY_DRAW_BORDER);
				if (flag != null) {
					if (flag.equals(Boolean.FALSE))
						continue;
					if (flag.equals(TREE_BORDER))
						inactiveBorder = true;
					else if (flag.equals(TEXT_BORDER))
						textBorder = true;
				}
				if (getBorderStyle() == SWT.BORDER) {
					if (!inactiveBorder && !textBorder) {
						continue;
					}
					if (c instanceof Text || c instanceof Table
							|| c instanceof Tree)
						continue;
				}
				if (!inactiveBorder
						&& (c instanceof Text || c instanceof CCombo || textBorder)) {
					Rectangle b = c.getBounds();
					GC gc = event.gc;
//					gc.setForeground(c.getBackground());
					gc.setForeground(getColors().getBorderColor());
//					gc.drawRectangle(b);
//					gc.drawRectangle(b.x, b.y -1, b.width +2, b.height);
					gc.drawRectangle(b.x + 1, b.y + 1, b.width - 1,	b.height - 1);
					// gc.setForeground(getBorderStyle() == SWT.BORDER ? colors
					// .getBorderColor() : colors.getForeground());
//					if (c instanceof CCombo)
//						gc.drawRectangle(b.x + 1, b.y + 1, b.width - 1,
//								b.height - 1);
//					else
//					gc.drawRectangle(b.x + 1, b.y - 1, b.width - 1,	b.height - 1);
				} else if (inactiveBorder || c instanceof Table
						|| c instanceof Tree) {
					Rectangle b = c.getBounds();
					GC gc = event.gc;
					gc.setForeground(getColors().getBorderColor());
					gc.drawRectangle(b.x + 1, b.y + 1, b.width - 1,
							b.height - 1);
				}
			}
		}
		
	}
	
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
	
	private BorderPainter borderPainter;
	@Override
	public void paintBordersFor(Composite parent) {
		super.paintBordersFor(parent);
//		if (borderPainter == null)
//			borderPainter = new BorderPainter();
//		parent.addPaintListener(borderPainter);
	}
	
}
