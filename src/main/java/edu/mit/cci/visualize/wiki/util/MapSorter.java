package edu.mit.cci.visualize.wiki.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.ArticleContributions;
import edu.mit.cci.visualize.wiki.collector.Revision;
import edu.mit.cci.visualize.wiki.collector.Revisions;

public class MapSorter {

    /**
     * Generate Ranking for top (maxNodes) authors (most edits)
     */
	public List<ArticleContributions> generateTopAuthorRanking(final Revisions revisionData, int maxNodes) {

		Hashtable<String, Integer> numberOfArticleEditsPerUser = new Hashtable<String, Integer>();
		Hashtable<String, Integer> editSizeTable = new Hashtable<String, Integer>();

		int previousArticleSize  = 0;

		for (Revision rev : revisionData.getRevisions()) {
			String userID = rev.getUserID();
			previousArticleSize = storeEditSizes(editSizeTable, previousArticleSize, rev, userID);
			storeNumberOfEdits(numberOfArticleEditsPerUser, userID);
		}

		// Sort by edit count
		ArrayList<Entry<String, Integer>> topUsers = Lists.newArrayList(numberOfArticleEditsPerUser.entrySet());
		Collections.sort(topUsers, new Comparator<Entry<String, Integer>>(){
            @Override
            public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
                return -(o1.getValue() - o2.getValue());
            }
		});

		if (topUsers.size() < maxNodes || maxNodes == 0) {
			maxNodes = topUsers.size();
		}

		List<ArticleContributions> output = Lists.newArrayList();
		for (int j = 0; j < maxNodes; j++) {
			Entry<String, Integer> entry = topUsers.get(j);
			String userID = entry.getKey();
			Integer nbrEdits = entry.getValue();
			Integer editSize = editSizeTable.get(userID);
			output.add(new ArticleContributions(nbrEdits, userID, editSize));
		}
		return output;
	}

    private void storeNumberOfEdits(final Hashtable<String, Integer> numberOfArticleEditsPerUser,
                                    final String userID) {
        if (numberOfArticleEditsPerUser.containsKey(userID)) {
        	numberOfArticleEditsPerUser.put(userID, numberOfArticleEditsPerUser.get(userID) + 1);
        } else {
        	numberOfArticleEditsPerUser.put(userID, 1);
        }
    }

    private int storeEditSizes(final Hashtable<String, Integer> editSizeTable,
                               final int previousArticleSize,
                               final Revision rev,
                               final String userID) {
        int size = rev.getEditSize();
        int sizeDifference = size - previousArticleSize;

        if (editSizeTable.containsKey(userID)) {
        	int v = editSizeTable.get(userID);
        	v += sizeDifference;
        	editSizeTable.put(userID, v);
        } else {
        	editSizeTable.put(userID, sizeDifference);
        }
        return size;
    }

}
