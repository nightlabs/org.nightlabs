package org.nightlabs.xml;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * @author Marc Klinger - marc[at]nightlabs[dot]de
 */
public class NLDOMUtilTest
{
	@Test
	public void testGetTextContent1() throws Exception
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
		Assert.assertEquals(text, "Text1Text2Text3");
	}

	// TODO test line break behaviour in windows. The underlying parser always returns '\n' for "\r\n" in linux.
	@Test
	public void testGetTextContent2() throws Exception
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
		Assert.assertEquals(text, "Text1\n\nText2\nText3\n\n");
	}

	@Test
	public void testGetTextContent3() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test></test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement());
		Assert.assertNotNull(text);
		Assert.assertEquals("", text);
	}

	@Test
	public void testGetTextContent4() throws Exception
	{
		String xml =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<test></test>";
		ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(in);
		String text = NLDOMUtil.getTextContent(document.getDocumentElement(), false);
		Assert.assertNull(text);
	}
}
