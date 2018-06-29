package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import rbccps.smartcity.IDEAM.registerapi.ldap.LDAP;

@Path("/share")
public class RequestShare extends HttpServlet{
	
	/**
	 * 
	 */
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

	@GET
	public void doGet(@Context HttpServletResponse response) throws IOException{
		
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		System.out.println("In RequestRedirect");
		System.out.println("++++++++++++++++++++++++++++++++++++++++++");
		response.sendRedirect("http://rbccps.org/smartcity/");
		return;
}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public void doPost(@Context HttpServletRequest request, @Context HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("------------");
		System.out.println(request.getRequestURI());
		System.out.println("------------");
		
		try {
			getHeaderInfo(request);
			body = getBody(request);
			getshareinfo(body);
			sendsharerequest(_entityID, _permission, _requestorID, _validity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}

	private void getHeaderInfo(@Context HttpServletRequest request) {
		// TODO Auto-generated method stub
		System.out.println("In Request Header");
		
		System.out.println("------------HEADERS----------------");
		
		authorization = request.getHeader("authorization");
		System.out.println(authorization);
		
		X_Consumer_Custom_ID = request.getHeader("X-Consumer-Custom-ID");
		System.out.println(X_Consumer_Custom_ID);
		
		X_Consumer_Username = request.getHeader("X-Consumer-Username");
		System.out.println(X_Consumer_Username);
		
		apikey = request.getHeader("apikey");
		System.out.println(apikey);
		X_Consumer_Groups = request.getHeader("X-Consumer-Groups");
		System.out.println(X_Consumer_Groups);
		
		System.out.println("------------HEADERS----------------");
	}

	private String getBody(@Context HttpServletRequest request) throws IOException {
		// TODO Auto-generated method stub
		
		System.out.println("In Request Body");
		
		String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;

	    try {
	        InputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
	            char[] charBuffer = new char[128];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	             stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        throw ex;
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	                throw ex;
	            }
	        }
	    }
	    body = stringBuilder.toString();
	    return body;
	}
	
	private void getshareinfo(String json) {
		// TODO Auto-generated method stub
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
			
			System.out.println(_requestorID);
						
			permission = jsonObject.get("permission");
			_permission= permission.toString().replace("\"", "");

			System.out.println(_permission);
			
			validity = jsonObject.get("validity");
			_validity= validity.toString().replace("\"", "");

			System.out.println(_validity);
		}
		catch(Exception e)
		{
			System.out.println("Error : Not found");

		}
	}
	
	private void sendsharerequest(String _entityID, String _permission, String _requestorID, String _validity) {
		// TODO Auto-generated method stub
		
		if(_permission.contains("read")) {
			_read = "true";
			_write = "false";
		} else if(_permission.contains("write")) {
			_read = "false";
			_write = "true";
		} else if(_permission.contains("read-write")) {
			_read = "true";
			_write = "true";
		}		

		// YMDhms
		
		if(_validity.contains("Y")) {
			_validityUnits = "Year";
		} else if(_validity.contains("M")) {
			_validityUnits = "Month";
		} else if(_validity.contains("D")) {
			_validityUnits = "Day";
		} else if(_validity.contains("h")) {
			_validityUnits = "hour";
		} else if(_validity.contains("m")) { 
			_validityUnits = "minute";
		} else if(_validity.contains("s")) {
			_validityUnits = "seconds";
		}
		System.out.println(_validityUnits);
		temp = "";
		for(int i = 0; i< _validity.length() -1; i ++) {
			System.out.println(_validity.charAt(i));
			temp = temp + _validity.charAt(i);
		}

		if(_validityUnits == "Year") {
			_expireDate = LocalDate.now().plusYears(Long.parseLong(temp));
		} else if(_validityUnits == "Month") {
			_expireDate = LocalDate.now().plusMonths(Long.parseLong(temp));
		}  else if(_validityUnits == "Day") {
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
		addShareEntryToLdap.addShareEntry(_entityID, _requestorID, _read, _write, _validity);

	}
}
