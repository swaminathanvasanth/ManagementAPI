package rbccps.smartcity.IDEAM.registerapi.parser;

import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;
import rbccps.smartcity.IDEAM.registerapi.video.videoserverConfigurationFields;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class externalServerValidator {

	static JsonElement serverConfiguration;
	static String _serverConfiguration = null;

	static JsonElement serverConfiguration_jsonTree;
	static JsonObject serverConfiguration_jsonObject = null;
	static JsonParser parser = new JsonParser();
	static String serviceType;
	static String response;

	public String parse(JsonObject jsonObject) {
		
		loraserverConfigurationFields.LoRaServer = false;
		loraserverConfigurationFields.serverConfiguration = false;
	
		videoserverConfigurationFields.videoServer = false;
		videoserverConfigurationFields.serverConfiguration = false;

		
		try {
			serverConfiguration = jsonObject.get("entityType");
			_serverConfiguration = serverConfiguration.toString();
			serverConfiguration_jsonTree = parser.parse(_serverConfiguration);
			serverConfiguration_jsonObject = serverConfiguration_jsonTree
					.getAsJsonObject();
			System.out.println(serverConfiguration_jsonObject.toString()
					+ "\n---------------\n");

			// Find if it is Video or LoRa

			serviceType = serverConfiguration_jsonObject.get("type")
					.toString();

			if (serviceType.contains("lora")) {
				loraserverConfigurationFields.LoRaServer = true;
				loraserverConfigurationFields.serverConfiguration = true;
				System.out.println("Its LoRa device");
				response = "loraDevice";
			} else if (serviceType.contains("video")) {
				videoserverConfigurationFields.videoServer = true;
				videoserverConfigurationFields.serverConfiguration = true;
				System.out.println("Its IPCamera");
				response = "videoCamera";
			}

		} catch (Exception e) {
			System.out.println("Error : entityType not found");
			response = "IPDevice";
		}
		
		return response;
	}

}
