package rbccps.smartcity.IDEAM.urls;

public class URLs {
	
	public static String brokerURL = "http://10.156.14.6:8989";
	public static String apiGatewayURL = "http://10.156.14.4:2959";
	public static String uCatURL = "https://smartcity.rbccps.org/api/0.1.0/cat";
	public static String mCatURL;
	public static String LDAPURL = "ldap://10.156.14.20:389";
	public static String dbURL = "";
	
	public static String getBrokerURL() {
		return brokerURL;
	}
	public static String getApiGatewayURL() {
		return apiGatewayURL;
	}
	public static String getuCatURL() {
		return uCatURL;
	}
	public static String getmCatURL() {
		return mCatURL;
	}
	public static String getLDAPURL() {
		return LDAPURL;
	}
	public static String getDbURL() {
		return dbURL;
	}
}
