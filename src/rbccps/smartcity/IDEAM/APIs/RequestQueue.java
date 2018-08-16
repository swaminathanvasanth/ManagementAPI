package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RequestQueue extends HttpServlet 
{
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String queue=request.getRequestURI().split("/")[3];
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");

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
			channel.queueDeclare(queue, true, false, false, null);
			connection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		response.getWriter().println("Created Queue "+queue);
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String queue=request.getRequestURI().split("/")[3];
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");

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
			channel.queueDelete(queue);
			connection.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		response.getWriter().println("Deleted Queue "+queue);
	}
	
	
}
