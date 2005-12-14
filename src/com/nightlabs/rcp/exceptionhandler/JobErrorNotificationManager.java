/*
 * Created 	on Oct 6, 2005
 * 					by alex
 *
 */
package com.nightlabs.rcp.exceptionhandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.internal.progress.ErrorInfo;

public class JobErrorNotificationManager 
// implements IJobErrorNotificationManager 
{

	public JobErrorNotificationManager() {
		super();
	}

	public boolean showErrorFor(Job job, String title, String msg) {
		ExceptionHandlerRegistry.asyncHandleException(job.getResult().getException());
		return true;
	}

	public boolean hasErrors() {
		return errors.size() > 0;
	}

	private List errors = new ArrayList();
	
	public void addError(IStatus status, Job job) {
		errors.add(new ErrorInfo(status, job));
		ExceptionHandlerRegistry.asyncHandleException(status.getException());
//		clearAllErrors();
	}
	
	public Collection getErrors() {
		return errors;
	}

	public void clearAllErrors() {
//		ErrorNotificationManager.removeFromFinishedJobs(errors);
		errors.clear();
	}

}
