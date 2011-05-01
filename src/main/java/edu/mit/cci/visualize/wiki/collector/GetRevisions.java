package edu.mit.cci.visualize.wiki.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;


public class GetRevisions {
	private static final Logger log = Logger.getLogger(GetRevisions.class.getName());

	public String getArticleRevisions(final String lang, String title, final String _limit) {
		String data = "";
		int limit = Integer.parseInt(_limit);
		Result result = new Result();

		try {
			title = title.replaceAll(" ", "_");
			String xml = getArticleRevisionsXML(lang, title,"");

			XMLParseRevision parse = new XMLParseRevision(title,result,xml);
			//parse.setUserName(userName);
			parse.parse();
			int count = 0;
			while(result.hasNextId()) {
				count++;
				if (limit != 0 && count >= limit) {
					break;
				}
				String nextId = result.getNextId();
				data += result.getResult();
				result.clear();
				//tmpData.clear();
				xml = getArticleRevisionsXML(lang, title, nextId);
				parse = new XMLParseRevision(title,result,xml);
				parse.parse();
				//Thread.sleep(1000);
			}
			data += result.getResult();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}


	private String getArticleRevisionsXML(final String lang, String pageid, final String nextId) {
		String rvstartid = "&rvstartid=" + nextId;
		if (nextId.equals("")) {
			rvstartid = "";
		}
		String xml = "";

		try {
			//String urlStr = "http://en.wikipedia.org/w/api.php";
			pageid = URLEncoder.encode(pageid,"UTF-8");

			String urlStr = "http://" + lang + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles="+pageid+"&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser%7Csize&rvdir=older"+rvstartid;
			//urlStr = URLEncoder.encode(urlStr);
			log.info("Requesting URL: " + urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
			urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; ja-jp) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16");
			urlCon.setRequestMethod("GET");
			urlCon.setInstanceFollowRedirects(false);
			/*urlCon.addRequestProperty("format", "xml");
            urlCon.addRequestProperty("action", "query");
            urlCon.addRequestProperty("prop", "revisions");
            urlCon.addRequestProperty("titles", pageid);
            urlCon.addRequestProperty("rvlimit", "500");
            urlCon.addRequestProperty("rvprop", "flags|timestamp|user|size");
            urlCon.addRequestProperty("rvdir", "newer");*/

			urlCon.connect();
			//urlCon.setRequestProperty("titles", pageid);

			BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			//BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				xml += line + "\n";
			}
			//log.info(xml);
			reader.close();

		} catch (MalformedURLException e) {
			// ...
			log.info(e.getMessage());
		} catch (IOException e) {
			// ...
			log.info(e.getMessage());
		}
		return xml;
	}

}
