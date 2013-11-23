package org.nightlabs.classloader.url;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.nightlabs.util.IOUtil;
import org.nightlabs.util.Util;

/**
 * A subclass of {@link URLClassLoader} that can be configured with urls (paths)
 * that it will be recursively scanned (directories and nested jars) and made
 * available for classloading.
 * <p>
 * Instantiate the {@link NestedURLClassLoaderTest} either with or without
 * initial URLs. Then and add URL entries using the
 * {@link #addURL(URL, boolean)} method.
 * </p>
 * <p>
 * Note that this classloader makes all package-names available it knows
 * (whether a class from this packages was already loaded or not) via the
 * {@link #getPackageNames()}-method.
 * </p>
 * 
 * @author Chairat Kongarayawetchakun - chairat [AT] nightlabs [DOT] de
 * @author Marco หงุ่ยตระกูล-Schulze - marco at nightlabs dot de
 * @author Alexander Bieber <!-- bieber [AT] NightLabs (dot) de -->
 */
public class NestedURLClassLoader extends URLClassLoader
{
	/** File suffixes for all files this ClassLoader recognizes as nested archives */
	private static final String[] NESTED_ARCHIVE_SUFFIXES = {
		".ear",
		".jar",
		".rar",
		".war",
		".zip"
	};

	/** All package names this Cl found while scanning */
	private SortedSet<String> packageNames = new TreeSet<String>();

	/**
	 * Constructor that will initially configure this ClassLoader with the given
	 * url and will use the given parentClassLoader for delegation.
	 * 
	 * @param urls The initial urls.
	 * @param parentClassLoader The parent class-loader.
	 */
	public NestedURLClassLoader(URL[] urls, ClassLoader parentClassLoader) {
		super(urls, parentClassLoader);
	}
	
	/**
	 * Constructor for a {@link NestedURLClassLoader} that will have no initial urls.
	 * 
	 * @param parentClassLoader The parent class-loader.
	 */
	public NestedURLClassLoader(ClassLoader parentClassLoader) {
		this(new URL[0], parentClassLoader);
	}

	/**
	 * Get all java package names this ClassLoader found while scanning its urls.
	 * The package-names are available even before loading any class from that packages. 
	 * 
	 * @return All packages this ClassLoader found while scanning its urls.
	 */
	public Set<String> getPackageNames() {
		return packageNames;
	}
	
	/**
	 * Add an URL to the urls this ClassLoader uses. 
	 * <p>
	 * If <code>recursive</code> is <code>true</code> this method will recursively check for nested archives
	 * (ear, jar, rar, war, zip) inside the given URL. The url can itself be a file (archive) or local directory.
	 * </p> 
	 * @param url The url to add.
	 * @param recursive Whether to recursively scan the url for nested archives.
	 * @throws IOException If scanning fails.
	 */
	public void addURL(URL url, boolean recursive) throws IOException {
		if (url == null) {
			throw new IOException("URL-parameter can not be null");
		}
		Set<URL> urls = new HashSet<URL>();
		populateClassLoaderURLs(null, url, urls, recursive);
		for (URL newURL : urls) {
			addURL(newURL);
		}
	}
	
	/**
	 * Private recursive helper method that scans archives for nested ones.
	 * 
	 * @param tmpBaseDir Base dir for extracting found nested archives to.
	 * @param dirOrFile The current url/archive to scan.
	 * @param urls Collection where to put found urls to.
	 * @param recursive Whether or not to recurse the scan.
	 * @throws IOException If scanning fails.
	 */
	private void populateClassLoaderURLs(File tmpBaseDir, URL dirOrFile, Collection<URL> urls, boolean recursive) throws IOException
	{
		try {
			if (tmpBaseDir == null)
				tmpBaseDir = new File(IOUtil.createUserTempDir("jfire_server.", null), "nested-jars");

			if (!tmpBaseDir.isDirectory())
				tmpBaseDir.mkdirs();

			if (!tmpBaseDir.isDirectory())
				throw new IOException("Could not create directory: " + tmpBaseDir.getAbsolutePath());
		} catch (IOException e) {
			throw e;
		}

		// Name (last path segment) of the url
		String dirOrFileName = getURLName(dirOrFile);
		
		if (recursive) {
			if ("file".equals(dirOrFile.getProtocol())) {
				File file = new File(Util.urlToUri(dirOrFile));
				File[] children = file.listFiles();
				if (children != null) {
					for (File child : children) {
						URL childURL = child.toURI().toURL();
						populateClassLoaderURLs(new File(tmpBaseDir, dirOrFileName), childURL, urls, true);
					}
				}
			}
		}

		if (isZipAccordingToSuffix(dirOrFileName)) {
			InputStream urlStream = dirOrFile.openStream();
			ZipInputStream zi = null;
			if (urlStream != null) {
				zi = new ZipInputStream(urlStream);
			}
			try {

				if (zi != null) {
					urls.add(dirOrFile);

					boolean containsNestedZip = false;
					ZipEntry ze;
					while ((ze = zi.getNextEntry()) != null) {
						String zeName = ze.getName();

						if (isZipAccordingToSuffix(zeName)) {
							containsNestedZip = true;
						} else {
							int lastSlashIdx = zeName.lastIndexOf('/');
							if (lastSlashIdx < 0)
								packageNames.add(""); // empty default package
							else {
								String packageName = zeName.substring(0,
										lastSlashIdx);
								packageName = packageName.replace('/', '.');
								packageNames.add(packageName);
							}
						}
					}

					if (containsNestedZip) {
						File tmpEarDir = new File(tmpBaseDir, "n_" + dirOrFileName);
						try {
							IOUtil.unzipArchiveIfModified(dirOrFile, tmpEarDir);
						} catch (IOException e) {
							throw e;
						}

						// The contents of a zip (e.g. an EAR) must always be
						// processed recursively - no matter if the argument
						// 'recursive' is true or false.
						populateClassLoaderURLs(
								tmpEarDir,
								tmpEarDir.toURI().toURL(), urls, true);
					}
				}
			} finally {
				if (zi != null)
					zi.close();
			}
		}
	}
	
	/**
	 * @param url URl
	 * @return Returns the last path segment of the given URL (all after the last slash)
	 */
	private static String getURLName(URL url) {
		return url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
	}
	
	/**
	 * Check if the given fileName can be considered a nested archive.
	 * 
	 * @param fileName
	 * @return
	 */
	private static boolean isZipAccordingToSuffix(String fileName)
	{
		for (String suffix : NESTED_ARCHIVE_SUFFIXES) {
			if (fileName.endsWith(suffix))
				return true;
		}
		return false;
	}
}
