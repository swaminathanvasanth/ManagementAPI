package rbccps.smartcity.registerapi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;	
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import rbccps.smartcity.registerapi.kong.Register;

@Path("/register")
public class RequestController {

	RequestService requestService = new RequestService();
	Register register = new Register();
	boolean _publish, _subscribe, _historic;
	static JSONObject responseObj;
	
	static String authorization;
	static String X_Consumer_Custom_ID;
	static String X_Consumer_Username;
	static String User_Name;
	static String _apiKey;
	static String serviceType;
	static String resourceID;
	static String X_Consumer_Groups;
	static String _allowedapis;
	static String _response;
	
	static String publicationEndPoint;
	static String subscriptionEndPoint;
	static String accessEndPoint;
	static String resourceAPIInfo;
	
	static URI publicationEndPoint_URI;
	static URI subscriptionEndPoint_URI;
	static URI accessEndPoint_URI;
	static URI resourceAPIInfo_URI;
	
	static String ID_Already_Used_Response = "The ID is already used. Please Use a Unique ID for Registration.";
	static String API_ERROR_Response = "API Generation Error."; 
	static String WhiteList_ERROR_Response = "ServiceType Request Error";
	static String LDAP_ERROR_Response = "DB Error"; 
		
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getAPIKey(@Context HttpHeaders httpHeaders) {

		System.out.println("------------HEADERS----------------");
		
		authorization = httpHeaders.getRequestHeaders().getFirst("authorization");
		System.out.println(authorization);
		
		X_Consumer_Custom_ID = httpHeaders.getRequestHeaders().getFirst("X-Consumer-Custom-ID");
		System.out.println(X_Consumer_Custom_ID);
		
		X_Consumer_Username = httpHeaders.getRequestHeaders().getFirst("X-Consumer-Username");
		System.out.println(X_Consumer_Username);
		
		User_Name = httpHeaders.getRequestHeaders().getFirst("User-Name");
		System.out.println(User_Name);
		
		_apiKey = httpHeaders.getRequestHeaders().getFirst("Key");
		System.out.println(_apiKey);
		
		X_Consumer_Groups = httpHeaders.getRequestHeaders().getFirst("X-Consumer-Groups");
		System.out.println(X_Consumer_Groups);
		
		System.out.println("------------HEADERS----------------");
		
		// X-Consumer-Custom-ID: mapunity
		//X-Consumer-Username: mapunity
		//Authorization: Basic: bWFwdW5pdHk6Y2VlYzNmNDdiZTE1NDQ5YjkyNzVhZjgwMzhjZTdhZTk=
		//User-Name: mapunity
		//Key: ceec3f47be15449b9275af8038ce7ae9
		//X-Consumer-Groups: provider

		responseObj = new JSONObject();

		/** how to get specific header info? **/
		try {
			resourceID = httpHeaders.getRequestHeader("resourceID").get(0);
			System.out.println("resourceID: " + resourceID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			responseObj.put("Registration", "failure");
			responseObj.put("Reason", "resourceID Header not provided.");
			_response = responseObj.toJSONString();
			return _response;
		}

		/** how to get specific header info? **/
		try {
			serviceType = httpHeaders.getRequestHeader("serviceType").get(0);
			System.out.println("serviceType: " + serviceType);
			
			boolean validRequest = serviceType.matches("(\\bpublish\\b|\\bsubscribe\\b|\\bhistoricData\\b|\\b,\\b)+");
			// boolean validRequest = serviceType.matches("/^(publish|subscribe|historicData|[[:space:][:punct:]])+$/");
			if(validRequest){
				System.out.println("serviceType: " + serviceType +" is a Valid Request");
			} else {
				responseObj.put("Registration", "failure");
				responseObj.put("Reason", "serviceType Header not valid.");
				_response = responseObj.toJSONString();
				return _response;
			}

			if (serviceType.contains("publish")) {
				_publish = true;
			}

			if (serviceType.contains("subscribe")) {
				_subscribe = true;
				System.out.println("+++++++++++In Subscribe Check+++++++++++");
			}

			if (serviceType.contains("historicData")) {
				_historic = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			responseObj.put("Registration", "failure");
			responseObj.put("Reason", "serviceType Header not provided.");
			_response = responseObj.toJSONString();
			return _response;
		}

		String apikey = httpHeaders.getRequestHeader("apikey").get(0);
		System.out.println("apikey: " + apikey);

		/** get list of all header parameters from request **/
		Set<String> headerKeys = httpHeaders.getRequestHeaders().keySet();
		for (String header : headerKeys) {
			System.out.println(header + ":"
					+ httpHeaders.getRequestHeader(header).get(0));
		}
		
		try {
			_response = Register.createUser(resourceID);
			System.out.println(_response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			responseObj.put("Registration", "failure");
			responseObj.put("Reason", "APIKey Generation Error. Retry Again.");
			_response = responseObj.toJSONString();
			return _response;
		}

		if (_response.contains("created")) {
			try {
				_apiKey = Register.generateAPIKey(resourceID);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				responseObj.put("Registration", "failure");
				responseObj.put("Reason",
						"APIKey Generation Error. Retry Again.");
				_response = responseObj.toJSONString();
				return _response;
			}
		} else {
			responseObj.put("Registration", "failure");
			responseObj.put("Reason", ID_Already_Used_Response);
			_response = responseObj.toJSONString();
			return _response;
			// _response = ID_Already_Used_Response;
		}

		if (_apiKey != null) {

			
			if(_subscribe){
				System.out.println("+++++++++++In Subscribe Block inside White-List creation+++++++++++");
				Register.createQueue(resourceID);
			}
			
			if (_publish && _subscribe && _historic) {
				System.out.println("---ALL---");
				// Call Consumer White List with
				// serviceType="publish,subscribe,historicData"
				_allowedapis = "publish,subscribe,historic,cat";
				_response = Register.assignWhiteListGroup(resourceID,
						"publish");
				_response = Register.assignWhiteListGroup(resourceID,
						"subscribe");
				_response = Register.assignWhiteListGroup(resourceID,
						"historicData");
				
			} else if (_publish && _subscribe) {
				System.out.println("---Pub-Sub---");
				_allowedapis = "publish,subscribe,cat";
				// Call Consumer White List with serviceType="publish,subscribe"
				_response = Register.assignWhiteListGroup(resourceID,
						"publish");
				_response = Register.assignWhiteListGroup(resourceID,
						"subscribe");
			} else if (_publish && _historic) {
				System.out.println("---Pub-His---");
				_allowedapis = "publish,historic,cat";
				// Call Consumer White List with
				// serviceType="publish,historicData"
				_response = Register.assignWhiteListGroup(resourceID,
						"publish");
				_response = Register.assignWhiteListGroup(resourceID,
						"historicData");
			} else if (_subscribe && _historic) {
				System.out.println("---Sub-His---");
				_allowedapis = "subscribe,historic,cat";
				// Call Consumer White List with
				// serviceType="subscribe,historicData"
				_response = Register.assignWhiteListGroup(resourceID,
						"subscribe");
				_response = Register.assignWhiteListGroup(resourceID,
						"historicData");
			} else if (_publish) {
				System.out.println("---Pub---");
				_allowedapis = "publish,cat";
				// Call Consumer White List with serviceType="publish"
				_response = Register.assignWhiteListGroup(resourceID,
						"publish");
			} else if (_subscribe) {
				System.out.println("---Sub---");
				_allowedapis = "subscribe,cat";
				// Call Consumer White List with serviceType="subscribe"
				_response = Register.assignWhiteListGroup(resourceID,
						"subscribe");
			} else if (_historic) {
				System.out.println("---His---");
				_allowedapis = "historic,cat";
				// Call Consumer White List with serviceType="historicData"
				_response = Register.assignWhiteListGroup(resourceID,
						"historicData");
			}

			System.out.println("In White List Creation");
			System.out.println(_response);

			if (_response.contains("created_at")) {
				System.out.println("Added to List: " + _response);
			}
		}

		if (_apiKey != null) {
			_response = Register.updateLDAP(X_Consumer_Username, resourceID, _apiKey);
		} else {
			responseObj.put("Registration", "failure");
			responseObj.put("Reason", ID_Already_Used_Response);
			_response = responseObj.toJSONString();
			return _response;
			// _response = ID_Already_Used_Response;
		}

		if (_response.contains("Success")) {

			publicationEndPoint = "https://smartcity.rbccps.org/api/0.1.0/publish";
			publicationEndPoint_URI = null;
						
			subscriptionEndPoint = "https://smartcity.rbccps.org/api/0.1.0/subscribe";
			subscriptionEndPoint_URI = null;
			
			accessEndPoint = "https://smartcity.rbccps.org/api/0.1.0/historicData";
			accessEndPoint_URI = null;
			
			resourceAPIInfo = "https://rbccps-iisc.github.io";
			resourceAPIInfo_URI = null;
						
			try {
				subscriptionEndPoint_URI = new URI(subscriptionEndPoint);
				accessEndPoint_URI = new URI(accessEndPoint);
				resourceAPIInfo_URI = new URI(resourceAPIInfo);
				publicationEndPoint_URI = new URI(publicationEndPoint);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(_subscribe){
				responseObj.put("Subscription Queue Name", resourceID);	
			}

			if(_publish){
				responseObj.put("publicationEndPoint", publicationEndPoint_URI); // live
			}
						
			responseObj.put("Registration", "success");
			responseObj.put("ResourceID", resourceID);
			responseObj.put("APIKey", _apiKey);
			responseObj.put("AllowedAPIs", _allowedapis);
			responseObj.put("subscriptionEndPoint", subscriptionEndPoint_URI);
			responseObj.put("accessEndPoint", accessEndPoint_URI);
			responseObj.put("resourceAPIInfo", resourceAPIInfo_URI);

			_response = responseObj.toJSONString();
			
			// _response =
			// "The resource is registered with the Smart City Middleware with ID "
			// + resourceID +". The API Key for the resource is : " +_apiKey
			// +"\n"
			// +
			// " Follow the documentation at https://rbccps-iisc.github.io/, to interact with the middleware using APIs.\n";
		} else {

			responseObj.put("Registration", "failure");
			responseObj.put("Reason", ID_Already_Used_Response);
			_response = responseObj.toJSONString();
			return _response;
			// _response = ID_Already_Used_Response;
		}

		return _response;
	}
}
