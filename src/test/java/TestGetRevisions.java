import org.junit.Assert;
import org.junit.Test;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;
import edu.mit.cci.visualize.wiki.collector.Revisions;


public class TestGetRevisions {

    @Test
    public void testGetArticleRevisions() throws Exception {
        GetRevisions rev = new GetRevisions();
        Revisions articleRevisionsTree = rev.getArticleRevisions("en", "Tree");
        Revisions articleRevisionsHSR = rev.getArticleRevisions("en", "Northeastern_United_States_blizzard_of_1978");
        Assert.assertEquals(4181, articleRevisionsTree.getNumberOfRevisions());
        Assert.assertEquals(434, articleRevisionsHSR.getNumberOfRevisions());
    }

    @Test
    public void testGetArticleRevisionsLarge() throws Exception {
        GetRevisions rev = new GetRevisions();
        Revisions articleRevisionsWikiLeaks = rev.getArticleRevisions("en", "WikiLeaks");
        Assert.assertEquals(3673, articleRevisionsWikiLeaks.getNumberOfRevisions());
    }
}
