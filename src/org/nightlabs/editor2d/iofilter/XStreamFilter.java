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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;
import org.nightlabs.editor2d.DrawComponent;
import org.nightlabs.editor2d.ImageDrawComponent;
import org.nightlabs.editor2d.RootDrawComponent;
import org.nightlabs.editor2d.impl.AbstractTextDrawComponent;
import org.nightlabs.editor2d.impl.TextDrawComponentImpl;
import org.nightlabs.editor2d.iofilter.xstream.AffineTransformConverter;
import org.nightlabs.editor2d.iofilter.xstream.GeneralShapeConverter;
import org.nightlabs.i18n.I18nText;
import org.nightlabs.i18n.I18nTextBuffer;
import org.nightlabs.io.AbstractIOFilterWithProgress;
import org.nightlabs.io.DataBuffer;
import org.nightlabs.io.ReadException;
import org.nightlabs.util.IOUtil;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class XStreamFilter
extends AbstractIOFilterWithProgress
{
	/**
	 * This is the default version that this filter
	 * will set when writing files.
	 * Subclasses may overwrite this by overriding
	 * {@link #createManifestWriter()} and setting
	 * their own version.
	 */
	public static final String XSTREAM_FILTER_VERSION = "0.9.6";

	/**
	 * LOG4J logger used by this class
	 */
	private static final Logger logger = Logger.getLogger(XStreamFilter.class.getName());

	public static final String FILE_EXTENSION = "e2d";

	public static final String SUBTASK_DECOMPRESS = "Decompress";
	public static final String SUBTASK_PARSING = "Parsing";
	public static final String SUBTASK_REBUILD = "Rebuilding";

	public static final String SUBTASK_COMPRESS = "Compress";
	public static final String SUBTASK_WRITING = "Writing";

	public static final String MANIFEST_NAME = "manifest.xml";
	public static final String META_INF_DIR = "meta-inf/";
	public static final String IMAGES_DIR = "images/";
	public static final String CHARSET_NAME = IOUtil.CHARSET_NAME_UTF_8;

	public static final String EDITOR2D_CONTENT_TYPE = "application/x-nightlabs-editor2d";

	public XStreamFilter() {
		super();
	}

	@Override
	protected String[] initFileExtensions() {
		return new String[] {FILE_EXTENSION};
	}

	@Override
	protected String initDescription() {
		return "Editor2D File Format";
	}

	@Override
	protected String initName() {
		return "Editor2D File Format";
	}

	@Override
	protected Map<String, I18nText> initDescriptions()
	{
		Map<String, I18nText> descriptions = new HashMap<String, I18nText>();
		I18nText description = new I18nTextBuffer();
		description.setText(Locale.ENGLISH.getLanguage(), initDescription());
		descriptions.put(getFileExtensions()[0], description);
		return descriptions;
	}

	public boolean supportsRead(String fileExtension)
	{
		if (fileExtension.equals(getFileExtensions()[0]))
			return true;
		return false;
	}

	public boolean supportsWrite(String fileExtension) {
		if (fileExtension.equals(getFileExtensions()[0]))
			return true;
		return false;
	}

	protected void initReadSubTasks()
	{
		flushSubTasks();
		addSubTask(SUBTASK_DECOMPRESS, 10);
		addSubTask(SUBTASK_PARSING, 80);
		addSubTask(SUBTASK_REBUILD, 10);
	}

	protected void initWriteSubTasks()
	{
		flushSubTasks();
		addSubTask(SUBTASK_COMPRESS, 10);
		addSubTask(SUBTASK_WRITING, 90);
	}

	protected RootDrawComponent root = null;
	protected boolean debug = true;

	protected void readManifest(InputStream in)
	{
		manifestReader = new ManifestReader(in, CHARSET_NAME);
		manifestReader.read();
		entryName2ImageInfo = manifestReader.getEntryName2ImageInfo();
	}

	protected void readContent(InputStream in)
	{
		try {
			InputStreamReader reader = new InputStreamReader(new BufferedInputStream(in), CHARSET_NAME);
			processSubTask(SUBTASK_DECOMPRESS);
			long newTime = System.currentTimeMillis() - startTime;
			if (debug)
				logger.debug("XStream PARSING BEGINS after "+newTime+" ms");
			// read and restore content
			root = (RootDrawComponent) xstream.fromXML(reader);
			processSubTask(SUBTASK_PARSING);
			newTime = System.currentTimeMillis() - startTime;
			if (debug)
				logger.debug("XStream PARSING took = "+newTime+" ms");

			rebuild(root);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	protected long startTime = 0;
	protected XStream xstream = null;
	protected ManifestReader manifestReader = null;
	protected Map<String, String[]> entryName2ImageInfo = null;
//	protected Map<String, String[]> entryName2ImageInfo = new HashMap<String, String[]>();
	protected Map<String, String[]> getEntryName2ImageInfo() {
		if (entryName2ImageInfo == null) {
			entryName2ImageInfo = new HashMap<String, String[]>();
		}
		return entryName2ImageInfo;
	}
	protected void cleanRead()
	{
		if (manifestReader != null) {
			manifestReader.close();
			manifestReader = null;
		}

		imageKey2ImageDrawComponents = null;
		startTime = 0;
		xstream = null;
		manifestReader = null;
		entryName2ImageInfo = null;
		root = null;
	}

	/**
	 * Reads the XStream-File-Format and returns the RootDrawComponent
	 * First the File is unzipped and then redundant information is rebuild
	 * and the binary images are assigned to the ImageDrawComponents
	 *
	 * @param in the InputStream to read from
	 * @return the RootDrawComponent to read
	 */
	public RootDrawComponent read(InputStream in)
	throws IOException
	{
		cleanRead();
		try {
			if (debug)
				logger.debug("XStream READ BEGIN!");

			startTime = System.currentTimeMillis();
			initReadSubTasks();
			xstream = new XStream(new XppDriver());
			registerConverter(xstream);

			ZipInputStream zipStream = new ZipInputStream(in);
			// in case we stumble over images before having read the manifest and the content.xml
			Map<String, DataBuffer> earlyImages = null;
			DataBuffer earlyContent = null;

			// we do not know the order of the ZipEntries, hence we
			// need to iterate all!
			ZipEntry zipEntry = zipStream.getNextEntry();
			while (zipEntry != null) {
				String entryName = zipEntry.getName();
				if ((META_INF_DIR).equals(entryName)) {
					// ignore the directory entry for "meta-inf/"
				}
				else if ((META_INF_DIR + MANIFEST_NAME).equals(entryName)) {
					// read the MANIFEST.MF
					readManifest(zipStream);

					if (earlyContent != null) {
						InputStream inputStream = earlyContent.createInputStream();
						try {
							readContent(inputStream);
						} finally {
							inputStream.close();
						}
						earlyContent = null;
					}
				}
				else if (ManifestWriter.DEFAULT_CONTENT_NAME.equals(entryName)) {
					if (manifestReader == null) {
						earlyContent = new DataBuffer(zipStream);
					}
					else {
						// read the content.xml
						readContent(zipStream);
					}
				}
				else if (entryName.equals(ManifestWriter.DEFAULT_IMAGE_DIR + '/')) {
					// ignore the directory entry "images/"
				}
				else if (entryName.startsWith(ManifestWriter.DEFAULT_IMAGE_DIR + '/')) {
					if (root == null || manifestReader == null) {
						if (earlyImages == null)
							earlyImages = new HashMap<String, DataBuffer>();

						earlyImages.put(entryName, new DataBuffer(zipStream));
					}
					else {
						readImage(entryName, zipStream, zipEntry.getSize());
					}
				}
				else {
					logger.warn("Unknown data in document! ZipEntry: Name=" + zipEntry.getName() + " Time=" + zipEntry.getTime() + " Size=" + zipEntry.getSize());
				}

				zipStream.closeEntry();
				zipEntry = zipStream.getNextEntry();
			} // while (zipEntry != null) {

			if (earlyImages != null) {
				for (Map.Entry<String, DataBuffer> me : earlyImages.entrySet()) {
					InputStream inputStream = me.getValue().createInputStream();
					try {
						readImage(me.getKey(), inputStream, me.getValue().size());
					} finally {
						inputStream.close();
					}
				}
				earlyImages = null;
			}

			if (!getEntryName2ImageInfo().isEmpty()) {
				StringBuffer sb = new StringBuffer();
				for (String entryName : entryName2ImageInfo.keySet()) {
					if (entryName.length() != 0)
						sb.append(File.pathSeparatorChar);
					sb.append(entryName);
				}
				throw new ReadException("There are image files missing in the zip file that are registered in the manifest: " + sb.toString());
			}

			zipStream.close();

			long newTime = System.currentTimeMillis() - startTime;
			if (debug)
				logger.debug("XStream TOTAL READ took "+newTime+" ms");

//			try {
			return root;
		} finally {
			// to release root (memory) after read
			cleanRead();
		}
	}

	protected Map<String, Set<ImageDrawComponent>> imageKey2ImageDrawComponents = null;

	/**
	 * @param entryName
	 * @param in
	 * @param entrySizeHint This should be the actual size of the data to be read (in bytes), but
	 *		can be -1, if it is unknown. In this case, the DataBuffer will be initialized with 1024 bytes.
	 *		If the size is incorrect, it doesn't matter as well, because Databuffer is dynamic.
	 * @throws IOException
	 */
	protected void readImage(String entryName, InputStream in, long entrySizeHint)
	throws IOException
	{
		if (entrySizeHint < 0) {
			logger.warn("The entrySizeHint is negative for entry: " + entryName);
			entrySizeHint = 1024; // we can safely do this - the DataBuffer automatically extends its memory if necessary
		}

		String[] imageInfo = this.entryName2ImageInfo.remove(entryName);
		if (imageInfo == null) {
			logger.warn("There is no image registration in the manifest for entry: " + entryName);
			return;
		}

		String imageKey = imageInfo[2];
		if (imageKey2ImageDrawComponents == null)
			imageKey2ImageDrawComponents = getImageKey2ImageDrawComponent(root);

		Set<ImageDrawComponent> imageDCs = imageKey2ImageDrawComponents.get(imageKey);
		int size = 0;
		if (imageDCs != null) {
			size = imageDCs.size();	
		}
		if (logger.isDebugEnabled()) {
			logger.debug(size+" imageDCs are found for imageKey "+imageKey);	
		}

		DataBuffer dataBuffer = null;
		if (size > 1)
			dataBuffer = new DataBuffer(entrySizeHint, in);

		for (ImageDrawComponent imageDC : imageDCs) {
//				ZipEntry imageEntry = zipStream.getNextEntry();
			if (dataBuffer != null) {
				InputStream inputStream = dataBuffer.createInputStream();
				try {
					imageDC.reloadImage(inputStream);
				} finally {
					inputStream.close();
				}
			} else {
				imageDC.reloadImage(in);
			}
		}
	}

	protected Map<String, Set<ImageDrawComponent>> getImageKey2ImageDrawComponent(RootDrawComponent root)
	{
//		List<ImageDrawComponent> imageDrawComponents = root.getDrawComponents(ImageDrawComponentImpl.class);
		Collection<ImageDrawComponent> imageDrawComponents = root.findDrawComponents(ImageDrawComponent.class);
		Map<String, Set<ImageDrawComponent>> imageKey2ImageDrawComponents = new HashMap<String, Set<ImageDrawComponent>>();

		for (Iterator<ImageDrawComponent> it = imageDrawComponents.iterator(); it.hasNext(); )
		{
			ImageDrawComponent imageDC = it.next();
			String imageKey = ManifestWriter.getImageKeyString(imageDC);
			if (imageKey2ImageDrawComponents.containsKey(imageKey)) {
				Set<ImageDrawComponent> images = imageKey2ImageDrawComponents.get(imageKey);
				images.add(imageDC);
			} else {
				Set<ImageDrawComponent> images = new HashSet<ImageDrawComponent>();
				images.add(imageDC);
				imageKey2ImageDrawComponents.put(imageKey, images);
			}
		}

		return imageKey2ImageDrawComponents;
	}

	/**
	 * Write the <b>uncompressed</b> <code>meta-inf/manifest.properties</code> entry, which must always
	 * be the first entry in the generated zip file after the {@link #META_INF_DIR meta-inf/} directory entry.
	 * <p>
	 * See {@link #writeManifestProperties(Writer)} for further details.
	 * </p>
	 * 
	 * @param zipStream the output stream into which this method adds a new {@link ZipEntry} with method {@link ZipEntry#STORED}.
	 * @throws IOException if IO fails.
	 * @see {@link #writeManifestProperties(Writer)}
	 */
	protected void writeManifestPropertiesZipEntry(ZipOutputStream zipStream)
	throws IOException
	{
		ZipEntry zipEntry = new ZipEntry(META_INF_DIR + "manifest.properties");
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Writer w = new OutputStreamWriter(bout, "ISO-8859-1");
		writeManifestProperties(w);
		w.close();
		bout.close();
		byte[] manifestProperties = bout.toByteArray();
		
		zipEntry.setMethod(ZipEntry.STORED);
		zipEntry.setSize(manifestProperties.length);
		CRC32 crc = new CRC32();
		crc.update(manifestProperties);
		zipEntry.setCrc(crc.getValue());

		zipStream.putNextEntry(zipEntry);
		zipStream.write(manifestProperties);
		zipStream.closeEntry();
	}

	/**
	 * Write the contents of the <code>meta-inf/manifest.properties</code> entry. It must be uncompressed
	 * (method {@link ZipEntry#STORED}) and it must be the first entry after the
	 * {@link #META_INF_DIR meta-inf/} directory entry
	 * (which must be added with method <code>STORED</code> as well).
	 * Therefore, the order of the entries in a usual editor-2d-file is this:
	 * <ol>
	 * <li><code>meta-inf/</code> (<i>stored</i> required)</li>
	 * <li><code>meta-inf/manifest.properties</code> (<i>stored</i> required)</li>
	 * <li><code>meta-inf/manifest.xml</code> (<i>deflated</i> recommended)</li>
	 * <li><code>content.xml</code> (<i>deflated</i> recommended)</li>
	 * <li><code>images/</code> (<i>stored</i> recommended)</li>
	 * <li><code>...</code></li>
	 * </ol>
	 * The order of the entries after <code>meta-inf/manifest.properties</code> does not matter
	 * (only the first 2 entries' order is guaranteed).
	 * <p>
	 * The <code>meta-inf/manifest.properties</code> serves primarily for the purpose of mime-type
	 * detection based on magic numbers (as done by the Unix <code>/usr/bin/file</code> command and
	 * many other programs). Therefore, this is not a simple properties file and <b>you must not use
	 * a {@link Properties} object</b> to write it (because the <code>Properties</code> would
	 * write an arbitrarily long comment at the beginning and it would mess up the order of the entries).
	 * </p>
	 * <p>
	 * The format of the <code>meta-inf/manifest.properties</code> follows the rules of Java properties
	 * files (i.e. ISO-8859-1 encoding, key-value-pairs separated by '=') and can therefore be read
	 * by a {@link Properties} instance. Additionally, it has the following constraints:
	 * <ul>
	 * <li>There must not be any comment at the beginning of the file (there should be no comment at all).</li>
	 * <li>There must be no space before or after the '=' character.</li>
	 * <li>The first entry must be the <code>media-type</code> (aka MIME content-type).</li>
	 * <li>The second entry must be the <code>version</code>.</li>
	 * </ul>  
	 * </p>
	 * <p>
	 * For magic mime type detection, this means:
	 * <ul>
	 * <li>The file is a valid zip file</li>
	 * <li>and at offset 0x61 (= 97 decimal), the media-type property (e.g. "media-type=application/x-nightlabs-editor2d") starts.</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Here's an entry for the /etc/magic.mime (it seems, the unix file command has restrictions on the length
	 * of the searched text, hence I had to break it into separate rules):
	 * <pre>
	 * # NightLabs Editor2D vector graphics
	 * 0    string   PK\003\004
	 * >0x61   string  media-type=application/
	 * >>0x78      string  x-nightlabs-editor2d    application/x-nightlabs-editor2d
	 * >>0x78      string  x-nightlabs-jfire-voucher   application/x-nightlabs-jfire-voucher
	 * </pre>
	 * </p>
	 * 
	 * @param w the writer to write into.
	 * @throws IOException if IO fails.
	 * @see #writeManifestPropertiesZipEntry(ZipOutputStream)
	 */
	protected void writeManifestProperties(Writer w)
	throws IOException
	{
		w.write("media-type=");
		w.write(manifestWriter.getContentType());
		w.write('\n');

		w.write("version=");
		w.write(manifestWriter.getVersion());
		w.write('\n');
	}

	protected ManifestWriter manifestWriter = null;
	/**
	 * Writes the given Object (RootDrawComponent) zipped to the OutputStream
	 *
	 * @param o the RootDrawComponent to write
	 * @param out the OutputStream to write the File to
	 */
	public void write(Object o, OutputStream out)
	throws IOException
	{
//		RootDrawComponent root = (RootDrawComponent) o;
		root = (RootDrawComponent) o;
		root.getBounds();

		manifestWriter = createManifestWriter();
		
		XStream xstream = new XStream(new XppDriver());
		initWriteSubTasks();

		ZipOutputStream zipStream = new ZipOutputStream(out);
		zipStream.setLevel(9);

		// META-INF-Directory
		ZipEntry metaInfEntry = new ZipEntry(META_INF_DIR);
		metaInfEntry.setMethod(ZipEntry.STORED);
		metaInfEntry.setSize(0);
		metaInfEntry.setCrc(0);
		zipStream.putNextEntry(metaInfEntry);
		zipStream.closeEntry();

		writeManifestPropertiesZipEntry(zipStream);

		// Manifest.xml
		ZipEntry manifestEntry = new ZipEntry(META_INF_DIR + MANIFEST_NAME);
		zipStream.putNextEntry(manifestEntry);
		OutputStreamWriter manifestOut = new OutputStreamWriter(new BufferedOutputStream(zipStream), CHARSET_NAME);
		writeManifest(manifestOut, root);
		manifestOut.flush();
		zipStream.closeEntry();

		// Content.xml
		ZipEntry contentEntry = new ZipEntry(ManifestWriter.DEFAULT_CONTENT_NAME);
		zipStream.putNextEntry(contentEntry);
		OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(zipStream), CHARSET_NAME);
		registerConverter(xstream);
		processSubTask(SUBTASK_COMPRESS);
		xstream.toXML(o, writer);
		processSubTask(SUBTASK_WRITING);
		zipStream.closeEntry();

		// Images
		if (manifestWriter.containsImages(root))
		{
			// Images-Directory
			ZipEntry imagesEntry = new ZipEntry(IMAGES_DIR);
			imagesEntry.setMethod(ZipEntry.STORED);
			imagesEntry.setSize(0);
			imagesEntry.setCrc(0);
			zipStream.putNextEntry(imagesEntry);
			zipStream.closeEntry();

			// Images
			writeImages(root, zipStream);
		}

		zipStream.finish();
		zipStream.close();

		manifestOut.close();
		writer.close();
	}

	/**
	 * @deprecated Use {@link #writeManifest(OutputStreamWriter)} instead
	 */
	protected void writeManifest(OutputStreamWriter manifestOut, RootDrawComponent root)
	throws IOException
	{
		writeManifest(manifestOut);
	}

	protected void writeManifest(OutputStreamWriter manifestOut)
	throws IOException
	{
		StringBuffer sb = manifestWriter.writeManifest(root);
		String manifest = sb.toString();
		manifestOut.write(manifest);
		if (debug) {
			logger.debug("Manifest.xml");
			logger.debug(manifest);
		}
	}

	protected ManifestWriter createManifestWriter() {
		ManifestWriter manifestWriter = new ManifestWriter(EDITOR2D_CONTENT_TYPE);
		manifestWriter.setVersion(XSTREAM_FILTER_VERSION);
		return manifestWriter;
	}
	
	/**
	 * @deprecated Use {@link #writeImages(ZipOutputStream)} instead
	 */
	protected void writeImages(RootDrawComponent mldc, ZipOutputStream out)
	{
		writeImages(out);
	}

	/**
	 * writes all originalImageDatas of all ImageDrawComponents of the RootDrawComponent
	 * duplicate originalImageDatas are skipped
	 *
	 * @param out the ZipOutputStream to write the images to
	 */
	protected void writeImages(ZipOutputStream out)
	{
		Map<String, Set<ImageDrawComponent>> imageKey2ImageDrawComponentsSet =
			manifestWriter.getImageKey2ImageDrawComponentsSet();
		for (Iterator<Map.Entry<String, Set<ImageDrawComponent>>> it = imageKey2ImageDrawComponentsSet.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry<String, Set<ImageDrawComponent>> entry = it.next();
			Set<ImageDrawComponent> images = entry.getValue();
			ImageDrawComponent image = images.iterator().next();
			writeImage(image, out);
		}
	}

	/**
	 * writes the originalImageData of a {@link ImageDrawComponent} to a ZipOutputStream
	 *
	 * @param image the {@link ImageDrawComponent} which originalImageData should be written
	 * @param out the ZipOutputStream to write to
	 */
	protected void writeImage(ImageDrawComponent image, ZipOutputStream out)
	{
		try {
			ZipEntry imageEntry = new ZipEntry(IMAGES_DIR + ManifestWriter.getUniqueImageName(image));
			out.putNextEntry(imageEntry);
			out.write(image.getOriginalImageData());
	    out.closeEntry();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * rebuilds all necessary redundant information which is not saved in the file-format
	 *
	 * @param root the RootDrawComponent to rebuild
	 */
	protected void rebuild(RootDrawComponent mldc)
	{
		// All these maps are now lazily initialized correctly when first accessing the protected getters.
//		if (debug)
//			logger.debug("Rebuild BEGIN!");
//		long startTime = System.currentTimeMillis();
//		rebuildIDAndTypeMap(mldc.getDrawComponents(), mldc);
//		long newTime = System.currentTimeMillis() - startTime;
//		if (debug)
//			logger.debug("Rebuild took "+newTime+" ms");
	}

//	/**
//	 * Rebuilds both Maps (id2DrawComponent and class2DrawComponents) of a
//	 * RootDrawComponent based on the entries in the drawComponents-Lists of
//	 * all DrawComponentContainers.
//	 *
//	 * This is done by recursivly going through all drawComponents-Lists of
//	 * all DrawComponentContainers.
//	 *
//	 * @param drawComponents a List of drawComponents
//	 * (the drawComponents-List of a DrawComponentContainer)
//	 * @param root The RootDrawComponent to rebuild
//	 */
//	protected void rebuildIDAndTypeMap(List<DrawComponent> drawComponents, RootDrawComponent mldc)
//	{
//		for (Iterator<DrawComponent> it = drawComponents.iterator(); it.hasNext(); )
//		{
//			DrawComponent dc = it.next();
//			long id = dc.getId();
//			mldc.getId2DrawComponent().put(new Long(id), dc);
//			rebuildTypeMap(dc, mldc);
//			if (dc instanceof DrawComponentContainer) {
//				DrawComponentContainer container = (DrawComponentContainer) dc;
//				List<DrawComponent> children = container.getDrawComponents();
//				rebuildIDAndTypeMap(children, mldc);
//			}
//		}
//	}
//
//	/**
//	 * rebuilds the class2DrawComponents-Map for the RootDrawComponent
//	 * this Method is called recursively by rebuildIDAndTypeMap(List, RootDrawComponent)
//	 *
//	 * @param drawComponent the drawComponent to add to the typeMap
//	 * @param root the Root-Object for the given drawComponent
//	 */
//	protected void rebuildTypeMap(DrawComponent drawComponent, RootDrawComponent mldc)
//	{
//    Class<? extends DrawComponent> dcClass = drawComponent.getClass();
//    List<DrawComponent> typeCollection = null;
//    if (!mldc.getClass2DrawComponents().containsKey(dcClass))
//    {
//      typeCollection = new ArrayList<DrawComponent>();
//      typeCollection.add(drawComponent);
//      mldc.getClass2DrawComponents().put(dcClass, typeCollection);
//    }
//    else if (mldc.getClass2DrawComponents().containsKey(dcClass))
//    {
//      typeCollection = mldc.getClass2DrawComponents().get(dcClass);
//      typeCollection.add(drawComponent);
//    }
//	}

	/**
	 * registers Converters to the given XStream
	 *
	 * @param xstream the XStream to which Converters should be added
	 */
	protected void registerConverter(XStream xstream)
	{
		xstream.registerConverter(new GeneralShapeConverter());
		xstream.registerConverter(new AffineTransformConverter());
//		xstream.registerConverter(new ImageDrawComponentConverter());

//		xstream.registerConverter(new TextDrawComponentConverter());
//		xstream.registerConverter(new LineDrawComponentConverter());
//		xstream.registerConverter(new EllipseDrawComponentConverter());
//		xstream.registerConverter(new RectangleDrawComponentConverter());

		xstream.registerConverter(
				new ReflectionConverter(xstream.getMapper(), new PureJavaReflectionProvider())
				{
					/* (non-Javadoc)
					 * @see com.thoughtworks.xstream.converters.reflection.ReflectionConverter#canConvert(java.lang.Class)
					 */
					@SuppressWarnings("unchecked")
					@Override
					public boolean canConvert(Class type) {
						if (DrawComponent.class.isAssignableFrom(type))
							return true;
						return false;
					}
				}
		);

		xstream.omitField(AbstractTextDrawComponent.class, "originalShape");
		xstream.omitField(TextDrawComponentImpl.class, "originalShape");
	}

}
