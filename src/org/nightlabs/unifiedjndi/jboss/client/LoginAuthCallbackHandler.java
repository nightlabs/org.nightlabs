package org.nightlabs.unifiedjndi.jboss.client;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * Simple callback handler that returns the set loginID as well as the password if a {@link NameCallback} and a
 * {@link PasswordCallback} are set, respectively.
 *
 * @author Marius Heinzmann - marius[at]nightlabs[dot]de
 */
public class LoginAuthCallbackHandler
	implements CallbackHandler
{
	private String loginID;
	private String password;

	/**
	 * @param loginID
	 * @param password
	 */
	public LoginAuthCallbackHandler(String loginID, String password)
	{
		if (loginID == null || loginID.trim().length() == 0)
			throw new IllegalStateException("The loginID may not be null or empty!");

		if (password == null || password.trim().length() == 0)
			throw new IllegalStateException("The password may not be null or empty!");

		this.loginID = loginID;
		this.password = password;
	}

	/* (non-Javadoc)
	 * @see javax.security.auth.callback.CallbackHandler#handle(javax.security.auth.callback.Callback[])
	 */
	@Override
	public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException
	{
		for (int i = 0; i < callbacks.length; ++i) {
			Callback cb = callbacks[i];
			if (cb instanceof NameCallback) {
				((NameCallback)cb).setName(loginID);
			}
			else if (cb instanceof PasswordCallback) {
				((PasswordCallback)cb).setPassword(password.toCharArray());
			}
			else throw new UnsupportedCallbackException(cb);
		}
	}

}
