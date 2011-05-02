package edu.mit.cci.visualize.wiki.collector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Logger;


public class GetUsertalkNetwork {

	private static final Logger log = Logger.getLogger(GetUsertalkNetwork.class.getName());

	public static String getNetwork(final String lang, final String nodes) {
		String data = "";

		Result result = new Result();
		try {
			String[] node = nodes.split("\n");
			int[][] matrix = new int[node.length][node.length];
			for (int i = 0; i < node.length; i++) {
				for (int j = 0; j < node.length; j++) {
					if (i == j) {
						continue;
					}
					String from = node[i].split("\t")[0];
					from = from.replaceAll(" ","_");
					String to = node[j].split("\t")[0];
					to = to.replaceAll(" ", "_");

					String xml = getUserTalkContribs(lang, to, from, "");
					if (xml.indexOf("<revisions>") > 0) {
						log.info(xml);
						XMLParseUserTalk parse = new XMLParseUserTalk(to,result,xml);
						parse.parse();
					}
					String out = result.getResult();

					//log.info(i + "-" + from + " " + j + "-" + to + " " + out);
					if (out.length() > 1) {
						matrix[i][j] = (out.split("\n").length);
					}
					//System.out.println("To:" + to + "¥tFrom:" + from + "¥tCount:" + matrix[i][j]);
					result.clear();

				}
			}
			for (int i = 0; i < matrix.length; i++) {
				for (int j = i+1; j < matrix[i].length; j++) {
					int value = matrix[i][j] + matrix[j][i];
					if (value > 0) {
						data += node[i].split("\t")[0] + "\t" + node[j].split("\t")[0] + "\t" + String.valueOf(value) + "\n";
					}
				}
			}
			//log.info(data);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private static String getUserTalkContribs(final String lang, String to, String from, final String nextId) {
		String rvstartid = "&rvstartid=" + nextId;
		if (nextId.equals("")) {
			rvstartid = "";
		}
		String xml = "";
		try {
			to = URLEncoder.encode(to,"UTF-8");
			from = URLEncoder.encode(from,"UTF-8");
			String urlStr = "http://" + lang + ".wikipedia.org/w/api.php?format=xml&action=query&prop=revisions&titles=User_talk:" + to + "&rvlimit=500&rvprop=flags%7Ctimestamp%7Cuser&rvuser=" + from + rvstartid;
			//log.info(urlStr);
			URL url = new URL(urlStr);
			HttpURLConnection urlCon = (HttpURLConnection)url.openConnection();
			urlCon.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; ja-jp) AppleWebKit/533.16 (KHTML, like Gecko) Version/5.0 Safari/533.16");
			urlCon.setRequestMethod("GET");
			urlCon.setInstanceFollowRedirects(false);

			urlCon.connect();


			BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			String line;

			while ((line = reader.readLine()) != null) {
				//tmpData.append(line + "\n");
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

