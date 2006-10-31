/**
 * 
 */
package org.nightlabs.base.entity.tree;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * Default impelentation of {@link IEntityTreeCategoryViewBinding} that 
 * delegates most methods to the category it is bound to and allows
 * to overwrite name and icon for the binding by decleration in the extension.
 * 
 * @author Alexander Bieber <!-- alex [AT] nightlabs [DOT] de -->
 *
 */
public class EntityTreeCategoryViewBinding implements IEntityTreeCategoryViewBinding {

	private String viewID;
	private String editorID;

	private String name;
	private Image image;
	
	private int indexHint;
	
	private IEntityTreeCategory category;
	
	
	/**
	 * 
	 */
	public EntityTreeCategoryViewBinding() {
	}

	/* (non-Javadoc)
	 * @see org.nightlabs.base.entity.tree.IEntityCategoryViewBinding#getViewID()
	 */
	public String getViewID() {
		return viewID;
	}

	public ITreeContentProvider createContentProvider(IEntityTreeCategoryContentConsumer contentConsumer) {
		return category.createContentProvider(contentConsumer, this);
	}

	public IEditorInput createEditorInput(Object element) {
		return category.createEditorInput(element);
	}

	public ITableLabelProvider createLabelProvider() {
		return category.createLabelProvider();
	}

	public String getEditorID() {
		return editorID;
	}

	public IEntityTreeCategory getEntityTreeCategory() {
		return category;
	}

	public Image getImage() {
		if (image == null)
			return category.getImage();
		return image;
	}

	public int getIndexHint() {
		return indexHint;
	}

	public String getName() {
		if (name == null)
			return category.getName();
		return name;
	}

	public void setEntityTreeCategory(IEntityTreeCategory category) {
		this.category = category;
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		viewID = config.getAttribute("viewID");
		if (viewID == null || "".equals(viewID))
			throw new IllegalArgumentException("Attribute viewID must be defined for a viewBinding.");
		
		editorID = config.getAttribute("editorID");
		if (editorID == null || "".equals(editorID))
			throw new IllegalArgumentException("Attribute editorID must be defined for a viewBinding");
		
		name = config.getAttribute("name");
		if (name == null || "".equals(name))
			this.name = null;
		
		
		String index = config.getAttribute("indexHint");
		try {
			indexHint = Integer.parseInt(index);
		} catch (Exception e) {
			indexHint = Integer.MAX_VALUE / 2;
		}
		
		String iconStr = config.getAttribute("icon");
		if(iconStr != null)
			image = AbstractUIPlugin.imageDescriptorFromPlugin(config.getDeclaringExtension().getNamespaceIdentifier(), iconStr).createImage();
		else
			image = null;
	}

	public int compareTo(IEntityTreeCategoryBinding other) {
		return this.getIndexHint() - other.getIndexHint();
	}

}
