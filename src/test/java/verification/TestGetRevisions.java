package verification;
import org.junit.Assert;
import org.junit.Test;

import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.fetcher.PageRevisionFetcher;


public class TestGetRevisions {

    @Test
    public void testGetArticleRevisions() throws Exception {
        Revisions articleRevisionsTree = new PageRevisionFetcher("en", "Tree").getArticleRevisions();
        Revisions articleRevisionsHSR = new PageRevisionFetcher("en", "Northeastern_United_States_blizzard_of_1978").getArticleRevisions();
        Assert.assertEquals(4181, articleRevisionsTree.getNumberOfRevisions());
        Assert.assertEquals(434, articleRevisionsHSR.getNumberOfRevisions());
    }

    @Test
    public void testGetArticleRevisionsLarge() throws Exception {
        Revisions articleRevisionsWikiLeaks = new PageRevisionFetcher("en", "WikiLeaks").getArticleRevisions();
        Assert.assertEquals(3675, articleRevisionsWikiLeaks.getNumberOfRevisions());
    }
}
