package rbccps.smartcity.IDEAM.APIs;

import java.io.IOException;
import java.io.PrintWriter;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestPublish extends HttpServlet {

	static String X_Consumer_Username;
	static String apikey;
	static String body;
	static PrintWriter out;
	static Map<String,Channel> pool=new ConcurrentHashMap<String,Channel>();
	static int STATUS_OK = 200;
	
	Connection connection = null;
	Channel channel = null;
	ConnectionFactory factory = null;
	String[] requestURI;
	String exchange;
	String routingKey;
	String token;
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		requestURI = request.getPathInfo().toString().split("/");
		X_Consumer_Username = request.getHeader("X-Consumer-Username");
		apikey = request.getHeader("apikey");
		exchange = requestURI[1];
		routingKey = requestURI[2];
		token=X_Consumer_Username+":"+apikey;

		if(!pool.containsKey(token))
		{
			factory = new ConnectionFactory();
			factory.setUsername(X_Consumer_Username);
			factory.setPassword(apikey);
			factory.setVirtualHost("/");
			factory.setHost("127.0.0.1");
			factory.setPort(12082);

			try {
				connection = factory.newConnection();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			channel = connection.createChannel();
			pool.put(token, channel);		
		}
		else
		{
			if(!pool.get(token).isOpen())
			{
				factory = new ConnectionFactory();
				factory.setUsername(X_Consumer_Username);
				factory.setPassword(apikey);
				factory.setVirtualHost("/");
				factory.setHost("127.0.0.1");
				factory.setPort(12082);

				try {
					connection = factory.newConnection();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				channel = connection.createChannel();
				pool.replace(token, channel);		
			}
		}

		body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		pool.get(token).basicPublish(exchange, routingKey, null, body.getBytes("UTF-8"));
		
		out = response.getWriter();
		response.setStatus(STATUS_OK);
		response.setContentType("application/json");
		out.print("Publish Sent");
	}
}
