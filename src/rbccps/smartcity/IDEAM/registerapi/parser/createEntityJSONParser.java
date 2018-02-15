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

		final String Access_Mechanism_JSON_PATH = "/home/vasanth/JavaEE_Workspace/JSONParser/accessMechanism.json";

		try {
			br = new BufferedReader(new FileReader(Access_Mechanism_JSON_PATH));
			while ((_accessMechanism_json = br.readLine()) != null) {
				accessMechanism_json = accessMechanism_json
						+ _accessMechanism_json;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("File Not Found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IO Exception");
		}

		System.out.println(accessMechanism_json);

		// Access Mechanism JSON 
		JsonObject testjsonObject = new JsonObject();
		testjsonObject.addProperty("describes", "URI for getting permissions to access the device");
		testjsonObject.addProperty("value","https://rbccps.org/middleware/api/{api_ver}/db");
		testjsonObject.addProperty("describes","End point to access the archived values (database access endpoint)");
		testjsonObject.addProperty("value","https://smartcity.rbccps.org/api/0.1.0/subscribe");
		testjsonObject.addProperty("describes","End point for subscribing to LIVE data");
		
		System.out.println(testjsonObject.toString());
		
		
		controller = new RequestController();
		json = controller.getBody();
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
						"publish,subscribe,historic,cat");
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
					response_createQueue = broker.createQueue(ID);
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
			if (response_createQueue.contains("declare queue ok")) {

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
				response_updateCat = uCatServer.post(_dataSchema);
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
		return response.toString();
	}
}