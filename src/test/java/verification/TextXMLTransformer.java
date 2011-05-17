package verification;
import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import edu.mit.cci.visualize.wiki.xml.XMLTransformer;



public class TextXMLTransformer {

    @Test
    public void transformRevisions() throws Exception {
        String initialFile = "src/test/resources/revision_test_xml/initial.xml";
        String xmlContent = FileUtils.readFileToString(new File(initialFile));
        XMLTransformer.getRevisionFromXML(xmlContent);
        //revisionFromXML.getQuery().
    }

    @Test
    public void transformRevisions2() throws Exception {
        String initialFile = "src/test/resources/revision_test_xml/final.xml";
        String xmlContent = FileUtils.readFileToString(new File(initialFile));
        XMLTransformer.getRevisionFromXML(xmlContent);
    }

}
