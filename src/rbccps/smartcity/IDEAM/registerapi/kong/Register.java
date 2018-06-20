package rbccps.smartcity.IDEAM.registerapi.kong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import rbccps.smartcity.IDEAM.registerapi.ldap.addEntryToLDAP;
import rbccps.smartcity.IDEAM.urls.URLs;
import java.security.SecureRandom;

public class Register {

	static String _value;
	static String _apikey;
	static String _providerID;
	static String kong_url;
	static String broker_url;

	public static String createUser(String resourceID) {
		// TODO Auto-generated method stub
		_value = resourceID;
		kong_url=URLs.getApiGatewayURL();
		String response = null;

		try {
            
			
			URL url = new URL(kong_url+"/consumers/");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("username", _value);

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(
						String.valueOf(param.getValue()), "UTF-8"));
			}
			
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			response = "ID not available. Please Use a Unique ID for Registration.";
		}

		return response;

		// Add a FLAG to process the Registration further

	}

	public static String generateAPIKey(String resourceID) throws Exception {
		// TODO Auto-generated method stub

		URL url = new URL(kong_url+"/consumers/" + resourceID
				+ "/key-auth");

        String key=genAPIKey(32);
        String urlParameters = "key="+key;
        System.out.println(key);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
	    writer.write(urlParameters);
	    writer.flush();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
	  
		Reader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));

		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char) c);
		String response = sb.toString();
		System.out.println(response);

		Object obj = JSONValue.parse(response);
		JSONObject jsonObject = (JSONObject) obj;

		_apikey = (String) jsonObject.get("key");
		System.out.println("APIKey is : " + _apikey);

		return _apikey;
		// Add a FLAG to process the Registration further

	}

	public static String assignWhiteListGroup(String resourceID, String serviceType) {

		// Assign Consumer to WhiteList based on resourceType

		_value = resourceID;
		String response = null;
				
		try {
					URL url = new URL(kong_url+"/consumers/"+_value+"/acls");
					Map<String, Object> params = new LinkedHashMap<>();
					params.put("group", serviceType);

					StringBuilder postData = new StringBuilder();for (Map.Entry<String, Object> param : params.entrySet()) {
						if (postData.length() != 0)
							postData.append('&');
						postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
						postData.append('=');
						postData.append(URLEncoder.encode(
								String.valueOf(param.getValue()), "UTF-8"));
					}
					byte[] postDataBytes = postData.toString().getBytes("UTF-8");

					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length",
							String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					conn.getOutputStream().write(postDataBytes);
					Reader in = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;)
						sb.append((char) c);
					response = sb.toString();
					System.out.println(response);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					response = "Cannot add to ServiceList.";
				}

				return response;

				// Add a FLAG to process the Registration further

			}
	
	
	public static String createQueue(String resourceID){
		_value = resourceID;
		String response = null;
		System.out.println("+++++++++++In createQueue Block+++++++++++");
				
		try {
			        broker_url=URLs.getBrokerURL();
					URL url = new URL(broker_url+"/queue");

					String _postData;
					_postData = "{\"name\": \""+ resourceID + "\"}";
					
					System.out.println("+++++++++++In createQueue try Block+++++++++++" + "\n" + _postData.toString() + "\n");
									
					byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length",
							String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
					conn.getOutputStream().write(postDataBytes);
					Reader in = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "UTF-8"));
					StringBuilder sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;)
						sb.append((char) c);
					response = sb.toString();
					System.out.println(response);
					System.out.println("In Queue Creation");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					System.out.println("+++++++++++In createQueue catch Block+++++++++++" + e.toString());
					response = "Cannot add to ServiceList.";
				}
				return response;

				// Add a FLAG to process the Registration further

	}

	public static String updateLDAP(String providerID, String resourceID, String apiKey) {
		// TODO Auto-generated method stub

		_providerID = providerID;
		addEntryToLDAP addEntryToLdap = new addEntryToLDAP();
		String addEntry_Response;
		if (addEntryToLdap.addEntry(_providerID, _value, _apikey)) {
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
	
	public static String genAPIKey(int len)
	{
		String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
		   return sb.toString();
	}

}
