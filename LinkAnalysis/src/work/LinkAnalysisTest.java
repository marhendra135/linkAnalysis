package work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import data.Email;

import upload.DocumentCreator;

import graph.GraphGenerator;
import graph.GraphObj;

public class LinkAnalysisTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		GraphGenerator gg = new GraphGenerator();
		ArrayList<GraphObj> listGObjEmail = gg.generateGraphInputModelforEmail();
		System.out.println("Done Email");
		
		HashMap<String, String> docMap = new HashMap<String, String>();
  		docMap.put("mId", "mId");
  		docMap.put("date", "date");
  		docMap.put("senderEmails", "senderEmails");
  		docMap.put("senderName", "senderName");
  		docMap.put("senderStatus", "senderStatus");
  		docMap.put("subject", "subject");
  		docMap.put("body", "body");
  		docMap.put("recEmail", "recEmail");
  		docMap.put("recName", "recName");
  		docMap.put("recStatus", "recStatus");
  		docMap.put("recStatus", "recStatus");
		DocumentCreator gc = new DocumentCreator(docMap);
		ArrayList<Email> listEmails = gc.emailGenerator();
		ArrayList<GraphObj> listGObjEmailAddress = gg.generateGraphInputModelforEmailAddress(listEmails);
		System.out.println("Done Email Address");
	}

}
