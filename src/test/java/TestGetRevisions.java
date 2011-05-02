import org.junit.Test;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;


public class TestGetRevisions {

    @Test
    public void testGetArticleRevisions() {
        GetRevisions rev = new GetRevisions();
        String articleRevisions = rev.getArticleRevisions("en", "Tree", 500);
        //System.out.println(articleRevisions);
    }
}
