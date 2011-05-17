package edu.mit.cci.visualize.wiki.collector;

public class ArticleContributions {

    private final String userID;
    private final int numberOfChanges;
    private final int editSize;

    public ArticleContributions(final int numberOfChanges, final String userID, final int editSize) {
        this.numberOfChanges = numberOfChanges;
        this.userID = userID;
        this.editSize = editSize;
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




}
