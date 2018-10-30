package org.owasp.appsensor.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * Helper class for XML related utility methods
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public class XmlUtils {
	
	/**
	 * Validate XML matches XSD. Path-based method.
	 * Simple redirect to the overloaded version that accepts streams.
	 * 
	 * @param xsdPath resource based path to XSD file
	 * @param xmlPath resource based path to XML file
	 * @throws IOException io exception for loading files
	 * @throws SAXException sax exception for parsing files
	 */
	public static void validateXMLSchema(String xsdPath, String xmlPath) throws IOException, SAXException {
		InputStream xsdStream = null;
		InputStream xmlStream = null;
		try {
			xsdStream = XmlUtils.class.getResourceAsStream(xsdPath);
		
			//try loading from classpath first - fallback to disk
			if (xsdStream == null) {
				File xsdFile = new File(xsdPath);
			    xsdStream = new FileInputStream(xsdFile);
			}
			
			xmlStream = XmlUtils.class.getResourceAsStream(xmlPath);
			
			//try loading from classpath first - fallback to disk
			if (xmlStream == null) {
				File xmlFile = new File(xmlPath);
				xmlStream = new FileInputStream(xmlFile);
			}
			
			validateXMLSchema(xsdStream, xmlStream);
			
		} finally {
			if (xsdStream != null) {
				try {
					xsdStream.close();
				} catch(Exception e) {
					xsdStream = null;
				}
			}
			if (xmlStream != null) {
				try {
					xmlStream.close();
				} catch(Exception e) {
					xmlStream = null;
				}
			}
		}
    }
	
	/**
	 * Validate XML matches XSD. Stream-based method.
	 * 
	 * @param xsdStream resource based path to XSD file
	 * @param xmlStream resource based path to XML file
	 * @throws IOException io exception for loading files
	 * @throws SAXException sax exception for parsing files
	 */
	public static void validateXMLSchema(InputStream xsdStream, InputStream xmlStream) throws IOException, SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = factory.newSchema(new StreamSource(xsdStream));
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xmlStream));
    }
	
	/**
	 * Helper method for getting qualified name from stax reader given a set of specified schema namespaces
	 * 
	 * @param xmlReader stax reader
	 * @param namespaces specified schema namespaces
	 * @return qualified element name
	 */
	public static String getElementQualifiedName(XMLStreamReader xmlReader, Map<String, String> namespaces) {
		String namespaceUri = null;
		String localName = null;
		
		switch(xmlReader.getEventType()) {
			case XMLStreamConstants.START_ELEMENT:
			case XMLStreamConstants.END_ELEMENT:
				namespaceUri = xmlReader.getNamespaceURI();
				localName = xmlReader.getLocalName();
				break;
			default:
				localName = StringUtils.EMPTY;
				break;
		}
		
		return namespaces.get(namespaceUri) + ":" + localName;
	}
	
}
