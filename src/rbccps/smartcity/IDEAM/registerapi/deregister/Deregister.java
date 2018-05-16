package rbccps.smartcity.IDEAM.registerapi.deregister;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

import rbccps.smartcity.IDEAM.urls.*;

public class Deregister {
	
	public static String removeEntries(String key, String id)throws Exception
	{
		String response="";
		URL url = new URL("http://kong:8000/api/1.0.0/register");
		
		String data =  "{\"entityID\":\""+id+"\"}";
		
		System.out.println(data);
		
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("DELETE");
		conn.setDoOutput(true);
		conn.setRequestProperty("Content-Type","application/json");
		conn.setRequestProperty("apikey",key);
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
	    out.write(data);
	    out.close();

		Reader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream(), "UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (int c; (c = in.read()) >= 0;)
			sb.append((char) c);
		response = sb.toString();
		
		return response;
	}

}
