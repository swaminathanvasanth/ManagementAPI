package rbccps.smartcity.IDEAM.registerapi.parser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class serverConfigurationParser {

	JsonElement serverConfiguration;
	String _serverConfiguration = null;

	JsonElement serverConfiguration_jsonTree;
	JsonObject serverConfiguration_jsonObject = null;

	JsonElement serverConfiguration_credentials;
	JsonElement serverConfiguration_configuration;

	String _serverConfiguration_credentials = null;
	String _serverConfiguration_configuration = null;

	JsonElement serverConfiguration_credentials_jsonTree;
	JsonElement serverConfiguration_configuration_jsonTree;

	JsonObject serverConfiguration_credentials_jsonObject = null;
	JsonObject serverConfiguration_configuration_jsonObject = null;

	// Striping the Server Information from request //

	JsonElement serverConfiguration_credentials_serverURL;
	JsonElement serverConfiguration_credentials_serverMethod;
	JsonElement serverConfiguration_credentials_username;
	JsonElement serverConfiguration_credentials_password;
	JsonElement serverConfiguration_credentials_apiKey;

	String serverURL = "serverURL" + ":"
			+ "(/video) or https://gateways.rbccps.org:8080/api/nodes";
	String serverMethod = "serverMethod" + ":" + "POST";
	String username = "username" + ":" + "bob";
	String password = "password" + ":" + "alice";
	String apiKey = "apiKey" + ":" + "adsfl809";

	JsonElement entitySchema;
	String _entitySchema;

	JsonObject entitySchemaObject;
	JsonElement id_element;
	String id;

	JsonParser parser = new JsonParser();
	
	public String parse(JsonObject jsonObject){
		

		try {
			serverConfiguration = jsonObject.get("serverConfiguration");
			_serverConfiguration = serverConfiguration.toString();

			serverConfiguration_jsonTree = parser
					.parse(_serverConfiguration);

			serverConfiguration_jsonObject = serverConfiguration_jsonTree
					.getAsJsonObject();
			/*
			System.out.println(serverConfiguration_jsonObject.toString()
					+ "\n---------------\n");
*/
		} catch (Exception e) {
			System.out.println("Error : serverConfiguration not found");
			System.exit(0);
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
			
	/*		System.out.println(serverConfiguration_credentials_jsonObject
					.toString() + "\n---------------\n");
*/
			serverConfiguration_credentials_username = serverConfiguration_credentials_jsonObject
					.get("username");
			serverConfiguration_credentials_password = serverConfiguration_credentials_jsonObject
					.get("password");
			serverConfiguration_credentials_serverURL = serverConfiguration_credentials_jsonObject
					.get("serverURL");
			serverConfiguration_credentials_serverMethod = serverConfiguration_credentials_jsonObject
					.get("serverMethod");
			serverConfiguration_credentials_apiKey = serverConfiguration_credentials_jsonObject
					.get("apiKey");

			username = serverConfiguration_credentials_username.toString();
			password = serverConfiguration_credentials_password.toString();
			serverMethod = serverConfiguration_credentials_serverMethod.toString();
			serverURL = serverConfiguration_credentials_serverURL.toString();
			apiKey = serverConfiguration_credentials_apiKey.toString();
			
		} catch (Exception e) {
			System.out
					.println("Error : serverConfiguration_credentials not found");
			System.exit(0);
		}

		try {

			serverConfiguration_configuration = serverConfiguration_jsonObject
					.get("configuration");
			_serverConfiguration_configuration = serverConfiguration_configuration
					.toString();

		} catch (Exception e) {
			System.out
					.println("Error : serverConfiguration_configuration not found");
			System.exit(0);
		}

/*		System.out.println("\n-------NS Information--------\n"
				+ _serverConfiguration
				+ "\n-------NS Credentials--------\n"
				+ _serverConfiguration_credentials
				+ "\n-------NS Configuration--------\n"
				+ _serverConfiguration_configuration);

		System.out.println(username +" : "+ password +" : "+ serverMethod +" : "+ serverURL +" : "+ apiKey);
*/		
		return username +" : "+ password +" : "+ serverMethod +" : "+ serverURL +" : "+ apiKey;

		
	}

}
