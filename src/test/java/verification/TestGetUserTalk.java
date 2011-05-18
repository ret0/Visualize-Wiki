package verification;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.ArticleContributions;
import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.collector.UsertalkEdge;
import edu.mit.cci.visualize.wiki.fetcher.PageRevisionFetcher;
import edu.mit.cci.visualize.wiki.fetcher.UsertalkNetworkFetcher;
import edu.mit.cci.visualize.wiki.util.MapSorter;

public class TestGetUserTalk {

    private static final String TEST_LANG = "en";
    private static final int MAX_NODES = 10;

    @Test
    public void getUserTalk() {
        Revisions download = new PageRevisionFetcher(TEST_LANG, "WikiLeaks").getArticleRevisions();

        List<ArticleContributions> sortMap = new MapSorter().generateTopAuthorRanking(download, MAX_NODES);
        List<ArticleContributions> expected = Lists.newArrayList(
                new ArticleContributions(89, "Smartse", -7047),
                new ArticleContributions(84, "Cybercobra", -7814),
                new ArticleContributions(79, "Gregcaletta", 38543),
                new ArticleContributions(78, "Veritysense", 1742),
                new ArticleContributions(68, "Spitzl", -1128),
                new ArticleContributions(58, "AndyTheGrump", -13534),
                new ArticleContributions(50, "Funandtrvl", -1809),
                new ArticleContributions(47, "Felixhonecker", -6955),
                new ArticleContributions(46, "F.F.McGurk", -10030),
                new ArticleContributions(42, "Ocaasi", -10827));
        Assert.assertEquals(expected, sortMap);
    }

    @Test
    public void getNetworkEdges() {
        Revisions revisionData = new PageRevisionFetcher(TEST_LANG, "WikiLeaks").getArticleRevisions();
        // Sort data, with second parameter: getting Top N editors
        List<ArticleContributions> editRanking = new MapSorter().generateTopAuthorRanking(revisionData, MAX_NODES);

        List<String> userIDs = Lists.newArrayList();
        for (ArticleContributions rankingEntry : editRanking) {
            userIDs.add(rankingEntry.getUserID());
        }

        List<UsertalkEdge> edges = new UsertalkNetworkFetcher(TEST_LANG, userIDs).getNetwork();
        List<UsertalkEdge> expected = Lists.newArrayList(
                new UsertalkEdge("Smartse", "AndyTheGrump", 2),
                new UsertalkEdge("Cybercobra", "AndyTheGrump", 1),
                new UsertalkEdge("Cybercobra", "Ocaasi", 1),
                new UsertalkEdge("Gregcaletta", "AndyTheGrump", 9),
                new UsertalkEdge("Spitzl", "AndyTheGrump", 1),
                new UsertalkEdge("AndyTheGrump", "Ocaasi", 2),
                new UsertalkEdge("Funandtrvl", "Ocaasi", 2));
        Assert.assertEquals(expected, edges);
    }


}
