package edu.mit.cci.visualize.wiki.collector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class GetRevisions {

    private final static Logger LOG = LoggerFactory.getLogger(GetRevisions.class.getName());

    public Revisions getArticleRevisions(final String lang,
                                         final String title) {

        final String articleName = title.replaceAll(" ", "_");
        Revisions revisionsResult = new Revisions(articleName);

        DefaultHttpClient httpclient = new DefaultHttpClient();
        addGzipRequestInterceptor(httpclient);
        addGzipResponseInterceptor(httpclient);

        try {
            String xml = getArticleRevisionsXML(lang, title, "", httpclient);

            Api revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
            for (Revision rev : revisionFromXML.getAllRevisionsForRequest()) {
                revisionsResult.addEditEntry(rev);
            }

            while (!revisionFromXML.isLastPageInRequestSeries()) {
                xml = getArticleRevisionsXML(lang, title, revisionFromXML.getQueryContinueID(),
                        httpclient);
                revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
                for (Revision rev : revisionFromXML.getAllRevisionsForRequest()) {
                    revisionsResult.addEditEntry(rev);
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
                                          final HttpClient httpclient) {
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
        return executeHTTPRequest(urlStr, httpclient);
    }

    private String executeHTTPRequest(final String url,
                                      final HttpClient httpclient) {
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent", Const.USER_AGENT);
            LOG.debug("executing request " + httpget.getURI());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }

        } catch (ClientProtocolException e) {
            LOG.error("ClientProtocolException", e);
        } catch (IOException e) {
            LOG.error("IOException", e);
        }
        LOG.error("Problem while executing request");
        return "";
    }

    private void addGzipRequestInterceptor(final DefaultHttpClient httpclient) {
        httpclient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(final HttpRequest request,
                                final HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }
        });
    }

    private void addGzipResponseInterceptor(final DefaultHttpClient httpclient) {
        httpclient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(final HttpResponse response,
                                final HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                Header ceheader = entity.getContentEncoding();
                if (ceheader != null) {
                    for (HeaderElement codec : ceheader.getElements()) {
                        if (codec.getName().equalsIgnoreCase("gzip")) {
                            response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                            return;
                        }
                    }
                }
            }
        });
    }

}
