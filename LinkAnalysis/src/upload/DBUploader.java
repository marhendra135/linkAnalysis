package upload;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Properties;



public class DBUploader {
	/*
	 * This class main function is to populate data from MySQL Database
	 * Properties for connection are defined within file "prop/jdbcconn.properties" 
	 * */
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private String QUERY_STR = 
			"select e.lastname as sender_last, e.firstname as sender_first, ifnull(e.status,\"-\") as sender_status, " +
			"e.email_id, e.email2, e.email3, e.email4, m.mid, m.sender, m.date, m.subject, m.body, "+
			"r2.rvalue, r2.rec_last, r2.rec_first, ifnull(r2.status,\"-\") as rec_status, " +
			"r2.rtype from employeelist e, message m,  " +
			"(select e.lastname as rec_last, e.firstname as rec_first, e.status, r.* from " +
			"employeelist e, recipientinfo r	where " +
			"(r.rvalue = e.email_id or r.rvalue = e.email2 or r.rvalue = e.email3 or " +
			"r.rvalue = e.email4)) r2 " +
			"where (m.sender = e.email_id or m.sender = e.email2 or " +
			"m.sender = e.email3 or m.sender = e.email4) and m.mid=r2.mid " +
			"order by m.mid asc, m.date asc, r2.rvalue asc"; 
		  
	
	public ResultSet readDataBase() throws Exception {
		  /*
		   * This function prepare and execute the query to retrieve the Enron email documents.
		   * This function results the ResultSet
		   * */
	    try {
	    	System.out.println("Start populating from database :" + Calendar.getInstance().getTime());
	    	String path = getClass().getProtectionDomain().getCodeSource(). 
	    			getLocation().toString().substring(6);
	    	path = path.substring(0, path.length()-4);
	    	
	    	System.out.println("Path = " + path);
	    	Properties props = new Properties();
	    	
	        FileInputStream fis = new FileInputStream(path + "prop/jdbcconn.properties");
	        props.load(fis);
	    	
	    	String url = props.getProperty("jdbc.url");
	        String user = props.getProperty("jdbc.username");
	        String password = props.getProperty("jdbc.password");
	   
	        fis.close();
	        
	        Class.forName("com.mysql.jdbc.Driver");
	        connect = DriverManager.getConnection(url, user, password);

	        statement = connect.createStatement();

	        resultSet = statement.executeQuery(this.QUERY_STR);
	        System.out.println("Done populating from database :" + Calendar.getInstance().getTime());
	      
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
		return resultSet; 
	  }


	  public void closeConnection() {
		  /*
		   * Close the resultSet and database connection
		   * */
	    try {
	      if (resultSet != null) {
	        resultSet.close();
	      }

	      if (statement != null) {
	        statement.close();
	      }

	      if (connect != null) {
	        connect.close();
	      }
	    } catch (Exception e) {

	    }
	  }
}
