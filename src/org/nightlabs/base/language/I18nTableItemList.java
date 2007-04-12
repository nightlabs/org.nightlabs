package org.nightlabs.base.language;

import java.util.HashMap;
import java.util.Map;


public abstract class I18nTableItemList {
	protected Map<String, I18nTableItem> i18nTableItemMap = new HashMap<String, I18nTableItem>();

	public void setI18nTableItemMap(Map<String, I18nTableItem> tableItemMap) {
		i18nTableItemMap = tableItemMap;
	}

	/**
	 * Return the map of i18nTextMap
	 */
	public Map getI18nTableItemMap() {
		return i18nTableItemMap;
	}
}
