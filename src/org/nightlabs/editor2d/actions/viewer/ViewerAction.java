package org.nightlabs.editor2d.actions.viewer;

import org.nightlabs.editor2d.AbstractEditor;
import org.nightlabs.editor2d.actions.AbstractEditorAction;
import org.nightlabs.editor2d.viewer.TestDialog;

public class ViewerAction 
extends AbstractEditorAction 
{
	public static String ID = ViewerAction.class.getName();
	
	public ViewerAction(AbstractEditor editor) {
		super(editor);
	}
	
	protected void init() {
		setId(ID);
		setText("Show Viewer");		
	}

	protected boolean calculateEnabled() {
		return true;
	}

	public void run() 
	{
		TestDialog dialog = new TestDialog(getShell(), getMultiLayerDrawComponent());
		dialog.open();
	}
	
}
