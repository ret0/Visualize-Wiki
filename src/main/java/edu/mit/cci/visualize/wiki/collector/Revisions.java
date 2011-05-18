package edu.mit.cci.visualize.wiki.collector;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Revisions implements Serializable {

    private static final long serialVersionUID = -4852482473135369468L;

    private final String articleName;

    public String getArticleName() {
        return articleName;
    }

    private final List<Revision> edits = Lists.newArrayList();

    public Revisions(final String articleName) {
        this.articleName = articleName;
    }

    public void addEditEntry(final Revision rev) {
        // ignore anonymous edits
        if(rev.getUserID() != null) {
            edits.add(rev);
        }
    }

    public int getNumberOfRevisions() {
        return edits.size();
    }

    @Override
    public String toString() {
        return StringUtils.join(edits, "\n");
    }

    public ImmutableList<Revision> getRevisions() {
        return ImmutableList.copyOf(edits);
    }

}
