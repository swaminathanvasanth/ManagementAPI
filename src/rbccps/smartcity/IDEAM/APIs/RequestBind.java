package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

public class RequestBind extends HttpServlet 
{
	static String ldap_pwd;
	static String rmq_pwd;
	
	public void readldappwd() {
		
		try
		{
			BufferedReader br=new BufferedReader(new FileReader("/etc/pwd"));
			
			ldap_pwd=br.readLine();			
			br.close();
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void readbrokerpassword() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/etc/rmqpwd"));

			rmq_pwd = br.readLine();

			br.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		readbrokerpassword();
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
	
		routingKey=request.getHeader("routingKey");
		
		if(routingKey== null)
		{
			routingKey="#";
		}
		

		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
		
		
		Connection connection;
		Channel channel=null;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(rmq_pwd);
		factory.setVirtualHost("/");
		factory.setHost("rabbitmq");
		factory.setPort(5672);
		
		if(!username.equalsIgnoreCase(queue.split("\\.")[0]))
		{
			response.setStatus(401);
			response.getWriter().println("You do not have access to unbind this queue");
			return;
		}
		
		//If the exchange and queue belongs to the same device
		
		if(queue.split("\\.")[0].equalsIgnoreCase(exchange.split("\\.")[0]))
		{
			try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				channel.queueBind(queue,exchange,routingKey,args);
				response.getWriter().println("Bind Queue OK");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				response.getWriter().println("Unable to bind queue");
			}
		}
		else
		{
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldapd:8389/dc=smartcity");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=smartcity");
			env.put(Context.SECURITY_CREDENTIALS, ldap_pwd);
			
			DirContext ctx=null;
			
			try 
			{
				ctx = new InitialDirContext(env);
			} 
			catch (NamingException e1) 
			{
				e1.printStackTrace();
			}
			
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchControls.setCountLimit(10);
			NamingEnumeration<SearchResult> namingEnumeration=null;
			
			try 
			{
				namingEnumeration = ctx.search("description="+queue.split("\\.")[0]+",description=share,description=broker,uid="+exchange.split("\\.")[0]+",cn=devices", "(description=*)", new Object[]{}, searchControls);
			} 
			catch (NamingException e1) 
			{
				response.getWriter().println("Share entry does not exist");
				return;
			}
			
			try 
			{
				while (namingEnumeration.hasMore()) 
				{
				   SearchResult sr = namingEnumeration.next();
				   
				   long validity=Long.parseLong(sr.getAttributes().get("validity").toString().split(":")[1].trim().split("\\.")[0]);
				   
				   if(Instant.now().getEpochSecond()<validity)
				   {
						try 
						{
							connection = factory.newConnection();
							channel = connection.createChannel();
							Map<String, Object> args=new HashMap<String, Object>();
							args.put("durable", "true");
							channel.queueBind(queue,exchange,routingKey,args);
							response.getWriter().println("Bind Queue OK");
							break;
						}
						catch(Exception e)
						{
							e.printStackTrace();
							response.getWriter().println("Unable to bind queue");
						}
				   }
				   else
				   {
					   response.getWriter().println("Your data lease time has expired");
				   }
				}
				
				ctx.close();
			} 
			catch (NamingException e1) 
			{
				e1.printStackTrace();
			}
			
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		readbrokerpassword();
		
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		
		routingKey=request.getHeader("routingKey");
		
		if(routingKey== null)
		{
			routingKey="#";
		}
		
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
		
		if (apikey==null)
		{
			apikey=request.getParameter("apikey");
		}
		
		if(!username.equalsIgnoreCase(queue.split("\\.")[0]))
		{
			response.setStatus(401);
			response.getWriter().println("You do not have access to unbind this queue");
			return;
		}

		Connection connection;
		Channel channel=null;
		ConnectionFactory factory = new ConnectionFactory();
			
		factory.setUsername("admin.ideam");
		factory.setPassword(rmq_pwd);
		factory.setVirtualHost("/");
		factory.setHost("rabbitmq");
		factory.setPort(5672);
		
		
		if(queue.split("\\.")[0].equalsIgnoreCase(exchange.split("\\.")[0]))
		{
			try {
				connection = factory.newConnection();
				channel = connection.createChannel();
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				channel.queueUnbind(queue,exchange,routingKey,args);
				response.getWriter().println("Unbind Queue OK");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				response.getWriter().println("Unable to unbind queue");
			}
		}
		else
		{
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldapd:8389/dc=smartcity");
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=smartcity");
			env.put(Context.SECURITY_CREDENTIALS, ldap_pwd);
			
			DirContext ctx=null;
			
			try 
			{
				ctx = new InitialDirContext(env);
			} 
			catch (NamingException e1) 
			{
				e1.printStackTrace();
			}
			
			SearchControls searchControls = new SearchControls();
			searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			searchControls.setCountLimit(10);
			NamingEnumeration<SearchResult> namingEnumeration=null;
			
			try 
			{
				namingEnumeration = ctx.search("description="+queue.split("\\.")[0]+",description=share,description=broker,uid="+exchange.split("\\.")[0]+",cn=devices", "(description=*)", new Object[]{}, searchControls);
			} 
			catch (NamingException e1) 
			{
				response.setStatus(401);
				return;
			}

			try 
			{
				connection = factory.newConnection();
				channel = connection.createChannel();
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				channel.queueUnbind(queue,exchange,routingKey,args);
				response.getWriter().println("Unbind Queue OK");
				ctx.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				response.getWriter().println("Unable to unbind queue");
			}
			
		}
	}
}
