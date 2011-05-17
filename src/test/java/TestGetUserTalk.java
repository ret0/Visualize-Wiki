import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;
import edu.mit.cci.visualize.wiki.collector.GetUsertalkNetwork;
import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.util.MapSorter;

public class TestGetUserTalk {

    private static final String TEST_LANG = "en";
    private static final int MAX_NODES = 10;

    @Test
    public void getUserTalk() {
        Revisions download = new GetRevisions().getArticleRevisions(TEST_LANG, "WikiLeaks");

        List<String> sortMap = new MapSorter().generateTopAuthorRanking(download, MAX_NODES);
        List<String> expected = Lists.newArrayList(
                "89\tSmartse\t-7047",
                "84\tCybercobra\t-7814",
                "79\tGregcaletta\t38543",
                "78\tVeritysense\t1742",
                "68\tSpitzl\t-1128",
                "57\tAndyTheGrump\t-13505",
                "50\tFunandtrvl\t-1809",
                "47\tFelixhonecker\t-6955",
                "46\tF.F.McGurk\t-10030",
                "42\tOcaasi\t-10827");

        Assert.assertEquals(expected, sortMap);
    }

    @Test
    public void getNetworkEdges() {
        Revisions revisionData = new GetRevisions().getArticleRevisions(TEST_LANG, "WikiLeaks");
        // Sort data, with second parameter: getting Top N editors
        List<String> editRanking = new MapSorter().generateTopAuthorRanking(revisionData, MAX_NODES);

        String nodes = "";
        for (String rankingEntry : editRanking) {
            // Name \t # of edits \t # of edit articles
            nodes += rankingEntry.split("\t")[1] + "\t" + rankingEntry.split("\t")[0] + "\t1\n";
        }

        String edges = new GetUsertalkNetwork().getNetwork(TEST_LANG, nodes);
        String expected = "Smartse\tAndyTheGrump\t2\nCybercobra\tAndyTheGrump\t1\nCybercobra\tOcaasi\t1\nGregcaletta\tAndyTheGrump\t9\nSpitzl\tAndyTheGrump\t1\nAndyTheGrump\tOcaasi\t2\nFunandtrvl\tOcaasi\t2\n";
        Assert.assertEquals(expected, edges);

    }


}
