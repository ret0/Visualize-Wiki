package edu.mit.cci.visualize.wiki.vizservlet;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;
import edu.mit.cci.visualize.wiki.collector.GetUsertalkNetwork;
import edu.mit.cci.visualize.wiki.util.MapSorter;
import edu.mit.cci.visualize.wiki.util.Processing;

public class WikipediaUsertalkVizServlet{

	private static final Logger LOG = Logger.getLogger(WikipediaUsertalkVizServlet.class.getName());
	private static final String EOL = "\n";

	private final ServletContext context;
	private final HttpServletRequest request;

	public WikipediaUsertalkVizServlet(final HttpServletRequest request, final ServletContext context) {
		this.request = request;
		this.context = context;
	}

	public String magicMethod()
	throws IOException, ServletException {
		long currentTimeMillis = System.currentTimeMillis();
		StringWriter out = new StringWriter();
		try {
			String responseStr = "";

			String tableContents = "";
			String code = "";
			boolean includeMe = false;

			// Set arguments
			String pageTitle = readStringParameter("name", ""); // WikiPedia article title

			if (pageTitle.isEmpty()) {
				responseStr += "Please set arguments.<br>" + EOL;
				responseStr += "name: Page_name of a Wikipedia article." + EOL;
				out.append(responseStr);
				out.close();
				return out.toString();
			}

			String loginUser = readStringParameter("user", "");
			boolean cacheFlag = readBooleanParameter("cache", true);
			// # of edits (limit x 500 edits)
			String limit = readStringParameter("limit", "1"); // default: last 500 edits

			// # of nodes
			String nodeLimit = readStringParameter("nodeLimit", "10");
			String canvasSize = readStringParameter("size", "300");

			// Language
			String lang = readStringParameter("lang", "en");

			String data = "";
			// Searching cache
			//PersistenceManager pm = PMF.get().getPersistenceManager();

			//String query = "select from " + ArticleCache.class.getName() + " where pageTitle==\'" + pageTitle.replaceAll("\'", "\\\\\'") + "\'";
			//List<ArticleCache> articleCaches = (List<ArticleCache>) pm.newQuery(query).execute();
			GetRevisions gr = new GetRevisions();

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			// No cache
			//if (articleCaches.isEmpty() || (!articleCaches.isEmpty() && !cacheFlag)) {
				data = downloadData(pageTitle, limit, lang, data, gr);
			//}
			// Already cached and using cache
//			else if (!articleCaches.isEmpty() && cacheFlag){
//				LOG.info("Cached");
//				for(ArticleCache ac:articleCaches) {
//					data += ac.getPageTitle() + "\t" + ac.getAuthor() + "\t" + df.format(ac.getDate()) + "\t0\t" + String.valueOf(ac.getSize()) + "\n";
//				}
//			}
//			pm.close();

			// Sort data, with second parameter: getting Top N editors
			List<String> editRanking = new MapSorter().sortMap(data,Integer.parseInt(nodeLimit));

			// Generate HTML code from the edit history
			tableContents += "<table>" + EOL;
			tableContents += "<tbody>" + EOL;
			int sumNum = 0;
			String userName_editSize = "";
			for (int i = 0; i < editRanking.size(); i++) {
				String[] tmpArray = editRanking.get(i).split("\t");

				String name = tmpArray[1]; // Editor name
				String num = tmpArray[0]; // # of edits
				String editSize = tmpArray[2]; // total byte change by this user

				userName_editSize += name + "\t" + editSize + "\n";
				sumNum += Integer.parseInt(num);
				if (name.equals(loginUser)) {
					includeMe = true;
				}
				tableContents += "<tr>" + EOL;
				tableContents += "<td>" + name + "</td>" + EOL;
				tableContents += "<td>" + num + "</td>" + EOL;
				tableContents += "</tr>" + EOL;
			}
			// other edits top10
			/*
			tableContents += "<tr>" + eol;
			tableContents += "<td>Others</td>" + eol;
			tableContents += "<td>" + String.valueOf(dataSize-sumNum) + "</td>" + eol;
			tableContents += "</tr>" + eol;
			 */
			tableContents += "</tbody>" + EOL;
			tableContents += "</table>" + EOL;
			tableContents += "<p id =\"map\"></p>" + EOL;

			// Get absolute path to skeleton file
			String path = context.getRealPath("/skelton/skelton_spring.js");

			String nodes = "";
			for (int i = 0; i < editRanking.size(); i++) {
				// Name \t # of edits \t # of edit articles
				nodes += editRanking.get(i).split("\t")[1] + "\t" + editRanking.get(i).split("\t")[0] + "\t1\n";
			}

			// Nodeに自分が含まれていない場合は、自分を追加する
			if (!includeMe && loginUser.length() > 0) {
				nodes += loginUser + "\t20\t1\n";
			}

			//pm = PMF.get().getPersistenceManager();

			//query = "select from " + UsertalkCache.class.getName() + " where pageTitle==\'" + pageTitle.replaceAll("\'", "\\\\\'") + "\' && author==\'" + loginUser + "\'";
			//List<UsertalkCache> usertalkCaches = (List<UsertalkCache>)pm.newQuery(query).execute();
			// No cache
			String edges = "";
//			if (usertalkCaches.isEmpty() ||  (!usertalkCaches.isEmpty() && !cacheFlag) ) {
//				if (!usertalkCaches.isEmpty()) {
//					// Clear cached data
//					for(UsertalkCache uc:usertalkCaches) {
//						pm.deletePersistent(uc);
//					}
//				}
				edges = GetUsertalkNetwork.getNetwork(lang, nodes);
//				UsertalkCache usertalkCache = new UsertalkCache(pageTitle,loginUser,edges,(new Date()));
//				PersistenceManager pmWriter = PMF.get().getPersistenceManager();
//				try {
//					pmWriter.makePersistent(usertalkCache);
//				} finally {
//					pmWriter.close();
//				}
//			}
			// Cached and use cache
//			else if (!usertalkCaches.isEmpty() && cacheFlag){
//				for(UsertalkCache uc:usertalkCaches) {
//					edges = uc.getNetwork();
//				}
//			}
//			pm.close();

			if (edges.length() > 0) {
				LOG.info(nodes);
				LOG.info(edges);
				//Optimization op = new Optimization();
				//String location = op.run(nodes, edges, Integer.parseInt(size), Integer.parseInt(size));
				//log.info("Location\n" + location);
				Processing pro = new Processing();
				//code = pro.processingCode(location, edges, loginUser, path, userName_editSize, size, size);
				code = pro.processingCode(nodes, edges, path, canvasSize);
				//log.info("Processing code\n" + code);

				// 多次元尺度構成法の場合
				//sd.setupData(nodes,edges);
				//code = sd.processingCode(sd.scaledown(0.01),edges,loginUser);
			}

			//出力ストリームを取得する
			responseStr += "<script id=\"processing-code\" type=\"application/processing\">" + EOL;
			// ここにProcessingのコードを挿入
			responseStr += code;
			responseStr += "</script>" + EOL;

			responseStr += "<div><canvas width=\"" + canvasSize + "\" height=\"" + canvasSize + "\"></canvas></div>" + EOL;

			responseStr += tableContents;

			out.append(responseStr);
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
			throw new ServletException(e);
		}
		long currentTimeMillisAfterDl = System.currentTimeMillis();
		LOG.info("TIME: " + (currentTimeMillisAfterDl - currentTimeMillis));
		return out.toString();
	}

	private String downloadData(final String pageTitle, final String limit, final String lang,
			String data, final GetRevisions gr) throws ParseException {
		LOG.info("NOT cached");


		// Get # of edits on the pageTitle
		String download = gr.getArticleRevisions(lang,pageTitle,limit);
		LOG.info("Downloaded XML: " + download);
		String[] line = download.split("\n");
		for (String element : line) {
			String[] arr = element.split("\t");
			//arr[0] pageTitle, arr[1] userName, arr[2] timestamp, arr[3] minor, arr[4] size
			String timestamp = arr[2];
			timestamp = timestamp.replaceAll("T", " ");
			timestamp = timestamp.replaceAll("Z", "");
			data += arr[0] + "\t" + arr[1] + "\t" + timestamp + "\t0\t" + arr[4] + "\n";

//			// Storing data
//			ArticleCache articleCache = new ArticleCache(arr[0],arr[1],df.parse(timestamp),Integer.parseInt(arr[4]));
//			PersistenceManager pmWriter = PMF.get().getPersistenceManager();
//			try {
//				pmWriter.makePersistent(articleCache);
//			} finally {
//				pmWriter.close();
//			}
		}
		return data;
	}

	private String readStringParameter(final String paramName, final String defaultValue) {
		if (request.getParameter(paramName) != null) {
			return request.getParameter(paramName);
		}
		return defaultValue;
	}

	private boolean readBooleanParameter(final String paramName, final boolean defaultValue) {
		if (request.getParameter(paramName) != null) {
			return "true".equals(request.getParameter(paramName));
		}
		return defaultValue;
	}
}
