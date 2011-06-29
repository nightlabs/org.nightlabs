/**
 * 
 */
package org.nightlabs.classloader.url;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.nightlabs.util.IOUtil;

/**
 * @author abieber
 *
 */
public class NestedURLClassLoaderTest {

	private static final String TESTCLASSES_PKG = "org.nightlabs.classloader.url.testclasses";
	
	private NestedURLClassLoader cl; 
	
	@Before
	public void createCL() {
		cl = new NestedURLClassLoader(ClassLoader.getSystemClassLoader().getParent());
	}
	
	@Test
	public void testLoadFromJar_ClassloaderURL() throws IOException {
		cl.addURL(getClass().getResource("testjar1.jar"), false);
		assertLoad(1);
	}

	@Test
	public void testLoadFromNestedJar_ClassloaderURL() throws IOException {
		cl.addURL(getClass().getResource("testjar-nested.jar"), false);
		assertLoad(1);
		assertLoad(2);
	}
	
	@Test
	public void testLoadFromJar_FilesystemURL() throws IOException {
		File tmpFile = File.createTempFile("NestedURLClassloader_test_1", ".jar");
		tmpFile.deleteOnExit();
		IOUtil.copyResource(getClass(), "testjar1.jar", tmpFile);
		cl.addURL(tmpFile.toURI().toURL(), false);
		assertLoad(1);
	}
	
	@Test
	public void testLoadFromNestedJar_FilesystemURL() throws IOException {
		File tmpFile = File.createTempFile("NestedURLClassloader_test_nested", ".jar");
		tmpFile.deleteOnExit();
		IOUtil.copyResource(getClass(), "testjar-nested.jar", tmpFile);
		cl.addURL(tmpFile.toURI().toURL(), false);
		assertLoad(1);
		assertLoad(2);
	}
	
	@Test
	public void testLoadMixed_FilesystemURL_recursive() throws IOException {
		File tempDir = IOUtil.getTempDir();
		File tempFolder = IOUtil.createUniqueRandomFolder(tempDir, "NestedURLClassloader_test_recursive");
		try {
			IOUtil.copyResource(getClass(), "testjar-nested.jar", new File(tempFolder, "testjar-nested.jar"));
			IOUtil.copyResource(getClass(), "testjar3.jar", new File(tempFolder, "testjar3.jar"));
			cl.addURL(tempFolder.toURI().toURL(), true);
			assertLoad(1);
			assertLoad(2);
			assertLoad(3);
		} finally {
			IOUtil.deleteDirectoryRecursively(tempFolder);
		}
	}
	
	@Test
	public void testLoadMixed_ClassloaderURL_recursive_2Level() throws IOException {
		cl.addURL(getClass().getResource("testjar-nested-2level.jar"), true);
		assertLoad(1);
		assertLoad(2);
		assertLoad(3);
	}	
	
	@Test
	public void testLoadWithReferenceAndURLUpdate() throws Exception {
		
		cl.addURL(getClass().getResource("testjar4.jar"), false);
		
		String testClass4Name = TESTCLASSES_PKG + "4.A";
		boolean exThrown = false;
		Class<?> testClass4 = cl.loadClass(testClass4Name);
		try {
			testClass4.getField("reference");
		} catch (NoClassDefFoundError err) {
			exThrown = err.getMessage().contains("testclasses1");
		}
		Assert.assertTrue(testClass4Name + " could be loaded although not all referenced files in scope?!?", exThrown);
		
		// now ex extend the URLs
		cl.addURL(getClass().getResource("testjar1.jar"), false);
		// and try to re-reference the field
		Field field = testClass4.getField("reference");
		Assert.assertEquals("Reference field of testclass4.A is not of expected type", field.getType(), cl.loadClass(TESTCLASSES_PKG + "1.A"));
	}
	
	private void assertLoad(int packageNo) {
		String packageName = TESTCLASSES_PKG + packageNo;
		Assert.assertTrue("NestedURLClassloader did not publish package before loading class", cl.getPackageNames().contains(packageName));
		try {
			cl.loadClass(packageName + ".A");
		} catch (ClassNotFoundException e) {
			Assert.fail("NestedURLClassloader failed loading class from package " + packageName);
		}
		
	}
}
