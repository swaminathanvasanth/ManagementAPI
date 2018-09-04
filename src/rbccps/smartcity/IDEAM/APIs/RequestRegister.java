package rbccps.smartcity.IDEAM.APIs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;

import rbccps.smartcity.IDEAM.registerapi.kong.Register;
import rbccps.smartcity.IDEAM.registerapi.parser.createEntityJSONParser;
import rbccps.smartcity.IDEAM.registerapi.parser.deleteEntityJSONParser;
import rbccps.smartcity.IDEAM.registerapi.parser.entity;
import rbccps.smartcity.IDEAM.registerapi.parser.owner;
import rbccps.smartcity.IDEAM.registerapi.deregister.*;

public class RequestRegister extends HttpServlet {

	private static final deleteEntityJSONParser DeleteEntityJSONParser = null;

	Register register = new Register();

	static JSONObject responseObj;
	static String response;
	static String authorization;
	static String X_Consumer_Custom_ID;
	static String X_Consumer_Username;
	static String apikey;
	static String X_Consumer_Groups;
	static String body;
	static String security_level;
	static String returnData;
	static final int SERVICE_UNAVAILABLE = 503;
	static final int CONFLICT = 409;
	static final int BAD_REQUEST = 400;
	static PrintWriter out;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("http://rbccps.org/smartcity/");
		return;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		resp = (HttpServletResponse) getAPIKey(req, resp);
		out = resp.getWriter();
		resp.setContentType("application/json");
		out.print(returnData);
	}

	
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		resp = (HttpServletResponse) deleteAPIKey(req,resp);
		System.out.println("Completed : " + returnData);
		out = resp.getWriter();
		resp.setContentType("application/json");
		out.print(returnData);
		
	}

	public HttpServletResponse getAPIKey(HttpServletRequest request, HttpServletResponse resp) {

		System.out.println("------------");
		System.out.println(request.getRequestURI());
		System.out.println("------------");

		try {
			getHeaderInfo(request);
			body = getBody(request);
			// System.out.println(body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		returnData = createEntityJSONParser.JSONParser();
		System.out.println(returnData);

		if (returnData.contains("ID already used")) {
			resp.setStatus(CONFLICT);
			clearEntries();
			return resp;
		}

		else if (returnData.contains("uCat update Failure") || returnData.contains("Server Not Reachable")
				|| returnData.contains("API KeyGen failed") || returnData.contains("Failed in Broker")
				|| returnData.contains("Failed in adding ID into the ACL") || returnData.contains("LDAP update Failure")
				|| returnData.contains("Queue Deletion Failure") || returnData.contains("uCat deletion Failure")) {
			try {
				String response = Deregister.removeEntries(getApikey(), entity.getEntityID());
				System.out.println(response);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			resp.setStatus(SERVICE_UNAVAILABLE);
			clearEntries();
			return resp;
		}

		else if (returnData.contains("ID not provided") || returnData.contains("Security level must be between 1-5")
				|| returnData.contains("JSON parse error") || returnData.contains("Possible missing fields")
				|| returnData.contains("Missing LoRa information") || returnData.contains("Missing Video information")
				|| returnData.contains("Cannot Onboard Video camera in VideoServer. POST Error.")
				|| returnData.contains("serverConfiguration_credentials, some field not found in json")
				|| returnData.contains("PlayURL is not specified in json")) {
			try {
				String response = Deregister.removeEntries(getApikey(), entity.getEntityID());
				System.out.println(response);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			resp.setStatus(BAD_REQUEST);
			clearEntries();
			return resp;
		} else
			resp.setStatus(200, returnData);
		    clearEntries();
		return resp;
	}

	public HttpServletResponse deleteAPIKey(HttpServletRequest request, HttpServletResponse response) {

		try {
			getHeaderInfo(request);
			body = getBody(request);
			System.out.println(body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		returnData = DeleteEntityJSONParser.JSONParser();
		System.out.println(returnData);
		response.setStatus(200, returnData);
		return response;
	}

	private void getHeaderInfo(HttpServletRequest request) {
		// TODO Auto-generated method stub
		System.out.println("In Request Header");

		System.out.println("------------HEADERS----------------");

		authorization = request.getHeader("authorization");
		System.out.println("Authorisation=" + authorization);

		X_Consumer_Custom_ID = request.getHeader("X-Consumer-Custom-ID");
		System.out.println("Custom ID=" + X_Consumer_Custom_ID);

		X_Consumer_Username = request.getHeader("X-Consumer-Username");
		System.out.println("Username=" + X_Consumer_Username);

		apikey = request.getHeader("apikey");
		System.out.println("Apikey=" + apikey);
		
		owner.setOwnerID(X_Consumer_Username);
		owner.setOwnerKey(apikey);
		
		try {
			security_level = request.getHeader("security_level");
		} catch (Exception e) {
			System.out.println("Security Level not specififed");
		} finally {
			if (security_level == null) {
				System.out.println("Using default security level of 4");
				security_level = String.valueOf(4);
			}
		}

		System.out.println(security_level);
		X_Consumer_Groups = request.getHeader("X-Consumer-Groups");
		System.out.println(X_Consumer_Groups);

		System.out.println("------------HEADERS----------------");
	}

	private String getBody(HttpServletRequest request) throws IOException {
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

	public static String getAuthorization() {
		return authorization;
	}

	public static void setAuthorization(String authorization) {
		RequestRegister.authorization = authorization;
	}

	public static String getX_Consumer_Custom_ID() {
		return X_Consumer_Custom_ID;
	}

	public static String getX_Consumer_Username() {
		return X_Consumer_Username;
	}

	public static String getApikey() {
		return apikey;
	}

	public static String getSecurityLevel() {
		return security_level;
	}

	public static String getX_Consumer_Groups() {
		return X_Consumer_Groups;
	}

	public static String getBody() {
		return body;
	}
	
	public static void clearEntries()
	{
		entity.setEntityapikey("");
		entity.setEntityID("");
		entity.setEntitySchemaObject("");
	}
}
