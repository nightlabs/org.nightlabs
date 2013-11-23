package org.nightlabs.clone;

/**
 * Thrown if the target object cannot be cloned by the {@link CloneContext}.
 * 
 * @author Marius Heinzmann <!-- Marius[at]NightLabs[dot]de -->
 */
public class UnClonableException
	extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public UnClonableException()
	{
		super();
	}

	public UnClonableException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public UnClonableException(String message)
	{
		super(message);
	}

	public UnClonableException(Throwable cause)
	{
		super(cause);
	}
}
