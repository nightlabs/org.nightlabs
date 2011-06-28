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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import org.apache.log4j.Logger;
import org.nightlabs.jdo.moduleregistry.id.ModuleMetaDataID;
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
	private static final Logger logger = Logger.getLogger(ModuleMetaData.class);

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

	/**
	 * Get or newly create and initialize from the ears manifest the
	 * {@link ModuleMetaData} for the given moduleID.
	 * 
	 * @param pm
	 *            The PersistenceManager to use.
	 * @param moduleID
	 *            The moduleID of the module to get/initialize the
	 *            {@link ModuleMetaData} for.
	 * @param clazz
	 *            The class to use in order to relove the manifest if the
	 *            ModuleMetaData needs to be written from there. See
	 *            {@link #createModuleMetaDataFromManifest(String, Class)}.
	 * @return A persistent instance of {@link ModuleMetaData}
	 * @throws IOException
	 *             ...
	 * @throws MalformedVersionException
	 *             ...
	 */
	public static ModuleMetaData initModuleMetadata(PersistenceManager pm, String moduleID, Class<?> clazz) throws IOException, MalformedVersionException {
		// => We check if the ModuleMetaData was already persisted
		ModuleMetaData moduleMetaData = getModuleMetaData(pm, moduleID);
		if (moduleMetaData != null)
			return moduleMetaData;

		moduleMetaData = ModuleMetaData.createModuleMetaDataFromManifest(moduleID, clazz);
		
		if (logger.isInfoEnabled())
			logger.info("Initializing ModuleMetaData of " + moduleID + " to Schema-Version " + moduleMetaData.getSchemaVersion());
		
		return pm.makePersistent(moduleMetaData);
	}
	
	/**
	 * Get the ModuleMetaData for a certain module via direct SQL-connection.
	 * 
	 * @param connection The connection 
	 * @param moduleID The ID of the module 
	 * @return An instance (not Persistent) of ModuleMetaData, or <code>null</code>, if no object exists in the datastore.
	 * @throws SQLException
	 * @throws MalformedVersionException
	 */
	public static ModuleMetaData getModuleMetaData(Connection connection, String moduleID) throws SQLException, MalformedVersionException {
		PreparedStatement preparedStatement = connection.prepareStatement(
				"SELECT module_id, schema_version " +
				"FROM nightlabsjdo_modulemetadata " +
				"WHERE module_id = ?"
		);
		try {
			preparedStatement.setString(1, moduleID);
			ResultSet resultSet = preparedStatement.executeQuery();

			// If there is an entry
			if (resultSet.next()) {
				String schemaVersion = resultSet.getString("schema_version");
				return new ModuleMetaData(moduleID, schemaVersion);
			}
			return null;
		} finally {
			preparedStatement.close();
		}
	}		

	public static ModuleMetaData createModuleMetaDataFromManifest(String moduleID, Class<?> clazz)
	throws IOException, MalformedVersionException
	{
		if (moduleID == null)
			throw new IllegalArgumentException("moduleID must not be null!");

		if (clazz == null)
			throw new IllegalArgumentException("clazz must not be null!");

		String referenceClassRelativePath = "/" + clazz.getName().replace('.', '/') + ".class";
		URL referenceClassURL = clazz.getResource(referenceClassRelativePath);
		
		return createModuleMetaDataFromManifest(moduleID, referenceClassURL);
	}
	
	/**
	 * Reads the {@link ModuleMetaData} from the MANIFEST file of the Ear file the given Resource is in.
	 * 
	 * @param moduleID The expected moduleID to be read.
	 * @param resourceURL The URL of the reference resource (must be part of the ear).
	 * @return A new {@link ModuleMetaData} (not persistent).
	 * @throws IOException If this method fails reading the MANIFEST
	 * @throws MalformedVersionException ...
	 */
	public static ModuleMetaData createModuleMetaDataFromManifest(String moduleID, URL resourceURL)
	throws IOException, MalformedVersionException
	{
		Manifest manifest = new Manifest();
		String referenceResourceRelativePath = resourceURL.getPath();
		if ("jar".equals(resourceURL.getProtocol())) {
			referenceResourceRelativePath = referenceResourceRelativePath.substring(referenceResourceRelativePath.indexOf("!") + 1 );
		}
		String referenceResourceURLBase = resourceURL.toExternalForm();
		if (!referenceResourceURLBase.endsWith(referenceResourceRelativePath))
			throw new IllegalStateException("referenceClassURL does not end on \"" + referenceResourceRelativePath + "\": " + referenceResourceURLBase);

		referenceResourceURLBase = referenceResourceURLBase.substring(0, referenceResourceURLBase.length() - referenceResourceRelativePath.length());

		// strip the EJB-JAR-inside part of the URL ("jar:" at the beginning and "!" at the end)
		String jarPrefix = "jar:";
		if (referenceResourceURLBase.startsWith(jarPrefix)) {
			if (!referenceResourceURLBase.endsWith("!"))
				throw new IllegalStateException("referenceClassURLBase starts with \"" + jarPrefix + "\" but does not end with \"!\": " + referenceResourceURLBase);

			referenceResourceURLBase = referenceResourceURLBase.substring(jarPrefix.length(), referenceResourceURLBase.length() - 1);
		}
		else
			throw new UnsupportedOperationException("referenceClassURL does not reference a class inside a JAR!");

		// strip the name of the JAR at the end
		if (referenceResourceURLBase.endsWith("/") || referenceResourceURLBase.endsWith(File.separator))
			throw new IllegalStateException("Expected a normal character but found \"/\" at the end: " + referenceResourceURLBase);

		int lastSlashIdx = referenceResourceURLBase.replace(File.separatorChar, '/').lastIndexOf('/'); // We find both, slashes and backslashes this way.
		if (lastSlashIdx < 0)
			throw new IllegalStateException("referenceClassURLBase does not contain any EAR! Cannot find separator anymore: " + referenceResourceURLBase);

		String jarNameToBeCut = referenceResourceURLBase.substring(lastSlashIdx);
		if (jarNameToBeCut.contains("!"))
			throw new IllegalStateException("We have a JAR-separator (!) inside the part that we would cut (lastSlashIdx="+ lastSlashIdx +"): " + referenceResourceURLBase);

		referenceResourceURLBase = referenceResourceURLBase.substring(0, lastSlashIdx);

		String manifestResourceName = "/META-INF/MANIFEST.MF";
		URL manifestResourceUrl = new URL(referenceResourceURLBase + manifestResourceName);

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
//		String earModuleVersionKey = "EAR-Module-Version";
//		String earModuleVersionValue = manifest.getMainAttributes().getValue(earModuleVersionKey);
		String earSchemaVersionKey = "EAR-Schema-Version";
		String earSchemaVersionValue = manifest.getMainAttributes().getValue(earSchemaVersionKey);

		if (earModuleIDValue != null)
			earModuleIDValue = earModuleIDValue.trim();

//		if (earModuleVersionValue != null)
//			earModuleVersionValue = earModuleVersionValue.trim();

		if (earSchemaVersionValue != null)
			earSchemaVersionValue = earSchemaVersionValue.trim();

		if (earModuleIDValue == null || earModuleIDValue.isEmpty())
			throw new IllegalStateException("Key \"" + earModuleIDKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (!moduleID.equals(earModuleIDValue))
			throw new IllegalStateException("Key \"" + earModuleIDKey + "\" is expected to have value \"" + moduleID + "\" but has value \"" + earModuleIDValue + "\" in file: " + manifestResourceUrl);

//		if (earModuleVersionValue == null || earModuleVersionValue.isEmpty())
//			throw new IllegalStateException("Key \"" + earModuleVersionKey + "\" missing or having empty value in file: " + manifestResourceUrl);

		if (earSchemaVersionValue == null || earSchemaVersionValue.isEmpty())
			throw new IllegalStateException("Key \"" + earSchemaVersionKey + "\" missing or having empty value in file: " + manifestResourceUrl);

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
