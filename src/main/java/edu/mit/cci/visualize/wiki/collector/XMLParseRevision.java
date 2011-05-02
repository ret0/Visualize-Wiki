package edu.mit.cci.visualize.wiki.collector;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParseRevision extends DefaultHandler {

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
            parser.parse(new ByteArrayInputStream(xml.getBytes()), new XMLParseRevision(userName,
                    result, xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reading the element start tag
     */
    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String qName,
                             final Attributes attributes) {

        // System.out.println("Element:" + qName);
        if (qName.equals("rev")) {
            if (attributes.getLength() != 0) {
                String minor = "0";
                if (attributes.getValue("minor") != null) {
                    minor = "1";
                } else {
                    // if (user.split("\\.").length != 4) // Remove IP editors
                    // (xxx.xxx.xxx.xxx)
                    result.append(getUserName() + "\t" + attributes.getValue("user") + "\t"
                            + attributes.getValue("timestamp") + "\t" + minor + "\t"
                            + attributes.getValue("size") + "\n");
                }
            }
        }
        if (qName.equals("revisions")) {
            if (attributes.getLength() > 0) {
                // System.out.println("\trevision continues " +
                // attributes.getValue("rvstartid"));
                result.setNextId(attributes.getValue("rvstartid"));
            }
        }
    }
}
