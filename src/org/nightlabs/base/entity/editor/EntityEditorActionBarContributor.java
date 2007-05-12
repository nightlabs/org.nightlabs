/**
 * 
 */
package org.nightlabs.base.entity.editor;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;

/**
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityEditorActionBarContributor extends MultiPageEditorActionBarContributor {
	
	// TODO find out how to trigger re-contribution of EditorActionBarContributions
	
	private Logger logger = Logger.getLogger(EntityEditorActionBarContributor.class);

	/**
	 * 
	 */
	public EntityEditorActionBarContributor() {
	}

	@Override
	public void contributeToCoolBar(ICoolBarManager coolBarManager) {
		super.contributeToCoolBar(coolBarManager);
		logger.info("contributeToCoolBar");
	}
	
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		super.contributeToMenu(menuManager);
		logger.info("contributeToMenu");
	}
	
	@Override
	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
		logger.info("contributeToToolBar");
	}
	
	@Override
	public void setActivePage(IEditorPart activeEditor) {
		System.out.println("ActivePage: "+activeEditor);
	}
}
