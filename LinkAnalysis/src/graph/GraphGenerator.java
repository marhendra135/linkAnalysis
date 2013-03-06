package graph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import data.Email;

public class GraphGenerator {
	private ArrayList<String> listStopTerms;
	private void initStopTerms(){
		listStopTerms = new ArrayList<String>();
		listStopTerms.add("(EmailAddress:");
		listStopTerms.add("(Person:");
		listStopTerms.add("(PhoneNumber:");
		listStopTerms.add("(Company:Enron");
		listStopTerms.add("(URL:");
		listStopTerms.add("(Position:");
		listStopTerms.add("(http:");
		//listStopTerms.add("Enron");
		//listStopTerms.add("Business");
		//listStopTerms.add("Applied ethics");
	}
	public GraphGenerator() throws IOException{
		super();
		initStopTerms();
		
		//read from file
		
		//generate the model
	}
	
	public ArrayList<GraphObj> generateGraphInputModelforEmail() throws IOException{
		ArrayList<GraphObj> listGObj = new ArrayList<GraphObj>();
		ArrayList<ContentObj> fileContents = readFromFile();
		HashMap<String,ArrayList<GraphObj>> mapsModel = generateInitModel(fileContents);
		listGObj = getCleanedInitModel(mapsModel);
		
		StringBuilder strOut = new StringBuilder();
		int i=0;
		for (Iterator<GraphObj> iterator = listGObj.iterator(); iterator.hasNext();) {
			i++;
			GraphObj gObj = (GraphObj) iterator.next();
			strOut.append(gObj.toString() +"\n");
		}
		//System.out.println("Graph : " + strOut.toString());
		System.out.println("Graph Emails Size: " + i);
		return listGObj;
	}
	
	public ArrayList<GraphObj> generateGraphInputModelforEmailAddress(ArrayList<Email> listEmails) throws IOException{
		ArrayList<GraphObj> listGObj = new ArrayList<GraphObj>();
		
		Iterator<Email> iter = listEmails.iterator();
		GraphObj gObj = null;
		StringTokenizer st = null;
		while (iter.hasNext()){
			Email email = iter.next();

			String entity = email.getRecEmail();
			if (!entity.trim().equals("")){
				st =new StringTokenizer(entity, " ");
				while (st.hasMoreElements()) {
					String strAdd = (String) st.nextElement();
					if (!strAdd.equals("") && !email.getSenderEmails().equals(strAdd)){
						gObj = new GraphObj(email.getSenderEmails(), strAdd, email.getmId());
							listGObj.add(gObj);
					}
				}			
			}		

		}
		
		 Set<GraphObj> set = new HashSet<GraphObj>();
		 ArrayList<GraphObj> newList = new ArrayList<GraphObj>();
		 for (Iterator<GraphObj> itr = listGObj.iterator();itr.hasNext(); ) {
			 GraphObj element = itr.next();
			 if (set.add(element))
				 newList.add(element);
		 }
		 listGObj.clear();
		 listGObj.addAll(newList);
		
		StringBuilder strOut = new StringBuilder();
		int i=0;
		for (Iterator<GraphObj> iterator = listGObj.iterator(); iterator.hasNext();) {
			i++;
			gObj = (GraphObj) iterator.next();
			strOut.append(gObj.toString() +"\n");
		}
		//System.out.println("Graph Email Address : " + strOut.toString());
		System.out.println("Graph Email Address Size: " + i);
		return listGObj;
	}
	
	private ArrayList<GraphObj> getCleanedInitModel(HashMap<String,ArrayList<GraphObj>> mapsModel){
		ArrayList<GraphObj> listGObj = new ArrayList<GraphObj>();
		ArrayList<GraphObj> curListGObj = null;
		System.out.println("Cleaned");
		if (mapsModel!=null){
			for (Entry<String, ArrayList<GraphObj>> entry : mapsModel.entrySet()) {
			    curListGObj = entry.getValue();
			    if (curListGObj.size()>1){
			    	curListGObj.remove(0); // remove head
			    	listGObj.addAll(curListGObj);
			    }
		    
			    
			}
		}
	
		return listGObj;
	}
	
	private HashMap<String,ArrayList<GraphObj>> generateInitModel(ArrayList<ContentObj> fileContents) throws IOException{
		HashMap<String,ArrayList<GraphObj>> maps = new HashMap<String,ArrayList<GraphObj>>();
		Set<String> setToBeRemoved = new HashSet<String>(); 
		Iterator<ContentObj> iter = fileContents.iterator();
		while (iter.hasNext()){
			ContentObj obj = iter.next();
			String content = obj.getContent();
			
			ArrayList<String> entities = new ArrayList<String>();
			entities.add((content.substring((new String("[NamedEntity:]")).length()-1, 
					content.indexOf("[CategoryName:]")-1)).trim());//[NamedEntity:]
			//entities.add((content.substring(content.indexOf("[CategoryName:]") + (new String("[CategoryName:]")).length()-1, 
			//		content.indexOf("[SocialTags:]")-1)).trim());//		[CategoryName:]
			//entities.add((content.substring(content.indexOf("[SocialTags:]") + (new String("[SocialTags:]")).length()-1, 
			//		content.length()-1)).trim()); //	[SocialTags:]
			ArrayList<String> cleanedEntities = getCleanedString(entities);
			
			Iterator<String> iterEnt = cleanedEntities.iterator();
			ArrayList<GraphObj> listGObj = null;
			ArrayList<GraphObj> newListGObj = null;
			
			while (iterEnt.hasNext()){
				String entity = iterEnt.next();
				boolean isExist = (maps.get(entity))!=null;
				if (!isExist){ // first input
					listGObj = new ArrayList<GraphObj>();
					listGObj.add(new GraphObj(obj.getmID(), "-99", entity));
					maps.put(entity, listGObj);
				}else{
					listGObj = maps.get(entity);
					//System.out.println("Size of exist List = " +entity + "-" + listGObj.size());
					if (listGObj.size()<=500){
						newListGObj = new ArrayList<GraphObj>();
						boolean found =false;
						
						Iterator<GraphObj> itrObj = listGObj.iterator();
						GraphObj newObj = null;
						while (!found && itrObj.hasNext()){
							GraphObj gObj = itrObj.next();
							//System.out.println(gObj.getvOut() + "-" + gObj.getvIn() + "-" + obj.getmID());
							//if (gObj.getvOut()==obj.getmID())
							if (gObj.getvOut()==obj.getmID() || gObj.getvIn()==obj.getmID())
								found=true;
							else{
								//System.out.println("2::" + gObj.getvOut() + "-" + gObj.getvIn() + "-" + obj.getmID());
								if (listGObj.size()>1){
									if (!gObj.getvIn().equals("-99")){
										if (Integer.parseInt(gObj.getvOut())>Integer.parseInt(obj.getmID())){
											if (Integer.parseInt(gObj.getvIn())>Integer.parseInt(obj.getmID()))
												newListGObj.add(new GraphObj(gObj.getvIn(), obj.getmID(), entity));
											else{
												newListGObj.add(new GraphObj(gObj.getvOut(), obj.getmID(), entity));
												newListGObj.add(new GraphObj(obj.getmID(), gObj.getvIn(), entity));
											}
										}else{
										
											newListGObj.add(new GraphObj(obj.getmID(), gObj.getvOut(), entity));
										}
									}
								} else {
									if (Integer.parseInt(gObj.getvOut())>Integer.parseInt(obj.getmID())){
										if (Integer.parseInt(gObj.getvIn())>Integer.parseInt(obj.getmID()))
											newListGObj.add(new GraphObj(gObj.getvIn(), obj.getmID(), entity));
										else{
											newListGObj.add(new GraphObj(gObj.getvOut(), obj.getmID(), entity));
											newListGObj.add(new GraphObj(obj.getmID(), gObj.getvIn(), entity));
										}
									}else{
									
										newListGObj.add(new GraphObj(obj.getmID(), gObj.getvOut(), entity));
									}
								}
//								if (listGObj.size()>1){
//									if (!gObj.getvIn().equals("-99")){
//										newListGObj.add(new GraphObj(gObj.getvOut(), obj.getmID(), entity));
//										newListGObj.add(new GraphObj(obj.getmID(), gObj.getvOut(), entity));
//									}
//								} else {
//									newListGObj.add(new GraphObj(gObj.getvOut(), obj.getmID(), entity));
//									newListGObj.add(new GraphObj(obj.getmID(), gObj.getvOut(), entity));
//								}
							}
						}
						if (!found){
							//System.out.println("Size of new List  = " +entity + "-" + newListGObj.size());
							listGObj.addAll(newListGObj);
							
							 Set<GraphObj> set = new HashSet<GraphObj>();
							 ArrayList<GraphObj> newList = new ArrayList<GraphObj>();
							 for (Iterator<GraphObj> itr = listGObj.iterator();itr.hasNext(); ) {
								 GraphObj element = itr.next();
								 if (set.add(element))
									 newList.add(element);
							 }
							 listGObj.clear();
							 listGObj.addAll(newList);
							

							maps.remove(entity);
							maps.put(entity, listGObj);
						}
					}	
					else {
						//System.out.println("Entity  = " +entity + " is going to explode");
						setToBeRemoved.add(entity);
					}
				}
			}
					
		}
		//remove ToBeRemoved
		System.out.println("Entity  toberemoved= " +setToBeRemoved.size());
		for (Iterator<String> iterator = setToBeRemoved.iterator(); iterator.hasNext();) {
			String strEntity = (String) iterator.next();
			maps.remove(strEntity);
		}
		return maps;
	} 
	
	private ArrayList<String> getCleanedString(ArrayList<String> strInputs){
		
		StringTokenizer st = null;
		ArrayList<String> listStr = new ArrayList<String>(); 
		Iterator<String> iter = strInputs.iterator();
		while (iter.hasNext()){
			String entity = iter.next();
			if (!entity.trim().equals(",")){
				st =new StringTokenizer(entity, ",");
				while (st.hasMoreElements()) {
					String strAdd = (String) st.nextElement();
					if (!strAdd.equals("")){
						if (isStringCanBeAdded(strAdd))
							listStr.add(strAdd);
					}
				}			
			}		
		}
		
		return listStr;
	}
	
	private boolean isStringCanBeAdded(String strIn){
		boolean found = false;
		Iterator<String> iter = listStopTerms.iterator();
		while (!found && iter.hasNext()){
			String str = iter.next();
			if (strIn.toLowerCase().indexOf(str.toLowerCase())!=-1)
				found=true;
		}
		return (!found);
	}
	
	
	private ArrayList<ContentObj> readFromFile() throws IOException{
		ArrayList<ContentObj> fileContents = new ArrayList<ContentObj>();
		String strLine, content, id = "";
		BufferedReader in = new BufferedReader(new FileReader("EnronEmailTermsCalais-Body.txt"));
		int i=0;
		ContentObj contObj = null;
		while ((strLine = in.readLine()) != null){
			if (strLine.trim().length()>0){
				i++;
				id = strLine.substring(0, strLine.indexOf(">"));
				id = id.trim();
				
				content = strLine.substring(strLine.indexOf(">")+1, strLine.length());
				content = content.trim();
				if (!content.equals("[NamedEntity:][CategoryName:][SocialTags:]")){
					contObj = new ContentObj(id, content);
					fileContents.add(contObj);
				}
					
			}
		}
		System.out.println("Size of filecontents body : " + fileContents.size());
		in = new BufferedReader(new FileReader("EnronEmailTermsCalais-Subject.txt"));
		i=0;
		while ((strLine = in.readLine()) != null){
			if (strLine.trim().length()>0){
				i++;
				id = strLine.substring(0, strLine.indexOf(">"));
				id = id.trim();
				
				content = strLine.substring(strLine.indexOf(">")+1, strLine.length());
				content = content.trim();
				if (!content.equals("[NamedEntity:][CategoryName:][SocialTags:]")){
					contObj = new ContentObj(id, content);
					fileContents.add(contObj);
				}
			}
		}
		System.out.println("Size of filecontents body and subject: " + fileContents.size());		
		
		return fileContents;
	}

}
