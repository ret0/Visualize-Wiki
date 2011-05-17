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

public class GetRevisions {

    private final static Logger LOG = LoggerFactory.getLogger(GetRevisions.class.getName());

    public Revisions getArticleRevisions(final String lang, final String title) {

        final String articleName = title.replaceAll(" ", "_");
        Revisions revisionsResult = new Revisions(articleName);
        DefaultHttpClient httpclient = new DefaultHttpClient();
        WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient);

        try {
            String queryContinueID = "";
            Api revisionFromXML = null;
            while(true) {
                final String xml = getArticleRevisionsXML(lang, title, queryContinueID, wikiAPIClient);
                revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
                for (Revision rev : revisionFromXML.getAllRevisionsForRequest()) {
                    revisionsResult.addEditEntry(rev);
                }
                if (revisionFromXML.isLastPageInRequestSeries()) {
                    break;
                } else {
                    queryContinueID = revisionFromXML.getQueryContinueID();
                }
            }
        } catch (Exception e) {
           LOG.error("Error while executing HTTP request", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return revisionsResult;
    }

    private String getArticleRevisionsXML(final String lang,
                                          String pageid,
                                          final String nextId,
                                          final WikiAPIClient wikiAPIClient) {
        String rvstartid = "&rvstartid=" + nextId;
        if (nextId.equals("")) {
            rvstartid = "";
        }

        try {
            pageid = URLEncoder.encode(pageid, Const.ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Encoding failed!");
        }

        String urlStr = "http://" + lang
                + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=" + pageid
                + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvdir=older" + rvstartid;
        LOG.info("Requesting URL: " + urlStr);
        return wikiAPIClient.executeHTTPRequest(urlStr);
    }

}
