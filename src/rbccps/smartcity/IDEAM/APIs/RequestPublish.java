package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Basic;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestPublish extends HttpServlet 
{	
	static ExecutorService executor=Executors.newSingleThreadExecutor();
	
	static Connection connection = null;
	static Channel channel = null;
	static ConnectionFactory factory = null;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String[] requestURI;
		String exchange;
		String routingKey;
		String token;
		
		String X_Consumer_Username;
		String apikey;
		String body;
		
		publish pub=new publish();

		requestURI = request.getPathInfo().toString().split("/");
		X_Consumer_Username = request.getHeader("X-Consumer-Username");
		apikey = request.getHeader("apikey");
		
		exchange = requestURI[1];
		
		routingKey=request.getHeader("routingKey");
		
		if(routingKey==null)
		{
			routingKey="<unspecified>";
		}
		
		token=X_Consumer_Username+":"+apikey;
		body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		pub.token=token;
		pub.body=body;
		pub.exchange=exchange;
		pub.key=routingKey;

		response.setStatus(202);
		
		//Future task=executor.submit(pub);
		
		executor.execute(pub);	
		
		}
}

class publish implements Runnable
{
	String exchange,key,body,token;
	
	static Map<String,Channel> pool=new ConcurrentHashMap<String,Channel>();
	
	Connection connection = null;
	Channel channel = null;
	ConnectionFactory factory = null;
	
	publish()
	{
		this.exchange="";
		this.key="";
		this.body="";
		this.token="";
	}
	
	publish(String exchange,String key,String body,String token)
	{
		this.exchange=exchange;
		this.key=key;
		this.body=body;
		this.token=token;
	}

	@Override
	public void run() 
	{
		if(!pool.containsKey(token))
		{
			factory = new ConnectionFactory();
			factory.setUsername(token.split(":")[0]);
			factory.setPassword(token.split(":")[1]);
			factory.setVirtualHost("/");
			factory.setHost("broker");
			factory.setPort(5672);

			try 
			{
				connection = factory.newConnection();
				channel = connection.createChannel();
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
			
			pool.put(token, channel);		
		}
		else
		{
			if(!pool.get(token).isOpen())
			{
				factory = new ConnectionFactory();
				factory.setUsername(token.split(":")[0]);
				factory.setPassword(token.split(":")[1]);
				factory.setVirtualHost("/");
				factory.setHost("broker");
				factory.setPort(5672);

				try 
				{
					connection = factory.newConnection();
					channel = connection.createChannel();
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
				
				pool.replace(token, channel);		
			}
		}

		
		try 
		{
			pool.get(token).basicPublish(exchange, key, null, body.getBytes("UTF-8"));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
}

	
