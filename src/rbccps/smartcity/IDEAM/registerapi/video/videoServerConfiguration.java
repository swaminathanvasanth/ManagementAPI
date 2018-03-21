package rbccps.smartcity.IDEAM.registerapi.video;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;
import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationParser;
import rbccps.smartcity.IDEAM.registerapi.parser.createEntityJSONParser;
import rbccps.smartcity.IDEAM.registerapi.parser.entity;
import rbccps.smartcity.IDEAM.urls.URLs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class videoServerConfiguration {

	static JsonObject response_jsonObject;
	static String jwtKey;
	static String _url;
	static String _value;
	static String response;
	static loraserverConfigurationParser serverConfigurationParser_LoRa;
	static String credentials;
	static JsonObject request;

	static JsonElement serverConfiguration_credentials;
	static JsonElement serverConfiguration_configuration;

	static String _serverConfiguration_credentials = null;
	static String _serverConfiguration_configuration = null;

	static JsonElement serverConfiguration_credentials_jsonTree;
	static JsonElement serverConfiguration_configuration_jsonTree;

	static JsonObject serverConfiguration_credentials_jsonObject = null;
	public static JsonObject serverConfiguration_configuration_jsonObject = null;
	
	static TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
			// No need to implement.
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
			// No need to implement.
		}
	} };

	public static String registervideoEntity(String entityID) {

		// _url = URLs.getVideoserverURL();
		
		_url = videoserverConfigurationFields.getServerURL().trim().replaceAll("^\"|\"$", "");
		
		// Add Path and Playback
		_url = _url + "/create_stream?id=" + entity.getEntityID() + "&playurl=" + videoserverConfigurationFields.getPlaybackurl().replaceAll("^\"|\"$", "");
		_url = _url.trim();
		
		System.out.println(_url);
		
		response = null;
		System.out.println("+++++++++++In create video Entity Block+++++++++++");

		try {
			System.out.println("In get video Onboarding ");
						
			URL url = new URL(_url);
			
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
			//((HttpsURLConnection) conn).setDefaultSSLSocketFactory(sc
			//		.getSocketFactory());
			
			conn.setRequestMethod(videoserverConfigurationFields.getServerMethod().trim().replaceAll("^\"|\"$", ""));
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("no-check", "true");
			conn.setRequestProperty("pwd", videoserverConfigurationFields.getServerPassword().trim().replaceAll("^\"|\"$", ""));

			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			Reader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println("In Onboard video Entity");
			System.out.println(response);
			System.out.println("In Onboard video Entity");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject
					.addProperty("Reason", "Cannot Onboard Video camera in VideoServer. POST Error.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");

			System.out
					.println("+++++++++++In Onboarding catch Block+++++++++++"
							+ e.toString());
			response = response_jsonObject.toString();

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response;

		// Add a FLAG to process the Registration further
	}

}
