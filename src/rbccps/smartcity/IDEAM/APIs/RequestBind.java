package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
import java.util.Hashtable;

public class RequestBind extends HttpServlet 
{
	static String password;
	public void readldappwd() {
		System.out.println("constructer to LDAP bind");
		
		try
		{
			BufferedReader br=new BufferedReader(new FileReader("/etc/pwd"));
			
			password=br.readLine();			
			br.close();
			
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		
		try
		{
			routingKey=request.getHeader("routingKey");
		}
		catch(Exception e)
		{}
		finally
		{
			routingKey="#";
		}
		
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
		
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://ldapd:8389");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=smartcity");
		env.put(Context.SECURITY_CREDENTIALS, password);
		
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
			namingEnumeration = ctx.search("", "(description=share,description=broker,uid="+exchange.split(".")[0]+"cn=devices,dc=smartcity)", new Object[]{}, searchControls);
		} 
		catch (NamingException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			while (namingEnumeration.hasMore()) 
			{
			    SearchResult sr = namingEnumeration.next();
			    System.out.println("Name " + sr.getName());
			}
		} 
		catch (NamingException e1) 
		{
			e1.printStackTrace();
		}
		
		try 
		{
			ctx.close();
		} 
		catch (NamingException e1) 
		{
			e1.printStackTrace();
		}

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
			response.getWriter().println("Bind Queue OK");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			response.getWriter().println("Unable to bind queue");
		}
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
			response.getWriter().println("Unbind Queue OK");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			response.getWriter().println("Unable to unbind queue");
		}
	}
}
