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

    @Override
    public String toString() {
        return "UsertalkEdge [from=" + from + ", to=" + to + ", nbrOfConversations="
                + nbrOfConversations + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + nbrOfConversations;
        result = prime * result + ((to == null) ? 0 : to.hashCode());
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
        UsertalkEdge other = (UsertalkEdge) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (nbrOfConversations != other.nbrOfConversations)
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }




}
