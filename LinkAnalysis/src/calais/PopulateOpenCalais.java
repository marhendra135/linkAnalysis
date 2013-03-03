package calais;

import java.io.IOException;

import mx.bigdata.jcalais.*;
import mx.bigdata.jcalais.rest.CalaisRestClient;

public class PopulateOpenCalais {
	public static void main(String[] args) throws IOException {
	    CalaisClient client = new CalaisRestClient("u3zw4yry5krwskjbqw896htn");
	    CalaisResponse response = client.analyze("Prosecutors at the trial of former Liberian President Charles Taylor " 
	           + " hope the testimony of supermodel Naomi Campbell " 
	           + " will link Taylor to the trade in illegal conflict diamonds, "
	           + " which they say he used to fund a bloody civil war in Sierra Leone.");
	    for (CalaisObject entity : response.getEntities()) {
	        System.out.println(entity.getField("_type") + ":" 
	                           + entity.getField("name"));
	      }
	    for (CalaisObject topic : response.getTopics()) {
	        System.out.println(topic.getField("categoryName"));
	      }
	    for (CalaisObject tags : response.getSocialTags()){
	        System.out.println(tags.getField("_typeGroup") + ":" 
	                           + tags.getField("name"));
	      }
	}
}
