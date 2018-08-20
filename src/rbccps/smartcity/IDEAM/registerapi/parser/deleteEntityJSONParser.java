package rbccps.smartcity.IDEAM.registerapi.parser;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import rbccps.smartcity.IDEAM.APIs.RequestRegister;
import rbccps.smartcity.IDEAM.registerapi.broker.broker;
import rbccps.smartcity.IDEAM.registerapi.catalog.uCat;
import rbccps.smartcity.IDEAM.registerapi.kong.apiGateway;
import rbccps.smartcity.IDEAM.registerapi.ldap.updateLDAP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class deleteEntityJSONParser {
	
	static JsonObject response_jsonObject;
	
	public static String JSONParser() {
	
		// Get Headers from request.
		// Find resourceID

		BufferedReader br;
		String _json = null;
		String apiKey;
		String ID;
		String response_deleteID = null;
		String response_deleteapiKey = null;
		String response_removewhitelist = null;
		String response_deleteQueue = null;
		String response_deleteExchange = null;
		String response_deleteLDAPEntry = null;
		int response_deleteCat = 0;

		RequestRegister controller = new RequestRegister();
		String json = controller.getBody();
		
		deleteEntitySchemaParser _deleteEntitySchemaParser = new deleteEntitySchemaParser();
		uCat uCatServer = new uCat();
		JsonObject response;
		
		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(json);
		response = new JsonObject();

		/*
		 * System.out.println(access_jsonObject.toString() +
		 * "\n---------------\n");
		 */

		if (jsonTree.isJsonObject()) {
			JsonObject jsonObject = jsonTree.getAsJsonObject();
		
			System.out.println(jsonObject.toString() + "\n---------------\n");
			
			// Store entitySchema and ID in entity class for easy access.
		
			
			System.out.println("Kick Start the flow");

			try {
				ID = entity.getEntityID().toString();
				System.out.println(entity.getEntityID().toString());
	
				ID = ID.replaceAll("^\"|\"$", "");
				System.out.println(ID);
				
				// STEP 1
				if (ID != null) {
					response_deleteID = apiGateway.deleteUser(ID);
					System.out.println("------STEP 1------");
					System.out.println("------------");
					System.out.println(response_deleteID);
					System.out.println("------------");
				} else {
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "ID not provided");
				}

				// STEP 2				
				if (response_deleteID.contains("created")) {
					System.out.println("------STEP 2------");
					broker.deleteExchange(ID);
					broker.deleteExchange(ID+".configure");
					response_deleteQueue = broker.deleteQueue(ID);
					
					System.out.println("------------");
					System.out.println(response_deleteQueue);
					System.out.println("------------");
					
				} else if(response_deleteID.contains("Server Not Reachable")){
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "Server Not Reachable");
					return response.toString();
				} else {
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "ID already used.");
					return response.toString();
				}
				
				// STEP 3
				
				if (response_deleteQueue.contains("delete queue ok")) {
					
					System.out.println(ID);
					System.out.println(entity.getEntityapikey());
					
					ID = ID.replaceAll("^\"|\"$", "");
					System.out.println(ID);
					
					response_deleteLDAPEntry = updateLDAP.deleteEntry(RequestRegister.getX_Consumer_Custom_ID(),
							ID.toString(), entity.getEntityapikey().toString());
					
					/*
					response_updateLDAPEntry = updateLDAP.createEntry("rbccps",
							"anotheroneheretotest", "2a9f60f9196945e09e4aab522b788a4b");
					*/
					
					System.out.println("------STEP 3------");
					System.out.println("------------");
					System.out.println(response_deleteLDAPEntry);
					System.out.println("------------");
				} else {
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "Queue Deletion Failure");
				}
				
				// STEP 4				
				
				if (response_deleteLDAPEntry != null) {
					response_deleteCat = uCatServer.postCat("");
					System.out.println("------STEP 6------");
					System.out.println("------------");
					System.out.println(response_deleteCat);
					System.out.println("------------");
				} else {
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "LDAP update Failure");
				}
			
				// STEP 5
				if(response_deleteCat >=200 && response_deleteCat<300){
					response.addProperty("De-Registration", "success");
					response.addProperty("entityID", ID);
				} else {
					response.addProperty("De-Registration", "failure");
					response.addProperty("Reason", "uCat deletion Failure");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response.toString();		
	}
}