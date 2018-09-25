package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import rbccps.smartcity.IDEAM.registerapi.broker.Pool;
import rbccps.smartcity.IDEAM.registerapi.ldap.LDAP;

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
	
	static String[] decoded_authorization_datas = new String[2];
	static boolean isOwner = false;
	static JsonObject jsonObject;
	
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
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
	
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		jsonObject = new JsonObject();
		
		routingKey=request.getHeader("routingKey");
		System.out.println(routingKey);
		
		if(routingKey== null)
		{
			routingKey="#";
		} else if (! routingKey.equals("#")) {
			response.setStatus(401);
			jsonObject.addProperty("status", "Failure");
			jsonObject.addProperty("reason", "You do not have access to bind this routingKey to the queue");
			response.getWriter().println(jsonObject);
			return;
		}
		

		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
	
		
		decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
		decoded_authorization_datas[1] = request.getHeader("apikey");

					if ((LDAP.verifyProvider(queue, decoded_authorization_datas))) {
								System.out.println("Device belongs to owner");
								isOwner = true;
								username = queue;
							}


					if (!isOwner)
						if(!username.equalsIgnoreCase(queue.split("\\.")[0]))
							{
								response.setStatus(401);
								jsonObject.addProperty("status", "Failure");
								jsonObject.addProperty("reason", "You do not have access to bind this queue");
								response.getWriter().println(jsonObject);
								return;
							}
					
					System.out.println(username + "   ====   " + apikey + "   ====   " + isOwner);
					
					isOwner = false;
					
		
		//If the exchange and queue belongs to the same device
		
		if((queue.split("\\.")[0].equalsIgnoreCase(exchange.split("\\.")[0]))||(exchange.split("\\.")[1].equalsIgnoreCase("public")))
		{
			try 
			{	
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				Pool.getAdminChannel().queueBind(queue,exchange,routingKey,args);
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("info", "Bind Queue OK");
				response.getWriter().println(jsonObject);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Unable to bind queue");
				response.getWriter().println(jsonObject);
			}
		}
		else
		{
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldapd:8389");
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
			NamingEnumeration<SearchResult> namingEnumeration=null;
			
			try 
			{
				namingEnumeration = ctx.search("description="+exchange+",description=read,description=share,description=broker,uid="+username+",cn=devices,dc=smartcity", "(description=*)", new Object[]{}, searchControls);
			} 
			catch (NamingException e1) 
			{
				response.setStatus(404);
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Share entry does not exist");
				response.getWriter().println(jsonObject);
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
							//So that the binding survives a server restart
							
							Map<String, Object> args=new HashMap<String, Object>();
							args.put("durable", "true");
							
							Pool.getAdminChannel().queueBind(queue,exchange,routingKey,args);
							jsonObject.addProperty("status", "success");
							jsonObject.addProperty("info", "Bind Queue OK");
							response.getWriter().println(jsonObject);
							break;
						}
						catch(Exception e)
						{
							e.printStackTrace();
							jsonObject.addProperty("status", "Failure");
							jsonObject.addProperty("reason", "Unable to bind queue");
							response.getWriter().println(jsonObject);
						}
				   }
				   else
				   {
					   response.setStatus(401);
					   jsonObject.addProperty("status", "Failure");
					   jsonObject.addProperty("reason", "Your data lease time has expired");
					   response.getWriter().println(jsonObject);
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
		
		String queue=request.getRequestURI().split("/")[3];
		String exchange=request.getRequestURI().split("/")[4];
		
		String routingKey;
		jsonObject = new JsonObject();
		routingKey=request.getHeader("routingKey");
		
		if(routingKey== null)
		{
			routingKey="#";
		} else if (! routingKey.equals("#")) {
			response.setStatus(401);
			jsonObject.addProperty("status", "Failure");
			jsonObject.addProperty("reason", "You do not have access to unbind this routingKey to the queue");
			response.getWriter().println(jsonObject);
			return;
		}
		
		String username=request.getHeader("X-Consumer-Username");
		String apikey=request.getHeader("Apikey");
		
		if (apikey==null)
		{
			apikey=request.getParameter("apikey");
		}
				
		
		decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
		decoded_authorization_datas[1] = request.getHeader("apikey");

					if ((LDAP.verifyProvider(queue, decoded_authorization_datas))) {
								System.out.println("Device belongs to owner");
								isOwner = true;
								username = queue;	
							}


					if (!isOwner)
						if(!username.equalsIgnoreCase(queue.split("\\.")[0]))
						{
							response.setStatus(401);
							jsonObject.addProperty("status", "Failure");
							jsonObject.addProperty("reason", "You do not have access to unbind this queue");
							response.getWriter().println(jsonObject);
							return;
						}
		
					isOwner = false;
		
		if(queue.split("\\.")[0].equalsIgnoreCase(exchange.split("\\.")[0])||(exchange.split("\\.")[1].equalsIgnoreCase("public")))
		{
			try 
			{
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				Pool.getAdminChannel().queueUnbind(queue,exchange,routingKey,args);
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("info", "Unbind Queue OK");
				response.getWriter().println(jsonObject);
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Unable to unbind queue");
				response.getWriter().println(jsonObject);
			}
		}
		else
		{
			Hashtable<String, Object> env = new Hashtable<String, Object>();
			
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, "ldap://ldapd:8389");
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
			NamingEnumeration<SearchResult> namingEnumeration=null;
			
			try 
			{
				namingEnumeration = ctx.search("description="+exchange+",description=read,description=share,description=broker,uid="+username+",cn=devices,dc=smartcity", "(description=*)", new Object[]{}, searchControls);
			} 
			catch (NamingException e1) 
			{
				response.setStatus(401);
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Share entry does not exist");
				response.getWriter().println(jsonObject);
				return;
			}

			try 
			{
				Map<String, Object> args=new HashMap<String, Object>();
				args.put("durable", "true");
				Pool.getAdminChannel().queueUnbind(queue,exchange,routingKey,args);
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("info", "Unbind Queue OK");
				response.getWriter().println(jsonObject);
				ctx.close();
			}
			
			catch(Exception e)
			{
				e.printStackTrace();
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Unable to unbind queue");
				response.getWriter().println(jsonObject);
			}
			
		}
	}
}
