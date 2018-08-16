package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

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
					 byte[] message = resp.getBody();
					 list.add(new String(message,StandardCharsets.UTF_8));
					 count++;
				 }
			}
			
			response.getWriter().println(list.toString());
	}
}
