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
    private final DefaultHttpClient httpclient = new DefaultHttpClient();
    private final WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient, false);
    private final List<String> sanitizedUserIDs;

    public UsertalkNetworkFetcher(final String lang, final List<String> userIDs) {
        this.lang = lang;
        sanitizedUserIDs = sanitizeUserIDs(userIDs);
    }

    public List<UsertalkEdge> getNetwork() {
        int numberOfUsersInNetwork = sanitizedUserIDs.size();
        int[][] userTalkMatrix = new int[numberOfUsersInNetwork][numberOfUsersInNetwork];
        try {
            for (int i = 0; i < numberOfUsersInNetwork; i++) {
                for (int j = 0; j < numberOfUsersInNetwork; j++) {
                    if (i == j) {
                        continue;
                    }
                    updateMatrixValues(i, j, userTalkMatrix);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception while fetching UserTalk Info", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return collectUserTalkEdges(userTalkMatrix);
    }

    private List<UsertalkEdge> collectUserTalkEdges(final int[][] userTalkMatrix) {
        List<UsertalkEdge> userTalkEdges = Lists.newArrayList();
        for (int i = 0; i < userTalkMatrix.length; i++) {
            for (int j = i + 1; j < userTalkMatrix[i].length; j++) {
                int totalConversations = userTalkMatrix[i][j] + userTalkMatrix[j][i]; //sum of talk in both directions
                if (totalConversations > 0) {
                    userTalkEdges.add(new UsertalkEdge(sanitizedUserIDs.get(i), sanitizedUserIDs.get(j), totalConversations));
                }
            }
        }
        return userTalkEdges;
    }

    private void updateMatrixValues(final int i, final int j, final int[][] userTalkMatrix) throws Exception {
        final String from = sanitizedUserIDs.get(i);
        final String to = sanitizedUserIDs.get(j);
        final String xml = getUserTalkContribs(to, from, wikiAPIClient);
        if (xml.indexOf("<revisions>") > 0) {
            Api revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
            int numberOfRevisions = revisionFromXML.getAllRevisionsForRequest().size();
            if (numberOfRevisions >= 1) {
                userTalkMatrix[i][j] = numberOfRevisions;
            }
        }
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
            String urlStr = "http://" + lang
                    + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=User%20talk:"
                    + to + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvuser=" + from;
            return wikiAPIClient.executeHTTPRequest(urlStr);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException", e);
        }
        return "";
    }
}
