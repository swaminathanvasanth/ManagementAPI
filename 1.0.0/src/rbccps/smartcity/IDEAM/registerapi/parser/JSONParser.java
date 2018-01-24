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

import rbccps.smartcity.IDEAM.registerapi.RequestController;
import rbccps.smartcity.IDEAM.registerapi.broker.broker;
import rbccps.smartcity.IDEAM.registerapi.catalog.uCat;
import rbccps.smartcity.IDEAM.registerapi.kong.apiGateway;
import rbccps.smartcity.IDEAM.registerapi.ldap.updateLDAP;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JSONParser {
	
	static JsonObject response_jsonObject;
	
	public static String JSONParser() {
	
		// Get Headers from request.
		// Find resourceID

		final String Access_Mechanism_JSON_PATH = "/home/vasanth/JavaEE_Workspace/JSONParser/accessMechanism.json";

		BufferedReader br;
		String _json = null;
		String accessMechanism_json = "";
		String _accessMechanism_json = "";
		String apiKey;
		String ID;
		String response_createID = null;
		String response_generateapiKey = null;
		String response_assignwhitelist = null;
		String response_createQueue = null;
		String response_updateLDAPEntry = null;
		String response_updateCat = null;

		RequestController controller = new RequestController();
		String json = controller.getBody();
		
		serverConfigurationParser _serverConfigurationParser = new serverConfigurationParser();
		entitySchemaParser _entitySchemaParser = new entitySchemaParser();
		uCat uCatServer = new uCat();

		JsonObject response;
		
		try {
			br = new BufferedReader(new FileReader(Access_Mechanism_JSON_PATH));
			while ((_accessMechanism_json = br.readLine()) != null) {
				// System.out.println(_accessMechanism_json);
				accessMechanism_json = accessMechanism_json
						+ _accessMechanism_json;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonParser parser = new JsonParser();
		JsonElement jsonTree = parser.parse(json);
		JsonParser access_parser = new JsonParser();
		JsonElement access_jsonTree = access_parser.parse(accessMechanism_json);
		JsonObject access_jsonObject = access_jsonTree.getAsJsonObject();
		response = new JsonObject();

		/*
		 * System.out.println(access_jsonObject.toString() +
		 * "\n---------------\n");
		 */

		if (jsonTree.isJsonObject()) {
			JsonObject jsonObject = jsonTree.getAsJsonObject();
			/*
			 * System.out.println(jsonObject.toString() +
			 * "\n---------------\n");
			 */

			String _credentials = _serverConfigurationParser.parse(jsonObject);
			System.out.println(_credentials);
			String _dataSchema = _entitySchemaParser.parse(jsonObject,
					access_jsonTree);

			// Store entitySchema and ID in entity class for easy access.
			entity.setEntitySchemaObject(_dataSchema);
			System.out.println(entity.getEntitySchemaObject());
			System.out.println(entity.getEntityID().toString());

			System.out.println("Kick Start the flow");

			try {
				ID = entity.getEntityID().toString();
				System.out.println(ID);
				
				ID = ID.replaceAll("^\"|\"$", "");
				System.out.println(ID);
				
				if (ID != null) {
					response_createID = apiGateway.createUser(ID);
					System.out.println("------STEP 1------");
					System.out.println("------------");
					System.out.println(response_createID);
					System.out.println("------------");
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "ID not provided");
				}

				if (response_createID.contains("created")) {
					response_generateapiKey = apiGateway.generateAPIKey(ID);
					System.out.println("------STEP 2------");
					System.out.println("------------");
					System.out.println(response_generateapiKey);
					System.out.println("------------");
				} else if(response_createID.contains("Server Not Reachable")){
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "Server Not Reachable");
					return response.toString();
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "ID already used.");
					return response.toString();
				}
				
				if (response_generateapiKey.contains("key")) {
					response_assignwhitelist = apiGateway.assignWhiteListGroup(
							ID, "publish,subscribe,historic,cat");
					System.out.println("------STEP 3------");
					System.out.println("------------");
					System.out.println(response_assignwhitelist);
					System.out.println("------------");
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "Failed in generating API Key");
					return response.toString();
				}
				if (response_assignwhitelist.contains("created")) {
					broker.createExchange(ID);
					broker.createExchange(ID+".configure");
					response_createQueue = broker.createQueue(ID);
					
					System.out.println("------STEP 4------");
					System.out.println("------------");
					System.out.println(response_createQueue);
					System.out.println("------------");
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "Failed in adding ID into the ACL");
				}
				if (response_createQueue.contains("declare queue ok")) {
					
					System.out.println(ID);
					System.out.println(entity.getEntityapikey());
					
					ID = ID.replaceAll("^\"|\"$", "");
					System.out.println(ID);
										
					apiKey = entity.getEntityapikey().replaceAll("^\"|\"$", "");
					System.out.println(apiKey);
					
					response_updateLDAPEntry = updateLDAP.createEntry(RequestController.getX_Consumer_Custom_ID(),
							ID.toString(), entity.getEntityapikey().toString());
					
					/*
					response_updateLDAPEntry = updateLDAP.createEntry("rbccps",
							"anotheroneheretotest", "2a9f60f9196945e09e4aab522b788a4b");
					*/
					
					System.out.println("------STEP 5------");
					System.out.println("------------");
					System.out.println(response_updateLDAPEntry);
					System.out.println("------------");
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "Queue Creation Failure");
				}
				if (response_updateLDAPEntry != null) {
					response_updateCat = uCatServer.post(_dataSchema);
					System.out.println("------STEP 6------");
					System.out.println("------------");
					System.out.println(response_updateCat);
					System.out.println("------------");
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "LDAP update Failure");
				}
				
				if(response_updateCat != null){
					response.addProperty("Registration", "success");
					response.addProperty("entityID", ID);
					response.addProperty("apiKey", ID);
					response.addProperty("subscriptionEndPoint", ID);
					response.addProperty("accessEndPoint", ID);
					response.addProperty("publicationEndPoint", ID);
					response.addProperty("resourceAPIInfo", ID);					
				} else {
					response.addProperty("Registration", "failure");
					response.addProperty("Reason", "uCat update Failure");
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return response.toString();		
	}
}