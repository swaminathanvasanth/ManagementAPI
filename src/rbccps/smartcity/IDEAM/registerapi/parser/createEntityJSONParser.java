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
import rbccps.smartcity.IDEAM.registerapi.lora.loraServerConfiguration;
import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;
import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationParser;
import rbccps.smartcity.IDEAM.registerapi.video.videoServerConfiguration;
import rbccps.smartcity.IDEAM.registerapi.video.videoserverConfigurationFields;
import rbccps.smartcity.IDEAM.registerapi.video.videoserverConfigurationParser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class createEntityJSONParser {

	static JsonObject response_jsonObject;
	static BufferedReader br;
	static String _json = null;
	static String accessMechanism_json = "";
	static String _accessMechanism_json = "";
	static String apiKey;
	static String ID;
	static String response_createID = null;
	static String response_generateapiKey = null;
	static String response_assignwhitelist = null;
	static String response_createQueue = null;
	static String response_updateLDAPEntry = null;
	static String response_updateCat = null;
	static RequestController controller;
	static String json;

	static externalServerValidator _externalServerValidator = new externalServerValidator();
	static loraserverConfigurationParser _loraserverConfigurationParser = new loraserverConfigurationParser();
	static videoserverConfigurationParser _videoserverConfigurationParser = new videoserverConfigurationParser();
	static createEntitySchemaParser _entitySchemaParser = new createEntitySchemaParser();
	static uCat uCatServer = new uCat();
	static JsonObject response;

	static JsonParser parser;
	static JsonElement jsonTree;
	static JsonParser access_parser;
	static JsonElement access_jsonTree;
	static JsonObject access_jsonObject;
	static JsonObject jsonObject;
	static String serverType;
	static boolean videoCamera = false;

	public static String JSONParser() {

		// Get Headers from request.
		// Find resourceID

		videoCamera = false;
		System.out.println("JSONParser");

		// Access Mechanism JSON 
		JsonObject requestAccessSite_jsonObject = new JsonObject();
		requestAccessSite_jsonObject.addProperty("describes", "URI for getting permissions to access the device");
		requestAccessSite_jsonObject.addProperty("value","https://rbccps.org/middleware/api/{api_ver}/db");
	
		JsonObject accessEndPoint_jsonObject = new JsonObject();
		accessEndPoint_jsonObject.addProperty("describes", "URI for getting permissions to access the device");
		accessEndPoint_jsonObject.addProperty("value","https://rbccps.org/middleware/api/{api_ver}/db");
		
		JsonObject additionalResourceInfo_jsonObject = new JsonObject();
		additionalResourceInfo_jsonObject.addProperty("describes", "End point for subscribing to LIVE data");
		additionalResourceInfo_jsonObject.addProperty("value","http://rbccps.org/resourceInfo/{id}");
		
		JsonObject subscriptionEndPoint_jsonObject = new JsonObject();
		subscriptionEndPoint_jsonObject.addProperty("describes", "Additional information about the device");
		subscriptionEndPoint_jsonObject.addProperty("value","https://smartcity.rbccps.org/api/0.1.0/subscribe");
		
		JsonObject resourceAPIInfo_jsonObject = new JsonObject();
		resourceAPIInfo_jsonObject.addProperty("describes", "Information on how to use various APIs (access, update, cat) associated with this resource");
		resourceAPIInfo_jsonObject.addProperty("value","https://rbccps-iisc.github.io/");

		JsonObject accessModifier_Entries_jsonObject = new JsonObject();
		
		accessModifier_Entries_jsonObject.addProperty("accessEndPoint", accessEndPoint_jsonObject.toString());
		accessModifier_Entries_jsonObject.addProperty("requestAccessSite", requestAccessSite_jsonObject.toString());
		accessModifier_Entries_jsonObject.addProperty("additionalResourceInfo", additionalResourceInfo_jsonObject.toString());
		accessModifier_Entries_jsonObject.addProperty("subscriptionEndPoint", subscriptionEndPoint_jsonObject.toString());
		accessModifier_Entries_jsonObject.addProperty("resourceAPIInfo", resourceAPIInfo_jsonObject.toString());
	
		String s="{\n\t\"requestAccessSite\": {\n\t\t\"describes\": \"URI for getting permissions to access the device\",\n\t\t\"value\": \"http://rbccps.org/middleware/requestAccess\"\n\t},\n\t\"accessEndPoint\": {\n\t\t\"value\": \"https://rbccps.org/middleware/api/{api_ver}/db\",\n\t\t\"describes\": \"End point to access the archived values (database access endpoint)\"\n\t},\n\t\"subscriptionEndPoint\": {\n\t\t\"value\": \"mqtt://rbccps.org/subscription/live\",\n\t\t\"describes\": \"End point for subscribing to LIVE data\"\n\t},\n\t\"additionalResourceInfo\": {\n\t\t\"value\": \"http://rbccps.org/resourceInfo/{id}\",\n\t\t\"describes\": \"Additional information about the device\"\n\t},\n\t\"resourceAPIInfo\": {\n\t\t\"value\": \"http://rbccps.org/resourceInfo/api\",\n\t\t\"describes\": \"Information on how to use various APIs (access, update, cat) associated with this resource\"\n\t}\n}";

		System.out.println("First ------- ");
		System.out.println(accessModifier_Entries_jsonObject.toString());
		
		accessMechanism_json = accessModifier_Entries_jsonObject.toString();
		
		accessMechanism_json = s;
		System.out.println("Second ------- ");
		System.out.println(accessMechanism_json);
		
		// accessMechanism_json = accessMechanism_json.replaceAll("\\\\", "");
		
		// System.out.println(accessMechanism_json.replaceAll("\\\\", ""));

		controller = new RequestController();
		json = controller.getBody();
		
		System.out.println("------------------BODY------------------");
		System.out.println(json);
		
		parser = new JsonParser();
		jsonTree = parser.parse(json);
		access_parser = new JsonParser();
		
		 access_jsonTree = access_parser.parse(accessMechanism_json);
		 access_jsonObject = access_jsonTree.getAsJsonObject();

		response = new JsonObject();

		/*
		 * System.out.println(access_jsonObject.toString() +
		 * "\n---------------\n");
		 */

		if (jsonTree.isJsonObject()) {
			jsonObject = jsonTree.getAsJsonObject();
			/*
			 * System.out.println(jsonObject.toString() +
			 * "\n---------------\n");
			 */

			System.out.println("Kick Start the flow");

			// Check if it is a LoRa or Video or IP Device

			serverType = _externalServerValidator.parse(jsonObject);
			System.out.println(serverType);

			if (serverType.contains("lora")) {
				String _credentials = _loraserverConfigurationParser
						.parse(jsonObject);
				System.out.println(_credentials);

				if (loraserverConfigurationFields.serverConfiguration) {
					if (loraserverConfigurationFields.LoRaServer) {
						System.out
								.println("loraserverConfigurationFields.LoRaServer = TRUE");
						if (loraserverConfigurationFields.appEUIFlag
								&& loraserverConfigurationFields.devEUIFlag) {
							System.out.println("Looks Good! Its a LoRa device");
							startFlow();

						} else {
							response.addProperty("Registration", "failure");
							response.addProperty("Reason",
									"Missing LoRa information");
							System.out
									.println("Something went wrong with LoRa!");
						}
					}
				}

			} else if (serverType.contains("video")) {
				String _credentials = _videoserverConfigurationParser
						.parse(jsonObject);
				System.out.println(_credentials);

				if (videoserverConfigurationFields.serverConfiguration) {
					if (videoserverConfigurationFields.videoServer) {
						System.out
								.println("videoserverConfigurationFields.videoServer = TRUE");
						if (videoserverConfigurationFields.url) {
							System.out
									.println("Looks Good! Its a video device");
							videoCamera = true;
							startFlow();

						} else {
							response.addProperty("Registration", "failure");
							response.addProperty("Reason",
									"Missing Video information");
							System.out
									.println("Something went wrong with Video!");
						}
					}
				}
			} else {
				System.out.println("Looks Good!, Its an IP Device");
				startFlow();
			}
		}
		return response.toString();
	}

	private static String startFlow() {
		// TODO Auto-generated method stub

		String _dataSchema = _entitySchemaParser.parse(jsonObject,
				access_jsonTree);

		// Store entitySchema and ID in entity class for easy access.
		entity.setEntitySchemaObject(_dataSchema);

		System.out.println(entity.getEntitySchemaObject());
		System.out.println(entity.getEntityID().toString());

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
				return response.toString();
			}

			if (response_createID.contains("created")) {
				response_generateapiKey = apiGateway.generateAPIKey(ID);
				System.out.println("------STEP 2------");
				System.out.println("------------");
				System.out.println(response_generateapiKey);
				System.out.println("------------");
			} else if (response_createID.contains("Server Not Reachable")) {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Server Not Reachable");
				// Delete the created ID in KONG
				apiGateway.deleteUser(ID);
				return response.toString();
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "ID already used.");
				return response.toString();
			}

			if (response_generateapiKey.contains("key")) {
				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID,
						"publish");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");

				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID,
						"subscribe");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");
				
				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID,
						"db");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");
				
				System.out.println("------STEP 3.1------");
				if (loraserverConfigurationFields.serverConfiguration
						&& loraserverConfigurationFields.LoRaServer) {
					String loraServerConfiguration_getJWTKey = loraServerConfiguration
							.getJWTKey();
					System.out.println(loraServerConfiguration_getJWTKey);
					String loraServerConfiguration_registerLoRaEntity = loraServerConfiguration
							.registerLoRaEntity(ID);
					System.out
							.println(loraServerConfiguration_registerLoRaEntity);
					loraserverConfigurationFields.serverConfiguration = false;
					loraserverConfigurationFields.LoRaServer = false;					
					
				} else if (videoserverConfigurationFields.videoServer) {
					videoServerConfiguration.registervideoEntity(ID);
				} else if (serverType.contains("IPDevice")) {
					System.out.println("Its an IPDevice, asssigned WhiteList");
				}
				System.out.println("------STEP 3.1------");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason",
						"Server not reachable. API KeyGen failed");
				apiGateway.deleteUser(ID);
				return response.toString();
			}
			if (response_assignwhitelist.contains("created")) {

				// Create Exchange and Queue for LoRa and IPDevice
				if (!videoCamera) {
					broker.createExchange(ID);
					broker.createExchange(ID + ".private");
					broker.createExchange(ID + ".public");
					broker.createExchange(ID + ".protected");
					broker.createExchange(ID + ".configure");
					broker.createExchange(ID + ".follow");
					System.out.println("+++++++++++Calling createDatabaseBinding Block+++++++++++");
					broker.createDatabaseBinding(ID);
					response_createQueue = broker.createQueue(ID);
					response_createQueue = broker.createQueue(ID + ".follow");
					
				} else {
					System.out.println("Its a videoCamera");
					response_createQueue = "videoCamera";
				}
				System.out.println("------STEP 4------");
				System.out.println("------------");
				System.out.println(response_createQueue);
				System.out.println("------------");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason",
						"Failed in adding ID into the ACL");
			}
			if (response_createQueue.contains("Declare")) {

				System.out.println("LDAP for LoRa and IPDevice");
				System.out.println(ID);
				System.out.println(entity.getEntityapikey());

				ID = ID.replaceAll("^\"|\"$", "");
				System.out.println(ID);

				apiKey = entity.getEntityapikey().toString()
						.replaceAll("^\"|\"$", "");
				System.out.println(apiKey);

				response_updateLDAPEntry = updateLDAP.createEntry(
						RequestController.getX_Consumer_Username(),
						ID.toString(), apiKey);
				System.out.println("LDAP Success !!!");
				System.out.println("------STEP 5------");
				System.out.println("------------");
				System.out.println(response_updateLDAPEntry);
				System.out.println("------------");
			} else if (response_createQueue.contains("videoCamera")) {
				System.out.println("LDAP for Video Camera");
				System.out.println(ID);
				System.out.println(entity.getEntityapikey());

				ID = ID.replaceAll("^\"|\"$", "");
				System.out.println(ID);

				apiKey = entity.getEntityapikey().toString()
						.replaceAll("^\"|\"$", "");
				System.out.println(apiKey);

				response_updateLDAPEntry = updateLDAP.createVideoEntry(
						RequestController.getX_Consumer_Username(),
						ID.toString(), apiKey);
				System.out.println("LDAP Success !!!");
				System.out.println("------STEP 5------");
				System.out.println("------------");
				System.out.println(response_updateLDAPEntry);
				System.out.println("------------");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Failed in Broker");
			}
			if (response_updateLDAPEntry != null) {
				
				response_updateCat = uCat.postCat(_dataSchema);
				System.out.println("------STEP 6------");
				System.out.println("------------");
				System.out.println(response_updateCat);
				System.out.println("------------");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "LDAP update Failure");
			}

			if (response_updateCat != null) {
				response.addProperty("Registration", "success");
				response.addProperty("entityID", ID);
				response.addProperty("apiKey", apiKey);
				response.addProperty("subscriptionEndPoint", "https://smartcity.rbccps.org/api/{version}/follow?id="+ID);
				response.addProperty("accessEndPoint", "https://smartcity.rbccps.org/api/{version}/db?id="+ID);
				response.addProperty("publicationEndPoint", "https://smartcity.rbccps.org/api/{version}/publish?id="+ID);
				response.addProperty("resourceAPIInfo", "https://rbccps-iisc.github.io");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "uCat update Failure");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response.toString();
	}
}