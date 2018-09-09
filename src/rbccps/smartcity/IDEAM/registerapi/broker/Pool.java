package rbccps.smartcity.IDEAM.registerapi.broker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.Basic.Publish;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.xnio.ChannelExceptionHandler;

public class Pool 
{
	static Map<String,Channel> pool=new ConcurrentHashMap<String,Channel>();
	
	static Connection connection = null;
	static Channel channel = null;
	static ConnectionFactory factory = null;
	
	static String password;
	
	public static void readbrokerpassword() 
	{
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader("/etc/rmqpwd"));
			password = br.readLine();
			br.close();

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	
	public static Channel getChannel(String token)
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
		
		return pool.get(token);
	}
	
	public static Channel getAdminChannel()
	{
		if(!pool.containsKey("admin.ideam"))
		{
			readbrokerpassword();
			
			factory = new ConnectionFactory();
			factory.setUsername("admin.ideam");
			factory.setPassword(password);
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
			
			pool.put("admin.ideam", channel);		
		}
		
		else
		{
			if(!pool.get("admin.ideam").isOpen())
			{
				readbrokerpassword();
				
				factory = new ConnectionFactory();
				factory.setUsername("admin.ideam");
				factory.setPassword(password);
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
				
				pool.replace("admin.ideam", channel);		
			}
		}
		
		return pool.get("admin.ideam");
	}

}
