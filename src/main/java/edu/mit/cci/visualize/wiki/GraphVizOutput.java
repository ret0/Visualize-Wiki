package edu.mit.cci.visualize.wiki;

import java.util.List;

import edu.mit.cci.visualize.wiki.collector.ArticleContributions;
import edu.mit.cci.visualize.wiki.collector.UsertalkEdge;
import edu.mit.cci.visualize.wiki.fetcher.UsertalkNetworkFetcher;
import edu.mit.cci.visualize.wiki.vizservlet.WikipediaUsertalkVizServlet;

public class GraphVizOutput {

    private static final String NEWLINE = "\n";

    /**
     */
    public static void main(final String[] args) {
        WikipediaUsertalkVizServlet servletz = new WikipediaUsertalkVizServlet("Medieval_cuisine");
        List<ArticleContributions> contributions = servletz.getContributions();
        List<String> userIds = servletz.prepareUserIDs(contributions);

        List<UsertalkEdge> edges = new UsertalkNetworkFetcher("en", userIds).getNetwork();

        StringBuilder output = new StringBuilder();

        output.append("digraph Hello {");
        output.append(" overlap=false;");
        output.append(" size =\"10!\";");
        output.append(" edge [color=gray, arrowtail=none, arrowhead=none];");
        output.append(" node [fontname=Verdana, fontsize=12, shape=circle, fixedsize=true];");
        output.append(NEWLINE);

        for (ArticleContributions articleContribution : contributions) {
            final double graphVizHeight = articleContribution.getGraphicalNodeSize() / 30;
            String color = articleContribution.getExperienceColor();
            output.append("\"" + articleContribution.getUserID() + "\"" + "[height=" + graphVizHeight + ", style=filled, color=\"" + color  + "\"]" + NEWLINE);
        }

        for (UsertalkEdge usertalkEdge : edges) {
            final double graphVizLineWidth = ((double) usertalkEdge.getLineWidth()) / 2;
            output.append("\"" + usertalkEdge.getFrom() + "\"" + "->" + "\"" + usertalkEdge.getTo() + "\"" + "[penwidth=" + graphVizLineWidth + "]" + NEWLINE);
        }
        output.append("}");

        System.out.println(output);
    }

}
