package org.nightlabs.keyreader;

import java.util.Locale;
import java.util.Map;

import org.nightlabs.i18n.I18nUtil;

public class KeyReaderImplementation
{
	private String keyReaderClassName;
	private Class<? extends KeyReader> keyReaderClass;
	private Map<Locale, String> names;
	private Map<Locale, String> descriptions;

	public KeyReaderImplementation(KeyReader keyReader)
	{
		this.keyReaderClass = keyReader.getClass();
		this.keyReaderClassName = keyReaderClass.getName();
		this.names = keyReader.getNames();
		this.descriptions = keyReader.getDescriptions();
	}

	public String getKeyReaderClassName()
	{
		return keyReaderClassName;
	}

	public void setKeyReaderClassName(String className)
	{
		this.keyReaderClassName = className;
	}

	public Class<? extends KeyReader> getKeyReaderClass()
	{
		return keyReaderClass;
	}

	public void setKeyReaderClass(Class<? extends KeyReader> clazz)
	{
		this.keyReaderClass = clazz;
	}

	public Map<Locale, String> getDescriptions()
	{
		return descriptions;
	}

	public void setDescriptions(Map<Locale, String> descriptions)
	{
		this.descriptions = descriptions;
	}

	public Map<Locale, String> getNames()
	{
		return names;
	}

	public String getName(Locale locale)
	{
		return I18nUtil.getClosestL10n(names, locale);
	}

	public String getDescription(Locale locale)
	{
		return I18nUtil.getClosestL10n(descriptions, locale);
	}

	public void setNames(Map<Locale, String> names)
	{
		this.names = names;
	}
}
