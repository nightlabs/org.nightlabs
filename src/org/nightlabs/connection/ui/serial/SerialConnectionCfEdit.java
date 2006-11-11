package org.nightlabs.connection.ui.serial;

import org.eclipse.swt.widgets.Composite;
import org.nightlabs.connection.ui.AbstractConnectionCfEdit;

public class SerialConnectionCfEdit
		extends AbstractConnectionCfEdit
{

	@Override
	protected Composite _createConnectionCfEditComposite(Composite parent)
	{
		return new SerialConnectionCfEditComposite(parent, this);
	}

}
