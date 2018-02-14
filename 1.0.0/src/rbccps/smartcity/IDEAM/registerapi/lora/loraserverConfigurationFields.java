package rbccps.smartcity.IDEAM.registerapi.lora;

public class loraserverConfigurationFields {

	public static boolean serverConfiguration = false;
	public static boolean LoRaServer = false;
	public static String appKey;
	public static String appEUI;	
	public static String devEUI;	
	public static boolean devEUIFlag = false;
	public static boolean appKeyFlag = false;
	public static boolean appEUIFlag = false;
	public static String serverURL;
	public static String serverMethod;
	public static String username;
	public static String password;
	public static String apiKeyURL;
	public static String _entitySchema;
	public static String id;
	
	public static String getAppKey() {
		return appKey;
	}
	public static void setAppKey(String appKey) {
		loraserverConfigurationFields.appKey = appKey.replaceAll("^\"|\"$", "");
	}
	public static String getAppEUI() {
		return appEUI;
	}
	public static void setAppEUI(String appEUI) {
		loraserverConfigurationFields.appEUI = appEUI.replaceAll("^\"|\"$", "");
	}
	public static String getDevEUI() {
		return devEUI;
	}
	public static void setDevEUI(String devEUI) {
		loraserverConfigurationFields.devEUI = devEUI.replaceAll("^\"|\"$", "");
	}
	public static boolean isDevEUIFlag() {
		return devEUIFlag;
	}
	public static void setDevEUIFlag(boolean devEUIFlag) {
		loraserverConfigurationFields.devEUIFlag = devEUIFlag;
	}
	public static boolean isAppKeyFlag() {
		return appKeyFlag;
	}
	public static void setAppKeyFlag(boolean appKeyFlag) {
		loraserverConfigurationFields.appKeyFlag = appKeyFlag;
	}
	public static boolean isAppEUIFlag() {
		return appEUIFlag;
	}
	public static void setAppEUIFlag(boolean appEUIFlag) {
		loraserverConfigurationFields.appEUIFlag = appEUIFlag;
	}
	public static String getServerURL() {
		return serverURL;
	}
	public static void setServerURL(String serverURL) {
		loraserverConfigurationFields.serverURL = serverURL.replaceAll("^\"|\"$", "");
	}
	public static String getServerMethod() {
		return serverMethod;
	}
	public static void setServerMethod(String serverMethod) {
		loraserverConfigurationFields.serverMethod = serverMethod.replaceAll("^\"|\"$", "");
	}
	public static String getUsername() {
		return username;
	}
	public static void setUsername(String username) {
		loraserverConfigurationFields.username = username.replaceAll("^\"|\"$", "");
	}
	public static String getPassword() {
		return password;
	}
	public static void setPassword(String password) {
		loraserverConfigurationFields.password = password.replaceAll("^\"|\"$", "");
	}
	public static String getApiKeyURL() {
		return apiKeyURL;
	}
	public static void setApiKeyURL(String apiKeyURL) {
		loraserverConfigurationFields.apiKeyURL = apiKeyURL.replaceAll("^\"|\"$", "");
	}
	public static String get_entitySchema() {
		return _entitySchema;
	}
	public static void set_entitySchema(String _entitySchema) {
		loraserverConfigurationFields._entitySchema = _entitySchema;
	}
	public static String getId() {
		return id;
	}
	public static void setId(String id) {
		loraserverConfigurationFields.id = id;
	}
	public static boolean isServerConfiguration() {
		return serverConfiguration;
	}
	public static void setServerConfiguration(boolean serverConfiguration) {
		loraserverConfigurationFields.serverConfiguration = serverConfiguration;
	}
	public static boolean isLoRaServer() {
		return LoRaServer;
	}
	public static void setLoRaServer(boolean loRaServer) {
		LoRaServer = loRaServer;
	}
	
	
	
}
