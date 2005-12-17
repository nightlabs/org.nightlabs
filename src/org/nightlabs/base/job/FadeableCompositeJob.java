/**
 * Created Sep 5, 2005, 12:17:33 AM by nick
 */
package org.nightlabs.base.job;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import org.nightlabs.base.composite.Fadeable;

/**
 * @author Niklas Schiffler <nick@nightlabs.de>
 *
 */
public abstract class FadeableCompositeJob extends Job
{
  private Fadeable composite;
  private Object source;
  
  public FadeableCompositeJob(String label, Fadeable comp, Object source)
  {
  	super(label);
  	this.composite = comp;
  	this.source = source;
  }

  protected IStatus run(IProgressMonitor monitor)
  {
  	IStatus ret = Status.CANCEL_STATUS;
  	try
  	{
  		Display.getDefault().syncExec(
  				new Runnable() 
  				{
  					public void run() 
  					{
  						composite.setFaded(true);
  					}
  				}
  		);
  		ret = run(monitor, source);
  	}
  	finally
  	{
  		Display.getDefault().syncExec(
  				new Runnable() 
  				{
  					public void run() 
  					{
  						composite.setFaded(false);
  					}
  				}
  		);
  	}
    return ret;
  }

  public abstract IStatus run(IProgressMonitor monitor, Object source);
}
