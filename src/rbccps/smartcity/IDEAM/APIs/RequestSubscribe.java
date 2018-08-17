package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;

import java.io.StringWriter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class RequestSubscribe extends HttpServlet 
{
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String queue=request.getRequestURI().split("/")[3];
		int num=Integer.parseInt(request.getRequestURI().split("/")[4]);
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("apikey");

		Connection connection;
		Channel channel=null;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername(username);
		factory.setPassword(apikey);
		factory.setVirtualHost("/");
		factory.setHost("rabbitmq");
		factory.setPort(5672);
		
		
		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		 int count=1;
	        
	        ArrayList<String> list=new ArrayList<String>();
	        
	        GetResponse resp=null;

	        JSONArray res=new JSONArray();
	        Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			while(count<=num)
			{
				 try 
				 {
					resp=channel.basicGet(queue, true);
				 } 
				 catch (Exception e) 
				 {
					e.printStackTrace();
				 }
				 
				 if(resp==null)break;
				 
				 else
				 {

					 byte[] body = resp.getBody();
					 String data=new String(body,StandardCharsets.UTF_8);
					 JSONParser parser = new JSONParser();
					 JSONObject obj = new JSONObject();
					 JSONObject message=null;
					 
					 try 
					 {
						message=(JSONObject)parser.parse(data);
					 } 
					 catch (Exception e) 
					 {
						e.printStackTrace();
					 }
					 obj.put("data", message);
					 obj.put("datasource", resp.getEnvelope().getExchange());
					 obj.put("datatype", resp.getEnvelope().getRoutingKey());
		
					 res.add(obj);
					 count++;
				 }
			}
			response.getWriter().println(gson.toJson(res));

	}
}
