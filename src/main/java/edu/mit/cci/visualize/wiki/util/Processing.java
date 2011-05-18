package edu.mit.cci.visualize.wiki.util;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import edu.mit.cci.visualize.wiki.collector.ArticleContributions;

public class Processing {

    /**
     * JS Syntax for an edge:
     * engine.addParticle(new Particle("Remco", random(0, canvasSize), random(0, canvasSize), 30, 0, 0, 0x80FF0000));
     */
    public Map<String, Double> getNodeCode(final List<ArticleContributions> nodes) {
        Map<String, Double> nodeSizes = Maps.newHashMap();
		for (ArticleContributions node : nodes) {
			double nodeSize = Math.log10(node.getNumberOfChanges()) * 20;
			if (nodeSize < 10) {
				nodeSize = 10;
			}
			nodeSizes.put(node.getUserID(), nodeSize);
		}
        return nodeSizes;
    }
}
