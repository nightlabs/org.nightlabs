package org.nightlabs.base.language;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.language.LanguageCf;

public class I18nTextTableContentProvider implements IStructuredContentProvider // , I18nTextTableListener // Marco: I don't think that we need this interface.
{
////		private I18nTextTableItemList i18nTextTableList;
//		private TableViewer tableViewer;

// Marco: We don't need to know our TableViewer - and it's better if we don't ;-)
//		public I18nTextTableContentProvider(
////				I18nTextTableItemList i18nTextList,
//				TableViewer tableViewer){
////			this.i18nTextTableList = i18nTextList;
//			this.tableViewer = tableViewer;
//		}

		private I18nText i18nText = null;
//		private Map<String, String> elementsMap = null;
//		private Map.Entry<String, String>[] elements = null;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput instanceof I18nText)
				this.i18nText = (I18nText) newInput;
			else
				this.i18nText = null;

//			this.elementsMap = null;
//			this.elements = null;
//			if (newInput != null)
//				((I18nTextTableItemList) newInput).addChangeListener(this);
//			if (oldInput != null)
//				((I18nTextTableItemList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
//			i18nTextTableList.removeChangeListener(this);
		}

		// Return the i18nText as an array of Objects
		@SuppressWarnings("unchecked")
		public Object[] getElements(Object parent) {
			if (i18nText == null)
				return new Object[0];

			Collection<LanguageCf> languageCfs = LanguageManager.sharedInstance().getLanguages();
			Map<String, String> es = new HashMap<String, String>(languageCfs.size());
			for (Map.Entry<String, String> me : i18nText.getTexts()) {
				String languageID = me.getKey();
				String text = me.getValue();
				es.put(languageID, text);
			}

			for (LanguageCf languageCf : languageCfs) {
				String languageID = languageCf.getLanguageID();
				if (!es.containsKey(languageID))
					es.put(languageID, "");
			}

			return es.entrySet().toArray();
//					elements = elementsMap.entrySet().toArray(new Map.Entry<String, String>[0]);
//			return i18nTextTableList.getI18nTableItemMap().values().toArray();
		}

// Marco: We only have ONE I18nText (which is set as input) - why should we add or remove them?
//		/* (non-Javadoc)
//		 * @see II18nTextListViewer#addI18nText(I18nText)
//		 */
//		public void addI18nText(I18nText i18nText) {
//			tableViewer.add(i18nText);
//		}
//
//		/* (non-Javadoc)
//		 * @see I18nTextListViewer#removeI18nText(I18nText)
//		 */
//		public void removeI18nText(I18nText i18nText) {
//			tableViewer.remove(i18nText);			
//		}
//
//		/* (non-Javadoc)
//		 * @see II18nTextListViewer#updateI18nText(I18nText)
//		 */
//		public void updateI18nText(I18nText i18nText) {
//			tableViewer.update(i18nText, null);	
//		}
	}