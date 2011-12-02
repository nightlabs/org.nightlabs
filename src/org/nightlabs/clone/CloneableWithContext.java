package org.nightlabs.clone;

/**
 * TODO: write it! (Marius)
 * 
 * @author Marius Heinzmann <!-- Marius[at]NightLabs[dot]de -->
 */
public interface CloneableWithContext
{
	// TODO: How can we return the correct type here?
	/**
	 * Clones the implementing instance by using the given CloneContext.
	 * 
	 * <p>
	 * 	<b>Important</b>: Implementations should use the given context to create clones of their referenced members for
	 * 	the context is the instance that can detect circular reference chains and will break it. <br/>
	 * 	Therefore implementations cannot rely on context to return the clone when trying to create one through it.
	 * </p>
	 * 
	 * @param context The context that can be used to clone member objects.
	 * @param cloneReferences Whether referenced real Objects shall be cloned, too.
	 */
	CloneableWithContext clone(CloneContext context, boolean cloneReferences);

	/**
	 * Map the references of the original object that this method is called upon to the cloned instances.
	 * <p>
	 * 	If the cloning process was started with the cloneReferences flag set to <code>true</code>, then each object needs
	 * 	to know how it can apply its own references to the cloned instance.
	 * </p>
	 * <p>
	 * 	<b>Important</b>: When this method is called, all objects referenced by the implementing object have been cloned
	 * 	already!
	 * </p>
	 * 
	 * @param clone The cloned instance.
	 * @param context The context to query for clones.
	 */
	void updateReferencesOfClone(CloneableWithContext clone, CloneContext context);
}
