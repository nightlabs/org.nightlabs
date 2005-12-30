/* *****************************************************************************
 * org.nightlabs.base - NightLabs Eclipse utilities                            *
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

package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Label above a Text wrapped in a Composite. 
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class LabeledText extends XComposite {
	private Label labelCaption;
	private Text textControl;

	/**
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 * @param setLayoutData Whether or not to create and assign a {@link GridData} object.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle,
			int labelStyle,
			int wrapperStyle,
			boolean setLayoutData
	) {
		super(parent, wrapperStyle,
				XComposite.LAYOUT_MODE_TIGHT_WRAPPER,
				setLayoutData ? XComposite.LAYOUT_DATA_MODE_GRID_DATA : LAYOUT_DATA_MODE_NONE);

//		this.setLayout(new GridLayout());
		getGridLayout().verticalSpacing = 5;
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		labelCaption = new Label(this, labelStyle);
		labelCaption.setText(caption);
		GridData labelCaptionLData = new GridData();
		labelCaptionLData.grabExcessHorizontalSpace = true;
		labelCaptionLData.horizontalAlignment = GridData.FILL;
		labelCaption.setLayoutData(labelCaptionLData);

		textControl = new Text(this, textStyle);
		GridData textControlLData = new GridData();
		textControlLData.grabExcessHorizontalSpace = true;
		textControlLData.horizontalAlignment = GridData.FILL;
		textControl.setLayoutData(textControlLData);
		textControl.setText("");
		this.layout();
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int, int, int, boolean)} with
	 * <tt>labelStyle = SWT.NONE</tt> and <tt>wrapperStyle = SWT.NONE</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 * @param setLayoutData Whether or not to create and assign a {@link GridData} object.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle,
			boolean setLayoutData
	) {
		this(parent, caption, textStyle, SWT.NONE, SWT.NONE, setLayoutData);
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int, boolean)} with <tt>setLayoutData = true</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 * @param textStyle The style that will be applied to the wrapped <tt>Text</tt>.
	 */
	public LabeledText(
			Composite parent,
			String caption,
			int textStyle
	) {
		this(parent, caption, textStyle, false);
	}

	/**
	 * This calls {@link #LabeledText(Composite, String, int)} with <tt>textStyle = SWT.BORDER</tt>.
	 *
	 * @param parent The composite into which the new <tt>LabeledText</tt> will be added as child.
	 * @param caption The title that will be shown by the wrapped <tt>Label</tt>.
	 */
	public LabeledText(
			Composite parent,
			String caption
	) {
		this(parent, caption, SWT.BORDER);
	}

	public Text getTextControl() {
		return textControl;
	}
	
	public Label getCaptionControl() {
		return labelCaption;
	}

}
