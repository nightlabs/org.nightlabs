/* *****************************************************************************
 * NightLabsJDO - NightLabs Utilities for JDO                                  *
 * Copyright (C) 2004-2005 NightLabs - http://NightLabs.org                    *
 *                                                                             *
 * This library is free software; you can redistribute it and/or               *
 * modify it under the terms of the GNU Lesser General Public                  *
 * License as published by the Free Software Foundation; either                *
 * version 2.1 of the License, or (at your option) any later version.          *
 *                                                                             *
 * This library is distributed in the hope that it will be useful,             *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of              *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU           *
 * Lesser General Public License for more details.                             *
 *                                                                             *
 * You should have received a copy of the GNU Lesser General Public            *
 * License along with this library; if not, write to the                       *
 *     Free Software Foundation, Inc.,                                         *
 *     51 Franklin St, Fifth Floor,                                            *
 *     Boston, MA  02110-1301  USA                                             *
 *                                                                             *
 * Or get it online :                                                          *
 *     http://www.gnu.org/copyleft/lesser.html                                 *
 *                                                                             *
 *                                                                             *
 ******************************************************************************/

package org.nightlabs.jdo.moduleregistry;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Manifest;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.nightlabs.moduleregistry.jdo.ModuleMetaDataID;
import org.nightlabs.version.MalformedVersionException;
import org.nightlabs.version.Version;

/**
 * An instance of this class should be stored into the datastore for every EAR. Hence,
 * we recommend to use the EAR name as <tt>moduleID</tt>. Your EAR should use the
 * <a href="https://www.jfire.org/modules/phpwiki/index.php/Framework%20OrganisationInit">organisation
 * initialisation</a> to update an old datastore or to initialise a new one.
 *
 * @author marco schulze - marco at nightlabs dot de
 */
@PersistenceCapable(
		objectIdClass=ModuleMetaDataID.class,
		identityType=IdentityType.APPLICATION,
		detachable="true",
		table="NightLabsJDO_ModuleMetaData"
)
@Inheritance(strategy=InheritanceStrategy.NEW_TABLE)
public class ModuleMetaData
{
//	private static final Logger logger = Logger.getLogger(ModuleMetaData.class);

	/**
	 * Get the ModuleMetaData for a certain module.
	 *
	 * @param pm The PersistenceManager to use.
	 * @param moduleID The ID of the module.
	 * @return An instance of ModuleMetaData or <code>null</code>, if no object exists in the datastore.
	 */
	public static ModuleMetaData getModuleMetaData(PersistenceManager pm, String moduleID)
	{
		pm.getExtent(ModuleMetaData.class, true);

		ModuleMetaData mmd;
		try {
			mmd = (ModuleMetaData)pm.getObjectById(ModuleMetaDataID.create(moduleID), true);
			mmd.getSchemaVersion(); // there seems to be a JPOX heisenbug that causes sometimes an unusable object to be returned. The JDOObjectNotFoundException should be forced this way.
		} catch (JDOObjectNotFoundException x) {
			mmd = null;
		}
		return mmd;
	}

	public static ModuleMetaData createModuleMetaDataFromManifest(String moduleID, Class<?> clazz)
	throws IOException, MalformedVersionException
	{
		if (moduleID == null)
			throw new IllegalArgumentException("moduleID must not be null!");

		if (clazz == null)
			throw new IllegalArgumentException("clazz must not be null!");

		Manifest manifest = new Manifest();

		String referenceClassRelativePath = "/" + clazz.getName().replace('.', '/') + ".class";
		URL referenceClassURL = clazz.getResource(referenceClassRelativePath);
		String referenceClassURLBase = referenceClassURL.toExternalForm();
		if (!referenceClassURLBase.endsWith(referenceClassRelativePath))
			throw new IllegalStateException("referenceClassURL does not end on \"" + referenceClassRelativePath + "\": " + referenceClassURLBase);

		referenceClassURLBase = referenceClassURLBase.substring(0, referenceClassURLBase.length() - referenceClassRelativePath.length());

		String manifestResourceName = "/META-INF/MANIFEST.MF";
		URL manifestResourceUrl = new URL(referenceClassURLBase + manifestResourceName);

		InputStream in = manifestResourceUrl.openStream();
		try {
			manifest.read(in);
		} catch (IOException x) {
			throw new IOException("Cannot read file: " + manifestResourceUrl, x);
		} finally {
			in.close();
		}

		String earModuleIDKey = "EAR-Module-ID";
		String earModuleIDValue = manifest.getMainAttributes().getValue(earModuleIDKey);
		String earModuleVersionKey = "EAR-Module-Version";
		String earModuleVersionValue = manifest.getMainAttributes().getValue(earModuleVersionKey);
		String earSchemaVersionKey = "EAR-Schema-Version";
		String earSchemaVersionValue = manifest.getMainAttributes().getValue(earSchemaVersionKey);

		String bundleIDKey = "Bundle-SymbolicName";
		String bundleIDValue = manifest.getMainAttributes().getValue(bundleIDKey);
		String bundleVersionKey = "Bundle-Version";
		String bundleVersionValue = manifest.getMainAttributes().getValue(bundleVersionKey);

		// Even though we persist only the schema version into the datastore, we read and check all relevant entries
		// to ensure our MANIFEST.MF is complete.

		if (earModuleIDValue != null)
			earModuleIDValue = earModuleIDValue.trim();

		if (earModuleVersionValue != null)
			earModuleVersionValue = earModuleVersionValue.trim();

		if (earSchemaVersionValue != null)
			earSchemaVersionValue = earSchemaVersionValue.trim();

		if (bundleIDValue != null)
			bundleIDValue = bundleIDValue.trim();

		if (bundleVersionValue != null)
			bundleVersionValue = bundleVersionValue.trim();

		if (earModuleIDValue == null || earModuleIDValue.isEmpty())
			throw new IllegalStateException("Key \"" + earModuleIDKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (!moduleID.equals(earModuleIDValue))
			throw new IllegalStateException("Key \"" + earModuleIDKey + "\" is expected to have value \"" + moduleID + "\" but has value \"" + earModuleIDValue + "\" in file: " + manifestResourceUrl);

		if (earModuleVersionValue == null || earModuleVersionValue.isEmpty())
			throw new IllegalStateException("Key \"" + earModuleVersionKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (earSchemaVersionValue == null || earSchemaVersionValue.isEmpty())
			throw new IllegalStateException("Key \"" + earSchemaVersionKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (bundleIDValue == null || bundleIDValue.isEmpty())
			throw new IllegalStateException("Key \"" + bundleIDKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (bundleVersionValue == null || bundleVersionValue.isEmpty())
			throw new IllegalStateException("Key \"" + bundleVersionKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		return new ModuleMetaData(moduleID, earSchemaVersionValue);
	}

	public ModuleMetaData() { }

	/**
	 * @deprecated this constructor is deprecated, because we do not persist the module version anymore. Use {@link #ModuleMetaData(String, String)} instead!
	 */
	@Deprecated
	public ModuleMetaData(String moduleID, String deprecatedModuleVersion, String schemaVersion)
	throws MalformedVersionException
	{
		this(moduleID, schemaVersion);
	}

	/**
	 * @deprecated this constructor is deprecated, because we do not persist the module version anymore. Use {@link #ModuleMetaData(String, Version)} instead!
	 */
	@Deprecated
	public ModuleMetaData(String moduleID, Version deprecatedModuleVersion, Version schemaVersion)
	{
		this(moduleID, schemaVersion);
	}

	public ModuleMetaData(String _moduleID, String _schemaVersion)
		throws MalformedVersionException
	{
		this.moduleID = _moduleID;
//		this.setModuleVersion(_moduleVersion);
		this.setSchemaVersion(_schemaVersion);
	}

	public ModuleMetaData(String _moduleID, Version _schemaVersion)
	{
		this.moduleID = _moduleID;
//		this.setModuleVersion(_moduleVersion);
		this.setSchemaVersion(_schemaVersion);
	}

	@PrimaryKey
	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	@Column(
			jdbcType="VARCHAR",
			length=100
	)
	protected String moduleID;

//	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
//	@Column(
//			jdbcType="VARCHAR",
//			length=100
//	)
//	protected String moduleVersion;

	@Persistent(persistenceModifier=PersistenceModifier.PERSISTENT)
	@Column(
			jdbcType="VARCHAR",
			length=100
	)
	protected String schemaVersion;

//	public Version getModuleVersionObj()
//	{
//		try {
//			return new Version(moduleVersion);
//		} catch (MalformedVersionException e) {
//			throw new RuntimeException("How the hell could this object have been corrupted?", e);
//		}
//	}

	public Version getSchemaVersionObj()
	{
		try {
			return new Version(schemaVersion);
		} catch (MalformedVersionException e) {
			throw new RuntimeException("How the hell could this object have been corrupted?", e);
		}
	}

//	/**
//	 * @return Returns the moduleVersion.
//	 */
//	public String getModuleVersion()
//	{
//		return moduleVersion;
//	}

//	/**
//	 * @param moduleVersion The moduleVersion string to set.
//	 */
//	public void setModuleVersion(String moduleVersion) throws MalformedVersionException
//	{
//		this.moduleVersion = new Version(moduleVersion).toString();
//	}
//
//	public void setModuleVersion(Version moduleVersion)
//	{
//		this.moduleVersion = moduleVersion.toString();
//	}

	/**
	 * @return Returns the schemaVersion.
	 */
	public String getSchemaVersion()
	{
		return schemaVersion;
	}

	/**
	 * @param schemaVersion The schemaVersion string to set.
	 */
	public void setSchemaVersion(String schemaVersion) throws MalformedVersionException
	{
		this.schemaVersion = new Version(schemaVersion).toString();
	}

	/**
	 * @param schemaVersion The schemaVersion to set.
	 */
	public void setSchemaVersion(Version schemaVersion)
	{
		this.schemaVersion = schemaVersion.toString();
	}

	/**
	 * @return Returns the moduleID.
	 */
	public String getModuleID() {
		return moduleID;
	}
}
