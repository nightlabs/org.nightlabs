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
package org.nightlabs.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * This class provides methods for creating/reading the tag file of cache directory according to
 * <a href="http://www.brynosaurus.com/cachedir/">Cache Directory Tagging Specification</a>.
 *
 * @author Marco Schulze - marco at nightlabs dot de
 */
public class CacheDirTag
{
	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(CacheDirTag.class);

	private File cacheDir;
	private File cacheDirTagFile;

	/**
	 * Use this constructor in order to check the tag of a directory or to create a tag for it.
	 *
	 * @param cacheDir The cache directory (which doesn't need to exist and won't be created in this constructor).
	 */
	public CacheDirTag(File cacheDir)
	{
		this.cacheDir = cacheDir;
		cacheDirTagFile = new File(cacheDir, CACHE_DIR_TAG_FILE);
	}

	/**
	 * Filename of the tagging file placed into a cache directory. Value: "CACHEDIR.TAG"
	 */
	public static final String CACHE_DIR_TAG_FILE = "CACHEDIR.TAG";

	/**
	 * The signature of the tag file. The file must be UTF-8 encoded and begin with this
	 * string: "Signature: 8a477f597d28d172789f06886806bc55"
	 */
	public static final char[] CACHE_DIR_SIGNATURE = "Signature: 8a477f597d28d172789f06886806bc55".toCharArray();

	/**
	 * This method tags a cache directory according to the <a href="http://www.brynosaurus.com/cachedir/">Cache Directory Tagging Specification</a>,
	 * which means a CACHEDIR.TAG file will be created (or updated if it's not conform to the spec).
	 *
	 * @param applicationName Your application's name. This will be written solely for user-/admin-information purposes into the file.
	 *		It may contain all characters.
	 * @param createDir Whether or not to create the directory if it doesn't exist yet. If this is
	 *		<code>false</code> and the directory does not exist, a {@link FileNotFoundException} will be thrown.
	 * @param retag If the tag file already exists and is valid, it will not be rewritten, if this is <code>false</code>.
	 *		If it is <code>true</code>, the tag file will be overwritten.
	 * @throws FileNotFoundException If the directory does not exist and either <code>createDir==false</code> or the creation
	 *		of the directory failed.
	 * @throws IOException If reading/writing the {@link #CACHE_DIR_TAG_FILE } failed.
	 */
	public void tag(String applicationName, boolean createDir, boolean retag)
	throws FileNotFoundException, IOException
	{
		if (!cacheDir.exists()) {
			if (!createDir)
				throw new FileNotFoundException("The cache directory does not exist and createDir==false: " + cacheDir.getAbsolutePath());

			if (!cacheDir.mkdirs())
				throw new FileNotFoundException("Creating cache directory failed: " + cacheDir.getAbsolutePath());
		}

		if (isTagged() && !retag) {
			if (logger.isDebugEnabled())
				logger.debug("Cache directory is correctly tagged and will not be retagged: " + cacheDir.getAbsolutePath());

			return;
		}

		OutputStream out = new BufferedOutputStream(new FileOutputStream(cacheDirTagFile));
		try {
			Writer w = new OutputStreamWriter(out, IOUtil.CHARSET_NAME_UTF_8);
			try {
				w.write(CACHE_DIR_SIGNATURE);
				w.write('\n');
				w.write("# This file is a cache directory tag created by:\n");
				w.write("# \t" + applicationName + "\n");
				w.write("# \n");
				w.write("# Creation timestamp:\n");
				w.write("# \t" + System.currentTimeMillis() + "\n");
				w.write("# \n");
				w.write("# For information about cache directory tags, see:\n");
				w.write("# \thttp://www.brynosaurus.com/cachedir/\n");
			} finally {
				w.close();
			}
		} finally {
			out.close();
		}
	}

	/**
	 * This method checks, whether the directory referenced by this instance
	 * of <code>CacheDirTag</code> is tagged.
	 *
	 * @return Returns <code>true</code> if the cache directory exists and if it
	 *		is properly tagged.
	 * @throws IOException If the cache dir tag file cannot be read.
	 */
	public boolean isTagged()
	throws IOException
	{
		if (!cacheDir.exists()) {
			if (logger.isDebugEnabled())
				logger.debug("Cache directory does not exist: " + cacheDir.getAbsolutePath());

			return false;
		}

		if (!cacheDirTagFile.exists()) {
			if (logger.isDebugEnabled())
				logger.debug("Cache dir tag file does not exist: " + cacheDirTagFile.getAbsolutePath());

			return false;
		}

		if (cacheDirTagFile.length() < CACHE_DIR_SIGNATURE.length) {
			if (logger.isDebugEnabled())
				logger.debug("Cache dir tag file is shorter than required signature length (" + CACHE_DIR_SIGNATURE.length + "): " + cacheDirTagFile.getAbsolutePath());

			return false;
		}

//	 check whether the file contains a valid signature
		FileInputStream fin = new FileInputStream(cacheDirTagFile);
		try {
			Reader r = new InputStreamReader(fin, IOUtil.CHARSET_NAME_UTF_8);
			try {
				int off = 0;
				char[] cbuf = new char[CACHE_DIR_SIGNATURE.length];
				int bytesRead = 0;
				while (bytesRead >= 0 && off < CACHE_DIR_SIGNATURE.length) {
					bytesRead = r.read(cbuf, off, CACHE_DIR_SIGNATURE.length - off);
					if (bytesRead > 0)
						off += bytesRead;
				}
				if (off < CACHE_DIR_SIGNATURE.length)
					throw new IOException("Even though the file claims to be long enough, I could only read " + off + " of " + CACHE_DIR_SIGNATURE.length + " bytes from the cache dir tag file: " + cacheDirTagFile.getAbsolutePath());

				if (Arrays.equals(CACHE_DIR_SIGNATURE, cbuf)) {
					if (logger.isDebugEnabled())
						logger.debug("The cache dir tag file exists and is valid: " + cacheDirTagFile.getAbsolutePath());

					return true;
				}

				if (logger.isDebugEnabled())
					logger.debug("The first " + CACHE_DIR_SIGNATURE.length + " bytes of the cache dir tag file are not a valid cache dir signature! File: " + cacheDirTagFile.getAbsolutePath());

				return false;
			} finally {
				r.close();
			}
		} finally {
			fin.close();
		}
	}
}
