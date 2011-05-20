package edu.mit.cci.visualize.wiki.util;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WikiAPIClient {

    private static final int HTTP_TIMEOUT = 10000;
    private static final Logger LOG = LoggerFactory.getLogger(WikiAPIClient.class.getName());

    private final DefaultHttpClient httpclient;

    /**
     * Allow Users to disable gzip for small requests
     */
    public WikiAPIClient(final DefaultHttpClient httpclient, final boolean enableGzip) {
        this.httpclient = httpclient;
        setHTTPClientTimeouts(httpclient);
        if(enableGzip) {
            addGzipRequestInterceptor(httpclient);
            addGzipResponseInterceptor(httpclient);
        }
    }

    public WikiAPIClient(final DefaultHttpClient httpclient) {
        this(httpclient, true);
    }

    public String executeHTTPRequest(final String url) {
        try {
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent", Const.USER_AGENT);
            LOG.debug("executing request " + httpget.getURI());
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                return EntityUtils.toString(entity);
            }
        } catch (SocketTimeoutException e) {
            LOG.error("Timeout!", e);
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

    private void setHTTPClientTimeouts(final DefaultHttpClient httpclient) {
        HttpParams httpParams = httpclient.getParams();
        int connectionTimeoutMillis = HTTP_TIMEOUT;
        int socketTimeoutMillis = HTTP_TIMEOUT;
        HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeoutMillis);
        HttpConnectionParams.setSoTimeout(httpParams, socketTimeoutMillis);
    }
}
