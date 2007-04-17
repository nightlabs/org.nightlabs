package org.nightlabs.base.language;

public abstract class I18nTextEditorTableItem {
	private String languageID;
	private String value;
	
	public I18nTextEditorTableItem(String languageID, String value){
		this.languageID = languageID;
		setValue(value);
	}
	/**
	 * @return String languageID
	 */
	public String getLanguageID() {
		return languageID;
	}

	/**
	 * Set the 'languageID' property
	 * 
	 * @param string
	 */
	public void setLanguageID(String languageID) {
		this.languageID = languageID;
	}

	/**
	 * @return String value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Set the 'value' property
	 * 
	 * @param string
	 */
	public void setValue(String value) {
		this.value = value;
	}
}