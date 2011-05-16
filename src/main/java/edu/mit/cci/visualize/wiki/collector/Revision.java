package edu.mit.cci.visualize.wiki.collector;

import org.joda.time.DateTime;

public class Revision {

    private final String userID;
    private final DateTime timestamp;
    private final int editSize;

    public int getEditSize() {
        return editSize;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public String getUserID() {
        return userID;
    }

    public Revision(final String userID, final String timestamp, final int editSize) {
        this.userID = userID;
        this.timestamp = new DateTime(timestamp);
        this.editSize = editSize;
    }

    @Override
    public String toString() {
        return userID;
    }

}
