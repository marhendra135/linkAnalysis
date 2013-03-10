package graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class CalcBackLink {
	/*
	 * This class is used to calculate the weight of each Email and Email Address using backlink Analysis. 
	 * */
	public CalcBackLink(){
		super();
	}
	public HashMap<String,Double> getBackLinks(ArrayList<GraphObj> listGObj){
		/*
		 * This function is used to calculate the weight of each Email and Email Address using backlink Analysis
		 * This function results HashMap<String,Double>
		 * *
		 */
		long start = System.currentTimeMillis();
		HashMap<String,Double> mapRes = null;
		if (listGObj!=null){
			mapRes = new HashMap<String,Double>();
			double i = 0;
			for (Iterator<GraphObj> iterator = listGObj.iterator(); iterator.hasNext();) {
				GraphObj graphObj = (GraphObj) iterator.next();
				if (mapRes.get(graphObj.getvIn())==null){ // new
					i = 1;
					mapRes.put(graphObj.getvIn(), new Double(i));
				}
				else{
					i = (mapRes.get(graphObj.getvIn())).doubleValue() + 1;
					mapRes.remove(graphObj.getvIn());
					mapRes.put(graphObj.getvIn(), new Double(i));
				}
			}
		}
		System.out.println("BackLink for the graph computed in " + (System.currentTimeMillis()-start) + " ms");
		return mapRes;
	}

}
