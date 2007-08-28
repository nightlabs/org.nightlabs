package org.nightlabs.base.celleditor;

import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.nightlabs.version.MalformedVersionException;
import org.nightlabs.version.Version;

/**
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 *
 */
public class VersionCellEditor 
	extends XCellEditor 
{
//	public VersionCellEditor() {
//		setValidator(new VersionValidator());
//	}
	
	/**
	 * @param parent
	 */
	public VersionCellEditor(Composite parent) {
		this(parent, SWT.NONE);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public VersionCellEditor(Composite parent, int style) {
		this(parent, style, false);
	}

	/**
	 * @param parent
	 * @param style
	 * @param readOnly
	 */
	public VersionCellEditor(Composite parent, int style, boolean readOnly) {
		super(parent, style, readOnly);
		setValidator(new VersionValidator());
	}

	protected Text cellWidget;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		cellWidget = new Text(parent, getStyle());
		return cellWidget;
	}

	protected class VersionValidator implements ICellEditorValidator {

		public String isValid(Object value) {
			if (value instanceof Version)
				return null;
			
			final String versionString = (String) value;
			try {
				new Version(versionString);
			} catch (MalformedVersionException e) {
				return e.getMessage();
			}
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doGetValue()
	 */
	@Override
	protected Version doGetValue() {
		try {
			return new Version(cellWidget.getText());
		} catch (MalformedVersionException e) {
			throw new RuntimeException("The stored String representation of a Version is invalid! "+ //$NON-NLS-1$
					"This should never happen since its validity is checked on change!",e); //$NON-NLS-1$
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetFocus()
	 */
	@Override
	protected void doSetFocus() {
  	if (cellWidget != null) {
  		cellWidget.selectAll();
  		cellWidget.setFocus();
  	}    
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.CellEditor#doSetValue(java.lang.Object)
	 */
	@Override
	protected void doSetValue(Object value) {
		if (isReadOnly())
			return;
		
  	Assert.isTrue(cellWidget != null && (value instanceof Version));
		final Version version = (Version) value;
		cellWidget.setText(version.toString());
	}

}
