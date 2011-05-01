package edu.mit.cci.visualize.wiki.collector;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParseRevision extends DefaultHandler {

	/**
	 * @param args
	 */
	private static String userName;
	private final Result result;
	private final String xml;

	public XMLParseRevision(final String _userName, final Result _result, final String _xml) {
		userName = _userName;
		result = _result;
		xml = _xml;
	}

	public void setUserName(final String _userName) {
		userName = _userName;
	}
	public String getUserName() {
		return userName;
	}

	public void parse() {
		try {
			// Create SAX Parser Factory
			SAXParserFactory spfactory = SAXParserFactory.newInstance();
			// Generate SAX Parser
			SAXParser parser = spfactory.newSAXParser();
			parser.parse(new ByteArrayInputStream(xml.getBytes()), new XMLParseRevision(userName,result,xml));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Document start
	 */
	@Override
	public void startDocument() {
		//System.out.println("Document start");
	}
	/**
	 * Reading the element start tag
	 */
	@Override
	public void startElement(final String uri,
			final String localName,
			final String qName,
			final Attributes attributes) {

		//System.out.println("Element:" + qName);
		if (qName.equals("rev")) {
			if(attributes.getLength()!=0){
				String minor = "0";
				if (attributes.getValue("minor") != null) {
					minor = "1";
				} else {
					String user = attributes.getValue("user");
					//if (user.split("\\.").length != 4) // Remove IP editors (xxx.xxx.xxx.xxx)
					result.append(getUserName() + "\t" + attributes.getValue("user") + "\t" + attributes.getValue("timestamp") + "\t" + minor + "\t" + attributes.getValue("size") + "\n");
				}
			}
		}
		if (qName.equals("revisions")) {
			if(attributes.getLength() > 0) {
				//System.out.println("\trevision continues " + attributes.getValue("rvstartid"));
				result.setNextId(attributes.getValue("rvstartid"));
			}
		}
	}
	/**
	 * Read text data
	 */
	@Override
	public void characters(final char[] ch,
			final int offset,
			final int length) {

		//System.out.println("Text：" + new String(ch, offset, length));
	}
	/**
	 * Read the element end tag
	 */
	@Override
	public void endElement(final String uri,
			final String localName,
			final String qName) {

		//System.out.println("Element end:" + qName);
	}
	/**
	 * End document
	 */
	@Override
	public void endDocument() {
		//System.out.println("Document end");
	}
}

