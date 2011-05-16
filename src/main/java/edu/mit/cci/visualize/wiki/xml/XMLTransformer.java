package edu.mit.cci.visualize.wiki.xml;

import org.simpleframework.xml.core.Persister;

public class XMLTransformer {

    private static final Persister PERSISTER = new Persister();

    /**
     * Turns XML String into a Java object
     */
    public static Api getRevisionFromXML(final String xmlContent) throws Exception {
        return PERSISTER.read(Api.class, xmlContent);
    }

}
