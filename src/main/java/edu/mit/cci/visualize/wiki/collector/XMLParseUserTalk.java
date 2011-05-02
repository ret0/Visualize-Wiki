package edu.mit.cci.visualize.wiki.collector;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLParseUserTalk extends DefaultHandler {

    private static String userName;
    private final Result result;
    private final String xml;

    public XMLParseUserTalk(final String _userName, final Result _result, final String _xml) {
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
            // SAXパーサーファクトリを生成
            SAXParserFactory spfactory = SAXParserFactory.newInstance();
            // SAXパーサーを生成
            SAXParser parser = spfactory.newSAXParser();
            // XMLファイルを指定されたデフォルトハンドラーで処理します
            parser.parse(new ByteArrayInputStream(xml.getBytes()), new XMLParseUserTalk(userName,
                    result, xml));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 要素の開始タグ読み込み時
     */
    @Override
    public void startElement(final String uri,
                             final String localName,
                             final String qName,
                             final Attributes attributes) {

        if (qName.equals("rev")) {
            if (attributes.getLength() != 0) {
                String minor = "0";
                if (attributes.getValue("minor") != null) {
                    minor = "1";
                } else {
                    result.append(getUserName() + "\t" + attributes.getValue("user") + "\t"
                            + attributes.getValue("timestamp") + "\t" + minor + "\n");
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
