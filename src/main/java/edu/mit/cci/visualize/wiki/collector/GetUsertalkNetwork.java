package edu.mit.cci.visualize.wiki.collector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.util.WikiAPIClient;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class GetUsertalkNetwork {

    private static final Logger LOG = LoggerFactory.getLogger(GetUsertalkNetwork.class.getName());

    public String getNetwork(final String lang, final String nodes) {
        String data = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient, false);
        try {
            String[] node = nodes.split("\n");
            int[][] userTalkMatrix = new int[node.length][node.length];

            for (int i = 0; i < node.length; i++) {
                for (int j = 0; j < node.length; j++) {
                    if (i == j) {
                        continue;
                    }
                    String from = node[i].split("\t")[0];
                    from = from.replaceAll(" ", "_");
                    String to = node[j].split("\t")[0];
                    to = to.replaceAll(" ", "_");

                    String xml = getUserTalkContribs(lang, to, from, wikiAPIClient);
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
                    int value = userTalkMatrix[i][j] + userTalkMatrix[j][i]; //sum of talk in both directions
                    if (value > 0) {
                        data += node[i].split("\t")[0] + "\t" + node[j].split("\t")[0] + "\t" + value + "\n";
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

    private String getUserTalkContribs(final String lang,
                                       String to,
                                       String from,
                                       final WikiAPIClient wikiAPIClient) {
        try {
            to = URLEncoder.encode(to, Const.ENCODING);
            from = URLEncoder.encode(from, Const.ENCODING);
            String urlStr = "http://"
                    + lang
                    + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=User%20talk:"
                    + to + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvuser=" + from;
            LOG.info(urlStr);
            return wikiAPIClient.executeHTTPRequest(urlStr);
        } catch (UnsupportedEncodingException e) {
            LOG.error("UnsupportedEncodingException", e);
        }
        return "";
    }
}
