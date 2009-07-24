/* *****************************************************************************
 * NightLabsConnection - Provides a unified & extensible connection API        *
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
 ******************************************************************************/

package org.nightlabs.connection.tcp;

import java.io.IOException;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.nightlabs.connection.Connection;
import org.nightlabs.connection.StreamPair;
import org.nightlabs.connection.config.ConnectionCf;
import org.nightlabs.connection.tcp.config.TCPConnectionCf;

public class TCPConnection
		extends Connection
{
	private static final Logger logger = Logger.getLogger(TCPConnection.class);

	public TCPConnection()
	{
	}

	@Override
	public Map<Locale, String> getNames()
	{
		Map<Locale, String> res = new HashMap<Locale, String>();
		res.put(Locale.ENGLISH, "TCP/IP Connection");
		return res;
	}

	@Override
	public Map<Locale, String> getDescriptions()
	{
		Map<Locale, String> res = new HashMap<Locale, String>();
		res.put(Locale.ENGLISH, "TCP/IP Connection");
		return res;
	}

	@Override
	public Class<? extends ConnectionCf> getConnectionCfClass()
	{
		return TCPConnectionCf.class;
	}

	private Socket socket;

	@Override
	protected StreamPair _open()
	throws IOException
	{
		TCPConnectionCf connectionCf = (TCPConnectionCf) getConnectionCf();

		SocketFactory socketFactory;
		if (connectionCf.isUseSSL() && !connectionCf.isVerifyCert()) {
			TrustManager[] trustAllCerts = new TrustManager[] {
					new X509TrustManager()
					{
						public X509Certificate[] getAcceptedIssuers()
						{
							return null;
						}
						public void checkClientTrusted(X509Certificate[] certs, String authType) {}
						public void checkServerTrusted(X509Certificate[] certs, String authType) {}
					}
			};

			try {
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				socketFactory = sc.getSocketFactory();
			} catch(Exception e) {
				logger.error("Error setting new trustmanager", e);
				throw new RuntimeException("Error setting new trustmanager", e);
			}
		}
		else if (connectionCf.isUseSSL())
			socketFactory = SSLSocketFactory.getDefault();
		else
			socketFactory = SocketFactory.getDefault();

		socket = socketFactory.createSocket(connectionCf.getHost(), connectionCf.getPort());
		socket.setSoTimeout(connectionCf.getSoTimeout());

		return new StreamPair(
				socket.getInputStream(),
				socket.getOutputStream());
	}

	@Override
	protected void _close()
			throws IOException
	{
		if (socket != null) {
			socket.close();
			socket = null;
		}
	}

}
