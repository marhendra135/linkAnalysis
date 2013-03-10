package work;

import graph.CalcBackLink;
import graph.CalcHITS;
import graph.CalcPageRank;
import graph.GraphGenerator;
import graph.GraphObj;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import analyze.SynonymAnalyzer;

import data.Email;

import upload.DocumentCreator;

public class IndexGeneratorTest {
	/* This is the main Class that is called by other client(s) to use Lucene Search (by LuceneSearchUI in this project.
	 * Index is built by populating document from DBMS and the index is saved into RAM.
	 * Each document in index will be weighted using selected link analysis with this equation :
	 * w = [tfxIDF] x link_analysis_weight, where [tfxIDF] is counted by internal Lucene calculation and
	 * link_analysis_weight is resulted by link analysis selected (no analysis, backlink, PageRank, or HITS)
	 * This class is also act as a wrapper for LuceneQuery
	 * */
	private static PerFieldAnalyzerWrapper analyzer = null;
	private static FSDirectory index = null;
	private static IndexWriterConfig config = null;
	private static IndexWriter writer = null;
	private static HashMap<String,String> docMap = null;
	private static ArrayList<Document> listRes = null;
	private static LuceneQuery lQuery = null;
	private static ArrayList<Email> listEmails = null;
	private static ArrayList<Document> listDocuments = null;
	private static int analysisType = 3;
	private static DocumentCreator docCreator = null;
	

	


	public static void main(String[] args) throws IOException, ParseException, java.text.ParseException{
		/*Initialize this class is also initializing Map properties that defines the document's attributes/ fields
		 * */
		initializeProp();
		docCreator = new DocumentCreator(docMap);
		buildIndex(true);
	}
	
	private static  void buildIndex(boolean forceReadFromDB) throws IOException, ParseException, java.text.ParseException {
		/*
		 * Index is built by populating document from DBMS and put into list of Documents by the DocumentCreator class
		 * list of Documents then indexed into RAM using several Analyzers
		 * */
		System.out.println("Start building index :" + Calendar.getInstance().getTime());
		if (forceReadFromDB) {
			listEmails = docCreator.emailGenerator();
		}
		GraphGenerator gg = new GraphGenerator();
		ArrayList<GraphObj> listGEmail = null;
		ArrayList<GraphObj> listGEmailAdr = null;
		Map<String, Double> listCalcEmail = null;
		Map<String, Double> listCalcEmailAdr = null;
		
		/*this "analysis" variable determines which network analysis will be used for the graph document
		* 0 = no link analysis
		* 1 = backlink analysis
		* 2 = PageRank analysis
		* 3 = HITS analysis
		*/
/*
			System.out.println("Back Link");
			CalcBackLink cBL = new CalcBackLink();
			if (listGEmail==null){
				listGEmail = gg.generateGraphInputModelforEmail();
				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
			}
			listCalcEmail = cBL.getBackLinks(listGEmail);
			listCalcEmailAdr = cBL.getBackLinks(listGEmailAdr);
			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
		    listDocuments = docCreator.documentGenerator(listEmails);
		    
		    analyzer = analyzeFields();
		    //index = new RAMDirectory();
		    index = FSDirectory.open(new File("c:/temp/backlink"));
		    config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		    writer = new IndexWriter(index, config);	  	
		    addDocList(writer, listDocuments);
		    System.out.println("Done building index :" + Calendar.getInstance().getTime());
		    writer.close();
			
			//////////////////////////////////////////////////////////////////////////////////
			System.out.println("Page Rank");
			if (listGEmail==null){
				listGEmail = gg.generateGraphInputModelforEmail();
				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
			}
			CalcPageRank pRG = new CalcPageRank(listGEmail, listGEmailAdr);
			listCalcEmail = pRG.computeForEmail();
			listCalcEmailAdr = pRG.computeForEmailAddress();
			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
		    listDocuments = docCreator.documentGenerator(listEmails);
		    
		    analyzer = analyzeFields();
		    //index = new RAMDirectory();
		    index = FSDirectory.open(new File("c:/temp/pagerank"));
		    config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		    writer = new IndexWriter(index, config);	  	
		    addDocList(writer, listDocuments);
		    System.out.println("Done building index :" + Calendar.getInstance().getTime());
		    writer.close();
*/
		    //////////////////////////////////////////////////////////////////////////////////////
			System.out.println("HITS");
			if (listGEmail==null){
				listGEmail = gg.generateGraphInputModelforEmail();
				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
			}
			CalcHITS cH = new CalcHITS(listGEmail, listGEmailAdr);
			listCalcEmail = cH.computeForEmail();
			listCalcEmailAdr = cH.computeForEmailAddress();
			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
		    listDocuments = docCreator.documentGenerator(listEmails);
		    
		    analyzer = analyzeFields();
		    //index = new RAMDirectory();
		    index = FSDirectory.open(new File("c:/temp/hits"));
		    config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		    writer = new IndexWriter(index, config);	  	
		    addDocList(writer, listDocuments);
		    System.out.println("Done building index :" + Calendar.getInstance().getTime());
		    writer.close();
/*		    
		    //////////////////////////////////////////////////////////////////////////////////////////
			System.out.println("No Analysis");
			docCreator.setEmailBVALValues(true, listEmails, listCalcEmail, listCalcEmailAdr);
		    listDocuments = docCreator.documentGenerator(listEmails);
		    
		    analyzer = analyzeFields();
		    //index = new RAMDirectory();
		    index = FSDirectory.open(new File("c:/temp/noanal"));
		    config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
		    writer = new IndexWriter(index, config);	  	
		    addDocList(writer, listDocuments);
		    System.out.println("Done building index :" + Calendar.getInstance().getTime());
		    writer.close();
*/
	  }

	private static void addDocList(IndexWriter w, ArrayList<Document> list) throws IOException{
		  /*
		   * This procedure is adding the Documents within list into the IndexWriter
		   * */
	  		Document doc =null;
	  		if (list!=null){
	  			Iterator<Document> iter = list.iterator();
	  			while (iter.hasNext()) {
	  				doc = iter.next();
	  				w.addDocument(doc);
	  			}
	  			
	  		}
	  	}
	  	


	private static void initializeProp() {
		 /*
		  * This procedure initializes the map that defines the document's fields and atttributes
		  * The map is used when populating from Database, inserting into IndexWriter, and querying
		  * */
  		docMap = new HashMap<String, String>();
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
	}


	private static PerFieldAnalyzerWrapper analyzeFields() throws IOException, java.text.ParseException {
  		HashMap<String,Analyzer> aMap = new HashMap<String, Analyzer>();
  		//aMap.put(docMap.get("mId"),new KeywordAnalyzer());
  		aMap.put(docMap.get("date"), new KeywordAnalyzer());
  		aMap.put(docMap.get("senderName"),new StandardAnalyzer(Version.LUCENE_41));
  		aMap.put(docMap.get("senderEmails"),new StandardAnalyzer(Version.LUCENE_41));
  		aMap.put(docMap.get("senderStatus"),new StandardAnalyzer(Version.LUCENE_41));
  		aMap.put(docMap.get("recName"),new StandardAnalyzer(Version.LUCENE_41));
  		aMap.put(docMap.get("recEmail"),new StandardAnalyzer(Version.LUCENE_41));
  		aMap.put(docMap.get("recStatus"),new StandardAnalyzer(Version.LUCENE_41));
		//aMap.put(docMap.get("subject"), new SynonymAnalyzer());
  		aMap.put(docMap.get("subject"), new StandardAnalyzer(Version.LUCENE_41));
		aMap.put(docMap.get("body"),new StandardAnalyzer(Version.LUCENE_41));
		//aMap.put(docMap.get("body"),new SynonymAnalyzer());

  		PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(Version.LUCENE_41),aMap);
		return analyzer;
  		
  	}
	  	
}
