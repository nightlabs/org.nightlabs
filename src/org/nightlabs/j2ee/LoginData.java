/* ********************************************************************
 * NightLabsBase - Utilities by NightLabs                             *
 * Copyright (C) 2004-2008 NightLabs GmbH - http://NightLabs.org      *
 *                                                                    *
 * This library is free software; you can redistribute it and/or      *
 * modify it under the terms of the GNU Lesser General Public         *
 * License as published by the Free Software Foundation; either       *
 * version 2.1 of the License, or (at your option) any later version. *
 *                                                                    *
 * This library is distributed in the hope that it will be useful,    *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of     *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  *
 * Lesser General Public License for more details.                    *
 *                                                                    *
 * You should have received a copy of the GNU Lesser General Public   *
 * License along with this library; if not, write to the              *
 *     Free Software Foundation, Inc.,                                *
 *     51 Franklin St, Fifth Floor,                                   *
 *     Boston, MA  02110-1301  USA                                    *
 *                                                                    *
 * Or get it online:                                                  *
 *     http://www.gnu.org/copyleft/lesser.html                        *
 **********************************************************************/
package org.nightlabs.j2ee;

import java.io.Serializable;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.naming.Context;

import org.nightlabs.util.ParameterMap;
import org.nightlabs.util.Util;

/**
 * The LoginData class is a wrapper for all necessary login information.
 *
 * @author Marius Heinzmann -- Marius[at]NightLabs[dot]de
 */
public class LoginData
	implements Serializable
{
	private static final long serialVersionUID = 2L;

	/**
	 * The default values for a local JBoss setup.
	 * TODO: this should be read from a property file! unascribed (probably Marius).
	 * I initialize that stuff now by {@link #setDefaultInitialContextFactory(String)} and {@link #setDefaultProviderURL(String)}
	 * in the server correctly. But still the values here should maybe be discarded and a better solution found. Marco.
	 */
//	private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "org.jboss.security.jndi.LoginInitialContextFactory";
	private static final String DEFAULT_INITIAL_CONTEXT_FACTORY = "org.nightlabs.jfire.jboss.cascadedauthentication.LoginInitialContextFactory";
	private static final String DEFAULT_PROVIDER_URL = "jnp://localhost:1099";
	public static final String DEFAULT_SECURITY_PROTOCOL = "jfire";

	private static String defaultInitialContextFactory = null;

	public static String getDefaultInitialContextFactory() {
		return defaultInitialContextFactory;
	}
	public static void setDefaultInitialContextFactory(String defaultInitialContextFactory) {
		LoginData.defaultInitialContextFactory = defaultInitialContextFactory;
	}

	private static String defaultProviderURL = null;

	public static String getDefaultProviderURL() {
		return defaultProviderURL;
	}
	public static void setDefaultProviderURL(String defaultProviderURL) {
		LoginData.defaultProviderURL = defaultProviderURL;
	}


	/**
	 * This separator is used when constructing the principal name which is the concatenation of the
	 * userID + 'this separator' + organisationID.
	 */
	public static final String USER_ORGANISATION_SEPARATOR = "@";

	/**
	 * This separator is used for the construction of the {@link #getLoginDataURL()} to separate the
	 * principal name from the additional information from {@link #additionalParams}.
	 */
	public static final String PRINCIPAL_ADDINFO_SEPARATOR = "?";

	private String organisationID;
	private String userID;
	private String password;
//	// principalName is the concatenated product of userID and organisationID (userID@organisationID)
//	private transient String principalName = null;
	private String providerURL;
	private String initialContextFactory;
	private String securityProtocol;

	public static final String SESSION_ID = "sessionID";
	public static final String WORKSTATION_ID = "workstationID";

	/**
	 * This maps stores additional login (key,value)-pairs like the workstationID if necessary.
	 */
	private ParameterMap additionalParams = new ParameterMap();

	/**
	 * Create a blank {@link LoginData}.
	 */
	public LoginData() {}

	public static final Pattern PATTERN_SPLIT_LOGIN = Pattern.compile("[@\\?]");

	/**
	 * @param login the composite of userID, organisationID and additional parameters.
	 * @param password the password.
	 */
	public LoginData(String login, String password) {
		assert login != null : "login == null";
//		assert password != null : "password == null"; // LoginData is used internally within the server and there's not always a password. Marco.

		String[] txt = PATTERN_SPLIT_LOGIN.split(login);
		if(txt.length != 2 && txt.length != 3)
			throw new IllegalArgumentException("Invalid login - not two or three parts (use user@organisation?sessionID=xxx&moreParams=yyy&..., session is optional): " + login);
		if(txt[0].length() == 0 || txt[1].length() == 0)
			throw new IllegalArgumentException("Invalid login - empty userID or empty organisationID (use user@organisation?sessionID=xxx&moreParams=yyy&..., session is optional): " + login);

		this.organisationID = txt[1];
		this.userID = txt[0];
		this.password = password;

//		if (txt.length < 3 || "".equals(txt[2]))
//			this.setSessionID(this.getUserID() + '!' + this.getOrganisationID());
//		else {
//			this.setAdditionalParams(new ParameterMap(txt[2]));
//		}
		if (txt.length >= 3)
			additionalParams.load(txt[2]);

		if (getSessionID() == null)
			setSessionID(Long.toString(System.currentTimeMillis(), 36) + '-' + Integer.toString(System.identityHashCode(this), 36));
//			setSessionID(this.getUserID() + '!' + this.getOrganisationID());
	}

	/**
	 * Creates a new login data bundle with the minimal login information needed.
	 * Further authentication information like the provider url, the security protocol, etc. can later
	 * on be specified.
	 *
	 * @param organisationID the organisation against which the authentication shall be performed.
	 * @param userID the user ID to use for authentication.
	 * @param password the password to use for authentication.
	 */
	public LoginData(String organisationID, String userID, String password) {
		assert organisationID != null : "organisationID == null";
		assert userID != null : "userID == null";
		assert password != null : "password == null";

		this.organisationID = organisationID;
		this.userID = userID;
		this.password = password;
	}

	/**
	 * Create a {@link LoginData} that is a clone of the given _loginData.
	 * @param _loginData The {@link LoginData} to clone.
	 */
	public LoginData(LoginData _loginData) {
//		this(_loginData.getOrganisationID(), _loginData.getUserID(), _loginData.getPassword()); // the given login-data might be incomplete => don't call the other constructor
		this.organisationID = _loginData.getOrganisationID();
		this.userID = _loginData.getUserID();
		this.password = _loginData.getPassword();
		this.providerURL = _loginData.getProviderURL();
		this.initialContextFactory = _loginData.getInitialContextFactory();
		this.securityProtocol = _loginData.getSecurityProtocol();
		this.additionalParams.putAll(_loginData.getAdditionalParams());
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return additionalParams.get(SESSION_ID);
	}

	/**
	 * @param sessionID the sessionID to use. If this is set to <code>null</code> a default one is
	 * 	generated.
	 */
	public void setSessionID(String sessionID) {
		// generate a default session id. This is used when a sytem user like '_SYSTEM_' authenticates
		//   itself against the system
		if (sessionID == null)
			sessionID = userID + '!' + organisationID;

		additionalParams.put(SESSION_ID, sessionID);
	}

	/**
	 * Get the workstation-identifier or <code>null</code> if it was not specified during login.
	 *
	 * @return the workstationID or <code>null</code>.
	 */
	public String getWorkstationID() {
		return additionalParams.get(WORKSTATION_ID);
	}

	/**
	 * @param workstationID the workstationID to use.
	 */
	public void setWorkstationID(String workstationID) {
		additionalParams.put(WORKSTATION_ID, workstationID);
	}

	/**
	 * @return the providerURL
	 */
	public String getProviderURL() {
		return providerURL != null ? providerURL : "";
	}

	/**
	 * @param providerURL the providerURL to set
	 */
	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}

	/**
	 * @return the initialContextFactory
	 */
	public String getInitialContextFactory() {
		return initialContextFactory != null ? initialContextFactory : "";
	}

	/**
	 * @param initialContextFactory the initialContextFactory to set
	 */
	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	/**
	 * @return the securityProtocol
	 */
	public String getSecurityProtocol() {
		return securityProtocol != null ? securityProtocol : "";
	}

	/**
	 * @param securityProtocol the securityProtocol to set
	 */
	public void setSecurityProtocol(String securityProtocol) {
		this.securityProtocol = securityProtocol;
	}

	/**
	 * @return the additionalParams
	 */
	public ParameterMap getAdditionalParams() {
		return additionalParams;
	}

	/**
	 * @param additionalParams the additionalParams to set
	 */
	public void setAdditionalParams(ParameterMap additionalParams) {
		if (additionalParams == null) {
			if (this.additionalParams != null)
				this.additionalParams.clear();
		}
		else
			this.additionalParams = additionalParams;
	}

	/**
	 * @return the organisationID
	 */
	public String getOrganisationID() {
		return organisationID;
	}

	/**
	 * @param organisationID The organisationID to set.
	 */
	public void setOrganisationID(String organisationID) {
		this.organisationID = organisationID;
//		principalName = null;
	}

	/**
	 * @return the userID
	 */
	public String getUserID() {
		return userID;
	}

	/**
	 * @param userID The userID to set.
	 */
	public void setUserID(String userID) {
		this.userID = userID;
//		principalName = null;
	}

//	/**
//	 * @return the concatenated product of userID and organisationID
//	 * 	(userID{@value #USER_ORGANISATION_SEPARATOR}organisationID)
//	 * @deprecated Do we really need this field?
//	 */
//	@Deprecated
//	public String getPrincipalName() {
//		if (principalName == null) {
//			StringBuffer sbUser = new StringBuffer();
//			sbUser.append(userID);
//			sbUser.append(USER_ORGANISATION_SEPARATOR);
//			sbUser.append(organisationID);
//			principalName = sbUser.toString();
//		}
//		return principalName;
//	}

	/**
	 * sets the default values of {@link #initialContextFactory}, {@link #providerURL} and
	 * {@link #securityProtocol}.
	 */
	public void setDefaultValues() {
		initialContextFactory = getDefaultInitialContextFactory();
		if (initialContextFactory == null)
			initialContextFactory = DEFAULT_INITIAL_CONTEXT_FACTORY;

		providerURL = getDefaultProviderURL();
		if (providerURL == null)
			providerURL = DEFAULT_PROVIDER_URL;

		securityProtocol = DEFAULT_SECURITY_PROTOCOL;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((organisationID == null) ? 0 : organisationID.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((userID == null) ? 0 : userID.hashCode());
		result = prime * result + ((getSessionID() == null) ? 0 : getSessionID().hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass())
			return false;

		final LoginData other = (LoginData) obj;

		if (Util.equals(organisationID, other.organisationID) &&
				Util.equals(userID, other.userID) &&
				Util.equals(password, other.password) &&
				(getSessionID() == null && other.getSessionID() == null ||
					getSessionID() == null && getSessionID().equals(other.getSessionID()))
				)
			return true;

		return false;
	}

	/**
	 *
	 * @return userID@organisationID[?additionalKey=additionalValue,...]
	 */
	public String getLoginDataURL() {
		StringBuffer sb = new StringBuffer();
		sb.append(userID).append(USER_ORGANISATION_SEPARATOR).append(organisationID);
		if (additionalParams != null)
			sb.append(PRINCIPAL_ADDINFO_SEPARATOR).append(additionalParams.dump());

		return sb.toString();
	}

	/**
	 *
	 * @return the Property information needed to connect to a J2EE server:	see
	 * {@link Context#INITIAL_CONTEXT_FACTORY}, {@link Context#PROVIDER_URL}, {@link Context#SECURITY_PRINCIPAL},
	 * {@link Context#SECURITY_CREDENTIALS}, {@link Context#SECURITY_PROTOCOL}
	 */
	public Properties getInitialContextProperties() {
		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, getInitialContextFactory());
		props.put(Context.PROVIDER_URL, getProviderURL());
		props.put(Context.SECURITY_PRINCIPAL, getLoginDataURL());
		props.put(Context.SECURITY_CREDENTIALS, getPassword() != null ? getPassword() : "");
		props.put(Context.SECURITY_PROTOCOL, getSecurityProtocol());
		return props;
	}

	/**
	 * @return userID:password{@value #USER_ORGANISATION_SEPARATOR}organisationID
	 * 	[{@link #PRINCIPAL_ADDINFO_SEPARATOR}additionalKey=additionalValue,...]
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(userID).append(":").append(password).append(USER_ORGANISATION_SEPARATOR).append(organisationID);
		if (additionalParams != null)
			sb.append(PRINCIPAL_ADDINFO_SEPARATOR).append(additionalParams.dump());

		return sb.toString();
	}

}
