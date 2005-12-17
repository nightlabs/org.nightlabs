package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * A Label above a Text wrapped in a Composite.
 *
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 * @deprecated Use {@link org.nightlabs.base.composite.LabeledText} instead!
 */
public class LabeledTextComposite extends TightWrapperComposite {
	private Label labelCaption;
	private Text textControl;

	public LabeledTextComposite(
			Composite parent,
			String caption,
			int style,
			boolean setLayoutData
	) {
		super(parent, style,false);
		
		this.setLayout(new GridLayout());
		this.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		labelCaption = new Label(this, SWT.NONE);
		labelCaption.setText(caption);
		GridData labelCaptionLData = new GridData();
		labelCaptionLData.grabExcessHorizontalSpace = true;
		labelCaptionLData.horizontalAlignment = GridData.FILL;
		labelCaption.setLayoutData(labelCaptionLData);

		textControl = new Text(this, SWT.BORDER);
		GridData textControlLData = new GridData();
		textControlLData.grabExcessHorizontalSpace = true;
		textControlLData.horizontalAlignment = GridData.FILL;
		textControl.setLayoutData(textControlLData);
		textControl.setText("");

		this.layout();
	}
	
	public LabeledTextComposite(
			Composite parent,
			String caption,
			int style
	) {
		this(parent, caption, style, false);
	}
	
	public Text getTextControl() {
		return textControl;
	}
	
	public Label getCaptionControl() {
		return labelCaption;
	}

}
