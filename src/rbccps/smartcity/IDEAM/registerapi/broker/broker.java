package rbccps.smartcity.IDEAM.registerapi.broker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import rbccps.smartcity.IDEAM.APIs.RequestRegister;
import rbccps.smartcity.IDEAM.urls.URLs;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class broker {

	static JsonObject response_jsonObject;
	static JsonObject publish_jsonObject;

	static String _url;
	static String _value;
	static String response;
	static String password = "";
	static String _message;


	public static String createExchange(String resourceID) 
	{
		response = null;
		System.out.println("+++++++++++In createExchange Block+++++++++++");
		
		try 
		{
			Pool.getAdminChannel().exchangeDeclare(resourceID, "topic",true);
			response="Created Exchange "+resourceID;  
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}

		
		return response;

	}

	public static String createQueue(String resourceID) {
		
		response = null;
		System.out.println("+++++++++++In createQueue Block+++++++++++");
			
		try
		{	
			if (resourceID.endsWith(".priority")||(resourceID.equals("database")))
			{
				Pool.getAdminChannel().queueDeclare(resourceID, true, false, false, null);
			}
			else
			{
				Map<String, Object> args = new HashMap<String, Object>();
				args.put("x-queue-mode", "lazy");
				
				Pool.getAdminChannel().queueDeclare(resourceID, true, false, false, args);

			}
			response="Created Queue "+resourceID;
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		return response;

	}

	public static String createBinding(String resourceID, String queueID) {

		System.out.println("+++++++++++In createBinding Block+++++++++++");

		String response = "";
		
		response = null;
			
		try
		{
			
			Map<String, Object> args=new HashMap<String, Object>();
			args.put("durable", "true");
			
			Pool.getAdminChannel().queueBind(queueID, resourceID, "#",args);
			response="Bind queue OK"; 
			
			System.out.println("Bound "+resourceID+" to "+queueID);
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;
	}


	public static String deleteExchange(String resourceID) {

		
		response = null;
		System.out.println("+++++++++++In deleteExchange Block+++++++++++");
		
		try 
		{	
			Pool.getAdminChannel().exchangeDelete(resourceID);
			response="Deleted Exchange "+resourceID;  
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(response);
		return response;

	}

	public static String deleteQueue(String resourceID) {
		
		response = null;
		System.out.println("+++++++++++In deleteQueue Block+++++++++++");
			
		try
		{	
			Pool.getAdminChannel().queueDelete(resourceID);
			response="Deleted Queue "+resourceID;
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println(response);
		return response;

	}

	
	public static String publish(String _entityID, String _permission, String _requestorID, String _validity) 
	{
		
		JsonObject response=new JsonObject();
			
		try
		{	
			JsonObject object=new JsonObject();
			
			object.addProperty("requestor", _requestorID);
			object.addProperty("permission", _permission);
			object.addProperty("validity", _validity);
			object.addProperty("timestamp", Instant.now().toString());

			Pool.getAdminChannel().basicPublish(_entityID, "#", null, object.toString().getBytes("UTF-8"));
			
			response.addProperty("status","Follow request has been made to "+_entityID+" with permission "+_permission+" at "+Instant.now());
			
			return response.toString();
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
			response.addProperty("status","Failed to make follow request");
		}
		
		return response.toString();

	}
}
