package work;

import java.io.IOException;

import org.apache.lucene.queryparser.classic.ParseException;
import data.Email;
public class LuceneTest {

	private static LuceneSearch lSearch = null;
	
	public static void main(String[] args) throws IOException, ParseException {
		lSearch = new LuceneSearch();
		try {
			lSearch.buildIndex(true);
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("tes standard query 1.1 -> single term");
		 String queryStr = "Person";
//		 lSearch.standardQuery(queryStr);
//		 System.out.println("tes standard query 1.1 -> phrase");
//		 queryStr = "senderName:\"Dickson Stacy\"";
//		 lSearch.standardQuery(queryStr);
		 System.out.println("tes standard query 1.1 -> wildcard");
		 queryStr = "dick*";
		 lSearch.standardQuery(queryStr);
//		 System.out.println("tes standard query 1.1 -> regex");
//		 queryStr = "/[d]ickson/";
//		 lSearch.standardQuery(queryStr);
//		 System.out.println("tes standard query 1.1 -> fuzzy");
//		 queryStr = "dick~";
//		 System.out.println("tes standard query 1.1 -> proximity");
//		 queryStr = "senderName:\"Dickson Stacy\"~3";
//		 lSearch.standardQuery(queryStr);	
//		 System.out.println("tes standard query 1.1 -> wildcard without syn");
//		 queryStr = "senderName:dick*";
//		 lSearch.standardQuery(queryStr);	
//		 
//		 System.out.println("tes standard query 1.1 -> wildcard with syn");
//		 queryStr = "body:submit*";
//		 lSearch.standardQuery(queryStr);	
//		System.out.println("tes standard query 2.1 -> boolean and fielded");
//		queryStr = "body:blackouts";
//		lSearch.standardQuery(queryStr);		 
//		 System.out.println("tes standard query 2.1 -> boolean and fielded");
//		 queryStr = "date:[20010101000000 TO 20011231000000]";
//		 lSearch.standardQuery(queryStr);		 
//		 System.out.println("tes assisted query");
//		Email email = new Email("", "","", "", "daso*",  "", " ", "", "", "",  "blackouts", "");
//		lSearch.assistedQuery(email);
	}
}
