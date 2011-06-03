package org.nightlabs.i18n.test;

import junit.framework.TestCase;

import org.junit.Test;
import org.nightlabs.i18n.MultiLanguagePropertiesBundle;

public class MultiLanguagePropertiesBundleTest extends TestCase {

	/**
	 * Tests the construction of the {@link MultiLanguagePropertiesBundle} and
	 * asserts the correct number of properties-files that should be found and load.
	 */
	@Test
	public void testMultiLanguagePropertiesBundleStringClassLoaderBoolean()
	{
		MultiLanguagePropertiesBundle bundle = loadTestBundle();
		assertNotNull(bundle);

		assertEquals("There must be 2 languages (de, fr): ", 2, bundle.getLocales().size());
		assertTrue("No default message.properties was read!", bundle.getKeys(null) != null && bundle.getKeys(null).size() > 0);
	}

//	public void testGetLocales() {
//		fail("Not yet implemented");
//	}
//
//	public void testHasEntryLocaleString() {
//		fail("Not yet implemented");
//	}
//
//	public void testHasEntryLocaleStringBoolean() {
//		fail("Not yet implemented");
//	}
//
//	public void testGetProperty() {
//		fail("Not yet implemented");
//	}


	/**
	 * Tests, if all (distinct!) keys of any resources will be found.
	 * This will be interesting, because there might be some differences
	 * in the keys in the different resources (*.properties-files).
	 */
	@Test
	public void testGetAllKeys()
	{
		MultiLanguagePropertiesBundle bundle = loadTestBundle();

		if (bundle.getAllKeys()==null)
			fail("bundle.getAllKeys()==null");

		if (bundle.getAllKeys().size()!=4)
			fail("bundle.getAllKeys().size()!=4; bundle.getAllKeys().size()=" + bundle.getAllKeys().size());

	}


	/**
	 * Loads all resource-files with the prefix "/resource/messages", near to this test-class via {@link ClassLoader}.
	 * @return
	 */
	private static MultiLanguagePropertiesBundle loadTestBundle()
	{
		ClassLoader classLoader = MultiLanguagePropertiesBundleTest.class.getClassLoader();
		MultiLanguagePropertiesBundle bundle = new MultiLanguagePropertiesBundle(
				MultiLanguagePropertiesBundleTest.class.getPackage().getName() + "/resource/messages",
				classLoader
		);
		return bundle;
	}
}
