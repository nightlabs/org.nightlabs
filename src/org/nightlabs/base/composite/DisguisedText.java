/*
 * Created 	on Jan 8, 2005
 * 					by alex
 *
 */
package org.nightlabs.base.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * Provides a Text Widget with no border
 * and the same Background as
 * its parent. As subclassing of {@link org.eclipse.swt.widgets.Widget}
 * is not allowed a static method will 
 * create and return a Text.
 * 
 * @author Alexander Bieber <alex[AT]nightlabs[DOT]de>
 */
public class DisguisedText {

	/**
	 * Creates a disguised Text.
	 * 
	 * @param parent
	 */
	public static Text createText(Composite parent) {
		Text text = new Text(parent,SWT.NONE);
		text.setBackground(parent.getBackground());
		return text;
	}
	
	/**
	 * Creates a Label and a disguised Text and adds
	 * GridDatas to them assuming that the parent has
	 * a GridLayout with two free columns in the current row. 
	 * 
	 * @param title
	 * @param parent
	 * @return
	 */
	public static LabeledDisguisedText createLabeledText(String title, Composite parent) {
		Label label = new Label(parent, SWT.NONE);
		label.setText(title);
		label.setLayoutData(new GridData());
		
		Text text = createText(parent);
		text.setText("");
		text.setEditable(true);
		GridData gd = new GridData();
		gd.grabExcessHorizontalSpace = true;
		gd.horizontalAlignment = GridData.FILL;
		text.setLayoutData(gd);
		LabeledDisguisedText result = new LabeledDisguisedText();
		result.setLabelControl(label);
		result.setTextControl(text);
		return result;
	}
	
	/**
	 * Helper class for {@link DisguisedText#createLabeledText(String, Composite)}
	 *
	 */
	public static class LabeledDisguisedText {

		/**
		 * 
		 */
		public LabeledDisguisedText() {
			super();
		}
		
		private Label labelControl;
		private Text textControl;
		
		public Label getLabelControl() {
			return labelControl;
		}
		public void setLabelControl(Label labelControl) {
			this.labelControl = labelControl;
		}
		public Text getTextControl() {
			return textControl;
		}
		public void setTextControl(Text textControl) {
			this.textControl = textControl;
		}

	}	
}
