package org.nightlabs.xml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NLDOMUtilTest
{
	private static void assertEquals(Object o1, Object o2)
	{
		if(o1 == o2)
			return;
		if(o1 != null && o2 != null && o1.equals(o2))
			return;
		fail("Not equal: "+String.valueOf(o1)+" and "+String.valueOf(o2));
	}

	private static void assertIsNull(Object o1)
	{
		if(o1 != null)
			fail("Not null: "+o1);
	}

	private static void assertIsNotNull(Object o1)
	{
		if(o1 == null)
			fail("Not null: "+o1);
	}

	private static void fail()
	{
		fail(null);
	}

	private static void fail(String msg)
	{
		throw new RuntimeException("Test failed"+(msg == null ? "" : ": "+msg));
	}

	public static void main(String[] args) throws Exception
	{
		NLDOMUtilTest test = new NLDOMUtilTest();
		test.testGetTextContent1();
		test.testGetTextContent2();
		test.testGetTextContent3();
		test.testGetTextContent4();
		System.out.println("Tests successful");
	}

	private void testGetTextContent1() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test>Text1" +
			"<x>blabla</x>" +
			"Text2" +
			"<!-- comment -->" +
			"Text3" +
			"</test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement());
		assertEquals(text, "Text1Text2Text3");
	}

	// TODO test line break behaviour in windows. The underlying parser always returns '\n' for "\r\n" in linux.
	private void testGetTextContent2() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test>Text1\n\n" +
			"<x>blabla</x>" +
			"Text2\n" +
			"<!-- comment -->" +
			"Text3\n\n" +
			"</test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement());
		assertEquals(text, "Text1\n\nText2\nText3\n\n");
	}

	private void testGetTextContent3() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test></test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement());
		assertIsNotNull(text);
		assertEquals("", text);
	}

	private void testGetTextContent4() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test></test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement(), false);
		assertIsNull(text);
	}
}
