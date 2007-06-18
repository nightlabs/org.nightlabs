/**
 * 
 */
package org.nightlabs.base.form;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

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
}
