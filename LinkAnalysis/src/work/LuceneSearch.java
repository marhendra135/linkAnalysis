package work;

import graph.CalcBackLink;
import graph.CalcHITS;
import graph.CalcPageRank;
import graph.GraphGenerator;
import graph.GraphObj;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
import data.EmailComparator;

import upload.DocumentCreator;

public class LuceneSearch {
	/* This is the main Class that is called by other client(s) to use Lucene Search (by LuceneSearchUI in this project.
	 * Index is built by populating document from DBMS and the index is saved into RAM.
	 * Each document in index will be weighted using selected link analysis with this equation :
	 * w = [tfxIDF] x link_analysis_weight, where [tfxIDF] is counted by internal Lucene calculation and
	 * link_analysis_weight is resulted by link analysis selected (no analysis, backlink, PageRank, or HITS)
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
	private int analysisType = 3;
	private DocumentCreator docCreator = null;
	private GraphGenerator gg = null;
	private ArrayList<GraphObj> listGEmail = null;
	private ArrayList<GraphObj> listGEmailAdr = null;
	

	
	public int getAnalysisType() {
		return analysisType;
	}

	public void setAnalysisType(int analysisType) {
		this.analysisType = analysisType;
	}

	public LuceneSearch(){
		/*Initialize this class is also initializing Map properties that defines the document's attributes/ fields
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
		gg = new GraphGenerator();
		
		Map<String, Double> listCalcEmail = null;
		Map<String, Double> listCalcEmailAdr = null;
		//listGEmail = null;
		
		/*this "analysis" variable determines which network analysis will be used for the graph document
		* 0 = no link analysis
		* 1 = backlink analysis
		* 2 = PageRank analysis
		* 3 = HITS analysis
		*/
		listGEmail=null;
		switch (analysisType) {
		case 1: //backlink
			System.out.println("Back Link");
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
			System.out.println("Page Rank");
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
//			if (listGEmail==null){
//				listGEmail = gg.generateGraphInputModelforEmail();
//				listGEmailAdr = gg.generateGraphInputModelforEmailAddress(listEmails);
//			}
//			CalcHITS cH = new CalcHITS(listGEmail, listGEmailAdr);
//			listCalcEmail = cH.computeForEmail();
//			listCalcEmailAdr = cH.computeForEmailAddress();
//			docCreator.setEmailBVALValues(false, listEmails, listCalcEmail, listCalcEmailAdr);
//			break;
			docCreator.setEmailBVALValues(true, listEmails, listCalcEmail, listCalcEmailAdr);
			break;
		default:
			System.out.println("No Analysis");
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
	    
	    if (analysisType==3){ // HITS
	    	lQuery.setMaxResult(listEmails.size());
	    	listRes = lQuery.simpleQuery(queryStr, analyzer, index);
	    	listRes = doReorderResultUsingHITS(listRes);
	    }else{
	    	listRes = lQuery.simpleQuery(queryStr, analyzer, index);
	    }
	    
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
	    
	    if (analysisType==3){ // HITS
	    	lQuery.setMaxResult(listEmails.size());
	    	listRes = lQuery.assistedQuery(email, analyzer, index);
	    	listRes = doReorderResultUsingHITS(listRes);
	    }
	    else{
	    	listRes = lQuery.assistedQuery(email, analyzer, index);
	    }
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
  		aMap.put(docMap.get("mId"),new KeywordAnalyzer());
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

  	private ArrayList<Document> doReorderResultUsingHITS(ArrayList<Document> listDocs) throws IOException{
		Map<String, Double> listCalcEmail = null;
		Map<String, Double> listCalcEmailAdr = null;
		ArrayList<GraphObj> listGEmailTemp = new ArrayList<GraphObj>();
	
		ArrayList<GraphObj> listGEmailAdrTemp = null;
		ArrayList<Email> listEmailTemp = new ArrayList<Email>();
		System.out.println("0 : " + listDocs.size());
		if (listGEmail==null){
			listGEmail = gg.generateGraphInputModelforEmail();
		}
  		//get the RootQ + Expansion 1x based on Query Result (listDocs)
  		Document doc = null;
  		ArrayList<GraphObj> listToBeRem = new ArrayList<GraphObj>();
  		for (Iterator<GraphObj> iterator = listGEmail.iterator(); iterator.hasNext();) {
			GraphObj gObj =  iterator.next();
			boolean found = false;
			Iterator<Document> itr = listDocs.iterator();
			while (!found && itr.hasNext()){
				doc = itr.next();
				if (doc.get("mId").equals(gObj.getvOut())){
					found=true;
					listGEmailTemp.add(gObj);
				}
			}

		}
  		//System.out.println("4 : " + listGEmailTemp.size());
  		/////////////////////////////
  		Email email = null;
  		Set<String> set = new HashSet<String>();
  		for (Iterator<GraphObj> iterator = listGEmailTemp.iterator(); iterator.hasNext();) {
			GraphObj gObj =  iterator.next();
			boolean found = false;
			Iterator<Email> itr = listEmails.iterator();
			while (!found && itr.hasNext()){
				email = itr.next();
				if (email.getmId().equals(gObj.getvIn()) || email.getmId().equals(gObj.getvOut())){
					found = true;
					//System.out.println("Found !");
					if (set.add(email.getmId())){
						//System.out.println("Added " + email.getmId());	
						listEmailTemp.add(email);
					}
				}
			}
			
		}
  		//System.out.println("5 : " + listEmailTemp.size());
  		for (Iterator<Document> iterator = listDocs.iterator(); iterator.hasNext();) {
			doc =  iterator.next();
			boolean found = false;
			Iterator<Email> itr = listEmails.iterator();
			while (!found && itr.hasNext()){
				email = itr.next();
				if (email.getmId().equals(doc.get("mId"))){
					found = true;
					if (set.add(email.getmId()))
						listEmailTemp.add(email);
				}
			}
			
		}
  		//System.out.println("6 : " + listEmailTemp.size());
  		listGEmailAdrTemp = gg.generateGraphInputModelforEmailAddress(listEmailTemp);
  		
  		//System.out.println("7 : " + listGEmailAdrTemp.size());
		CalcHITS cH = new CalcHITS(listGEmailTemp, listGEmailAdrTemp);
		listCalcEmail = cH.computeForEmail();
		listCalcEmailAdr = cH.computeForEmailAddress();

		docCreator.setEmailBVALValues(false, listEmailTemp, listCalcEmail, listCalcEmailAdr);
		Collections.sort(listEmailTemp, new EmailComparator());
		
		//sort the document list
		doc = null;
		set = new HashSet<String>();
		ArrayList<Document> listDocsRes = docCreator.createDocumentList(listEmailTemp, docMap);
		ArrayList<Document> listOrderedDocs = new ArrayList<Document>();
		for (Iterator<Document> iterator = listDocsRes.iterator(); iterator.hasNext();) {
			doc =  iterator.next();
			if (listOrderedDocs.size()==0)
				listOrderedDocs.add(doc);
			else
				listOrderedDocs.add(0,doc);
		}
		System.out.println("10 : " + listOrderedDocs.size());
		System.out.println("Found " + listOrderedDocs.size() + " hits.");
		for (int i = 0; i < listOrderedDocs.size(); i++) {
			Document d = listOrderedDocs.get(i);
			System.out.println((i + 1) + ". " + d.get("senderName") + "\t" + d.get("subject") + "\t" + d.get("mId") + "\t" + listEmailTemp.get(listEmailTemp.size()-i-1).getbVal());
		}
		return listOrderedDocs;
  	}
}
