package org.nightlabs.base.language;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.i18n.I18nText;

public class I18nTextTableContentProvider implements IStructuredContentProvider, I18nTextTableListener {
		private I18nTextTableItemList i18nTextTableList;
		private TableViewer tableViewer;
		
		public I18nTextTableContentProvider(I18nTextTableItemList i18nTextList, TableViewer tableViewer){
			this.i18nTextTableList = i18nTextList;
			this.tableViewer = tableViewer;
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((I18nTextTableItemList) newInput).addChangeListener(this);
			if (oldInput != null)
				((I18nTextTableItemList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			i18nTextTableList.removeChangeListener(this);
		}

		// Return the i18nText as an array of Objects
		public Object[] getElements(Object parent) {
			return i18nTextTableList.getI18nTableItemMap().values().toArray();
		}

		/* (non-Javadoc)
		 * @see II18nTextListViewer#addI18nText(I18nText)
		 */
		public void addI18nText(I18nText i18nText) {
			tableViewer.add(i18nText);
		}

		/* (non-Javadoc)
		 * @see I18nTextListViewer#removeI18nText(I18nText)
		 */
		public void removeI18nText(I18nText i18nText) {
			tableViewer.remove(i18nText);			
		}

		/* (non-Javadoc)
		 * @see II18nTextListViewer#updateI18nText(I18nText)
		 */
		public void updateI18nText(I18nText i18nText) {
			tableViewer.update(i18nText, null);	
		}
	}