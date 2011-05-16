import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;
import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.util.MapSorter;

public class TestGetUserTalk {

    @Test
    public void getUserTalk() {
        Revisions download = new GetRevisions().getArticleRevisions("en", "WikiLeaks");

        MapSorter ms = new MapSorter();
        List<String> sortMap = ms.generateTopAuthorRanking(download, 10);
        List<String> expected = Lists.newArrayList(
                "89\tSmartse\t-7047",
                "84\tCybercobra\t-7814",
                "79\tGregcaletta\t38543",
                "78\tVeritysense\t1742",
                "68\tSpitzl\t-1128",
                "57\tAndyTheGrump\t123093",
                "50\tFunandtrvl\t-1809",
                "47\tFelixhonecker\t-6955",
                "46\tF.F.McGurk\t-10030",
                "42\tOcaasi\t-10827");

        Assert.assertEquals(expected, sortMap);

    }
}
