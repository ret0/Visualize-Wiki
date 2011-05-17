package edu.mit.cci.visualize.wiki.collector;

public class UsertalkEdge {

    private final String from;
    private final String to;
    private final int nbrOfConversations;

    public UsertalkEdge(final String from, final String to, final int nbrOfConversations) {
        this.from = from;
        this.to = to;
        this.nbrOfConversations = nbrOfConversations;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getNbrOfConversations() {
        return nbrOfConversations;
    }

}
