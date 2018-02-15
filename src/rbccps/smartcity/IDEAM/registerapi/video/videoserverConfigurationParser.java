package rbccps.smartcity.IDEAM.registerapi.video;

import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class videoserverConfigurationParser {

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
	
	// Striping the Server appKey of LoRa device form request //

	static JsonElement serverConfiguration_configuration_playurl;
	static JsonElement entitySchema;
	static JsonObject entitySchemaObject;
	static JsonElement id_element;

	static JsonParser parser = new JsonParser();

	public String parse(JsonObject jsonObject) {

		try {
			serverConfiguration = jsonObject.get("serverConfiguration");
			_serverConfiguration = serverConfiguration.toString();

			serverConfiguration_jsonTree = parser.parse(_serverConfiguration);

			serverConfiguration_jsonObject = serverConfiguration_jsonTree
					.getAsJsonObject();

			System.out.println(serverConfiguration_jsonObject.toString()
					+ "\n---------------\n");

			videoserverConfigurationFields.videoServer = true;
			videoserverConfigurationFields.serverConfiguration = true;
			
		} catch (Exception e) {
			System.out.println("Error : serverConfiguration not found");
			videoserverConfigurationFields.videoServer = false;
			videoserverConfigurationFields.serverConfiguration = false;
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
			videoserverConfigurationFields.serverUsername = serverConfiguration_credentials_username
					.toString();
			System.out.println(videoserverConfigurationFields.serverUsername);

			serverConfiguration_credentials_password = serverConfiguration_credentials_jsonObject
					.get("password");
			videoserverConfigurationFields.serverPassword = serverConfiguration_credentials_password
					.toString();
			System.out.println(videoserverConfigurationFields.serverPassword);

			serverConfiguration_credentials_serverURL = serverConfiguration_credentials_jsonObject
					.get("serverURL");
			videoserverConfigurationFields.serverURL = serverConfiguration_credentials_serverURL
					.toString();
			System.out.println(videoserverConfigurationFields.serverURL);

			serverConfiguration_credentials_serverMethod = serverConfiguration_credentials_jsonObject
					.get("serverMethod");
			videoserverConfigurationFields.serverMethod = serverConfiguration_credentials_serverMethod
					.toString();
			System.out.println(videoserverConfigurationFields.serverMethod);

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

			serverConfiguration_configuration_playurl = serverConfiguration_configuration_jsonObject
					.get("playbackurl");
			videoserverConfigurationFields.playbackurl = serverConfiguration_configuration_playurl
					.toString();
			System.out.println(videoserverConfigurationFields.playbackurl);
			videoserverConfigurationFields.url = true;

		} catch (Exception e) {
			System.out
					.println("Error : serverConfiguration appEUI or devEUI not found");

			// Set Flag is not available.
			videoserverConfigurationFields.url = false;
			
			return "PlayURL is not specified in json";

		}

		return "Video information extracted";

	}

}
