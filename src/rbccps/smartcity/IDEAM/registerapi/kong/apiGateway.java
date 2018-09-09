package rbccps.smartcity.IDEAM.registerapi.kong;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import rbccps.smartcity.IDEAM.APIs.RequestRegister;
import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;
import rbccps.smartcity.IDEAM.registerapi.parser.entity;
import rbccps.smartcity.IDEAM.urls.URLs;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class apiGateway {

	static String _value;
	static JsonElement _apikey_JsonElement;
	static String _apikey;
	static String _providerID;
	static URL kong;
	static JsonParser parser;
	static JsonElement jsonTree;
	static JsonObject jsonObject;
	static JsonObject response_jsonObject;
	static String _url;
	static String security_level;
	static Map<Integer,Integer> bits;
	
	static
	{
		bits=new HashMap<>();
		bits.put(1, 6);
		bits.put(2,11);
		bits.put(3, 22);
		bits.put(4, 43);
		bits.put(5, 86);
	}
	
	public static String createUser(String resourceID) 
	{
		_url = URLs.getApiGatewayURL();
		_value = resourceID;
		String response = null;
		parser = new JsonParser();

		try 
		{
			URL url = new URL(_url + "/consumers/");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("username", _value);

			StringBuilder postData = new StringBuilder();
			
			for (Map.Entry<String, Object> param : params.entrySet()) 
			{
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
			conn.setConnectTimeout(3000);
			conn.getOutputStream().write(postDataBytes);

			Reader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			
			response = sb.toString();

			//System.out.println(response);

			if (response != null) 
			{
				//System.out.println(response);
				jsonTree = parser.parse(response);
				jsonObject = jsonTree.getAsJsonObject();
				//System.out.println(jsonObject.toString());
			}
		} 
		
		catch (SocketTimeoutException s) 
		{
			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject.addProperty("Reason", "Server Not Reachable");
			response = response_jsonObject.toString();
			//System.out.println("--------------");
			//System.out.println(response);
			//System.out.println("--------------");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject
					.addProperty("Reason",
							"ID not available. Please Use a Unique ID for Registration.");

			response = response_jsonObject.toString();
			//System.out.println("--------------");
			//System.out.println(response);
			//System.out.println("--------------");

		}

		return response;

		// Add a FLAG to process the Registration further

	}

	public static String deleteUser(String resourceID) 
	{
		_url = URLs.getApiGatewayURL();
		_value = resourceID;
		String response = null;
		parser = new JsonParser();

		//System.out.println(_value);
		try 
		{
			URL url = new URL(_url + "/consumers/" + _value);
		
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			
			//System.out.println(conn.getResponseCode());
			response = "Deleted consumer in KONG";
			

		} 
		
		catch (Exception e) 
		{
			response = "Failed to delete";
		}
		
		return response;

		// Add a FLAG to process the Registration further

	}

	public static String generateAPIKey(String resourceID) throws Exception 
	{
		URL url = null;
		String response = null;
		security_level=RequestRegister.getSecurityLevel();
		
		if(Integer.parseInt(security_level)>5)
			return "Security level must be between 1-5";
	
		if (loraserverConfigurationFields.serverConfiguration) 
		{
			if (loraserverConfigurationFields.LoRaServer) 
			{
				System.out.println("apiGateway -- loraserverConfigurationFields.LoRaServer = TRUE");
				
				if (loraserverConfigurationFields.appKeyFlag) 
				{
					System.out.println("LoRA Key Available");
					
					String apiKey = loraserverConfigurationFields.getAppKey().replaceAll("^\"|\"$", "");
					
					System.out.println("LoRA Key Available"+apiKey);
					
					url = new URL(_url + "/consumers/" + resourceID + "/key-auth");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					String key = "key="+apiKey;
					byte[] postDataBytes = key.getBytes("UTF-8");
					System.out.println("LoRA Key Available"+key);
					
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length",
							String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
		            conn.setDoInput(true);
		      
		            conn.getOutputStream().write(postDataBytes);
							            
					Reader in = new BufferedReader(new InputStreamReader(
							conn.getInputStream(), "UTF-8"));

					StringBuilder sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;)
						sb.append((char) c);
					response = sb.toString();
					System.out.println(response);

					parser = new JsonParser();
					jsonTree = parser.parse(response);
					jsonObject = jsonTree.getAsJsonObject();

					_apikey_JsonElement = jsonObject.get("key");

					_apikey = _apikey_JsonElement.toString();

					System.out.println("APIKey is : " + _apikey + " Used from LoRa");

					entity.setEntityapikey(_apikey);
	
				} 
				
				else 
				{
					url = new URL(_url + "/consumers/" + resourceID + "/key-auth");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					
					
					String key = "key="+genAPIKey(bits.get(Integer.parseInt(security_level)));
					
					byte[] postDataBytes = key.getBytes("UTF-8");					
					
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
			        conn.setDoInput(true);
			      
		            conn.getOutputStream().write(postDataBytes);

					Reader in = new BufferedReader(new InputStreamReader(
								conn.getInputStream(), "UTF-8"));

					StringBuilder sb = new StringBuilder();
						for (int c; (c = in.read()) >= 0;)
							sb.append((char) c);
						
					response = sb.toString();
					
					if(conn.getResponseCode()>=400&&conn.getResponseCode()<=500)
					{
						while(conn.getResponseCode()>=400&&conn.getResponseCode()<=500)
						{
							conn = (HttpURLConnection) url.openConnection();
							
							
							key = "key="+genAPIKey(bits.get(Integer.parseInt(security_level)));
							postDataBytes = key.getBytes("UTF-8");								
							conn.setRequestMethod("POST");
							conn.setRequestProperty("Content-Type",
										"application/x-www-form-urlencoded");
							conn.setRequestProperty("Content-Length",
							String.valueOf(postDataBytes.length));
							conn.setDoOutput(true);
					        conn.setDoInput(true);
					      
					        conn.getOutputStream().write(postDataBytes);
							in = new BufferedReader(new InputStreamReader(
										conn.getInputStream(), "UTF-8"));

							sb = new StringBuilder();
							for (int c; (c = in.read()) >= 0;)
								sb.append((char) c);
							
							response = sb.toString();
						}
					}
					
					//System.out.println(response);

					parser = new JsonParser();
					jsonTree = parser.parse(response);
					jsonObject = jsonTree.getAsJsonObject();

					_apikey_JsonElement = jsonObject.get("key");

					_apikey = _apikey_JsonElement.toString();

					//System.out.println("APIKey is : " + _apikey + " Generated for LoRa");
					//System.out.println(key);

					entity.setEntityapikey(_apikey);
				} 
			}
		} 
		
		else 
		{
			url = new URL(_url + "/consumers/" + resourceID + "/key-auth");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
		
			String key = "key="+genAPIKey(bits.get(Integer.parseInt(security_level)));
			
			byte[] postDataBytes = key.getBytes("UTF-8");				
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
	        conn.setDoInput(true);
	      
	        conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

			StringBuilder sb = new StringBuilder();
			
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			
			response = sb.toString();
			
			if(conn.getResponseCode()>=400&&conn.getResponseCode()<=500)
			{
				while(conn.getResponseCode()>=400&&conn.getResponseCode()<=500)
				{
					conn = (HttpURLConnection) url.openConnection();
					key = "key="+genAPIKey(bits.get(Integer.parseInt(security_level)));
					postDataBytes = key.getBytes("UTF-8");						
					conn.setRequestMethod("POST");
					conn.setRequestProperty("Content-Type",
								"application/x-www-form-urlencoded");
					conn.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
					conn.setDoOutput(true);
			        conn.setDoInput(true);
			      
			        conn.getOutputStream().write(postDataBytes);
					in = new BufferedReader(new InputStreamReader(
								conn.getInputStream(), "UTF-8"));

					sb = new StringBuilder();
					for (int c; (c = in.read()) >= 0;)
						sb.append((char) c);
					
					response = sb.toString();
				}
			}
			
			
			//System.out.println(response);

			parser = new JsonParser();
			jsonTree = parser.parse(response);
			jsonObject = jsonTree.getAsJsonObject();

			_apikey_JsonElement = jsonObject.get("key");

			_apikey = _apikey_JsonElement.toString();

			//System.out.println("APIKey is : " + _apikey + " Generated for LoRa");

			entity.setEntityapikey(_apikey);

		}

		
		return response;
		// Add a FLAG to process the Registration further

	}

	public static String assignWhiteListGroup(String resourceID,String serviceType) 
	{	// Assign Consumer to WhiteList based on resourceType

		_value = resourceID;
		String response = null;
		String _url = URLs.getApiGatewayURL();

		try 
		{
			URL url = new URL(_url + "/consumers/" + _value + "/acls");
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("group", serviceType);

			StringBuilder postData = new StringBuilder();
			
			for (Map.Entry<String, Object> param : params.entrySet()) 
			{
				if (postData.length() != 0)
					postData.append('&');
				
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length",String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			
			response = sb.toString();
			//System.out.println(response);

		} 
		
		catch (IOException e) 
		{
			e.printStackTrace();
			response = "Cannot add to ServiceList.";
		}

		return response;

		// Add a FLAG to process the Registration further

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
