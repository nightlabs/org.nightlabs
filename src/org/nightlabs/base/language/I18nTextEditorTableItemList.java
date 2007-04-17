package org.nightlabs.base.language;

import java.util.HashMap;
import java.util.Map;


public abstract class I18nTextEditorTableItemList {
	protected Map<String, I18nTextEditorTableItem> i18nTableItemMap = new HashMap<String, I18nTextEditorTableItem>();

	public void setI18nTableItemMap(Map<String, I18nTextEditorTableItem> tableItemMap) {
		i18nTableItemMap = tableItemMap;
	}

	/**
	 * Return the map of i18nTextMap
	 */
	public Map getI18nTableItemMap() {
		return i18nTableItemMap;
	}
}
