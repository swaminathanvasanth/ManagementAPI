package rbccps.smartcity.IDEAM.registerapi.lora;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class loraserverConfigurationParser {

	static JsonElement serverConfiguration;
	static String _serverConfiguration = null;

	static JsonElement serverConfiguration_jsonTree;
	static JsonObject serverConfiguration_jsonObject = null;

	static JsonElement serverConfiguration_credentials;
	static JsonElement serverConfiguration_configuration;

	static String _serverConfiguration_credentials = null;
	static String _serverConfiguration_configuration = null;

	static JsonElement serverConfiguration_credentials_jsonTree;
	static JsonElement serverConfiguration_configuration_jsonTree;

	static JsonObject serverConfiguration_credentials_jsonObject = null;
	public static JsonObject serverConfiguration_configuration_jsonObject = null;

	// Striping the Server Information from request //

	static JsonElement serverConfiguration_credentials_serverURL;
	static JsonElement serverConfiguration_credentials_serverMethod;
	static JsonElement serverConfiguration_credentials_username;
	static JsonElement serverConfiguration_credentials_password;
	static JsonElement serverConfiguration_credentials_apiKey;

	// Striping the Server appKey of LoRa device form request //

	static JsonElement serverConfiguration_configuration_appKey;
	static JsonElement serverConfiguration_configuration_appEUI;
	static JsonElement serverConfiguration_configuration_devEUI;
	static JsonElement entitySchema;

	static JsonObject entitySchemaObject;
	static JsonElement id_element;

	static JsonParser parser = new JsonParser();

	public static String parse(JsonObject jsonObject) {

		try {
			serverConfiguration = jsonObject.get("serverConfiguration");
			_serverConfiguration = serverConfiguration.toString();

			serverConfiguration_jsonTree = parser.parse(_serverConfiguration);

			serverConfiguration_jsonObject = serverConfiguration_jsonTree
					.getAsJsonObject();

			System.out.println(serverConfiguration_jsonObject.toString()
					+ "\n---------------\n");

			loraserverConfigurationFields.LoRaServer = true;
			loraserverConfigurationFields.serverConfiguration = true;
			
		} catch (Exception e) {
			System.out.println("Error : serverConfiguration not found");
			loraserverConfigurationFields.LoRaServer = false;
			loraserverConfigurationFields.serverConfiguration = false;
			return "Non-LoRa Registration";
		}

		try {
			serverConfiguration_credentials = serverConfiguration_jsonObject
					.get("credentials");
			_serverConfiguration_credentials = serverConfiguration_credentials
					.toString();

			serverConfiguration_credentials_jsonTree = parser
					.parse(_serverConfiguration_credentials);
			serverConfiguration_credentials_jsonObject = serverConfiguration_credentials_jsonTree
					.getAsJsonObject();

			System.out.println(serverConfiguration_credentials_jsonObject
					.toString() + "\n---------------\n");

			serverConfiguration_credentials_username = serverConfiguration_credentials_jsonObject
					.get("username");
			loraserverConfigurationFields.username = serverConfiguration_credentials_username
					.toString();
			System.out.println(loraserverConfigurationFields.username);

			serverConfiguration_credentials_password = serverConfiguration_credentials_jsonObject
					.get("password");
			loraserverConfigurationFields.password = serverConfiguration_credentials_password
					.toString();
			System.out.println(loraserverConfigurationFields.password);

			serverConfiguration_credentials_serverURL = serverConfiguration_credentials_jsonObject
					.get("serverURL");
			loraserverConfigurationFields.serverURL = serverConfiguration_credentials_serverURL
					.toString();
			System.out.println(loraserverConfigurationFields.serverURL);

			serverConfiguration_credentials_serverMethod = serverConfiguration_credentials_jsonObject
					.get("serverMethod");
			loraserverConfigurationFields.serverMethod = serverConfiguration_credentials_serverMethod
					.toString();
			System.out.println(loraserverConfigurationFields.serverMethod);

			serverConfiguration_credentials_apiKey = serverConfiguration_credentials_jsonObject
					.get("apiKeyURL");
			loraserverConfigurationFields.apiKeyURL = serverConfiguration_credentials_apiKey
					.toString();
			System.out.println(loraserverConfigurationFields.apiKeyURL);

		} catch (Exception e) {
			System.out
					.println("Error : serverConfiguration_credentials not found");
			return "LoRa Configuration Parser error";
		}

		try {


			serverConfiguration_configuration = serverConfiguration_jsonObject
					.get("configuration");
			_serverConfiguration_configuration = serverConfiguration_configuration
					.toString();
			System.out.println(_serverConfiguration_configuration);
			
			serverConfiguration_configuration_jsonTree = parser
					.parse(_serverConfiguration_configuration);
			serverConfiguration_configuration_jsonObject = serverConfiguration_configuration_jsonTree
					.getAsJsonObject();

			System.out.println("---------------");
			System.out.println(serverConfiguration_configuration_jsonObject
					.toString() + "\n---------------\n");


			serverConfiguration_configuration_appEUI = serverConfiguration_configuration_jsonObject
					.get("appEUI");
			loraserverConfigurationFields.appEUI = serverConfiguration_configuration_appEUI
					.toString();
			System.out.println(loraserverConfigurationFields.appEUI);

			serverConfiguration_configuration_devEUI = serverConfiguration_configuration_jsonObject
					.get("devEUI");
			loraserverConfigurationFields.devEUI = serverConfiguration_configuration_devEUI
					.toString();
			System.out.println(loraserverConfigurationFields.devEUI);

			loraserverConfigurationFields.appEUIFlag = true;
			loraserverConfigurationFields.devEUIFlag = true;

		} catch (Exception e) {
			System.out
					.println("Error : serverConfiguration appEUI or devEUI not found");

			// Set Flag is not available.
			loraserverConfigurationFields.appEUIFlag = false;
			loraserverConfigurationFields.devEUIFlag = false;

			return "LoRa EUI IDs not specified in json";

		}

		try {
			serverConfiguration_configuration_appKey = serverConfiguration_configuration_jsonObject
					.get("appKey");
			loraserverConfigurationFields.appKey = serverConfiguration_configuration_appKey
					.toString();
			loraserverConfigurationFields.appKeyFlag = true;
			System.out.println(loraserverConfigurationFields.appKey);
			return loraserverConfigurationFields.appKey;
		} catch (Exception ex) {
			System.out
					.println("Error : serverConfiguration appKeyFlag not found, Generate and add API key in the request");

			// Set Flag is not available.
			loraserverConfigurationFields.appKeyFlag = false;
		}

		/*
		 * System.out.println("\n-------NS Information--------\n" +
		 * _serverConfiguration + "\n-------NS Credentials--------\n" +
		 * _serverConfiguration_credentials +
		 * "\n-------NS Configuration--------\n" +
		 * _serverConfiguration_configuration);
		 * 
		 * System.out.println(username +" : "+ password +" : "+ serverMethod
		 * +" : "+ serverURL +" : "+ apiKey);
		 */
		return loraserverConfigurationFields.username + " : "
				+ loraserverConfigurationFields.password + " : "
				+ loraserverConfigurationFields.serverMethod + " : "
				+ loraserverConfigurationFields.serverURL + " : "
				+ loraserverConfigurationFields.apiKeyURL;

	}

}
