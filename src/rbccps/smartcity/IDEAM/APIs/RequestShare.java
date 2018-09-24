package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.StreamingOutput;

import org.json.simple.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.Basic.Return;

import rbccps.smartcity.IDEAM.registerapi.broker.Pool;
import rbccps.smartcity.IDEAM.registerapi.ldap.LDAP;

public class RequestShare extends HttpServlet
{
	
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
	static JsonElement requestorID;
	static String _entityID = null;
	static String _requestorID = null;
	
	static JsonElement permission;
	static String _permission = null;
	static String _read = null;
	static String _write = null;
	
	static JsonElement validity;
	static String _validity = null;
	static String _validityUnits = null;
	static String _expiryTime = null;
	static LocalDate _expireDate = null;
	static LocalTime _expiretime = null;
	static LocalDateTime _expiry = null; 
	static ZoneId zoneId = null;
	static long epoch;
	
	static String temp = null;
	static String rmq_pwd;
	static String ldap_pwd;
	
	static String[] decoded_authorization_datas = new String[2];
	static boolean isOwner = false;
	
	public static void readldappwd() 
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
			
			boolean flag = getshareinfo(body);
			
			decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
			decoded_authorization_datas[1] = request.getHeader("apikey");
			
			if ((LDAP.verifyProvider(_entityID, decoded_authorization_datas))) {
				System.out.println("Device belongs to owner");
				isOwner = true;
			}

			if (!isOwner)
				if(!request.getHeader("X-Consumer-Username").equals(_entityID))
					{
						response.setStatus(401);
						return;
					}
			
			if(!flag)
			{
				response.setStatus(400);
				response.getWriter().println("Possible missing fields");
				return;
			}
			
			String resp=sendsharerequest();
			
			if(resp.contains("Failed"))
			{
				response.setStatus(502);
			}
			
			response.getWriter().println(resp);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			response.setStatus(502);
			return;
		}
	}
	
	@Override
	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		readldappwd();
		
		body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		
		boolean flag = getshareinfo(body);
		
		decoded_authorization_datas[0] = request.getHeader("X-Consumer-Username");
		decoded_authorization_datas[1] = request.getHeader("apikey");
		
		if ((LDAP.verifyProvider(_entityID, decoded_authorization_datas))) {
			System.out.println("Device belongs to owner");
			isOwner = true;
		}

		if (!isOwner)
			if(!request.getHeader("X-Consumer-Username").equals(_entityID))
			{
				response.setStatus(401);
				return;
			}
		
		if(!flag)
		{
			response.setStatus(400);
			response.getWriter().println("Possible missing fields");
			return;
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
			{
				ctx.destroySubcontext("description="+_entityID+".protected,description=read,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				boolean unbind=unbind();	
				
				if(!unbind)
				{
					response.setStatus(502);
					response.getWriter().println("Unable to unbind queue");
				}
			}
			else if(_permission.equals("write"))
			{
				ctx.destroySubcontext("description="+_entityID+".configure,description=write,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");

			}
			else if(_permission.equals("read-write"))
			{
				ctx.destroySubcontext("description="+_entityID+".protected,description=read,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				ctx.destroySubcontext("description="+_entityID+".configure,description=write,description=share,description=broker,uid="+_requestorID+",cn=devices,dc=smartcity");
				
				boolean unbind=unbind();
				
				if(!unbind)
				{
					response.setStatus(502);
					response.getWriter().println("Unable to unbind queue");
				}
			}
		}
		
		catch (NamingException e1) 
		{
			response.getWriter().println("Share entry does not exist");
			return;
		}
		
		response.getWriter().println("Successfully unshared from "+_requestorID);
		
	}
	
	public boolean getshareinfo(String json) 
	{	
		
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
			
			System.out.println(_requestorID);
						
			permission = jsonObject.get("permission");
			_permission= permission.toString().replace("\"", "");
			
			if((!(_permission.equalsIgnoreCase("read")))&&(!(_permission.equalsIgnoreCase("write")))&&(!(_permission.equalsIgnoreCase("read-write"))))	
			return false;
			
			System.out.println(_permission);
			
			validity = jsonObject.get("validity");
			_validity= validity.toString().replace("\"", "");
			
			if((_validity.charAt(_validity.length()-1)!='M')&&(_validity.charAt(_validity.length()-1)!='Y')&&(_validity.charAt(_validity.length()-1)!='D'))
			return false;
			
			
			System.out.println(_validity);
			
			return true;
		}
		catch(Exception e)
		{
			return false;

		}
	}
	
	public static String sendsharerequest() 
	{
		
		readldappwd();
		
		boolean ldap=false,pub=false;
			
			
	    if(_validity.charAt(_validity.length()-1)=='Y') 
		{
			_validityUnits = "Year";
		} 
		else if(_validity.charAt(_validity.length()-1)=='M') 
		{
			_validityUnits = "Month";
		} 
		else if(_validity.charAt(_validity.length()-1)=='D') 
		{
			_validityUnits = "Day";
		} 
			
		System.out.println(_validityUnits);
			
		temp = _validity.substring(0, _validity.length()-1);
			
		System.out.println(temp);
			

		if(_validityUnits.equalsIgnoreCase("Year")) 
		{
			_expireDate = LocalDate.now().plusYears(Long.parseLong(temp));
		} 
		else if(_validityUnits.equalsIgnoreCase("Month")) 
		{
			_expireDate = LocalDate.now().plusMonths(Long.parseLong(temp));
		}  
		else if(_validityUnits.equalsIgnoreCase("Day")) 
		{
			_expireDate = LocalDate.now().plusDays(Long.parseLong(temp));	
		} 
			
		_expiretime = LocalTime.now();
			
		System.out.println("Expiry Date is : "+_expireDate.toString());
		System.out.println("Expiry Time is : "+_expiretime.toString()); 
			 
		_expiry = LocalDateTime.of(_expireDate, _expiretime);
			 
		System.out.println("Expiry is : "+_expiry.toString());
		zoneId = ZoneId.systemDefault();
		epoch = _expiry.atZone(zoneId).toInstant().toEpochMilli();
			 
		System.out.println("Epoch is : "+epoch);
		_validity = epoch+"";
			
		LDAP addShareEntryToLdap = new LDAP();
		ldap=addShareEntryToLdap.addShareEntry(_entityID, _requestorID, _permission, _validity);
		pub=publish(_entityID, _requestorID);
		
		JsonObject response=new JsonObject();
		if(ldap&&pub)
		{
			response.addProperty("status","Share request approved for "+_requestorID+" with permission "+_permission+" at "+Instant.now());
		}
		else 
		{
			response.addProperty("status", "Failed to approve share request");
		}
		
		return response.toString();
	}
	
	public static boolean publish(String entityID, String requestorID)
	{
			
		try
		{	
			JsonObject object=new JsonObject();
			
			object.addProperty("Status update for follow request sent to "+entityID, "Approved. You can now bind to "+entityID+".protected");
			
			Pool.getAdminChannel().basicPublish(requestorID+".notify", "#", null, object.toString().getBytes("UTF-8"));
			
			return true;
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
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
