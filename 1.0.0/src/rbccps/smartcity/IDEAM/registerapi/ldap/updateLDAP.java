package rbccps.smartcity.IDEAM.registerapi.ldap;
public class updateLDAP {

	
	public static String createEntry(String providerID, String resourceID, String apiKey) {
		// TODO Auto-generated method stub

		System.out.println(providerID + resourceID + apiKey);
		
		LDAP addEntryToLdap = new LDAP();
		String addEntry_Response;
		if (addEntryToLdap.addEntry(providerID, resourceID, apiKey)) {
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
}
