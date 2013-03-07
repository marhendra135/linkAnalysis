package upload;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import data.Email;

public class DocumentCreator {
	/*
	 * This class act as a wrapper for Database connection to populate the Documents/ Emails.
	 * 
	 * */
	//private ArrayList<Document> listDocuments= null;
	private HashMap<String,String> map = null;
	//private ArrayList<Email> listEmails = null;


	public DocumentCreator() {
		super();
	}
	
	public DocumentCreator(HashMap<String, String> map) {
		super();
		this.map = map;
	}

	public ArrayList<Document> documentGenerator(ArrayList<Email> listEmails) {
		/*
		 * This function populate the list of Documents from the database.
		 * This function results list of Documents.
		 * */
		ArrayList<Document> listDocuments = createDocumentList(listEmails, map);

		return listDocuments;
		
	}
	
	public ArrayList<Email> emailGenerator() {
		/*
		 * This function populate the list of Documents from the database.
		 * This function results list of Documents.
		 * */
		ArrayList<Email> listEmails = null;
		DBUploader uploader = new DBUploader();
		try {
			ResultSet rs = uploader.readDataBase();
			listEmails = createEmailList(rs);
			//listDocuments = createDocumentList(listEmails, map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			uploader.closeConnection();	
		}
		
		return listEmails;
		
	}
	


	private ArrayList<Email> createEmailList(ResultSet resultSet) throws SQLException {
		/*
		 * This function populate the list of Documents from the resultset of database query.
		 * This function maps the ResultSet with the Lucene Document class using predefined terms in Map
		 * This function results list of Documents.
		 * */
		ArrayList<Email> listMails= null;
		
		Email email = null;
		System.out.println("Start populating email list from RS :" + Calendar.getInstance().getTime());
		if (resultSet!=null){
			listMails = new ArrayList<Email>();
			String oldMId ="-999";
			String newMId ="-999";
			String recEmails ="";
			String recNames = "";
			String recStatuses = "";
			while (resultSet.next()) {
				newMId = resultSet.getString("mid");
				if (!newMId.equals(oldMId)){
					if (email!=null){
						email.setmId(oldMId);
						email.setRecEmail(recEmails);
						email.setRecName(recNames);
						email.setRecStatus(recStatuses);
						listMails.add(email);
						email = null;
					}
					
					recEmails ="";
					recNames = "";					
					email = new Email();
					Date date= resultSet.getTimestamp("date");
					email.setDate(date);
//					email.setSenderEmails(resultSet.getString("email_id") + " " +
//							resultSet.getString("email2") + " " +
//							resultSet.getString("email3") + " " +
//							resultSet.getString("email_id"));
					email.setSenderEmails(resultSet.getString("sender"));
					email.setSenderName(resultSet.getString("sender_last") + " " +
							resultSet.getString("sender_first"));
					email.setSenderStatus(resultSet.getString("sender_status"));
					recEmails = resultSet.getString("rvalue");
					recNames = resultSet.getString("rec_last") + " " +
							resultSet.getString("rec_first");
					recStatuses = resultSet.getString("rec_status");
					email.setSubject(resultSet.getString("subject"));
					email.setBody(resultSet.getString("body"));
					
					oldMId = newMId;
					//doc.add(new TextField("type", resultSet.getString("rtype"), Field.Store.YES));
				} else {
					recEmails = recEmails + " " + resultSet.getString("rvalue");
					recStatuses = recStatuses + " " + resultSet.getString("rec_status");
					recNames = recNames + " , " + resultSet.getString("rec_last") + " " +
							resultSet.getString("rec_first");
				}
			}
			if (email!=null){
				email.setmId(newMId);
				email.setRecEmail(recEmails);
				email.setRecName(recNames);
				email.setRecStatus(recStatuses);
				listMails.add(email);
				email=null;
			}			
			System.out.println("ArrayList size = "+ listMails.size());
		}
		System.out.println("Done populating email list from RS:" + Calendar.getInstance().getTime());
		return listMails; 
	}
	public ArrayList<Document> createDocumentList(ArrayList<Email> listEmails, HashMap<String, String> map)  {
		/*
		 * This function populate the list of Documents from the resultset of database query.
		 * This function maps the ResultSet with the Lucene Document class using predefined terms in Map
		 * This function results list of Documents.
		 * */
		ArrayList<Document> listDocs= null;
		Document doc = null;
		System.out.println("Start populating document list from emails :" + Calendar.getInstance().getTime());
		if (listEmails!=null){
			listDocs = new ArrayList<Document>();
			Field tField = null;
			float bValDef = (float) (1.0/listEmails.size());
			Iterator<Email> iter = listEmails.iterator();
			StringBuilder sb = new StringBuilder();
			while (iter.hasNext()) {
				Email email = iter.next();
				bValDef= (float) email.getbVal();
				doc = new Document();
				tField = new StringField(map.get("mId"), email.getmId(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField = new TextField(map.get("recEmail"), email.getRecEmail(), Field.Store.NO);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("recName"), email.getRecName(), Field.Store.NO);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("recStatus"), email.getRecStatus(), Field.Store.NO);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField =new StringField(map.get("date"), email.getStrDate(),Field.Store.YES);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderEmails"), email.getSenderEmails(), Field.Store.YES);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderName"), email.getSenderName(), Field.Store.YES);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderStatus"), email.getSenderStatus(), Field.Store.NO);
				tField.setBoost(bValDef);
				doc.add(tField);
				tField = new TextField(map.get("subject"), email.getSubject(), Field.Store.YES);
				tField.setBoost(((float) 1.2) * bValDef);
				doc.add(tField);
				tField = new TextField(map.get("body"), email.getBody(), Field.Store.YES);
				tField.setBoost(((float) 1.2) * bValDef);
				//doc.add(new TextField("type", resultSet.getString("rtype"), Field.Store.YES));
				listDocs.add(doc);
				sb.append(":>" + email.getmId() + "::"+ email.getbVal() + "\n");
			}
			System.out.println("ArrayList size = "+ listDocs.size());
			//writeStringToFile(sb.toString(),"bVALDoc.txt");
		}
		System.out.println("Done populating document list from emails:" + Calendar.getInstance().getTime());
		return listDocs; 
	}
	
	public void setEmailBVALValues(boolean setDefault, ArrayList<Email> listEmails, Map<String, Double> listCalcEmail, Map<String, Double> listCalcEmailAdr){
		double minEmail = 1000;
		double minEmailAdr =1000;
		double bVal = 1.0/listEmails.size(); 
		if (setDefault){
			minEmail = 1;
			minEmailAdr = 1;
		}
		else{
			//get min value
			for (Entry<String, Double> entry : listCalcEmail.entrySet()) {
			    double curVal = entry.getValue().doubleValue();
			    if (minEmail>curVal)
			    	minEmail = curVal;
			}
			for (Entry<String, Double> entry : listCalcEmailAdr.entrySet()) {
			    double curVal = entry.getValue().doubleValue();
			    if (minEmailAdr>curVal)
			    	minEmailAdr = curVal;
			}
		}
		StringBuilder sb = new StringBuilder();
		for (Iterator<Email> iterator = listEmails.iterator(); iterator.hasNext();) {
			Email email = (Email) iterator.next();
			
			double nEmailVal = minEmail;
			double nEmailAdrVal = minEmailAdr;
			if (!setDefault){
				//if (listCalcEmail.get(email.getmId())!=null)
					
				if (listCalcEmail.get(email.getmId())!=null)
					nEmailVal = listCalcEmail.get(email.getmId());
				if (listCalcEmailAdr.get(email.getSenderEmails())!=null)
					nEmailAdrVal = listCalcEmailAdr.get(email.getSenderEmails());	
			}
			email.setbVal(bVal*nEmailAdrVal*nEmailVal);
			sb.append(":>" + email.getmId() + "::"+ email.getbVal() + "\n");
			//System.out.println(":>" + email.getmId() + "::"+ email.getbVal() + "-" + bVal+ "-" +  nEmailVal + "-" + nEmailAdrVal);
		}
		//writeStringToFile(sb.toString(),"bVALEmail.txt");
	}
	private static void writeStringToFile(String strRes, String name){
		BufferedWriter writer = null;
        
        try {
            String text = strRes;
            //File file = new File("EnromEmailTermsCalais.txt");
            writer = new BufferedWriter(new FileWriter(name));
            writer.write(text);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}
}
