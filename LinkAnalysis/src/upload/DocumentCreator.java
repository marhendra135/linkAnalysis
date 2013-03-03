package upload;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

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
	private ArrayList<Document> listDocuments= null;
	private HashMap<String,String> map = null;
	private ArrayList<Email> listEmails = null;

	public DocumentCreator() {
		super();
	}
	
	public DocumentCreator(HashMap<String, String> map) {
		super();
		this.map = map;
	}

	public ArrayList<Document> documentGenerator() {
		/*
		 * This function populate the list of Documents from the database.
		 * This function results list of Documents.
		 * */
		DBUploader uploader = new DBUploader();
		try {
			ResultSet rs = uploader.readDataBase();
			listEmails = createEmailList(rs);
			listDocuments = createDocumentList(listEmails, map);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			uploader.closeConnection();	
		}
		
		return listDocuments;
		
	}
	
	public ArrayList<Document> getListDocuments() {
		return listDocuments;
	}

	public ArrayList<Email> getListEmails() {
		return listEmails;
	}

	public ArrayList<Email> createEmailList(ResultSet resultSet) throws SQLException {
		/*
		 * This function populate the list of Documents from the resultset of database query.
		 * This function maps the ResultSet with the Lucene Document class using predefined terms in Map
		 * This function results list of Documents.
		 * */
		ArrayList<Email> listEmails= null;
		
		Email email = null;
		System.out.println("Start populating email list from RS :" + Calendar.getInstance().getTime());
		if (resultSet!=null){
			listEmails = new ArrayList<Email>();
			String oldMId ="-999";
			String newMId ="-999";
			String recEmails ="";
			String recNames = "";
			String recStatuses = "";
			while (resultSet.next()) {
				newMId = resultSet.getString("mid");
				if (!newMId.equals(oldMId)){
					if (email!=null){
						email.setmId(newMId);
						email.setRecEmail(recEmails);
						email.setRecName(recNames);
						email.setRecStatus(recStatuses);
						listEmails.add(email);
						email = null;
					}
					
					recEmails ="";
					recNames = "";					
					email = new Email();
					Date date= resultSet.getTimestamp("date");
					email.setDate(date);
					email.setSenderEmails(resultSet.getString("email_id") + " " +
							resultSet.getString("email2") + " " +
							resultSet.getString("email3") + " " +
							resultSet.getString("email_id"));
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
					recEmails = recStatuses + " " + resultSet.getString("rec_status");
					recNames = recNames + " , " + resultSet.getString("rec_last") + " " +
							resultSet.getString("rec_first");
				}
			}
			if (email!=null){
				email.setmId(newMId);
				email.setRecEmail(recEmails);
				email.setRecName(recNames);
				email.setRecStatus(recStatuses);
				listEmails.add(email);
				email=null;
			}			
			System.out.println("ArrayList size = "+ listEmails.size());
		}
		System.out.println("Done populating email list from RS:" + Calendar.getInstance().getTime());
		return listEmails; 
	}
	public ArrayList<Document> createDocumentList(ArrayList<Email> listEmails, HashMap<String, String> map) throws SQLException {
		/*
		 * This function populate the list of Documents from the resultset of database query.
		 * This function maps the ResultSet with the Lucene Document class using predefined terms in Map
		 * This function results list of Documents.
		 * */
		ArrayList<Document> listDocuments= null;
		Document doc = null;
		System.out.println("Start populating document list from emails :" + Calendar.getInstance().getTime());
		if (listEmails!=null){
			listDocuments = new ArrayList<Document>();
			Field tField = null;
			float bValDef = 1/listEmails.size();
			Iterator<Email> iter = listEmails.iterator();
			while (iter.hasNext()) {
				Email email = iter.next();
				email.setbVal(bValDef);
				doc = new Document();
				tField = new StringField(map.get("mId"), email.getmId(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField = new TextField(map.get("recEmail"), email.getRecEmail(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("recName"), email.getRecName(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("recStatus"), email.getRecStatus(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new StringField(map.get("date"), email.getStrDate(),Field.Store.YES);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderEmails"), email.getSenderEmails(), Field.Store.YES);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderName"), email.getSenderName(), Field.Store.YES);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField =new TextField(map.get("senderStatus"), email.getSenderStatus(), Field.Store.NO);
				//tField.setBoost(bValDef);
				doc.add(tField);
				tField = new TextField(map.get("subject"), email.getSubject(), Field.Store.YES);
				//tField.setBoost(((float) 1.2) * bValDef);
				doc.add(tField);
				tField = new TextField(map.get("body"), email.getBody(), Field.Store.YES);
				//tField.setBoost(((float) 1.2) * bValDef);
				//doc.add(new TextField("type", resultSet.getString("rtype"), Field.Store.YES));
				listDocuments.add(doc);
			}
			System.out.println("ArrayList size = "+ listDocuments.size());
		}
		System.out.println("Done populating document list from emails:" + Calendar.getInstance().getTime());
		return listDocuments; 
	}
}
