package rbccps.smartcity.IDEAM.registerapi.video;

public class videoserverConfigurationFields {

	public static boolean serverConfiguration = false;
	public static boolean videoServer = false;
	
	public static String serverURL = "";
	public static String serverMethod = "";
	public static String serverUsername = "";	
	public static String serverPassword = "";
	public static String playbackurl = "";
	public static boolean url;
		
	public static boolean isServerConfiguration() {
		return serverConfiguration;
	}
	public static void setServerConfiguration(boolean serverConfiguration) {
		videoserverConfigurationFields.serverConfiguration = serverConfiguration;
	}
	public static boolean isVideoServer() {
		return videoServer;
	}
	public static void setVideoServer(boolean videoServer) {
		videoserverConfigurationFields.videoServer = videoServer;
	}
	public static String getPlaybackurl() {
		return playbackurl;
	}
	public static void setPlaybackurl(String playbackurl) {
		videoserverConfigurationFields.playbackurl = playbackurl.replaceAll("^\"|\"$", "");
	}
	public static boolean isUrl() {
		return url;
	}
	public static void setUrl(boolean url) {
		videoserverConfigurationFields.url = url;
	}
	public static String getServerURL() {
		return serverURL;
	}
	public static void setServerURL(String serverURL) {
		videoserverConfigurationFields.serverURL = serverURL.replaceAll("^\"|\"$", "");
	}
	public static String getServerMethod() {
		return serverMethod;
	}
	public static void setServerMethod(String serverMethod) {
		videoserverConfigurationFields.serverMethod = serverMethod;
	}
	public static String getServerUsername() {
		return serverUsername;
	}
	public static void setServerUsername(String serverUsername) {
		videoserverConfigurationFields.serverUsername = serverUsername;
	}
	public static String getServerPassword() {
		return serverPassword;
	}
	public static void setServerPassword(String serverPassword) {
		videoserverConfigurationFields.serverPassword = serverPassword;
	}
}
