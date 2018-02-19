package rbccps.smartcity.IDEAM.urls;

public class URLs {
	
	public static String brokerURL = "http://rabbitmq:8000";
	public static String apiGatewayURL = "http://kong:8001";
	public static String uCatURL = "http://kong:8000/api/1.0.0/cat";
	// public static String uCatURL = "https://10.156.14.210:10443/api/1.0.0/cat";
	public static String mCatURL;
	public static String LDAPURL = "ldap://ldapd:8389";
	public static String dbURL = "";
	public static String loraserverJWTURL = "https://gateways.rbccps.org:8080/api/internal/login";
	public static String loraserverURL = "https://gateways.rbccps.org:8080/api/nodes";
	public static String videoserverURL = "http://10.156.14.206:8088";
			
	public static String getLoraserverURL() {
		return loraserverURL;
	}
	public static void setLoraserverURL(String loraserverURL) {
		URLs.loraserverURL = loraserverURL;
	}
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
	public static String getLoraserverJWTURL() {
		return loraserverJWTURL;
	}
	public static void setLoraserverJWTURL(String loraserverJWTURL) {
		URLs.loraserverJWTURL = loraserverJWTURL;
	}
	public static String getVideoserverURL() {
		return videoserverURL;
	}
	public static void setVideoserverURL(String videoserverURL) {
		URLs.videoserverURL = videoserverURL;
	}
}
