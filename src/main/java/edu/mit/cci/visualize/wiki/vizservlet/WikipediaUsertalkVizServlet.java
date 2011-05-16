package edu.mit.cci.visualize.wiki.vizservlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import edu.mit.cci.visualize.wiki.collector.GetRevisions;
import edu.mit.cci.visualize.wiki.collector.GetUsertalkNetwork;
import edu.mit.cci.visualize.wiki.collector.Revisions;
import edu.mit.cci.visualize.wiki.util.MapSorter;
import edu.mit.cci.visualize.wiki.util.Processing;

public class WikipediaUsertalkVizServlet {

    private static final Logger LOG = Logger.getLogger(WikipediaUsertalkVizServlet.class.getName());
    private static final String EOL = "\n";

    private final ServletContext context;
    private final HttpServletRequest request;

    public WikipediaUsertalkVizServlet(final HttpServletRequest request, final ServletContext context) {
        this.request = request;
        this.context = context;
    }

    public String magicMethod() throws IOException, ServletException, NumberFormatException,
            ParseException {
        long currentTimeMillis = System.currentTimeMillis();
        String responseStr = "";
        String pageTitle = readStringParameter("name", ""); // WikiPedia article title

        if (pageTitle.isEmpty()) {
            String errorResp = "";
            errorResp += "Please set arguments.<br>" + EOL;
            errorResp += "name: Page_name of a Wikipedia article." + EOL;
            return errorResp;
        }

        // # of nodes
        String nodeLimit = readStringParameter("nodeLimit", "10");
        String canvasSize = readStringParameter("size", "300");

        // Language
        String lang = readStringParameter("lang", "en");

        Revisions revisionData = new GetRevisions().getArticleRevisions(lang, pageTitle);

        // Sort data, with second parameter: getting Top N editors
        List<String> editRanking = new MapSorter().generateTopAuthorRanking(revisionData, Integer.parseInt(nodeLimit));

        String nodes = "";
        for (int i = 0; i < editRanking.size(); i++) {
            // Name \t # of edits \t # of edit articles
            nodes += editRanking.get(i).split("\t")[1] + "\t" + editRanking.get(i).split("\t")[0] + "\t1\n";
        }

        responseStr += writeProcessingCode(canvasSize, lang, nodes);
        responseStr += writeAuthorsTable(editRanking);
        long currentTimeMillisAfterDl = System.currentTimeMillis();
        LOG.info("TIME: " + (currentTimeMillisAfterDl - currentTimeMillis));
        return responseStr;
    }

    private String prepareProcessingCode(final String canvasSize,
                                         final String lang,
                                         final String nodes) {
        String edges = new GetUsertalkNetwork().getNetwork(lang, nodes);
        if (edges.length() > 0) {
            LOG.info(nodes);
            LOG.info(edges);
            Processing pro = new Processing();
            String path = context.getRealPath("/skelton/skelton_spring.js");
            return pro.processingCode(nodes, edges, path, canvasSize);
        }
        return "";
    }

    private String writeProcessingCode(final String canvasSize,
                                       final String lang,
                                       final String nodes) {
        String responseStr = "";
        responseStr += "<script id=\"processing-code\" type=\"application/processing\">" + EOL;
        responseStr += prepareProcessingCode(canvasSize, lang, nodes);
        responseStr += "</script>" + EOL;
        responseStr += "<div><canvas width=\"" + canvasSize + "\" height=\"" + canvasSize
                + "\"></canvas></div>" + EOL;
        return responseStr;
    }

    private String writeAuthorsTable(final List<String> editRanking) {
        String tableContents = "";
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
            tableContents += "<tr>" + EOL;
            tableContents += "<td>" + name + "</td>" + EOL;
            tableContents += "<td>" + num + "</td>" + EOL;
            tableContents += "</tr>" + EOL;
        }

        tableContents += "</tbody>" + EOL;
        tableContents += "</table>" + EOL;
        tableContents += "<p id =\"map\"></p>" + EOL;
        return tableContents;
    }

    private String readStringParameter(final String paramName,
                                       final String defaultValue) {
        if (request.getParameter(paramName) != null) {
            return request.getParameter(paramName);
        }
        return defaultValue;
    }

}
