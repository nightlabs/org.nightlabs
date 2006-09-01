/**
 * 
 */
package org.nightlabs.base.entity.editor;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * <p>Implementations of this class are registered to the <b>entityEditor</b>
 * extension point with the <b>pageFactory</b> element.</p>
 * 
 * <p>They are responsible for creating the GUI (IFormPage) for an entity editor page
 * as well as its controller that is responsible for loading and saving
 * the page's model.</p>
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageFactory {
	
	/**
	 * Create an new IFormPage that will dipslay the part
	 * of the entity linked to this entity editor page. 
	 * For your convenience you might subclass {@link FormPage},
	 * or any other default implementation. Also take a look
	 * at {@link EntityEditorPageWithProgress} that provides
	 * support for loading (large or slow) data within
	 * asynchronous jobs.
	 * 
	 * @param formEditor The editor to create the page for.
	 * @return A new entity editor page.
	 */
	public IFormPage createPage(FormEditor formEditor);
	
	/**
	 * <p>Create a new page controller for the pages of this
	 * factory. The page controller will be added to the editors
	 * {@link EntityEditorController}. A {@link IEntityEditorPageFactory}
	 * might choose to return <code>null</code> here as well, then, however
	 * the associated editor or the page itself somehow have to handle
	 * data loading.</p>
	 * 
	 * <p>Also for the page controller there exists a base class with 
	 * extra functionality concerning background loading of data, see {@link EntityEditorPageController}</p>  
	 * 
	 * @param editor The editor for wich a controller should be created
	 * @return A new page controller appropriate to pages created by this factory.
	 */
	public IEntityEditorPageController createPageController(EntityEditor editor);

}
