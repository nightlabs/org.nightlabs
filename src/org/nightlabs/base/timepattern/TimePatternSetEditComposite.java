/**
 * 
 */
package org.nightlabs.base.timepattern;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.base.composite.XComposite;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class TimePatternSetEditComposite extends XComposite {

	/**
	 * @param parent
	 * @param style
	 */
	public TimePatternSetEditComposite(Composite parent, int style) {
		super(parent, style);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 */
	public TimePatternSetEditComposite(Composite parent, int style,
			LayoutMode layoutMode) {
		super(parent, style, layoutMode);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutDataMode
	 */
	public TimePatternSetEditComposite(Composite parent, int style,
			LayoutDataMode layoutDataMode) {
		super(parent, style, layoutDataMode);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 */
	public TimePatternSetEditComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode) {
		super(parent, style, layoutMode, layoutDataMode);
	}

	/**
	 * @param parent
	 * @param style
	 * @param layoutMode
	 * @param layoutDataMode
	 * @param cols
	 */
	public TimePatternSetEditComposite(Composite parent, int style,
			LayoutMode layoutMode, LayoutDataMode layoutDataMode, int cols) {
		super(parent, style, layoutMode, layoutDataMode, cols);
	}

}
