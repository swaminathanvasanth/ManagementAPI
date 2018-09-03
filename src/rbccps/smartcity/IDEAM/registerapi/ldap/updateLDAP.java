package rbccps.smartcity.IDEAM.registerapi.ldap;
public class updateLDAP {

	
	private static String[] decoded_authorization_datas;
	static String addEntry_Response;

	public static String createEntry(String providerID, String resourceID, String apiKey) {
		// TODO Auto-generated method stub

		System.out.println(providerID + resourceID + apiKey);
		
		LDAP addEntryToLdap = new LDAP();
		addEntryToLdap.readldappwd();
		String addEntry_Response;
		if (addEntryToLdap.addRegistrationEntry(providerID, resourceID, apiKey)) {
			System.out.println("entry creation completed");
			addEntry_Response = "Success";
			// Add a FLAG to process the Registration further

		} else {
			System.out.println("entry creation failed");
			addEntry_Response = "Failure";

			// End the Process with a RESPONSE stating ID already available.
		}

		return addEntry_Response;
	}
	
	public static String createsubscriberEntry(String providerID, String resourceID, String apiKey) {
		// TODO Auto-generated method stub

		System.out.println(providerID + resourceID + apiKey);
		
		LDAP addEntryToLdap = new LDAP();
		addEntryToLdap.readldappwd();
		String addEntry_Response;
		if (addEntryToLdap.addsubscriberRegistrationEntry(providerID, resourceID, apiKey)) {
			System.out.println("entry creation completed");
			addEntry_Response = "Success";
			// Add a FLAG to process the Registration further

		} else {
			System.out.println("entry creation failed");
			addEntry_Response = "Failure";

			// End the Process with a RESPONSE stating ID already available.
		}

		return addEntry_Response;
	}
	
	public static String createVideoEntry(String providerID, String resourceID, String apiKey) {
		// TODO Auto-generated method stub

		System.out.println(providerID + resourceID + apiKey);
		
		LDAP addEntryToLdap = new LDAP();
		String addEntry_Response;
		
		System.out.println(providerID + "----" + resourceID + "----" + apiKey );
		if (addEntryToLdap.addVideoEntry(providerID, resourceID, apiKey)) {
			System.out.println("entry creation completed");
			addEntry_Response = "Success";
			// Add a FLAG to process the Registration further

		} else {
			System.out.println("entry creation failed");
			addEntry_Response = "Failure";

			// End the Process with a RESPONSE stating ID already available.
		}

		return addEntry_Response;
	}

	public static String deleteEntry(String entityID,
			String x_Consumer_Custom_ID, String apiKey) {
		// TODO Auto-generated method stub

		LDAP.readldappwd();
		System.out.println("In deleteEntry");
		
		{
		
		if (LDAP.deleteEntry(entityID, x_Consumer_Custom_ID, apiKey )) {
			System.out.println("entry deletion completed");
			addEntry_Response = "Success";
			// Add a FLAG to process the Registration further

		} else {
			System.out.println("entry deletion failed");
			addEntry_Response = "Failure";

			// End the Process with a RESPONSE stating ID already available.
		}

		return addEntry_Response;
		}
		
	}
}
