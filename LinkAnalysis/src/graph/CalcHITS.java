package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uci.ics.jung.algorithms.scoring.HITS;
import edu.uci.ics.jung.graph.Graph;

public class CalcHITS{
	private double alpha;
	/*
	 * This class is used to calculate the weight of each Email and Email Address using HITS Analysis. 
	 * */
	private PopulateGraph g;

	public CalcHITS(ArrayList<GraphObj> listGObjEmail, ArrayList<GraphObj> listGObjEmailAddress) {
		this.alpha = 0.15;
		g = new PopulateGraph(listGObjEmail, listGObjEmailAddress);
	}

	/* Compute HITS for Email Graph and return a map with vertex of the graph as key mapped to
	   a list of hub and authority values.
	*/
	public Map<String, Double> computeForEmail() {
		System.out.println("Enter compute email : "); 
		Graph<String, String> ge = g.genEmailGraph();

		long start = System.currentTimeMillis();

		HITS<String,String> hitsRankEG = new HITS<String,String>(ge,alpha);
		hitsRankEG.evaluate();

		System.out.println("HITS for Email graph computed in " + (System.currentTimeMillis()-start) + " ms"); 

		Map<String, Double> result = new HashMap<String, Double>();
		for (String v : ge.getVertices()) {
//			List<Double> vals = new ArrayList<Double>();
//
//			HITS.Scores score = hitsRankEG.getVertexScore(v);
//			vals.add(score.hub);
//			vals.add(score.authority);
//			result.put(v, vals);
			HITS.Scores score = hitsRankEG.getVertexScore(v);
			double val = score.hub * score.authority;
			//double val = score.authority;
			//double val = Math.sqrt((score.hub*score.hub)+(score.authority*score.authority));
			result.put(v, new Double(val));
			
		}
		return result;
	}
	/* Compute HITS for Email Address Graph and return a map with vertex of the graph as key mapped to
	   a list of hub and authority values.
	*/
	public Map<String, Double> computeForEmailAddress() {
		System.out.println("Enter compute email addresses: ");
		Graph<String, String> gea = g.genEmailAddressGraph();
		long start = System.currentTimeMillis();

		HITS<String,String> hitsRankEGA = new HITS<String,String>(gea,alpha);
		hitsRankEGA.evaluate();

		System.out.println("HITS for Email Address graph computed in " + (System.currentTimeMillis()-start) + " ms"); 

		Map<String, Double> result = new HashMap<String, Double>();
		for (String v : gea.getVertices()) {
//			List<Double> vals = new ArrayList<Double>();
//
//			HITS.Scores score = hitsRankEGA.getVertexScore(v);
//			vals.add(score.hub);
//			vals.add(score.authority);
//			result.put(v, vals);
			HITS.Scores score = hitsRankEGA.getVertexScore(v);
			double val = score.hub * score.authority;
			//double val = score.authority;
			result.put(v, new Double(val));
		}
		return result;
	}
}