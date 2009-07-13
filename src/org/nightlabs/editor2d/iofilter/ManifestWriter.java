/* *****************************************************************************
 * NightLabs Editor2D - Graphical editor framework                             *
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

package org.nightlabs.editor2d.iofilter;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.util.IOUtil;

public class ManifestWriter
{
	public static final String DEFAULT_CONTENT_TYPE = "vnd.nightlabs.editor2d.root";
	public static final String DEFAULT_CONTENT_NAME = "content.xml";
	public static final String DEFAULT_VERSION = "0.9.6";
	public static final String DEFAULT_XML_VERSION = "1.0";
	public static final String DEFAULT_ENCODING = IOUtil.CHARSET_NAME_UTF_8;
	public static final String DEFAULT_IMAGE_DIR = "images";
	public static final String DEFAULT_CONTENT_MEDIA_TYPE = "text/xml";
	public static final String DEFAULT_XMLNS = "http://jfire.nightlabs.org/editor2d/manifest";
	public static final String DEFAULT_IMAGE_TYPE = "image/";

//	public static final String DEFAULT_MANIFEST_DTD = "Manifest.dtd";
//	public static final String DEFAULT_MANIFEST_IDENTIFIER = "-//NightLabs GmbH//DTD Editor2D 1.0//EN";
	public static final String APPLICATION = "application/";
	public static final String VERSION = "manifest:version";

	protected static final String ROW_BREAK = "\n";

	protected StringBuffer stringBuffer;
	public ManifestWriter()
	{
		this(DEFAULT_CONTENT_TYPE, DEFAULT_CONTENT_NAME, DEFAULT_XML_VERSION, DEFAULT_ENCODING);
	}

	public ManifestWriter(String contentType)
	{
		this(contentType, DEFAULT_CONTENT_NAME, DEFAULT_XML_VERSION, DEFAULT_ENCODING);
	}

	public ManifestWriter(String contentType, String contentName, String xmlVersion, String encoding)
	{
		super();
		this.contentType = contentType;
		this.contentName = contentName;
		this.xmlVersion = xmlVersion;
		this.encoding = encoding;
		stringBuffer = new StringBuffer();
	}

	/**
	 * the name of the contentType
	 */
	protected String contentType = DEFAULT_CONTENT_TYPE;
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	/**
	 * the name of the file which contains the content
	 */
	protected String contentName = DEFAULT_CONTENT_NAME;
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	/**
	 * the version of the XML
	 */
	protected String xmlVersion = DEFAULT_XML_VERSION;
	public String getXmlVersion() {
		return xmlVersion;
	}
	public void setXmlVersion(String xmlVersion) {
		this.xmlVersion = xmlVersion;
	}

	/**
	 * The encoding for the Charset
	 */
	protected String encoding = DEFAULT_ENCODING;
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * the name of the ImageDirectory
	 */
	protected String imageDirectory = DEFAULT_IMAGE_DIR;
	public String getImageDirectory() {
		return imageDirectory;
	}
	public void setImageDirectory(String imageDirectory) {
		this.imageDirectory = imageDirectory;
	}

	/**
	 * the name of MediaType for the content
	 */
	protected String contentMediaType = DEFAULT_CONTENT_MEDIA_TYPE;
	public String getContentMediaType() {
		return contentMediaType;
	}
	public void setContentMediaType(String contentMediaType) {
		this.contentMediaType = contentMediaType;
	}

	/**
	 * the XML NameSpace
	 */
	protected String xmlns = DEFAULT_XMLNS;
	public String getXmlNS() {
		return xmlns;
	}
	public void setXmlNS(String xmlns) {
		this.xmlns = xmlns;
	}

//	/**
//	 * the name of the DTD-File for the Manifest
//	 */
//	protected String manifestDTD = DEFAULT_MANIFEST_DTD;
//	public String getManifestDTD() {
//		return manifestDTD;
//	}
//	public void setManifestDTD(String manifestDTD) {
//		this.manifestDTD = manifestDTD;
//	}

//	/**
//	 * the name for the ManifestIdentifier
//	 */
//	protected String manifestIdentifier = DEFAULT_MANIFEST_IDENTIFIER;
//	public String getManifestIdentifier() {
//		return manifestIdentifier;
//	}
//	public void setManifestIdentifier(String manifestIdentifier) {
//		this.manifestIdentifier = manifestIdentifier;
//	}

	protected String version = DEFAULT_VERSION;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}

	protected void writeHeader(StringBuffer sb)
	{
		sb.append("<?xml version=\"");
		sb.append(xmlVersion);
		sb.append("\" encoding=\"");
		sb.append(encoding);
		sb.append("\"?>");
		sb.append(ROW_BREAK);
	}

	protected void writeNS(StringBuffer sb)
	{
		sb.append("<manifest:manifest ");
		sb.append("xmlns:manifest=\"");
		sb.append(xmlns);
		sb.append("\">");
		sb.append(ROW_BREAK);
	}

//	protected void writeDTD(StringBuffer sb)
//	{
//		sb.append("<!DOCTYPE manifest:manifest PUBLIC \"");
//		sb.append(manifestIdentifier);
//		sb.append("\" \"");
//		sb.append(manifestDTD);
//		sb.append("\">");
//		sb.append(ROW_BREAK);
//	}

	protected void writeVersion(StringBuffer sb)
	{
		sb.append("<manifest:version manifest:version=\"");
		sb.append(version);
		sb.append("\"/>");
		sb.append(ROW_BREAK);
	}

	protected void writeContentType(StringBuffer sb)
	{
		sb.append("<manifest:file-entry manifest:media-type=\"");
		sb.append(contentType);
		sb.append("\" manifest:full-path=\"/\"/>");
		sb.append(ROW_BREAK);
	}

	protected void writeContent(StringBuffer sb)
	{
		sb.append("<manifest:file-entry manifest:media-type=\"");
		sb.append(contentMediaType);
		sb.append("\" manifest:full-path=\"");
		sb.append(contentName);
		sb.append("\"/>");
		sb.append(ROW_BREAK);
	}

	protected String getImageType(ImageDrawComponent img)
	{
		return img.getImageFileExtension();
	}

	public boolean containsImages(RootDrawComponent root)
	{
//		Collection imageDCs = root.getDrawComponents(ImageDrawComponentImpl.class);
//		Collection<ImageDrawComponent> imageDCs = root.getDrawComponents(ImageDrawComponent.class);
		Collection<ImageDrawComponent> imageDCs = root.findDrawComponents(ImageDrawComponent.class);
		if (imageDCs != null && !imageDCs.isEmpty()) {
			return true;
		}
		return false;
	}

	protected void closeHeader(StringBuffer sb)
	{
		sb.append("</manifest:manifest>");
	}

	public StringBuffer writeManifest(RootDrawComponent mldc)
	{
		stringBuffer = new StringBuffer();
		writeHeader(stringBuffer);
//		writeContentVersion(stringBuffer);
//		writeDTD(stringBuffer);
		writeNS(stringBuffer);
		writeVersion(stringBuffer);
		writeContentType(stringBuffer);
		writeContent(stringBuffer);
		writeImages(stringBuffer, mldc);
		closeHeader(stringBuffer);
		return stringBuffer;
	}

//	public static String getUniqueImageName(ImageDrawComponent image, boolean id)
//	{
//		String md5key = getImageKeyString(image);
//		String name = null;
//		if (id)
//			name = image.getOriginalImageFileName() + '.' + image.getId() + '.' + md5key;
//		else
//			name = image.getOriginalImageFileName() + '.' + md5key;
//
////		return name;
//		try {
//			// Need to URL encode here because e.g. ZipEntry does not support setting of charset
//			return URLEncoder.encode(name, DEFAULT_ENCODING);
//		} catch (UnsupportedEncodingException e) {
//			throw new RuntimeException(e);
//		}
//	}

	public static String getUniqueImageName(ImageDrawComponent image)
	{
		return getImageKeyString(image);
	}

	public static String getImageKeyString(ImageDrawComponent image)
	{
		StringBuffer sb = new StringBuffer();
		byte[] imageKey = image.getImageKey();
		for (int i=0; i<imageKey.length; i++) {
			sb.append(imageKey[i]);
		}
		String md5key = sb.toString();
		return md5key;
	}

	protected Map<String, Set<ImageDrawComponent>> imageKey2ImageDrawComponentsSet = new HashMap<String, Set<ImageDrawComponent>>();
	public Map<String, Set<ImageDrawComponent>> getImageKey2ImageDrawComponentsSet() {
		return imageKey2ImageDrawComponentsSet;
	}

	/**
	 * writes the images of all {@link ImageDrawComponent} contained in the {@link RootDrawComponent}
	 * to the manifest file.
	 * ImageDrawComponents with the same originalImage are skipped, to avoid redundant file
	 * serialization. This information is stored in the imageKey2ImageDrawComponentsSet-Map
	 *
	 * @param sb the StringBuffer to write to
	 * @param root the RootDrawComponent which ImageDrawComponents should be written
	 */
	protected void writeImages(StringBuffer sb, RootDrawComponent mldc)
	{
//		Collection imageDCs = mldc.getDrawComponents(ImageDrawComponentImpl.class);
//		Collection<ImageDrawComponent> imageDCs = mldc.getDrawComponents(ImageDrawComponent.class);
		Collection<ImageDrawComponent> imageDCs = mldc.findDrawComponents(ImageDrawComponent.class);
		if (imageDCs != null)
		{
			for (Iterator<ImageDrawComponent> it = imageDCs.iterator(); it.hasNext(); )
			{
				ImageDrawComponent image = it.next();
				String imageKey = getImageKeyString(image);
				if (imageKey2ImageDrawComponentsSet.containsKey(imageKey)) {
					Set<ImageDrawComponent> images = imageKey2ImageDrawComponentsSet.get(imageKey);
					images.add(image);
				}
				else {
					Set<ImageDrawComponent> images = new HashSet<ImageDrawComponent>();
					imageKey2ImageDrawComponentsSet.put(imageKey, images);
					images.add(image);
					writeImage(sb, image);
				}
			}
		}
	}

//	protected void writeImages(StringBuffer sb, RootDrawComponent root)
//	{
//		Collection imageDCs = root.getDrawComponents(ImageDrawComponentImpl.class);
//		if (imageDCs != null)
//		{
//			for (Iterator it = imageDCs.iterator(); it.hasNext(); )
//			{
//				ImageDrawComponent image = (ImageDrawComponent) it.next();
//				writeImage(sb, image);
//			}
//		}
//	}

	protected void writeImage(StringBuffer sb, ImageDrawComponent image)
	{
		sb.append("<manifest:file-entry manifest:media-type=\"");
		sb.append(DEFAULT_IMAGE_TYPE + getImageType(image));
		sb.append("\" manifest:full-path=\"");
		sb.append(imageDirectory);
		sb.append("/");
		sb.append(getUniqueImageName(image));
		sb.append("\"/>");
		sb.append(ROW_BREAK);
	}
}
