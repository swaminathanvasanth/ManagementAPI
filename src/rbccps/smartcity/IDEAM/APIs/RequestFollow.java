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
import rbccps.smartcity.IDEAM.registerapi.ldap.LDAP;
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
	
	static String[] decoded_authorization_datas = new String[2];
	static boolean isOwner = false;
	static boolean isdefault = false;
	
	static String share_entityID = null;
	static String share_exchange = null;
	static String _exchange = null;
	static String [] share_entityIDs = null;
	static boolean invalid_access_request = false;
	static boolean public_access_request = false;
	
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
			
			checkentityID();
			jsonObject = new JsonObject();
			if(invalid_access_request) {
				invalid_access_request = false;
				response.setStatus(401);
				return;
			} else if (public_access_request) {
				public_access_request = false;
				jsonObject.addProperty("status", "success");
				jsonObject.addProperty("info", "No permission is required to access Public exchange.");
				jsonObject.addProperty("access", "Send bind request to the " + share_entityID+"."+share_exchange + " exchange to start getting data."  );
				response.getWriter().println(jsonObject);
				return;
			}
			
			
			decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
			decoded_authorization_datas[1] = request.getHeader("apikey");
			
			

				if ((LDAP.verifyProvider(_requestorID, decoded_authorization_datas))) {
							System.out.println("Device belongs to owner");
							isOwner = true;
						}

				if (!isOwner)
					if(!request.getHeader("X-Consumer-Username").equals(_requestorID))
						{
						response.setStatus(401);
						return;
						}
			
				isOwner = false;
				
			if(!flag)
			{
				response.setStatus(400);
				jsonObject.addProperty("status", "Failure");
				jsonObject.addProperty("reason", "Possible missing fields");
				response.getWriter().println(jsonObject);
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
			jsonObject.addProperty("status", "Failure");
			jsonObject.addProperty("reason", "Invalid request");
			response.getWriter().println(jsonObject);
			return;
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		
		body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		boolean flag = getfollowInfo(body);
		
		checkentityID();
		
		jsonObject = new JsonObject();
				
		if(invalid_access_request) {
			invalid_access_request = false;
			response.setStatus(401);
			return;
		} else if (public_access_request) {
			public_access_request = false;
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("info", "No permission is required to access Public exchange.");
			jsonObject.addProperty("access", "Send bind request to the " + share_entityID+"."+share_exchange + " exchange to start getting data."  );
			response.getWriter().println(jsonObject);
			return;
		}
		
		decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
		decoded_authorization_datas[1] = request.getHeader("apikey");

			if ((LDAP.verifyProvider(_requestorID, decoded_authorization_datas))) {
						System.out.println("Device belongs to owner");
						isOwner = true;
					}

			if (!isOwner)
				if(!request.getHeader("X-Consumer-Username").equals(_requestorID))
					{
						response.setStatus(401);
						return;
					}
		
			isOwner = false;
			
		if(!flag)
		{
			response.setStatus(502);
			jsonObject.addProperty("status", "Failure");
			jsonObject.addProperty("reason", "Internal server error");
			response.getWriter().println(jsonObject);
		}
		
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
		
		try 
		{
			if(_permission.equals("read"))
			{ // +share_entityID+"."+_exchange+
				ctx.destroySubcontext("description="+share_entityID+"."+_exchange+",description=read,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				boolean unbind=unbind();	
				
				if(!unbind)
				{
					response.setStatus(502);
					jsonObject.addProperty("status", "Failure");
					jsonObject.addProperty("reason", "Unable to unbind queue");
					response.getWriter().println(jsonObject);					
				}
			}
			else if(_permission.equals("write"))
			{
				ctx.destroySubcontext("description="+share_entityID+".configure,description=write,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");

			}
			else if(_permission.equals("read-write"))
			{
				ctx.destroySubcontext("description="+share_entityID+"."+_exchange+",description=read,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				ctx.destroySubcontext("description="+share_entityID+".configure,description=write,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				
				boolean unbind=unbind();
				
				if(!unbind)
				{
					response.setStatus(502);
					jsonObject.addProperty("status", "Failure");
					jsonObject.addProperty("reason", "Unable to unbind queue");
					response.getWriter().println(jsonObject);
				}
			}
			
			jsonObject.addProperty("status", "success");
			jsonObject.addProperty("info", "Successfully unfollowed");
			jsonObject.addProperty("entityID", share_entityID);
			jsonObject.addProperty("access", _exchange);
			response.getWriter().println(jsonObject);
		
		}
		catch (NamingException e1) 
		{
			jsonObject.addProperty("status", "failure");
			jsonObject.addProperty("reason", "Share entry does not exist");
			response.setStatus(404);
			response.getWriter().println(jsonObject);
			return;
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
	
	public static void checkentityID() {
		share_entityID = _entityID;
		if(share_entityID.contains("."))
		{
			isdefault = false;
			System.out.println(_entityID);
			System.out.println(_entityID.split("."));
			
			share_entityIDs = new String[2];
			share_entityIDs = _entityID.split("\\.");
			share_entityID = share_entityIDs[0];
			share_exchange = share_entityIDs[1];
			System.out.println(share_entityID);
			System.out.println(share_exchange);
			_exchange = share_exchange;
			
			if( ! ( _exchange.contains("private") || _exchange.contains("heartbeat") || _exchange.contains("public") || _exchange.contains("protected"))) {
				invalid_access_request = true;
			}
			
			if ( _exchange.contains("public") ) {
				public_access_request = true;
			}
			
		} else {
			isdefault = true;
			_exchange = "protected";
		}
		
	}

	private String sendfollowrequest() 
	{
		broker = new rbccps.smartcity.IDEAM.registerapi.broker.broker();
		String resp=broker.publish(share_entityID, _permission, _requestorID, _validity, _exchange);
		
		return resp;

	}
	
	public static boolean unbind()
	{		
		try 
		{
			Map<String, Object> args=new HashMap<String, Object>();
			args.put("durable", "true");
			Pool.getAdminChannel().queueUnbind(_requestorID,_entityID+".protected","#",args);
			
			return true;
			
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

}
