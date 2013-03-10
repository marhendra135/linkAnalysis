package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.Graph;

public class CalcPageRank{
	/*
	 * This class is used to calculate the weight of each Email and Email Address using Page Rank Analysis. 
	 * */
	private int maxIterations;
	private double alpha;
	private PopulateGraph g;


	public CalcPageRank(ArrayList<GraphObj> listGObjEmail, ArrayList<GraphObj> listGObjEmailAddress) {
		this.maxIterations = 10;
		this.alpha = 0.15;
		g = new PopulateGraph(listGObjEmail, listGObjEmailAddress);
	}

	public Map<String, Double> computeForEmail() {
		/*
		 * This function is used to calculate the weight of each Email using PageRank Analysis
		 * This function results HashMap<String,Double>
		 * *
		 */

		Graph<String, String> ge = g.genEmailGraph();

		long start = System.currentTimeMillis();

		PageRank<String, String> pRankEG = new PageRank<String, String>(ge, alpha);
		pRankEG.setMaxIterations(this.maxIterations);
		pRankEG.evaluate();

		System.out.println("PageRank for Email graph computed in " + (System.currentTimeMillis()-start) + " ms"); 

		Map<String, Double> result = new HashMap<String, Double>();
		for (String v : ge.getVertices()) {
			result.put(v, pRankEG.getVertexScore(v));
		}
		return result;
	}
	public Map<String, Double> computeForEmailAddress() {
		/*
		 * This function is used to calculate the weight of each Email Address using PageRank Analysis
		 * This function results HashMap<String,Double>
		 * *
		 */
		Graph<String, String> gea = g.genEmailAddressGraph();

		long start = System.currentTimeMillis();

		PageRank<String, String> pRankEAG = new PageRank<String, String>(gea, alpha);
		pRankEAG.setMaxIterations(this.maxIterations);
		pRankEAG.evaluate();

		System.out.println("PageRank for Email Address graph computed in " + (System.currentTimeMillis()-start) + " ms"); 

		Map<String, Double> result = new HashMap<String, Double>();
		for (String v : gea.getVertices()) {
			result.put(v, pRankEAG.getVertexScore(v));
		}
		return result;
	}
}