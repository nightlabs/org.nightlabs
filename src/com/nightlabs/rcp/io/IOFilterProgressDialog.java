/**
 * <p> Project: com.nightlabs.base </p>
 * <p> Copyright: Copyright (c) 2005 </p>
 * <p> Company: NightLabs GmbH (Germany) </p>
 * <p> Creation Date: 29.08.2005 </p>
 * <p> Author: Daniel Mazurek </p>
**/
package com.nightlabs.rcp.io;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Shell;

import com.nightlabs.io.AbstractIOFilterWithProgress;
import com.nightlabs.io.IOFilter;
import com.nightlabs.io.IOFilterWithProgress;

public class IOFilterProgressDialog 
extends ProgressMonitorDialog
{

	protected IOFilter ioFilter;
	protected IOFilterWithProgress progressFilter;
	public IOFilterProgressDialog(Shell parent, IOFilter ioFilter) 
	{
		super(parent);
		this.ioFilter = ioFilter;
		if (ioFilter instanceof IOFilterWithProgress) {
			progressFilter = (IOFilterWithProgress) ioFilter;		
			progressFilter.addPropertyChangeListener(subProgressListener);
			getProgressMonitor().beginTask(ioFilter.getDescription(), progressFilter.getTotalWork());			
		}
		else {
			getProgressMonitor().beginTask(ioFilter.getDescription(), 2);			
		}
	}
	
	protected PropertyChangeListener subProgressListener = new PropertyChangeListener()
	{	
		public void propertyChange(PropertyChangeEvent evt) 
		{
			Object newValue = evt.getNewValue();
			if (newValue.equals(AbstractIOFilterWithProgress.PROGRESS_CHANGED)) {
				int work = ((Integer)newValue).intValue();
				getProgressMonitor().internalWorked(work); 
			}
			else if (newValue.equals(AbstractIOFilterWithProgress.SUBTASK_FINISHED)) {
				String subTaskName = (String) newValue;
				getProgressMonitor().subTask(subTaskName);
			}
		}	
	};
	
}
