package edu.mit.cci.visualize.wiki.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.Revision;
import edu.mit.cci.visualize.wiki.collector.Revisions;

public class MapSorter {

	public List<String> sortMap(final Revisions revisionData, int maxNodes) {

		Hashtable<String, Integer> numberOfArticleEditsPerUser = new Hashtable<String, Integer>();
		Hashtable<String, Integer> editSizeTable = new Hashtable<String, Integer>();

		int previousArticleSize  = 0;

		for (Revision rev : revisionData.getRevisions()) {
			String user = rev.getUserID();
			int size = rev.getEditSize();
			int diff = size - previousArticleSize;

			if (editSizeTable.containsKey(user)) {
				int v = editSizeTable.get(user);
				v += diff;
				editSizeTable.put(user, v);
			} else {
				editSizeTable.put(user, diff);
			}
			previousArticleSize = size;

			if (numberOfArticleEditsPerUser.containsKey(user)) {
				int v = numberOfArticleEditsPerUser.get(user);
				v++;
				numberOfArticleEditsPerUser.put(user, v);
			} else {
				numberOfArticleEditsPerUser.put(user, 1);
			}
		}

		// Sort by edit count
		ArrayList<Entry<String, Integer>> al = Lists.newArrayList(numberOfArticleEditsPerUser.entrySet());
		Collections.sort(al, new Comparator<Entry<String, Integer>>(){
            @Override
            public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
                return -(o1.getValue() - o2.getValue());
            }
		});

		int alsize = al.size();
		if (alsize < maxNodes) {
			maxNodes = alsize;
		} else if (maxNodes == 0) {
			maxNodes = alsize;
		}

		List<String> output = Lists.newArrayList();
		for (int j = 0; j < maxNodes; j++) {
			String str = al.get(j).toString();
			String user = str.substring(0,str.lastIndexOf("="));
			String edits = str.substring(str.lastIndexOf("=")+1);
			String editSize = String.valueOf(editSizeTable.get(user));
			output.add(edits + "\t" + user + "\t" + editSize);
		}
		return output;
	}

}
