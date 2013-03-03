package work;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import data.Email;

public class LuceneQuery {
	/*
	 * This class perform the query activities by searching into the index
	 * This class normalize the query :
	 * a. Standard/ Simple Query
	 *    - remove all "*" and "?" prior to any letter/ number in a query term
	 *    - if there is no field defined, search will be done to all fields (OR)
	 * b. Assisted/ Advanced Query
	 *    - remove all "*" and "?" prior to any letter/ number in a query term
	 * Max result for the query is limited to 100 for testing purpose
	 * */
    private IndexReader reader = null;
    private IndexSearcher searcher = null;
    private int maxResult = 100;
    private TopScoreDocCollector collector = null;
    private HashMap<String,String> map = null;
    private Query q=null;
    
    
	public LuceneQuery(HashMap<String, String> map) {
		super();
		this.map = map;
	}

	private ArrayList<Document> query(String queryStr, Analyzer analyzer, Directory index) throws ParseException, IOException{
		/*This function is the main query activities.
		 *This function results list of Documents. 
		 * */
		ArrayList<Document> listRes = null;
	    if (!queryStr.equals("")){
			q = new QueryParser(Version.LUCENE_41, "body", analyzer).parse(queryStr);
		    
		    reader = DirectoryReader.open(index);
		    searcher = new IndexSearcher(reader);
		    collector = TopScoreDocCollector.create(maxResult, true);
		    searcher.search(q, collector);
		    if (searcher!=null){
			    ScoreDoc[] hits = collector.topDocs().scoreDocs;
			    System.out.println("Found " + hits.length + " hits.");
			    listRes = new ArrayList<Document>();
			    for(int i=0;i<hits.length;++i) {
			      int docId = hits[i].doc;
			      Document d = searcher.doc(docId);
			      System.out.println((i + 1) + ". " + d.get("senderName") + "\t" + d.get("subject") + "\t" + d.get("date"));
			      listRes.add(d);
			     
			    }
		    }
	    }
		return listRes;
	}
	public ArrayList<Document>  assistedQuery(Email email, Analyzer analyzer, Directory index) throws IOException, ParseException {
		/*This function wraps the query function and do normalization for assisted/ advanced query.
		 *This function results list of Documents. 
		 * */
		System.out.println("Query = " + email.toString());
		String queryStr = assistedQueryConstructor(email);
	    System.out.println("Query = " + queryStr);		
			
	    ArrayList<Document> listRes = query(queryStr, analyzer, index);

	    return listRes;
	}
	    
	public ArrayList<Document> simpleQuery(String queryStr, Analyzer analyzer, Directory index) throws IOException, ParseException {
		/*This function wraps the query function and do normalization for standard/ simple query.
		 *This function results list of Documents. 
		 * */
		System.out.println("Query = " + queryStr);
		ArrayList<Document> listRes = null;
		if (!queryStr.equals("")){
			queryStr = simpleQueryConstructor(queryStr);
			listRes = query(queryStr, analyzer, index);
		}	
	    return listRes;
	}
	

	public void close() throws IOException{
		reader.close();
	};
	
	private String simpleQueryConstructor(String query){
		/*
		 * This function normalize the query for simple/ standard query
		 * */
		while (!query.equals("") && (query.substring(0, 1).equals("*") || query.substring(0, 1).equals("?"))) {
			query = query.substring(1);
		}
		

		String queryOut = "";
		boolean found=false;
		Iterator<Entry<String, String>> iter = map.entrySet().iterator();
		
		while (iter.hasNext() && !found){
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter.next(); 
			String value = (String) entry.getValue();
			if (!queryOut.equals(""))
				queryOut = queryOut + " OR " + value +":" + query;
			else
				queryOut = queryOut + value +":" + query;	
			value= value + ":";
			found = query.toLowerCase().contains(value.toLowerCase());
		}
		
		
		if (!found)
			return queryOut;
		else
			return query;
	}
	
	private String assistedQueryConstructor(Email email){
		/*
		 * This function normalize the query for advanced/ assisted query
		 * */
		//build query
		String queryAss = "";
		if (email!=null){
			if((!email.getDateFrom().equals("")) && (!email.getDateTo().equals(""))){
				queryAss = queryAss + map.get("date") +":[" + email.getDateFrom() + " TO " + email.getDateTo() + "]";
			}
			if(!email.getSenderName().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + "((" +map.get("senderName") +":" + email.getSenderName() +
				") OR (" +map.get("senderEmails") +":" + email.getSenderName() + "))";
			}
			if(!email.getSenderStatus().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + map.get("senderStatus") +":" + email.getSenderStatus();
			}

			if(!email.getRecName().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + "((" + map.get("recName") +":" + email.getRecName() +
						") OR (" +map.get("recEmail") +":" + email.getRecName() + "))";
			}
			if(!email.getRecStatus().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + map.get("recStatus") +":" + email.getRecStatus();
			}
			if(!email.getSubject().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + map.get("subject") +":" + email.getSubject();
			}
			if(!email.getBody().equals("")) {
				if(!queryAss.equals("")) queryAss += " AND ";
				queryAss = queryAss + map.get("body") +":" + email.getBody();
			}
		}
		return queryAss;
	}
}
