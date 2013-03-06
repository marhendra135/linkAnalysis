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
	ArrayList<GraphObj> listGObjEmail;
	ArrayList<GraphObj> listGObjEmailAddress;
	
	public PopulateGraph(ArrayList<GraphObj> listGObjEmail, ArrayList<GraphObj> listGObjEmailAddress) {
		this.listGObjEmail = listGObjEmail;
		this.listGObjEmailAddress = listGObjEmailAddress;
		//GraphGenerator gg = new GraphGenerator();
		//this.listGObjEmail = gg.generateGraphInputModelforEmail();
		
//		HashMap<String, String> docMap = new HashMap<String, String>();
//  		docMap.put("mId", "mId");
//  		docMap.put("date", "date");
//  		docMap.put("senderEmails", "senderEmails");
//  		docMap.put("senderName", "senderName");
//  		docMap.put("senderStatus", "senderStatus");
//  		docMap.put("subject", "subject");
//  		docMap.put("body", "body");
//  		docMap.put("recEmail", "recEmail");
//  		docMap.put("recName", "recName");
//  		docMap.put("recStatus", "recStatus");
//  		docMap.put("recStatus", "recStatus");
//		DocumentCreator gc = new DocumentCreator(docMap);
//		ArrayList<Email> listEmails = gc.emailGenerator();
		//this.listGObjEmailAddress = gg.generateGraphInputModelforEmailAddress(listEmails);
	}
	
	public Graph<String, String> genEmailGraph(){
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
		System.out.println("genEmailAdress graph :" + listGObjEmailAddress.size());
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