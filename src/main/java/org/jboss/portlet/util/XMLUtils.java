package org.jboss.portlet.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;


import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;



import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.xml.sax.InputSource;

/**
 * Modified from
 * http://svn.blueleftistconstructor.com/projects/netflix-java-client/tags/netflix-java-client-2.1.1/src/test/java/com/netflix/api/utils/XMLUtils.java
 * 
 * @author jharen
 * @author prabhat jha 
 */
class XMLUtils
{
	private static final Logger logger = LoggerFactory.getLogger(XMLUtils.class);

	private static final String DEFAULT_ENCODING = "UTF-8";
	
	private static XPathFactory xPathFactory;
	
	private static javax.xml.xpath.XPath xPathInstance;
	
	static
	{
		xPathFactory = XPathFactory.newInstance();
		try
		{
			xPathInstance = xPathFactory.newXPath();
		} 
		catch (Exception e)
		{
			logger.error("Could not instantiate XPath due to exception", e);
		}
	}

	/**
	 * Applies an XSL transformation to <code>xmlString</code> using
	 * <code>xsltStr</code> as the stylesheet and returns the transformed
	 * string.
	 * 
	 * @param xmlString
	 *            The XML to transform.
	 * @param xsltStr
	 *            The stylesheet as an XML string (not a file).
	 * @return Transformed string.
	 * @throws Exception
	 */
	public static String applyStylesheetAsString(String xmlString, String xsltStr) throws Exception
	{
		// Use the static TransformerFactory.newInstance() method to instantiate
		// a TransformerFactory. The javax.xml.transform.TransformerFactory
		// system property setting determines the actual class to instantiate --
		// org.apache.xalan.transformer.TransformerImpl.

		TransformerFactory tFactory = TransformerFactory.newInstance();

		// Use the TransformerFactory to instantiate a Transformer that will work with
		// the stylesheet you specify. This method call also processes the
		// stylesheet into a compiled Templates object.

		Transformer transformer = tFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xsltStr.getBytes())));

		// Use the Transformer to apply the associated Templates object to an
		// XML document (foo.xml) and write the output to a file (foo.out).

		StringWriter swriter = new StringWriter();
		StreamResult sresult = new StreamResult(swriter);
		transformer.transform(new StreamSource(new ByteArrayInputStream(xmlString.getBytes("UTF-8"))), sresult);
		return swriter.toString();
	}

		/**
	 * Given an Element and an <code>attributeName</code> returns the attribute
	 * value
	 * 
	 * @param elem
	 * @param attributeName
	 * @return The attribute value for <code>attributeName</code>
	 * @throws Exception
	 */
	public static String getAttribute(Element elem, String attributeName) throws Exception
	{
		String value = elem.getAttributeValue(attributeName);
		return value;
	}

	/**
	 * Given an XML String, gets the root attribute value for the
	 * <code>attributeName</code>.
	 * 
	 * @param xmlString
	 * @param attributeName
	 *            name of the attribute whose value is returned
	 * @return The value for the attribute.
	 * @throws Exception
	 */
	public static String RootAttribute(String xmlString, String attributeName) throws Exception
	{
		Document doc = createDocumentFromString(xmlString);
		String value = getRootAttribute(doc, attributeName);
		return value;
	}

	/**
	 * Builds the Document object from the <code>xmlString</code>. Encodes the
	 * string to UTF-8 format before building the document.
	 * 
	 * @param xmlString
	 * @return Document object
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document createDocumentFromString(String xmlString) throws JDOMException, IOException
	{
		Document schemaDoc = null;
		SAXBuilder builder = new SAXBuilder(false);
		byte[] xmlBytes = null;
		try
		{
			// get the UTF 8 encoded bytes
			xmlBytes = xmlString.getBytes(DEFAULT_ENCODING);
			schemaDoc = builder.build(new ByteArrayInputStream(xmlBytes));
		} catch (UnsupportedEncodingException usee)
		{
			schemaDoc = builder.build(new StringReader(xmlString));
		}
		return schemaDoc;
	}

	/**
	 * @param xmlString
	 * @param encoding
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document createDocumentFromString(String xmlString, String encoding) throws JDOMException, IOException
	{
		Document schemaDoc = null;
		SAXBuilder builder = new SAXBuilder(false);
		byte[] xmlBytes = null;
		try
		{
			xmlBytes = xmlString.getBytes(encoding);
			schemaDoc = builder.build(new ByteArrayInputStream(xmlBytes));
		} catch (UnsupportedEncodingException usee)
		{
			schemaDoc = builder.build(new StringReader(xmlString));
		}
		return schemaDoc;
	}

	/**
	 * Given an <code>inputStream</code>, ceates a Document fromt he stream data
	 * and returns the Document
	 * 
	 * @param inputStream
	 * @return Document
	 * @throws UnsupportedEncodingException
	 * @throws JDOMException
	 */
	public static Document createDocumentFromStream(InputStream inputStream) throws UnsupportedEncodingException, JDOMException, IOException
	{
		Document xmlDoc = null;
		SAXBuilder builder = new SAXBuilder(false);
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, DEFAULT_ENCODING));
		xmlDoc = builder.build(new InputSource(reader));
		return xmlDoc;
	}

	/**
	 * Tests a string. If it is null OR blank returns false else true.
	 * 
	 * @param s
	 *            String to test.
	 * @return true if not null and not blank.
	 */
	public static boolean isSet(String s)
	{
		return ((s != null) && (!s.equals("")));
	}

	
	/**
	 * Given a the name of an element to search for (start) and a string to
	 * search in, returns the content of that element. This gets around the
	 * inability to use xpath when only a default namespace is defined for a
	 * document.
	 * 
	 * @param docString
	 *            The string to search in.
	 * @param start
	 *            The element name whose content you want.
	 * @return The element's text content.
	 * @throws Exception
	 */
	public static String getElementTextFromString(String docString, String start)
	{
		String tmp = "<" + start + ">";
		int begin = docString.indexOf(tmp);
		if (begin == -1)
		{
			logger.error("XMLUtils:getElementTextFromString: "
				+ " Unable to find <" + start + "> in string.");
			return null;
		}

		int end = docString.indexOf("</", begin);
		if (end == -1)
		{
			logger.error("XMLUtils:getElementTextFromString: Unable to find </" + start + ">.");
			return null;
		}

		String val = docString.substring(begin + tmp.length(), end);
		return val;
	}

	/**
	 * Given an <code>inputStream</code>, creates a Document and returns the
	 * specified attribute of the root element of the document.
	 * 
	 * @param inputStream
	 * @param attribute
	 * @return the specified attribute of the root element of the document
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String getRootAttribute(InputStream inputStream, String attribute) throws JDOMException, IOException, Exception
	{
		Document doc = null;
		doc = createDocumentFromStream(inputStream);
		if (doc == null)
		{
			logger.error("XMLUtils: " + " getRootAttribute failed. Null document.");
			throw new Exception("XMLUtils: getRootAttribute.  Unable to create Document from stream.");
		}
		Element root = doc.getRootElement();
		String attrVal = root.getAttributeValue(attribute);
		return attrVal;
	}

	/**
	 * Given a Document <code>xmlDoc</code>, returns the attribute value of
	 * the specified <code>attribute</code> of the root element of the
	 * document.
	 * 
	 * @param xmlDoc
	 * @param attributeName
	 *            The name of the attribute whose value is returned.
	 * @return the specified attribute of the root element of the document
	 * @throws JDOMException
	 * @throws IOException
	 */
	private static String getRootAttribute(Document xmlDoc, String attributeName) throws JDOMException, IOException, Exception
	{
		if (xmlDoc == null)
		{
			logger.error("XMLUtils:  getRootAttribute failed. Null document.");
			throw new Exception("XMLUtils: getRootAttribute.  Unable to get attribute. Null Document.");
		}
		Element root = xmlDoc.getRootElement();
		String attrVal = root.getAttributeValue(attributeName);
		return attrVal;
	}

	/**
	 * Given an XML string, creates a Document and returns attribute value of
	 * the specified attributeName of the root element of the Document.
	 * 
	 * @param xml
	 * @param attributeName
	 *            The name of the attribute whose value is returned.
	 * @return The attribute value of <code>attributeName</code> of the root
	 *         element of the document
	 * @throws JDOMException
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private static String getRootAttributeFromString(String xml, String attributeName) throws JDOMException, IOException, Exception
	{
		Document xmlDoc = createDocumentFromString(xml);
		if (xmlDoc == null)
		{
			logger.error("XMLUtils: getRootAttributeFromString failed. Null document.");
			throw new Exception("XMLUtils: getRootAttributeFromString.  Unable to get attribute. Null Document.");
		}
		return getRootAttribute(xmlDoc, attributeName);
	}

	/**
	 * Given a Document, returns the Document content as a string
	 * @param doc
	 * @return the content of the Document as a string
	 */
	public static String getXMLStringFromDoc(Document doc)
	{
		String resultingXML = null;
		Format format = Format.getPrettyFormat();
		format.setEncoding(DEFAULT_ENCODING);
		XMLOutputter xmlOut = new XMLOutputter(format);
		resultingXML = xmlOut.outputString(doc);
		return resultingXML;
	}
	
	/**
	 * Given a string of XML, returns a pretty-printed,
	 * indented copy.
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public static String prettyPrint(String xml) throws Exception
	{
		Document doc = createDocumentFromString(xml);
		return getXMLStringFromDoc(doc);
	}

	/**
	 * @param xmlString
	 * @param xPath
	 * @return
	 * @throws Exception
	 */
	public static Element getXPathElementFromString(String xmlString, String xPath) throws Exception
	{
		Document xmlDoc = createDocumentFromString(xmlString);
		return getXPathElementFromDoc(xmlDoc, xPath);
	}
	
	/**
	 * @param xmlString
	 * @param xPath
	 * @return String
	 * @throws Exception
	 */
	public static String getXPathElementTextFromString(String xmlString, String xPath) throws Exception
	{
		Document xmlDoc = createDocumentFromString(xmlString);
		Element elem = getXPathElementFromDoc(xmlDoc, xPath);
		if (elem != null)
			return elem.getText();
		else return null;
	}
	
	/**
	 * @param doc
	 * @param xPath
	 * @return
	 * @throws Exception
	 */
	public static String getXPathElementTextFromDoc(Document doc, String xPath) throws Exception
	{
		Element elem = getXPathElementFromDoc(doc, xPath);
		if (elem != null)
			return elem.getText();
		else return null;
	}
	
	/**
	 * @param xmlString
	 * @param xPath
	 * @return String
	 * @throws Exception
	 */
	public static String getXPathAttributeTextFromString(String xmlString, String xPath) throws Exception
	{
		Document xmlDoc = createDocumentFromString(xmlString);
		Attribute att = getXPathAttributeFromDoc(xmlDoc, xPath);
		if (att != null)
			return att.getValue();
		else return null;
	}

	/**
	 * Given a Document and an <code>xPath</code> returns the Element
	 * represented by the <code>xPath</code> from the <code>xmlDoc</code>.
	 * Assumes there is only one Element with this <code>xPath</code>.
	 * Results are indeterminate if more than one Element with
	 * <code>xPath</code> exists.
	 * 
	 * @param xmlDoc
	 * @param xPath
	 * @return the Element for the xpath. Assumes only one such element exists.
	 *         Results are undetermined if more than one Element exists with the
	 *         same xPath.
	 * @throws Exception
	 */
	public static Element getXPathElementFromDoc(Document xmlDoc, String xPath) throws Exception
	{
		XPath xpath1;
		if (xmlDoc == null)
		{
			throw new Exception("getXPathElementFromDoc. Null document passed.");
		}

		if (isSet(xPath) && xPath.endsWith("/"))
		{
			xPath = xPath.substring(0, xPath.lastIndexOf("/"));
		}

		xpath1 = XPath.newInstance(xPath);
		return (Element) xpath1.selectSingleNode(xmlDoc);
	}
	
	public static Attribute getXPathAttributeFromDoc(Document xmlDoc, String xPath) throws Exception
	{
		XPath xpath1;
		if (xmlDoc == null)
		{
			throw new Exception("getXPathElementFromDoc. Null document passed.");
		}

		if (isSet(xPath) && xPath.endsWith("/"))
		{
			xPath = xPath.substring(0, xPath.lastIndexOf("/"));
		}

		xpath1 = XPath.newInstance(xPath);
		return (Attribute) xpath1.selectSingleNode(xmlDoc);
	}
	
	/**
	 * Given a Document and an <code>xPath</code> returns the Element
	 * represented by the <code>xPath</code> from the <code>xmlDoc</code>.
	 * 
	 * @param xmlDoc
	 * @param xPath
	 * @return the Element for the xpath.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getXPathElementListFromDoc(Document xmlDoc, String xPathExpression) throws Exception
	{
		XPath xpath;
		if (xmlDoc == null)
		{
			throw new Exception("getXPathElementFromDoc. Null document passed.");
		}

		if (isSet(xPathExpression) && xPathExpression.endsWith("/"))
		{
			xPathExpression = xPathExpression.substring(0, xPathExpression.lastIndexOf("/"));
		}

		xpath = XPath.newInstance(xPathExpression);
		return xpath.selectNodes(xmlDoc);
	}
	
	public static List<Element> getXPathElementListFromString(String xmlString, String xPathExpression) throws Exception
	{
		Document doc = createDocumentFromString(xmlString);
		return getXPathElementListFromDoc(doc, xPathExpression);
	}

	/**
	 * Retrieves the text of an
	 * element represented by the <code>xPath</code> from the Document.
	 * Assumes there is only one element with <code>xPath</code>. Results are
	 * undetermined if there is more than one Element with <code>xPath</code>.
	 * @param xmlString
	 * @param xPath
	 * @return
	 * @throws Exception
	 */
	public static String getXPathValueFromString(String xmlString, String xPath)
	{
		try
		{
			Document doc = createDocumentFromString(xmlString);
			return getXPathValueFromDoc(doc, xPath);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Given a Document, <code>xmlDoc</code>, retrieves the text of an
	 * element represented by the <code>xPath</code> from the Document.
	 * Assumes there is only one element with <code>xPath</code>. Results are
	 * undetermined if there is more than one Element with <code>xPath</code>.
	 * 
	 * @param xmlDoc
	 * @param xPath
	 * @return the text of the element represented by the xpath
	 * @throws Exception
	 */
	public static String getXPathValueFromDoc(Document xmlDoc, String xPath) throws Exception
	{
		Element e;
		String xmlValue = null;
		XPathExpression xpe;
		if (xmlDoc == null)
		{
			throw new Exception("XMLUtils:getXPathValueFromDoc. Null document passed.");
		}

		if (isSet(xPath) && xPath.endsWith("/"))
		{
			xPath = xPath.substring(0, xPath.lastIndexOf("/"));
		}
		
		xpe = xPathInstance.compile(xPath);
		e = (Element) xpe.evaluate(xmlDoc, XPathConstants.NODE);
		if (e != null)
		{
			if (e.getTextTrim() != null)
			{
				xmlValue = e.getTextTrim();
			}
		}
		else
		{
			logger.error("XMLUtils:getXPathValueFromDoc.  No value found for " + xPath + ".");
			return null;
		}
		return xmlValue;
	}

	/**
	 * Given an <code>inputStream</code>, creates a Document and retrieves
	 * the text of an element represented by the <code>xPath</code> from the
	 * Document. Assumes there is only one element with <code>xPath</code>.
	 * Results are undetermined if there is more than one Element with
	 * <code>xPath</code>.
	 * 
	 * @param inputStream
	 *            An input stream used to create a Document.
	 * @param xPath
	 * @return The text content of the element.
	 * @throws JaxenException
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static String getXPathValueFromStream(InputStream inputStream, String xPath) throws Exception
	{
		Document doc = createDocumentFromStream(inputStream);
		return getXPathValueFromDoc(doc, xPath);
	}

	/**
	 * Given a Document, <code>xmlDoc</code> writes the content of the
	 * document to the logger for this class. Exceptions are caught;an error in
	 * this functionality does not stop the processing.
	 * @param xmlDoc the Document to log
	 */
	public static void writeMetadataToLog(Document xmlDoc)
	{
		try
		{
			Format format = Format.getPrettyFormat();
			format.setEncoding(DEFAULT_ENCODING);
			XMLOutputter xmlOut = new XMLOutputter(format);
			logger.debug("\n" + xmlOut.outputString(xmlDoc));
		}
		catch (Exception e)
		{
			logger.error("IOException in XMLUtils: writeMetadataToLog with document. No action taken", e);
		}
	}

	/**
	 * Given an input Stream, writes the content of the stream to the logger
	 * for this class. Exceptions are caught;an error in this functionality does not
	 * stop the processing.
	 * @param inputStream
	 */
	public static void writeMetadataToLog(InputStream inputStream)
	{
		if (logger.isDebugEnabled())
		{
			try
			{
				Document doc = createDocumentFromStream(inputStream);
				Format format = Format.getPrettyFormat();
				format.setEncoding(DEFAULT_ENCODING);
				XMLOutputter xmlOut = new XMLOutputter(format);
				xmlOut.outputString(doc);
			}
			catch (Exception ex)
			{
				logger.error("Exception in XMLUtils with document. No action taken", ex);
			}
			finally
			{
				try
				{
					inputStream.close();
				}
				catch (IOException ioex)
				{
					logger.error("Unable to close stream. No action taken.");
				}
			}
		}
	}

	/**
	 * Given a Document, <code>newDoc</code> and a
	 * <code>metadataOutputStream</code>, writes the content of the Document
	 * to the stream. Exceptions are caught; an error in this functionality does
	 * not stop processing.
	 * @param newDoc
	 *            the metadata as a Document
	 * @param metadataOutputStream
	 *            an OutputStream to which the contents of the Document are
	 *            written.
	 */
	public static void writeMetadataToOutputStream(Document newDoc, OutputStream metadataOutputStream)
	{
		try
		{
			Format format = Format.getPrettyFormat();
			format.setEncoding(DEFAULT_ENCODING);
			XMLOutputter xmlOut = new XMLOutputter(format);
			xmlOut.output(newDoc, metadataOutputStream);
		}
		catch (IOException ioex)
		{
			logger.error("IOException in XMLUtils: No action taken.", ioex);
		}
	}

}
