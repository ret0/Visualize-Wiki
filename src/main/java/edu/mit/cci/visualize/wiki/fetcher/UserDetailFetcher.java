package edu.mit.cci.visualize.wiki.fetcher;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.mit.cci.visualize.wiki.util.Const;
import edu.mit.cci.visualize.wiki.util.WikiAPIClient;
import edu.mit.cci.visualize.wiki.xml.Api;
import edu.mit.cci.visualize.wiki.xml.XMLTransformer;

public class UserDetailFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(UsertalkNetworkFetcher.class.getName());

    private final String userName;
    private final WikiAPIClient wikiAPIClient = new WikiAPIClient(new DefaultHttpClient());
    private final String langCode;

    public UserDetailFetcher(final String userName, final String langCode) {
        this.userName = userName;
        this.langCode = langCode;
    }

    public int downloadUsersScore() throws Exception {
        String urlStr = generateUserDetailsRequestURL(langCode, userName);
        String userXML = wikiAPIClient.executeHTTPRequest(urlStr);
        Api revisionFromXML = XMLTransformer.getRevisionFromXML(userXML);
        return revisionFromXML.generateScoreForUser();
    }

    private String generateUserDetailsRequestURL(final String langCode, final String userName) {
        try {
            String userFields = URLEncoder.encode("editcount|registration", Const.ENCODING);
            String userNameEncoded = URLEncoder.encode(userName, Const.ENCODING);
            return "http://" + langCode
            + ".wikipedia.org/w/api.php?action=query&list=users&ususers="
            + userNameEncoded + "&usprop=" + userFields + Const.ANSWER_FORMAT;
        } catch (UnsupportedEncodingException e) {
            LOG.error("Encoding issue", e);
        }
        return "";
    }

}
