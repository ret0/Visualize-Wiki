package edu.mit.cci.visualize.wiki.fetcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.visualize.wiki.collector.Revision;
import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.util.WikiAPIClient;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class PageRevisionFetcher {

    private final static Logger LOG = LoggerFactory.getLogger(PageRevisionFetcher.class.getName());

    private final DefaultHttpClient httpclient = new DefaultHttpClient();
    private final WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient);

    private final String pageTitle;
    private final String lang;

    public PageRevisionFetcher(final String lang, final String pageTitle) {
        this.lang = lang;
        this.pageTitle = pageTitle.replaceAll(" ", "_");
    }

    public Revisions getArticleRevisions() {
        CacheManager manager = CacheManager.getInstance();
        Cache cache = manager.getCache("revisions");

        final Element cachedValue = cache.get(pageTitle);
        if (cachedValue != null) {
            return (Revisions) cachedValue.getValue();
        } else {
            Revisions revisionsResult = downloadRevisions(cache);
            manager.shutdown();
            return revisionsResult;
        }
    }

    private Revisions downloadRevisions(final Cache cache) {
        Revisions revisionsResult = new Revisions(pageTitle);
        try {
            addAllRevisionsToList(revisionsResult);
        } catch (Exception e) {
            LOG.error("Error while executing HTTP request", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        cache.put(new Element(pageTitle, revisionsResult));
        cache.flush();
        return revisionsResult;
    }

    private void addAllRevisionsToList(final Revisions revisionsResult) throws Exception {
        Api revisionFromXML = null;
        String queryContinueID = "";
        while(true) {
            final String xml = getArticleRevisionsXML(queryContinueID);
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
    }

    private String getArticleRevisionsXML(final String nextId) {
        String rvstartid = "&rvstartid=" + nextId;
        if (nextId.equals("")) {
            rvstartid = "";
        }
        String pageid = "";

        try {
            pageid = URLEncoder.encode(pageTitle, Const.ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOG.error("Encoding failed!");
        }

        String urlStr = "http://" + lang
                + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=" + pageid
                + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvdir=older" + rvstartid;
        LOG.debug("Requesting URL: " + urlStr);
        return wikiAPIClient.executeHTTPRequest(urlStr);
    }

}
