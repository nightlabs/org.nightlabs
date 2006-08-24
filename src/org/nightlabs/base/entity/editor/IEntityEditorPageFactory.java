/**
 * 
 */
package org.nightlabs.base.entity.editor;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public interface IEntityEditorPageFactory {
	
	public IFormPage createPage(FormEditor formEditor);

}
