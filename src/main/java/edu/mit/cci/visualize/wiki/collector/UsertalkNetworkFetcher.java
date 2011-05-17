package edu.mit.cci.visualize.wiki.collector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.util.WikiAPIClient;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class UsertalkNetworkFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(UsertalkNetworkFetcher.class.getName());
    private final String lang;

    public UsertalkNetworkFetcher(final String lang) {
        this.lang = lang;
    }

    public String getNetwork(final List<String> userIDs) {
        List<String> sanitizedUserIDs = sanitizeUserIDs(userIDs);
        String data = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient, false);
        try {
            int numberOfUsersInNetwork = sanitizedUserIDs.size();
            int[][] userTalkMatrix = new int[numberOfUsersInNetwork][numberOfUsersInNetwork];

            for (int i = 0; i < numberOfUsersInNetwork; i++) {
                for (int j = 0; j < numberOfUsersInNetwork; j++) {
                    if (i == j) {
                        continue;
                    }
                    String from = sanitizedUserIDs.get(i);
                    String to = sanitizedUserIDs.get(j);
                    String xml = getUserTalkContribs(to, from, wikiAPIClient);
                    if (xml.indexOf("<revisions>") > 0) {
                        Api revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
                        int numberOfRevisions = revisionFromXML.getAllRevisionsForRequest().size();
                        if (numberOfRevisions >= 1) {
                            userTalkMatrix[i][j] = numberOfRevisions;
                        }
                    }
                }
            }
            for (int i = 0; i < userTalkMatrix.length; i++) {
                for (int j = i + 1; j < userTalkMatrix[i].length; j++) {
                    int totalConversations = userTalkMatrix[i][j] + userTalkMatrix[j][i]; //sum of talk in both directions
                    if (totalConversations > 0) {
                        data += sanitizedUserIDs.get(i) + "\t" + sanitizedUserIDs.get(j) + "\t" + totalConversations + "\n";
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Exception while fetching UserTalk Info", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return data;
    }

    private List<String> sanitizeUserIDs(final List<String> userIDs) {
        List<String> sanitizedUserIDs = Lists.newArrayList();
        for (String id : userIDs) {
            sanitizedUserIDs.add(id.replaceAll(" ", "_"));
        }
        return sanitizedUserIDs;
    }

    private String getUserTalkContribs(String to,
                                       String from,
                                       final WikiAPIClient wikiAPIClient) {
        try {
            to = URLEncoder.encode(to, Const.ENCODING);
            from = URLEncoder.encode(from, Const.ENCODING);
            String urlStr = "http://"
                    + lang
                    + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=User%20talk:"
                    + to + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvuser=" + from;
            return wikiAPIClient.executeHTTPRequest(urlStr);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException", e);
        }
        return "";
    }
}
