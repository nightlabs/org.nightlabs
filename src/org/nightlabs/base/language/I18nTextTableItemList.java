package org.nightlabs.base.language;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.language.LanguageCf;

/**
 * @deprecated Marco: This whole class is not necessary!
 */
public class I18nTextTableItemList {
	/*
	 * Map of ID & I18nText
	 */
	// Marco: why do you manage multiple instances of I18nText? there is only ONE instance!
	protected Map<String, I18nText> i18nTableItemMap = new HashMap<String, I18nText>();

	public void setI18nTableItemMap(Map<String, I18nText> tableItemMap) {
		i18nTableItemMap = tableItemMap;
	}

	/**
	 * Return the map of i18nTextMap
	 */
	public Map getI18nTableItemMap() {
		return i18nTableItemMap;
	}

	private Set<I18nTextTableListener> changeListeners = new HashSet<I18nTextTableListener>();
	private Object obj;

	/**
	 * Constructor
	 */
	public I18nTextTableItemList() {
		super();
		this.initData();
	}

	/*
	 * Initialize the table data.
	 * Create COUNT names and add them them to the 
	 * collection of names
	 */
	private void initData() {
		createI18nTextMap(null);
	}

	/**
	 * Add a new i18nText to the collection of i18nTexts
	 */
	public void addI18nText() {
	}

	/**
	 * @param i18nText
	 */
	public void removeI18nText(I18nText i18nText) {
	}

	/**
	 * @param i18nText
	 */
	public void i18nTextChanged(I18nText i18nText) {
		for(I18nTextTableListener listener: changeListeners)
			listener.updateI18nText(i18nText);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(I18nTextTableListener viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(I18nTextTableListener viewer) {
		changeListeners.add(viewer);
	}

	public Object getObject() {
		return obj;
	}

	public void setObject(Object obj) {
		this.obj = obj;
	}

	public void updateI18nTextMap(Object obj){
		this.obj = obj;

		Map<Object, I18nText> newI18nTextMap = new HashMap<Object, I18nText>();

		I18nText i18nText = (I18nText)obj;
		for(int i = 0; i < supportLanguages.length; i++){
			newI18nTextMap.put(supportLanguages[i].getLanguageID(), i18nText);
		}//for
		
		createI18nTextMap(newI18nTextMap);		
	}

	private LanguageCf[] supportLanguages = LanguageManager.sharedInstance().getLanguages().toArray(new LanguageCf[0]);
	/*
	 * Create geographyNames of input
	 */
	public void createI18nTextMap(Map<Object, I18nText> newI18nTextMap){
		I18nText i18nText =  null;
		Set<Object> inputLanguageKeySet = (newI18nTextMap == null ? null : newI18nTextMap.keySet());
		for (int i = 0; i < supportLanguages.length; i++) {
			if(inputLanguageKeySet != null && inputLanguageKeySet.size() > 0){
				for(Object inputLang : inputLanguageKeySet){
					if(inputLang != null && inputLang instanceof String) {
						String inputLanguageID = (String)inputLang;
						if(inputLanguageID.equalsIgnoreCase(supportLanguages[i].getLanguageID())){
							if(i18nTableItemMap.get(inputLang).getText(supportLanguages[i].getLanguageID()) == null){
								i18nText = new I18nTextBuffer();
								i18nText.setText(inputLanguageID, (newI18nTextMap.get(inputLang)).getText(inputLanguageID));
							}//if
							else{
								i18nText = (I18nText)i18nTableItemMap.get(inputLanguageID);
								i18nText.setText(inputLanguageID, newI18nTextMap.get(inputLang).getText(inputLanguageID));
							}//else
							i18nTableItemMap.put(inputLanguageID, i18nText);
						}//if
					}//if
				}//for
			}//if
			else{
				i18nText = new I18nTextBuffer();
				i18nText.setText(supportLanguages[i].getLanguageID(), "???");
				i18nTableItemMap.put(supportLanguages[i].getLanguageID(), i18nText);
			}//else
		}//for
	}
}