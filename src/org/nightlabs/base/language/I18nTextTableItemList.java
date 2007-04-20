package org.nightlabs.base.language;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.language.LanguageCf;

public class I18nTextTableItemList {
	/*
	 * Map of ID & I18nText
	 */
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

		Map<Object, Object> newI18nTextMap = new HashMap<Object, Object>();

		String[] languageCodeArray = new String[supportLanguages.length];
		for(int i = 0; i < supportLanguages.length; i++){
			languageCodeArray[i] = supportLanguages[i].getLanguageID();
		}//for
		Arrays.sort(languageCodeArray);

		I18nText i18nText = (I18nText)obj;
		for(int i = 0; i < languageCodeArray.length; i ++){
			String value = i18nText.getText(languageCodeArray[i]);
			newI18nTextMap.put(languageCodeArray[i], value);
		}//for

		createI18nTextMap(newI18nTextMap);		
	}

	private LanguageCf[] supportLanguages = LanguageManager.sharedInstance().getLanguages().toArray(new LanguageCf[0]);
	/*
	 * Create geographyNames of input
	 */
	public void createI18nTextMap(Map<Object, Object> newI18nTextMap){
		I18nText i18nText =  null;
		Set<Object> inputLanguageKeySet = (newI18nTextMap == null ? null : newI18nTextMap.keySet());

		for (int i = 0; i < supportLanguages.length; i++) {
			if(inputLanguageKeySet != null && inputLanguageKeySet.size() > 0){
				for(Object inputLang : inputLanguageKeySet){
					if(inputLang instanceof String) {
						String inputLanguageID = (String)inputLang;
						if(inputLanguageID.equalsIgnoreCase(supportLanguages[i].getLanguageID())){
							if(i18nTableItemMap.get(inputLang) == null){
								i18nText = new I18nTextBuffer();
								i18nText.setText(inputLanguageID, (newI18nTextMap.get(inputLang)).toString());
							}//if
							else{
								i18nText = (I18nText)i18nTableItemMap.get(inputLanguageID);
								i18nText.setText("", newI18nTextMap.get(inputLang).toString());
							}//else
							i18nTableItemMap.put(inputLanguageID, i18nText);
						}//if
					}//if
				}//for
			}//if
			else{
				i18nText = new I18nTextBuffer();
				i18nTableItemMap.put(supportLanguages[i].getLanguageID(), i18nText);
			}//else
		}//for
	}
}