package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RequestBind extends HttpServlet 
{
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		
		try
		{
			routingKey=request.getHeader("routingKey");
		}
		catch(Exception e)
		{
			System.out.println("Routing key not specified");
		}
		finally
		{
			routingKey="#";
		}
		
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
		
		System.out.println(queue+" "+exchange+" "+routingKey+" "+username+" "+apikey);

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
			channel.queueBind(queue,exchange,routingKey,null);
			System.out.println("Bind queue OK");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		response.getWriter().println("Bind Queue OK");
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		
		try
		{
			routingKey=request.getHeader("routingKey");
		}
		catch(Exception e)
		{
			System.out.println("Routing key not specified");
		}
		finally
		{
			routingKey="#";
		}
		
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
			channel.queueUnbind(queue,exchange,routingKey,null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		response.getWriter().println("Unbind Queue OK");
	}
}
