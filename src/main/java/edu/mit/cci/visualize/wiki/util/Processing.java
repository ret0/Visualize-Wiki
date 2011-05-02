package edu.mit.cci.visualize.wiki.util;

import java.io.BufferedReader;
import java.io.FileReader;

public class Processing {

	public String processingCode(final String nodes,final String edges, final String path, final String size) {

		String code = "";
		String eol = "\n";
		String firstName = "";
		String nodeCode = "";
		String[] nodeArr = nodes.split("\n");
		for (String node:nodeArr) {
			//engine.addParticle(new Particle("Remco", random(0, canvasSize), random(0, canvasSize), 30, 0, 0, 0x80FF0000));
			String name = node.split("\t")[0];
			firstName = name;

			String[] colors = {"0x800000FF", "0x80FF0000", "0x8000FF00"};

			double nodeSize = Double.parseDouble(node.split("\t")[1]);
			nodeSize = Math.log10(nodeSize) * 20;
			if (nodeSize < 10) {
				nodeSize = 10;
			}
			int numOfArticles = Integer.parseInt(node.split("\t")[2]);
			String color = "";
			if (numOfArticles < 4) {
				color = colors[numOfArticles-1];
			} else {
				color = "0x80000000";
			}

			nodeCode += "engine.addParticle(new Particle(\"" + name + "\", random(0, " + size + "), random(0, " + size + "), " + nodeSize + ", 0, 0, " + color + "));" + eol;
		}

		String edgeCode = "";
		String[] edgeArr = edges.split("\n");
		for (String edge:edgeArr) {
			//engine.connectParticles("Remco", "Joris", 2);
			String name1 = edge.split("\t")[0];
			String name2 = edge.split("\t")[1];
			double thick = Double.parseDouble(edge.split("\t")[2]);
			thick = Math.log10(thick) * 2;
			edgeCode += "engine.connectParticles(\"" + name1 + "\", \"" + name2 + "\", " + thick + ");" + eol;

		}
		try {
			// Read skeleton code
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.startsWith("#SIZE")) {
					code += "int canvasSize = " + size + ";" + eol;
				} else if (line.startsWith("#NODES")) {
					code += nodeCode;
				} else if (line.startsWith("#EDGES")) {
					code += edgeCode;
				} else if (line.startsWith("#PINNED")) {
					code += "engine.findParticle(\"" + firstName + "\").pin(" + size + "/2, " + size + "/2);" + eol;
				} else {
					code += line + eol;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
}