package edu.mit.cci.visualize.wiki.fetcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import edu.mit.cci.visualize.wiki.collector.UsertalkEdge;
import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.util.Pair;
import edu.mit.cci.visualize.wiki.util.WikiAPIClient;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class UsertalkNetworkFetcher {

    private static final int NUM_THREADS = 16;
    private static final Logger LOG = LoggerFactory.getLogger(UsertalkNetworkFetcher.class.getName());

    private final String lang;
    private final List<String> sanitizedUserIDs;
    private final int numberOfUsersInNetwork;
    private final int[][] userTalkMatrix;
    private final ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(NUM_THREADS);

    public UsertalkNetworkFetcher(final String lang, final List<String> userIDs) {
        this.lang = lang;
        sanitizedUserIDs = sanitizeUserIDs(userIDs);
        numberOfUsersInNetwork = sanitizedUserIDs.size();
        userTalkMatrix = new int[numberOfUsersInNetwork][numberOfUsersInNetwork];
    }

    public List<UsertalkEdge> getNetwork() {
        for (int i = 0; i < numberOfUsersInNetwork; i++) {
            for (int j = 0; j < numberOfUsersInNetwork; j++) {
                if (i == j) {
                    continue;
                }
                final String from = sanitizedUserIDs.get(i);
                final String to = sanitizedUserIDs.get(j);
                newFixedThreadPool.execute(new GetUserTalk(j, i, from, to));
            }
        }
        shutdownThreadPool();
        return collectUserTalkEdges();
    }

    private void shutdownThreadPool() {
        newFixedThreadPool.shutdown();
        try {
            newFixedThreadPool.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            LOG.error("Error while shutting down Threadpool", e);
        }
        while (!newFixedThreadPool.isTerminated()) {
            // wait for all tasks or timeout
        }
    }

    private List<UsertalkEdge> collectUserTalkEdges() {
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

    private final class GetUserTalk implements Runnable {
        private final String from;
        private final String to;
        private final int j;
        private final int i;

        private GetUserTalk(final int j, final int i, final String from, final String to) {
            this.j = j;
            this.i = i;
            this.from = from;
            this.to = to;
        }

        @Override
        public void run() {
            CacheManager manager = CacheManager.getInstance();
            Cache cache = manager.getCache("usertalks");

            Pair<String> userCommunicationPair = new Pair<String>(from, to);

            if(cache.get(userCommunicationPair) != null) {
                userTalkMatrix[j][i] = (Integer) cache.get(userCommunicationPair).getValue();
            } else {
                final DefaultHttpClient httpclient = new DefaultHttpClient();
                final WikiAPIClient wikiAPIClient = new WikiAPIClient(httpclient, false);
                final String xml = getUserTalkContribs(to, from, wikiAPIClient);
                int numberOfRevisions = 0;
                if (xml.indexOf("<revisions>") > 0) {
                    Api revisionFromXML;
                    try {
                        revisionFromXML = XMLTransformer.getRevisionFromXML(xml);
                        numberOfRevisions = revisionFromXML.getAllRevisionsForRequest().size();

                    } catch (Exception e) {
                        LOG.error("Exception while executing Fetch Thread", e);
                    }
                }
                cache.put(new Element(userCommunicationPair, numberOfRevisions));
                cache.flush();
                LOG.info(StringUtils.join(cache.getKeys(), ", "));
                userTalkMatrix[j][i] = numberOfRevisions;
            }


        }
    }
}
