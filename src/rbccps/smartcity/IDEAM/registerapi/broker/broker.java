package rbccps.smartcity.IDEAM.registerapi.broker;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import rbccps.smartcity.IDEAM.APIs.RequestController;
import rbccps.smartcity.IDEAM.urls.URLs;

import com.google.gson.JsonObject;

public class broker {

	static JsonObject response_jsonObject;
	static JsonObject publish_jsonObject;

	static String _url;
	static String _value;
	static String response;
	static String password = "";
	static String _message;

	public static void readbrokerpassword() {
		try {
			BufferedReader br = new BufferedReader(new FileReader("/etc/rmqpwd"));

			password = br.readLine();

			br.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static String createExchange(String resourceID) {

		readbrokerpassword();

		_url = URLs.getBrokerURL();
		_value = resourceID;
		response = null;
		System.out.println("+++++++++++In createExchange Block+++++++++++");
		// curl -i -X POST http://127.0.0.1:8080/exchange -d \
		// '{"name": "e1", "type": "topic", "durable": true, "autodelete": false}'
		try {
			URL url = new URL(_url + "/exchange"); // RabbitMQ Docker
			String _postData;
			System.out.println(resourceID);

			// Create a structured JSON as per the Broker requirement

			_postData = "{\"name\":" + "\"" + resourceID + "\"" + ","
					+ "\"type\":\"topic\",\"durable\": true, \"autodelete\": false}";

			System.out.println("+++++++++++In createQueue try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// conn.setRequestProperty("X-Consumer-Username",
			// RequestController.getX_Consumer_Custom_ID());
			// conn.setRequestProperty("Apikey", RequestController.getApikey());

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In Exchange Creation");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot create Exchange.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");

			System.out.println("+++++++++++In createExchange catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;

		// Add a FLAG to process the Registration further

	}

	public static String createQueue(String resourceID) {

		_url = URLs.getBrokerURL();
		_value = resourceID;
		response = null;
		System.out.println("+++++++++++In createQueue Block+++++++++++");

		try {
			URL url = new URL(_url + "/queue"); // RabbitMQ Docker
			String _postData;
			System.out.println(resourceID);

			// Create a structured JSON as per the Broker requirement

			// _postData = "{\"name\":" + "\""+ resourceID + "\""+ "}";

			_postData = "{\"name\":" + "\"" + resourceID + "\"" + "," + "\"durable\": true, \"autodelete\": false}";

			System.out.println("+++++++++++In createQueue try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// conn.setRequestProperty("X-Consumer-Username",
			// RequestController.getX_Consumer_Custom_ID());
			// conn.setRequestProperty("Apikey", RequestController.getApikey());

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In Queue Creation");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot create Queue.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");
			System.out.println("+++++++++++In createQueue catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;

		// Add a FLAG to process the Registration further

	}

	public static String createBinding(String resourceID, String queueID) {

		System.out.println("+++++++++++In createDatabaseBinding Block+++++++++++");

		String response = "";

		if (queueID.contains("database")) {
			response = bind(resourceID, queueID);
			response += bind(resourceID + ".configure", queueID);
			response += bind(resourceID + ".follow", queueID);
			response += bind(resourceID + ".private", queueID);
			response += bind(resourceID + ".protected", queueID);
			response += bind(resourceID + ".public", queueID);
		} else if (queueID.contains("follow")) {
			response += bind(resourceID + ".follow", queueID);
		}

		if (response.contains("Cannot create Queue"))
			return "Failed to bind exchange to DB queue";
		else
			return "Bind to DB queue successful";
	}

	public static String bind(String resourceID, String queueID) {
		readbrokerpassword();
		_url = URLs.getBrokerURL();
		_value = resourceID;
		response = null;
		System.out.println("+++++++++++In createQueue Block+++++++++++");

		try {
			URL url = new URL(_url + "/queue/bind"); // RabbitMQ Docker
			String _postData;
			System.out.println(resourceID);
			System.out.println(url.toString());

			// Create a structured JSON as per the Broker requirement

			_postData = "{\"exchange\":" + "\"" + resourceID + "\"" + "," + "\"key\":" + "[\"#\"]" + ",\"queue\":"
					+ "\"" + queueID + "\"" + "}";

			System.out.println("+++++++++++In createQueue try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);

			System.out.println(conn.toString());

			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In Bind Queue");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot create Queue.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");

			System.out.println("+++++++++++In createQueue catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;

	}

	public static String deleteExchange(String resourceID) {

		_url = URLs.getBrokerURL();
		_value = resourceID;
		response = null;
		System.out.println("+++++++++++In createExchange Block+++++++++++");
		// curl -i -X POST http://127.0.0.1:8080/exchange -d \
		// '{"name": "e1", "type": "topic", "durable": true, "autodelete": false}'
		try {
			URL url = new URL(_url + "/exchange"); // RabbitMQ Docker
			String _postData;
			System.out.println(resourceID);

			// Create a structured JSON as per the Broker requirement

			_postData = "{\"name\":" + "\"" + resourceID + "\"}";

			System.out
					.println("+++++++++++In deleteExchange try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// conn.setRequestProperty("X-Consumer-Username",
			// RequestController.getX_Consumer_Custom_ID());
			// conn.setRequestProperty("Apikey", RequestController.getApikey());

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In Exchange Deletion");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("De-Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot delete Exchange.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");

			System.out.println("+++++++++++In deleteExchange catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;

		// Add a FLAG to process the Registration further

	}

	public static String deleteQueue(String resourceID) {

		_url = URLs.getBrokerURL();
		_value = resourceID;
		response = null;
		System.out.println("+++++++++++In deleteQueue Block+++++++++++");

		try {
			URL url = new URL(_url + "/queue"); // RabbitMQ Docker
			String _postData;
			System.out.println(resourceID);

			// Create a structured JSON as per the Broker requirement

			_postData = "{\"name\":" + "\"" + resourceID + "\"" + "}";

			System.out.println("+++++++++++In deleteQueue try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			// conn.setRequestProperty("X-Consumer-Username",
			// RequestController.getX_Consumer_Custom_ID());
			// conn.setRequestProperty("Apikey", RequestController.getApikey());

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("DELETE");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In Queue Deletion");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("De-Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot delete Queue.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");
			System.out.println("+++++++++++In deleteQueue catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;
		// Add a FLAG to process the Registration further
	}

	public static String publish(String _entityID, String _permission, String _requestorID, String _validity) {
		// TODO Auto-generated method stub

		// $ curl -i -X POST "http://127.0.0.1:8080/publish" -d \
		// '{"exchange": "e1", "key": "bb", "deliverymode": 1, "priority": 99, "body":
		// "hahaha"}'

		readbrokerpassword();

		publish_jsonObject = new JsonObject();
		_url = URLs.getBrokerURL();
		_value = _entityID;
		response = null;
		System.out.println("+++++++++++In publish message Block+++++++++++");
		// curl -i -X POST http://127.0.0.1:8080/exchange -d \
		// '{"name": "e1", "type": "topic", "durable": true, "autodelete": false}'
		try {
			URL url = new URL(_url + "/publish"); // RabbitMQ Docker
			String _postData;
			System.out.println(_entityID);

			// Create a structured JSON as per the Broker requirement

			// '{"exchange": "e1", "key": "bb", "deliverymode": 1, "priority": 99, "body":
			// "hahaha"}'
			
			publish_jsonObject.addProperty("requestorID", _requestorID);
			publish_jsonObject.addProperty("permission", _permission);
			publish_jsonObject.addProperty("validity", _validity);
			
			System.out.println(publish_jsonObject.toString());
			
			_postData = "{\"exchange\":" + "\"" + _entityID + "\"" + "," 
					+ "\"key\":" + "\"" + _entityID + "\"" + ","
					+ "\"body\":" + "\"" + publish_jsonObject.toString() +"\"" + "}";

			System.out
					.println("+++++++++++In publish message try Block+++++++++++" + "\n" + _postData.toString() + "\n");

			byte[] postDataBytes = _postData.toString().getBytes("UTF-8");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			conn.setRequestProperty("X-Consumer-Username", "admin.ideam");
			conn.setRequestProperty("Apikey", password);

			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			conn.setDoOutput(true);
			conn.getOutputStream().write(postDataBytes);
			Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (int c; (c = in.read()) >= 0;)
				sb.append((char) c);
			response = sb.toString();
			System.out.println(response);
			System.out.println("In  publish message");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			response_jsonObject = new JsonObject();
			response_jsonObject.addProperty("Registration", "failure");
			response_jsonObject.addProperty("Reason", "Cannot create Exchange.");

			System.out.println("--------------");
			System.out.println(response_jsonObject.toString());
			System.out.println("--------------");

			System.out.println("+++++++++++In createExchange catch Block+++++++++++" + e.toString());
			response = response_jsonObject.toString();

		}
		return response;

		// Add a FLAG to process the Registration further

	}
}
