package verification;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.fetcher.UserDetailFetcher;

public class TestGetUserScore {

    @Test
    public void testScoreResults() {
        List<String> usersToTest = Lists.newArrayList("Andreasegde", "DocKino", "GabeMc", "Mclay1",
                "Hoops gza", "Steelbeard1", "Jamwins", "Y2kcrazyjoker4", "TonyTheTiger",
                "AaronRodgers27", "Bulldog73", "CityFeedback", "Raremetalmining", "Moxy",
                "WikitanvirBot", "GoingBatty", "Piriczki", "Indopug", "Ghmyrtle", "Oh babe");
        List<Integer> expectedResults = Lists.newArrayList(13793, 2762, 2101, 4970, 4565, 5486, 404,
                9648, 44182, 116, 308, 394, 76, 14721, 26186, 18740, 1770, 4760, 10114, 117);

        List<Integer> downloadedScore = Lists.newArrayList(Collections2.transform(usersToTest,
                new Function<String, Integer>() {
                    @Override
                    public Integer apply(final String input) {
                        try {
                            return new UserDetailFetcher(input, "en").downloadUsersScore();
                        } catch (Exception e) {
                            Assert.fail();
                            return -1;
                        }
                    }
                }));
        Assert.assertEquals(expectedResults, downloadedScore);
    }
}
