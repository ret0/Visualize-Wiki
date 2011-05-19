package edu.mit.cci.visualize.wiki.collector;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.visualize.wiki.fetcher.UserDetailFetcher;

public class ArticleContributions {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleContributions.class.getName());

    // red to green
    private static final String[] experienceColors = { "#ff0033", "#ff3333", "#ff6633", "#ff9933",
            "#ffcc33", "#ffff33", "#ccff33", "#99ff33", "#66ff33", "#33ff33" };

    public static String[] getExperiencecolors() {
        return experienceColors;
    }

    private final String userID;
    private final int numberOfChanges;
    private final int editSize;
    private final int userExperience;

    public ArticleContributions(final int numberOfChanges, final String userID, final int editSize) {
        this.numberOfChanges = numberOfChanges;
        this.userID = userID;
        this.editSize = editSize;
        this.userExperience = getUserExperience();
    }

    public String getUserID() {
        return userID;
    }

    public int getNumberOfChanges() {
        return numberOfChanges;
    }

    public int getEditSize() {
        return editSize;
    }

    public double getGraphicalNodeSize() {
        double nodeSize = Math.log10(numberOfChanges) * 20;
        if (nodeSize < 10) {
            nodeSize = 10;
        }
        return nodeSize;
    }

    public String getExperienceColor() {
        if (userExperience < 0) {
            return "#FFFFFF"; // white
        } else if (userExperience >= 0 && userExperience < 400) {
            final int wtf = userExperience/40;
            return experienceColors[wtf];
        } else {
            return experienceColors[experienceColors.length - 1];
        }
    }

    private int getUserExperience() {
        final CacheManager cacheManager = CacheManager.getInstance();
        final Cache userExperienceCache = cacheManager.getCache("userexperience");
        Element cacheElement = userExperienceCache.get(userID);

        if(cacheElement != null) {
            return (Integer) cacheElement.getValue();
        } else {
            UserDetailFetcher detailFetcher = new UserDetailFetcher(userID, "en"); // TODO lang!
            try {
                int usersScore = detailFetcher.downloadUsersScore();
                LOG.info("Fetched Score for User: " + userID + " Score: " + usersScore);
                userExperienceCache.put(new Element(userID, usersScore));
                return usersScore;
            } catch (Exception e) {
                LOG.error("Problem while getting experience for User: " + userID);
                return -1;
            }
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + editSize;
        result = prime * result + numberOfChanges;
        result = prime * result + ((userID == null) ? 0 : userID.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ArticleContributions other = (ArticleContributions) obj;
        if (editSize != other.editSize)
            return false;
        if (numberOfChanges != other.numberOfChanges)
            return false;
        if (userID == null) {
            if (other.userID != null)
                return false;
        } else if (!userID.equals(other.userID))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ArticleContributions [userID=" + userID + ", numberOfChanges=" + numberOfChanges
                + ", editSize=" + editSize + "]";
    }

}
