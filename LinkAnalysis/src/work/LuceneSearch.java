package work;

import graph.CalcBackLink;
import graph.CalcPageRank;
import graph.GraphGenerator;
import graph.GraphObj;

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
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import analyze.SynonymAnalyzer;

import data.Email;

import upload.DocumentCreator;

public class LuceneSearch {
	/* This is the main Class that is called by other client(s) to use Lucene Search (by LuceneSearchUI in this project.
	 * Index is built by populating document from DBMS and the index is saved into RAM.
	 * This class is also act as a wrapper for LuceneQuery
	 * */
	private PerFieldAnalyzerWrapper analyzer = null;
	private Directory index = null;
	private IndexWriterConfig config = null;
	private IndexWriter writer = null;
	private HashMap<String,String> docMap = null;
	private ArrayList<Document> listRes = null;
	private LuceneQuery lQuery = null;
	private ArrayList<Email> listEmails = null;
	ArrayList<Document> listDocuments = null;
	private int analysisType = 1;
	private DocumentCreator docCreator = null;
	

	
	public int getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(int analysisType) {
		this.analysisType = analysisType;
	}

	public LuceneSearch(){
		/*Initialize this class is alsi initializing Map properties that defines the document's attributes/ fields
		 * */
		initializeProp();
		docCreator = new DocumentCreator(docMap);
	}
	
	public  void buildIndex(boolean forceReadFromDB) throws IOException, ParseException, java.text.ParseException {
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
		
		switch (analysisType) {
		
		case 1: //backlink
			System.out.println("backlink");
			CalcBackLink cBL = new CalcBackLink();
			if (listGEmail==null){
				listGEmail = gg.generateGraphInputModelforEmail();
				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
			}
			listCalcEmail = cBL.getBackLinks(listGEmail);
			listCalcEmailAdr = cBL.getBackLinks(listGEmailAdr);
			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
			break;
		case 2: //pagerank
			System.out.println("pagerank");
			if (listGEmail==null){
				listGEmail = gg.generateGraphInputModelforEmail();
				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
			}
			CalcPageRank pRG = new CalcPageRank(listGEmail, listGEmailAdr);
			listCalcEmail = pRG.computeForEmail();
			listCalcEmailAdr = pRG.computeForEmailAddress();
			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
			break;
		case 3: //hits
			System.out.println("HITS");
			break;
		default:
			docCreator.setEmailBVALValues(true, listEmails, listCalcEmail, listCalcEmailAdr);
			break;
		}
	    listDocuments = docCreator.documentGenerator(listEmails);
	    
	    analyzer = analyzeFields();
	    index = new RAMDirectory();
	    //index = FSDirectory.open(new File("c:/temp"));
	    config = new IndexWriterConfig(Version.LUCENE_41, analyzer);
	    writer = new IndexWriter(index, config);	  	
	    addDocList(writer, listDocuments);
	    System.out.println("Done building index :" + Calendar.getInstance().getTime());
	    writer.close();
	  }

	  private void addDocList(IndexWriter w, ArrayList<Document> list) throws IOException{
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
	  	


	 private void initializeProp() {
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

	public ArrayList<Document> standardQuery(String queryStr) throws IOException, ParseException{
		/*
		 * This function calls LuceneQuery class to invoke the simple/ standard query
		 * This function results list of Documents
		 * */
		System.out.println("Start query :" + Calendar.getInstance().getTime());
	    lQuery = new LuceneQuery(docMap);
	    listRes = lQuery.simpleQuery(queryStr, analyzer, index);
	    System.out.println("Done query :" + Calendar.getInstance().getTime());
	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    //lQuery.close();

  		return listRes;
	}

	public ArrayList<Document> assistedQuery(Email email) throws IOException, ParseException{
		/*
		 * This function calls LuceneQuery class to invoke the assisted/ advanced query
		 * This function results list of Documents
		 * */
		System.out.println("Start query :" + Calendar.getInstance().getTime());
	    lQuery = new LuceneQuery(docMap);
	    listRes = lQuery.assistedQuery(email, analyzer, index);
	    System.out.println("Done query :" + Calendar.getInstance().getTime());
	    // reader can only be closed when there
	    // is no need to access the documents any more.
	    //lQuery.close();

  		return listRes;
	}
  	public void close() throws IOException{
  		if (lQuery!=null)
  			lQuery.close();
  	}
  	private PerFieldAnalyzerWrapper analyzeFields() throws IOException, java.text.ParseException {
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
