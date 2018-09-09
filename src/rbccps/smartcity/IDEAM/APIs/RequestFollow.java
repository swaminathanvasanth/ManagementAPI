package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import rbccps.smartcity.IDEAM.registerapi.broker.Pool;
import rbccps.smartcity.IDEAM.registerapi.lora.loraserverConfigurationFields;

/**
 * Servlet implementation class follow
 */

public class RequestFollow extends HttpServlet {

	public static final long serialVersionUID = 1L;
	static JSONObject responseObj;
	static String response;
	static String authorization;
	static String X_Consumer_Custom_ID;
	static String X_Consumer_Username;
	static String apikey;
	static String X_Consumer_Groups;
	static String body;

	static JsonParser parser;
	static JsonElement jsonTree;
	static JsonObject jsonObject;
	

	static JsonElement entityID;
	static String _entityID = null;
	
	static JsonElement requestorID;
	static String _requestorID = null;

	static JsonElement permission;
	static String _permission = null;

	static JsonElement validity;
	static String _validity = null;

	static rbccps.smartcity.IDEAM.registerapi.broker.broker broker;
	
	static String rmq_pwd;
	static String ldap_pwd;
	
	public void readldappwd() 
	{	
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try 
		{
			body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			boolean flag = getfollowInfo(body);
			
			if(!flag)
			{
				response.setStatus(400);
				response.getWriter().println("Possible missing fields");
				return;
			}
			
			String resp=sendfollowrequest();
			
			if(resp.contains("Failed"))
			{
				response.setStatus(502);
			}
			
			response.getWriter().println(resp);
			
		} 
		catch (IOException e) 
		{
			response.setStatus(400);
			response.getWriter().println("Invalid request");
			return;
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		
		body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		boolean flag = getfollowInfo(body);
		
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
		
		try 
		{
			ctx.destroySubcontext("description="+_requestorID+",description=share,description=broker,uid="+_entityID+",cn=devices");
		} 
		catch (NamingException e1) 
		{
			response.getWriter().println("Share entry does not exist");
			return;
		}
		
		try 
		{
			Map<String, Object> args=new HashMap<String, Object>();
			args.put("durable", "true");
			Pool.getAdminChannel().queueUnbind(_requestorID,_entityID+".protected","#",args);
			response.getWriter().println("Successfully unfollowed "+_entityID);
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
			response.setStatus(502);
			response.getWriter().println("Unable to unbind queue");
		}
	}

	private boolean getfollowInfo(String json) 
	{
		System.out.println(json);

		try 
		{
			parser = new JsonParser();
			jsonTree = parser.parse(json);
			jsonObject = jsonTree.getAsJsonObject();

			System.out.println(jsonObject.toString());

			entityID = jsonObject.get("entityID");
			_entityID = entityID.toString().replace("\"", "");

			System.out.println(_entityID);
			
			requestorID = jsonObject.get("requestorID");
			_requestorID = requestorID.toString().replace("\"", "");

			permission = jsonObject.get("permission");
			_permission = permission.toString().replace("\"", "");
			
			if((!(_permission.equalsIgnoreCase("read")))&&(!(_permission.equalsIgnoreCase("write")))&&(!(_permission.equalsIgnoreCase("read-write"))))	
			return false;

			System.out.println(_permission);

			validity = jsonObject.get("validity");
			_validity = validity.toString().replace("\"", "");
			
			if((_validity.charAt(_validity.length()-1)!='M')&&(_validity.charAt(_validity.length()-1)!='Y')&&(_validity.charAt(_validity.length()-1)!='D'))
			return false;

			System.out.println(_validity);
			
			return true;
			
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		}
	}

	private String sendfollowrequest() 
	{
		broker = new rbccps.smartcity.IDEAM.registerapi.broker.broker();
		String resp=broker.publish(_entityID+".follow", _permission, _requestorID, _validity);
		
		return resp;

	}

}
