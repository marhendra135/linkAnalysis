package graph;



import data.Email;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import graph.GraphGenerator;
import graph.GraphObj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import upload.DocumentCreator;

public class PopulateGraph{
	/*
	 * This class populates the graph with JUNG's data structure for further analysis
	 * */
	ArrayList<GraphObj> listGObjEmail;
	ArrayList<GraphObj> listGObjEmailAddress;
	
	public PopulateGraph(ArrayList<GraphObj> listGObjEmail, ArrayList<GraphObj> listGObjEmailAddress) {
		this.listGObjEmail = listGObjEmail;
		this.listGObjEmailAddress = listGObjEmailAddress;

	}
	
	public Graph<String, String> genEmailGraph(){
		System.out.println("generate Email graph :" + listGObjEmail.size());
		// create a graph with string type vertex and edge
		Graph<String, String> g1 = new SparseMultigraph<String, String>();
	
		// iterate over the graph object email list to add the email ids as nodes
		for (int i = 0; i < listGObjEmail.size(); i++){
			g1.addVertex(listGObjEmail.get(i).getvIn());
			g1.addVertex(listGObjEmail.get(i).getvOut());
			
		}
		
		// iterate over the graph object email list to add links between in and 
		// out nodes.
		for (int i = 0; i < listGObjEmail.size(); i++){
		
			g1.addEdge("Edge-"+i, listGObjEmail.get(i).getvIn(),
					listGObjEmail.get(i).getvOut());
		}
		
		return g1;
	}
	
	public Graph<String, String> genEmailAddressGraph(){
		// create a graph with string type vertex and edge
		Graph<String, String> g2 = new SparseMultigraph<String, String>();
	
		// iterate over the graph object email list to add the email ids as nodes
		System.out.println("generate EmailAdress graph :" + listGObjEmailAddress.size());
		for (int i = 0; i < listGObjEmailAddress.size(); i++){
			g2.addVertex(listGObjEmailAddress.get(i).getvIn());
			g2.addVertex(listGObjEmailAddress.get(i).getvOut());
			
		}
		
		// iterate over the graph object email list to add links between in and 
		// out nodes.
		for (int i = 0; i < listGObjEmailAddress.size(); i++){
		
			g2.addEdge("Edge-"+i, listGObjEmailAddress.get(i).getvIn(),
					listGObjEmailAddress.get(i).getvOut());
		}
		
		return g2;
	}
}