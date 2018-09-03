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

	public static void readbrokerpassword() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/etc/rmqpwd"));

			password = br.readLine();

			br.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String createExchange(String resourceID) {

		readbrokerpassword();
		response = null;
		System.out.println("+++++++++++In createExchange Block+++++++++++");

		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
		
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDeclare(resourceID, "topic",true);
			response="Created Exchange "+resourceID;  
			connection.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		return response;

	}

	public static String createQueue(String resourceID) {
		
		response = null;
		System.out.println("+++++++++++In createQueue Block+++++++++++");

		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
			
		try
		{
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.queueDeclare(resourceID, true, false, false, null);
			response="Created Queue "+resourceID;
			connection.close();
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;

	}

	public static String createBinding(String resourceID, String queueID) {

		System.out.println("+++++++++++In createBinding Block+++++++++++");

		String response = "";
		
		response = null;

		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
			
		try
		{
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			Map<String, Object> args=new HashMap<String, Object>();
			args.put("durable", "true");
			channel.queueBind(queueID, resourceID, "#",args);
			response="Bind queue OK"; 
			System.out.println("Bound "+resourceID+" to "+queueID);
			connection .close();
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

		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
		
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
			channel.exchangeDelete(resourceID);
			response="Deleted Exchange "+resourceID;  
			connection.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
		return response;

	}

	public static String deleteQueue(String resourceID) {
		
		response = null;
		System.out.println("+++++++++++In deleteQueue Block+++++++++++");

		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
			
		try
		{
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			channel.queueDelete(resourceID);
			response="Deleted Queue "+resourceID;
			connection.close();
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return response;

	}

	
	public static String publish(String _entityID, String _permission, String _requestorID, String _validity) 
	{
		readbrokerpassword();
		Connection connection;
		Channel channel;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(password);
		factory.setVirtualHost("/");
		factory.setHost("broker");
		factory.setPort(5672);
		
		JsonObject response=new JsonObject();
			
		try
		{
			connection = factory.newConnection();
			channel = connection.createChannel();
			
			JsonObject object=new JsonObject();
			
			object.addProperty("requestor", _requestorID);
			object.addProperty("permission", _permission);
			object.addProperty("validity", _validity);
			object.addProperty("timestamp", Instant.now().toString());

			channel.basicPublish(_entityID, "#", null, object.toString().getBytes("UTF-8"));
			
			response.addProperty("status","Follow request has been made to "+_entityID+" with permission "+_permission+" at "+Instant.now());
	
			connection.close();
			
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
