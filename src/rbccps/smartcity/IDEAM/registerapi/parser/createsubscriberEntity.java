package rbccps.smartcity.IDEAM.registerapi.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;

import rbccps.smartcity.IDEAM.APIs.RequestRegister;
import rbccps.smartcity.IDEAM.registerapi.broker.broker;
import rbccps.smartcity.IDEAM.registerapi.kong.apiGateway;
import rbccps.smartcity.IDEAM.registerapi.ldap.updateLDAP;

public class createsubscriberEntity {

	static String response_createID = null;
	static String response_generateapiKey = null;
	static String response_assignwhitelist = null;
	static String response_createQueue = null;
	static String response_updateLDAPEntry = null;
	static int state = 0;
	static JsonObject response;
	static String ID;
	static String apiKey;

	public static JsonObject createEntity(String _ID) {

		try {

			response = new JsonObject();
			ID = _ID.toLowerCase();
			System.out.println("Converted to lower case");
			System.out.println(ID);
			Pattern pattern = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(ID);
			boolean idvalidator = matcher.find();

			if (idvalidator) {
				System.out.println("There is a special character in " + ID);
				System.out.println(ID);
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "ID contains Special Characters");
				return response;
			}

			ID = ID.replaceAll("^\"|\"$", "");
			System.out.println(ID);

			if (ID != null) {
				state = 1;
				response_createID = apiGateway.createUser(ID);
				System.out.println("------STEP 1------");
				System.out.println("------------");
				System.out.println(response_createID);
				System.out.println("------------");
			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "ID not provided");
				return response;
			}

			if (response_createID.contains("created")) {
				state = 2;
				response_generateapiKey = apiGateway.generateAPIKey(ID);

				if (response_generateapiKey.contains("Security level must be between 1-5")) {
					apiGateway.deleteUser(ID);
					response.addProperty("Registration", "Failure");
					response.addProperty("Reason", "Security level must be between 1-5");
					return response;
				}

				System.out.println("------STEP 2------");
				System.out.println("------------");
				System.out.println(response_generateapiKey);
				System.out.println("------------");
			} else if (response_createID.contains("Server Not Reachable")) {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Server Not Reachable");
				// Delete the created ID in KONG
				apiGateway.deleteUser(ID);
				return response;
			} else {
				apiGateway.deleteUser(ID);
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "ID already used.");
				return response;
			}

			if (response_generateapiKey.contains("key")) {
				state = 3;
				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID, "publish");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");

				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID, "subscribe");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");

				response_assignwhitelist = apiGateway.assignWhiteListGroup(ID, "db");
				System.out.println("------STEP 3------");
				System.out.println("------------");
				System.out.println(response_assignwhitelist);
				System.out.println("------------");

			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Server not reachable. API KeyGen failed");
				apiGateway.deleteUser(ID);
				return response;
			}

			if (response_assignwhitelist.contains("created")) {
				state = 4;
				System.out.println("------STEP 4------");
				broker.createExchange(ID + ".notify");
				broker.createQueue(ID);
				response_createQueue = broker.createQueue(ID + ".notify");
				broker.createBinding(ID + ".notify", ID + ".notify");
				System.out.println("------STEP 4------");

			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Failed in adding ID into the ACL");
				apiGateway.deleteUser(ID);
				return response;
			}

			if (response_createQueue.contains("Created")) {

				state = 5;
				System.out.println("LDAP for subscriber application");
				System.out.println(ID);
				System.out.println(entity.getEntityapikey());

				ID = ID.replaceAll("^\"|\"$", "");
				System.out.println(ID);

				apiKey = entity.getEntityapikey().toString().replaceAll("^\"|\"$", "");
				System.out.println(apiKey);

				response_updateLDAPEntry = updateLDAP.createsubscriberEntry(RequestRegister.getX_Consumer_Username(), ID, apiKey);
				System.out.println("LDAP Success !!!");
				System.out.println("------STEP 5------");
				System.out.println("------------");
				System.out.println(response_updateLDAPEntry);
				System.out.println("------------");

				response.addProperty("Registration", "success");
				response.addProperty("entityID", ID);
				response.addProperty("apiKey", apiKey);
				response.addProperty("subscriptionEndPoint",
						"https://smartcity.rbccps.org/api/{version}/follow?id=" + ID);
				response.addProperty("accessEndPoint", "https://smartcity.rbccps.org/api/{version}/db?id=" + ID);
				response.addProperty("publicationEndPoint",
						"https://smartcity.rbccps.org/api/{version}/publish?id=" + ID);
				response.addProperty("resourceAPIInfo", "https://rbccps-iisc.github.io");
				System.out.println(response.toString());

			} else {
				response.addProperty("Registration", "failure");
				response.addProperty("Reason", "Failed in LDAP");
				apiGateway.deleteUser(ID);
				broker.deleteQueue(ID);
				return response;
			}
		} catch (Exception ex) {

			return response;
		}

		return response;
	}

}
