package rbccps.smartcity.IDEAM.registerapi.catalog;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import rbccps.smartcity.IDEAM.APIs.RequestRegister;
import rbccps.smartcity.IDEAM.registerapi.parser.entity;
import rbccps.smartcity.IDEAM.urls.URLs;


public class uCat {
	
	static String _url;
	static String response = null;
	static  int responseCode ;
	
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

	public static int postCat(String _dataSchema){

		//System.out.println("+++++++++++In on-board uCat Block+++++++++++");
		
		try 
		{
			_url = URLs.getuCatURL();
			String _postData;
			_postData = _dataSchema;
			
			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");	
			
			URL url = new URL(_url + "?id=" + entity.getEntityID());

			//System.out.println("uCat Entry URL : "+url.toString());
			//System.out.println("Data in body is : "+_dataSchema);
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/json");
			
			conn.setRequestProperty("no-check","1");
			conn.setRequestProperty("pwd", "local123");
			conn.setRequestProperty("Content-Length",
					String.valueOf(postDataBytes.length));
			
			conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            
            responseCode = conn.getResponseCode();
            
            Reader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			
            StringBuilder sb = new StringBuilder();
			
            for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			
            response = sb.toString();
			//System.out.println(response);
			//System.out.println(responseCode);
		} 
		
		catch (Exception e) 
		{
			e.printStackTrace();
			return -1;
		} 
		
		return responseCode; 
	}

	
	public static int deleteCat(String _id){

		//System.out.println("+++++++++++In on-board uCat Block+++++++++++");
		
		try 
		{
			_url = URLs.getuCatURL();
			
			URL url = new URL(_url + "?id=" + entity.getEntityID());

			//System.out.println("uCat Entry URL : "+url.toString());
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type","application/json");
			conn.setRequestProperty("no-check","1");
			conn.setRequestProperty("pwd", "local123");
			
			conn.setDoOutput(true);
            
			responseCode = conn.getResponseCode();
            
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			
			StringBuilder sb = new StringBuilder();
			
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			
			response = sb.toString();
			
			//System.out.println(response);
			//System.out.println(responseCode);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		return responseCode; 
	}

	
}
