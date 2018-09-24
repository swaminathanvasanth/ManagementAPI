package rbccps.smartcity.IDEAM.registerapi.ldap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.jar.Attributes.Name;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import rbccps.smartcity.IDEAM.urls.URLs;

public class LDAP {

	private static DirContext dirContext = null;
	static String url = URLs.getLDAPURL();
	static String conntype = "simple";
	static String AdminDn = "cn=admin,dc=smartcity";
	static String password = "";
	static Hashtable<String, String> environment = new Hashtable<String, String>();

	static String entryDN;
	static String brokerEntryDN;
	static String brokerExchangeEntryDN;
	static String brokerExchange_DeviceName_EntryDN;
	static String brokerExchange_DeviceName_Configure_EntryDN;
	static String brokerExchange_DeviceName_Private_EntryDN;
	static String brokerExchange_DeviceName_Public_EntryDN;
	static String brokerExchange_DeviceName_Protected_EntryDN;
	static String brokerExchange_DeviceName_Follow_EntryDN;
	static String brokerExchange_DeviceName_Notify_EntryDN;
	static String brokerQueueEntryDN;
	static String brokerQueue_Name_EntryDN;
	static String brokerQueue_Name_Follow_EntryDN;
	static String brokerQueue_Name_Notify_EntryDN;
	static String brokerShareEntryDN;
	static String brokerShare_Name_EntryDN;
	static String LDAPEntry;
	public static boolean entryexists = false;

	public static void readldappwd() {
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
		
		try 
		{
			environment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
			environment.put(Context.PROVIDER_URL, url);
			environment.put(Context.SECURITY_AUTHENTICATION, conntype);
			environment.put(Context.SECURITY_PRINCIPAL, AdminDn);
			environment.put(Context.SECURITY_CREDENTIALS, password);
			dirContext = new InitialDirContext(environment);
			System.out.println("Bind successful");
		} 
		catch (Exception exception) 
		{
			exception.printStackTrace();
			return;
		}
	}

	// Attributes to be set for new entry creation
	public boolean addRegistrationEntry(String providerId, String userId, String apiKey) 
	{
		boolean flag = false;

		System.out.println(providerId + userId + apiKey);

		Attribute OWNER = new BasicAttribute("owner", providerId);
		Attribute PASSWORD = new BasicAttribute("userPassword", apiKey);
		Attribute BLOCK = new BasicAttribute("block", "false");

		// ObjectClass attributes
		Attribute oc = new BasicAttribute("objectClass");
		oc.add("broker");
		oc.add("exchange");
		oc.add("queue");
		oc.add("share");

		Attributes entry = new BasicAttributes();

		entry.put(OWNER);
		entry.put(PASSWORD);
		entry.put(BLOCK);
		entry.put(oc);

		entryDN = "uid=" + userId + ",cn=devices,dc=smartcity";
		System.out.println("entryDN :" + entryDN + " Entry :"
				+ entry.toString());

		// Broker Object
		// Broker Entry
		brokerEntryDN = "description=broker," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";
		Attributes brokerEntry = new BasicAttributes();
		Attribute brokerread = new BasicAttribute("read", "true");
		Attribute brokerwrite = new BasicAttribute("write", "true");

		brokerEntry.put(brokerread);
		brokerEntry.put(brokerwrite);
		brokerEntry.put(oc);

		// Exchange

		brokerExchangeEntryDN = "description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchangeEntry = new BasicAttributes();
		// Attribute exchangeread = new BasicAttribute("read", "true");
		// Attribute exchangewrite = new BasicAttribute("write", "true");

		brokerExchangeEntry.put(oc);
		// brokerExchangeEntry.put(exchangeread);
		// brokerExchangeEntry.put(exchangewrite);

		// Exchange Name to which User can Read / Write

		brokerExchange_DeviceName_EntryDN = "description=" + userId
				+ ",description=exchange,description=broker," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_read = new BasicAttribute("read", "true");
		Attribute exchange_DeviceName_write = new BasicAttribute("write",
				"true");

		brokerExchange_DeviceName_Entry.put(oc);
		brokerExchange_DeviceName_Entry.put(exchange_DeviceName_read);
		brokerExchange_DeviceName_Entry.put(exchange_DeviceName_write);

		// Exchange Name (Configure) to which User can Read / Write

		brokerExchange_DeviceName_Configure_EntryDN = "description=" + userId
				+ ".configure,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Configure_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Configure_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Configure_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Configure_Entry.put(oc);
		brokerExchange_DeviceName_Configure_Entry
				.put(exchange_DeviceName_Configure_read);
		brokerExchange_DeviceName_Configure_Entry
				.put(exchange_DeviceName_Configure_write);

		// Exchange Name (Protected) to which User can Read / Write

		brokerExchange_DeviceName_Protected_EntryDN = "description=" + userId
				+ ".protected,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Protected_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Protected_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Protected_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Protected_Entry.put(oc);
		brokerExchange_DeviceName_Protected_Entry
				.put(exchange_DeviceName_Protected_read);
		brokerExchange_DeviceName_Protected_Entry
				.put(exchange_DeviceName_Protected_write);
		
		// Exchange Name (Private) to which User can Read / Write

		brokerExchange_DeviceName_Private_EntryDN = "description=" + userId
				+ ".private,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Private_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Private_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Private_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Private_Entry.put(oc);
		brokerExchange_DeviceName_Private_Entry
				.put(exchange_DeviceName_Private_read);
		brokerExchange_DeviceName_Private_Entry
				.put(exchange_DeviceName_Private_write);

		// Exchange Name (Public) to which User can Read / Write

		brokerExchange_DeviceName_Public_EntryDN = "description=" + userId
				+ ".public,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Public_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Public_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Public_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Public_Entry.put(oc);
		brokerExchange_DeviceName_Public_Entry
				.put(exchange_DeviceName_Public_read);
		brokerExchange_DeviceName_Public_Entry
				.put(exchange_DeviceName_Public_write);

		
		// Exchange Name (Protected) to which User can Read / Write

		brokerExchange_DeviceName_Follow_EntryDN = "description=" + userId
				+ ".follow,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Follow_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Follow_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Follow_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Follow_Entry.put(oc);
		brokerExchange_DeviceName_Follow_Entry
				.put(exchange_DeviceName_Follow_read);
		brokerExchange_DeviceName_Follow_Entry
				.put(exchange_DeviceName_Follow_write);

		
		// Exchange Name (Notify) to which User can Read / Write

		brokerExchange_DeviceName_Notify_EntryDN = "description=" + userId
				+ ".notify,description=exchange,description=broker,"
				+ "uid=" + userId + ",cn=devices,dc=smartcity";

		Attributes brokerExchange_DeviceName_Notify_Entry = new BasicAttributes();
		Attribute exchange_DeviceName_Notify_read = new BasicAttribute(
				"read", "true");
		Attribute exchange_DeviceName_Notify_write = new BasicAttribute(
				"write", "true");

		brokerExchange_DeviceName_Notify_Entry.put(oc);
		brokerExchange_DeviceName_Notify_Entry
				.put(exchange_DeviceName_Notify_read);
		brokerExchange_DeviceName_Notify_Entry
				.put(exchange_DeviceName_Notify_write);
	
		// Queue

		brokerQueueEntryDN = "description=queue,description=broker," + "uid="
				+ userId + ",cn=devices,dc=smartcity";

		Attributes brokerQueueEntry = new BasicAttributes();
		Attribute queueread = new BasicAttribute("read", "true");
		Attribute queuewrite = new BasicAttribute("write", "true");

		brokerQueueEntry.put(oc);
		brokerQueueEntry.put(queueread);
		brokerQueueEntry.put(queuewrite);

		// Queue (Name or ID) from which User can Read

		brokerQueue_Name_EntryDN = "description=" + userId
				+ ",description=queue,description=broker," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";

		Attributes brokerQueue_Name_Entry = new BasicAttributes();
		Attribute queue_Name_read = new BasicAttribute("read", "true");
		Attribute queue_Name_write = new BasicAttribute("write", "true");

		brokerQueue_Name_Entry.put(oc);
		brokerQueue_Name_Entry.put(queue_Name_read);
		brokerQueue_Name_Entry.put(queue_Name_write);

		// Queue Follow (Name or ID) from which User can Read

		brokerQueue_Name_Follow_EntryDN = "description=" + userId + ".follow"
				+ ",description=queue,description=broker," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";

		Attributes brokerQueue_Follow_Name_Entry = new BasicAttributes();
		Attribute queue_Follow_Name_read = new BasicAttribute("read", "true");
		Attribute queue_Follow_Name_write = new BasicAttribute("write", "true");

		brokerQueue_Follow_Name_Entry.put(oc);
		brokerQueue_Follow_Name_Entry.put(queue_Follow_Name_read);
		brokerQueue_Follow_Name_Entry.put(queue_Follow_Name_write);
		
		// Queue Notify (Name or ID) from which User can Read

		brokerQueue_Name_Notify_EntryDN = "description=" + userId + ".notify"
				+ ",description=queue,description=broker," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";

		Attributes brokerQueue_Notify_Name_Entry = new BasicAttributes();
		Attribute queue_Notify_Name_read = new BasicAttribute("read", "true");
		Attribute queue_Notify_Name_write = new BasicAttribute("write", "true");

		brokerQueue_Notify_Name_Entry.put(oc);
		brokerQueue_Notify_Name_Entry.put(queue_Notify_Name_read);
		brokerQueue_Notify_Name_Entry.put(queue_Notify_Name_write);
		
		String notifyQueueDN = "description="+userId+".notify,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
		Attributes notifyQueue = new BasicAttributes();
		
		notifyQueue.put(new BasicAttribute("read","true"));
		notifyQueue.put(new BasicAttribute("write","true"));
		notifyQueue.put(oc);
		
		String priorityQueueDN = "description="+userId+".priority,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
        Attributes priorityQueue = new BasicAttributes();
		
		priorityQueue.put(new BasicAttribute("read","true"));
		priorityQueue.put(new BasicAttribute("write","true"));
		priorityQueue.put(oc);
		
        String configureQueueDN = "description="+userId+".configure,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
        Attributes configureQueue = new BasicAttributes();
		
		configureQueue.put(new BasicAttribute("read","true"));
		configureQueue.put(new BasicAttribute("write","true"));
		configureQueue.put(oc);
		
		
		// Share

		brokerShareEntryDN = "description=share,description=broker," + "uid="
				+ userId + ",cn=devices,dc=smartcity";

		Attributes brokerShareEntry = new BasicAttributes();
		Attribute shareread = new BasicAttribute("read", "true");
		Attribute sharewrite = new BasicAttribute("write", "true");

		brokerShareEntry.put(oc);
		brokerShareEntry.put(shareread);
		brokerShareEntry.put(sharewrite);
		
		//Read access in share
		
		String readShareEntryDN = "description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
		Attributes readShareEntry = new BasicAttributes();
		
		readShareEntry.put(new BasicAttribute("read","true"));
		readShareEntry.put(oc);
		
		String queueReadEntryDN = "description="+userId+",description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
		Attributes queueReadEntry = new BasicAttributes();
		
		queueReadEntry.put(new BasicAttribute("read","true"));
		queueReadEntry.put(oc);
		
        String followQueueReadEntryDN = "description="+userId+".follow,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		
		Attributes followQueueReadEntry = new BasicAttributes();
		
		followQueueReadEntry.put(new BasicAttribute("read","true"));
		followQueueReadEntry.put(oc);
		
		String notifyQueueReadEntryDN = "description="+userId+".notify,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
			
		Attributes notifyQueueReadEntry = new BasicAttributes();
			
		notifyQueueReadEntry.put(new BasicAttribute("read","true"));
		notifyQueueReadEntry.put(oc);
		 
		String configureQueueReadEntryDN = "description="+userId+".configure,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
			
		Attributes configureQueueReadEntry = new BasicAttributes();
			
		configureQueueReadEntry.put(new BasicAttribute("read","true"));
		configureQueueReadEntry.put(oc);
		 
		String priorityQueueReadEntryDN = "description="+userId+".priority,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
			
		Attributes priorityQueueReadEntry = new BasicAttributes();
			
		priorityQueueReadEntry.put(new BasicAttribute("read","true"));
		priorityQueueReadEntry.put(oc);
		 
		
		//Write entry in share
		
		String writeShareEntryDN = "description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		 
		Attributes writeShareEntry = new BasicAttributes();
			
		writeShareEntry.put(new BasicAttribute("write","true"));
		writeShareEntry.put(oc);
		 
		String configureWriteEntryDN = "description="+userId+".configure,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		 
		Attributes configureWriteEntry = new BasicAttributes();
			
		configureWriteEntry.put(new BasicAttribute("write","true"));
		configureWriteEntry.put(oc);
		 
        String publicWriteEntryDN = "description="+userId+".public,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		 
		Attributes publicWriteEntry = new BasicAttributes();
			
		publicWriteEntry.put(new BasicAttribute("write","true"));
		publicWriteEntry.put(oc);
		 
        String protectedWriteEntryDN = "description="+userId+".protected,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		 
		Attributes protectedWriteEntry = new BasicAttributes();
			
		protectedWriteEntry.put(new BasicAttribute("write","true"));
		protectedWriteEntry.put(oc);
		 
        String privateWriteEntryDN = "description="+userId+".private,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity";
		 
		Attributes privateWriteEntry = new BasicAttributes();
			
		privateWriteEntry.put(new BasicAttribute("write","true"));
		privateWriteEntry.put(oc);


		try {
			dirContext.createSubcontext(entryDN, entry);
			dirContext.createSubcontext(brokerEntryDN, brokerEntry);
			dirContext.createSubcontext(brokerExchangeEntryDN,
					brokerExchangeEntry);
			dirContext.createSubcontext(brokerExchange_DeviceName_EntryDN,
					brokerExchange_DeviceName_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Configure_EntryDN,
					brokerExchange_DeviceName_Configure_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Private_EntryDN,
					brokerExchange_DeviceName_Private_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Public_EntryDN,
					brokerExchange_DeviceName_Public_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Protected_EntryDN,
					brokerExchange_DeviceName_Protected_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Follow_EntryDN,
					brokerExchange_DeviceName_Follow_Entry);
			dirContext.createSubcontext(
					brokerExchange_DeviceName_Notify_EntryDN,
					brokerExchange_DeviceName_Notify_Entry);
			dirContext.createSubcontext(brokerQueueEntryDN, brokerQueueEntry);
			dirContext.createSubcontext(brokerQueue_Name_EntryDN,
					brokerQueue_Name_Entry);
			dirContext.createSubcontext(brokerQueue_Name_Follow_EntryDN,
					brokerQueue_Follow_Name_Entry);
			dirContext.createSubcontext(brokerQueue_Name_Notify_EntryDN,
					brokerQueue_Notify_Name_Entry);
			dirContext.createSubcontext(brokerShareEntryDN, brokerShareEntry);
			
			dirContext.createSubcontext(priorityQueueDN, priorityQueue);
			
			dirContext.createSubcontext(readShareEntryDN, readShareEntry);
			dirContext.createSubcontext(queueReadEntryDN, queueReadEntry);
			dirContext.createSubcontext(followQueueReadEntryDN, followQueueReadEntry);
			dirContext.createSubcontext(notifyQueueReadEntryDN, notifyQueueReadEntry);
			dirContext.createSubcontext(configureQueueReadEntryDN, configureQueueReadEntry);
			dirContext.createSubcontext(priorityQueueReadEntryDN, priorityQueueReadEntry);
			
			dirContext.createSubcontext(writeShareEntryDN, writeShareEntry);
			dirContext.createSubcontext(configureWriteEntryDN, configureWriteEntry);
			dirContext.createSubcontext(publicWriteEntryDN, publicWriteEntry);
			dirContext.createSubcontext(protectedWriteEntryDN, protectedWriteEntry);
			dirContext.createSubcontext(privateWriteEntryDN, privateWriteEntry);
			dirContext.createSubcontext(configureQueueDN, configureQueue);

			flag = true;

			System.out.println("entryDN : "+entryDN );
			System.out.println("brokerEntryDN : "+brokerEntryDN);
			System.out.println("brokerExchangeEntryDN : "+brokerExchangeEntryDN);
			System.out.println("brokerExchange_DeviceName_EntryDN : "+brokerExchange_DeviceName_EntryDN);
			System.out.println("brokerExchange_DeviceName_Configure_EntryDN : "+brokerExchange_DeviceName_Configure_EntryDN);
			System.out.println("brokerExchange_DeviceName_Private_EntryDN : "+brokerExchange_DeviceName_Private_EntryDN);
			System.out.println("brokerExchange_DeviceName_Protected_EntryDN : "+brokerExchange_DeviceName_Protected_EntryDN);
			System.out.println("brokerExchange_DeviceName_Public_EntryDN : "+brokerExchange_DeviceName_Public_EntryDN);
			System.out.println("brokerExchange_DeviceName_Follow_EntryDN : "+brokerExchange_DeviceName_Follow_EntryDN);
			System.out.println("brokerExchange_DeviceName_Notify_EntryDN : "+brokerExchange_DeviceName_Notify_EntryDN);
			System.out.println("brokerQueueEntryDN : "+brokerQueueEntryDN);
			System.out.println("brokerQueue_Name_EntryDN : "+brokerQueue_Name_EntryDN);
			System.out.println("brokerQueue_Name_Follow_EntryDN : "+brokerQueue_Name_Follow_EntryDN);
			System.out.println("brokerQueue_Name_Notify_EntryDN : "+brokerQueue_Name_Notify_EntryDN);
			System.out.println("brokerShareEntryDN : "+brokerShareEntryDN);
			System.out.println("brokerShare_Name_EntryDN : "+brokerShare_Name_EntryDN);

		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
			return flag;
		}
		return flag;
	}
	
	// Attributes to be set for new entry creation
		public boolean addsubscriberRegistrationEntry(String providerId, String userId, String apiKey) {
			boolean flag = false;

			System.out.println(providerId + userId + apiKey);

			Attribute OWNER = new BasicAttribute("owner", providerId);
			Attribute PASSWORD = new BasicAttribute("userPassword", apiKey);
			Attribute BLOCK = new BasicAttribute("block", "false");

			// ObjectClass attributes
			Attribute oc = new BasicAttribute("objectClass");
			oc.add("broker");
			oc.add("exchange");
			oc.add("queue");
			oc.add("share");

			Attributes entry = new BasicAttributes();

			entry.put(OWNER);
			entry.put(PASSWORD);
			entry.put(BLOCK);
			entry.put(oc);

			entryDN = "uid=" + userId + ",cn=devices,dc=smartcity";
			System.out.println("entryDN :" + entryDN + " Entry :"
					+ entry.toString());

			// Broker Object
			// Broker Entry
			brokerEntryDN = "description=broker," + "uid=" + userId
					+ ",cn=devices,dc=smartcity";
			Attributes brokerEntry = new BasicAttributes();
			Attribute brokerread = new BasicAttribute("read", "true");
			Attribute brokerwrite = new BasicAttribute("write", "true");

			brokerEntry.put(brokerread);
			brokerEntry.put(brokerwrite);
			brokerEntry.put(oc);

			// Exchange

			brokerExchangeEntryDN = "description=exchange,description=broker,"
					+ "uid=" + userId + ",cn=devices,dc=smartcity";

			Attributes brokerExchangeEntry = new BasicAttributes();
	
			brokerExchangeEntry.put(oc);
			
			// Exchange Name (Notify) to which User can Read / Write

			brokerExchange_DeviceName_Notify_EntryDN = "description=" + userId
					+ ".notify,description=exchange,description=broker,"
					+ "uid=" + userId + ",cn=devices,dc=smartcity";

			Attributes brokerExchange_DeviceName_Notify_Entry = new BasicAttributes();
			Attribute exchange_DeviceName_Notify_read = new BasicAttribute(
					"read", "true");
			Attribute exchange_DeviceName_Notify_write = new BasicAttribute(
					"write", "true");

			brokerExchange_DeviceName_Notify_Entry.put(oc);
			brokerExchange_DeviceName_Notify_Entry
					.put(exchange_DeviceName_Notify_read);
			brokerExchange_DeviceName_Notify_Entry
					.put(exchange_DeviceName_Notify_write);

			
			// Queue

			brokerQueueEntryDN = "description=queue,description=broker," + "uid="
					+ userId + ",cn=devices,dc=smartcity";

			Attributes brokerQueueEntry = new BasicAttributes();
			Attribute queueread = new BasicAttribute("read", "true");
			Attribute queuewrite = new BasicAttribute("write", "true");

			brokerQueueEntry.put(oc);
			brokerQueueEntry.put(queueread);
			brokerQueueEntry.put(queuewrite);

			// Queue (Name or ID) from which User can Read

			brokerQueue_Name_EntryDN = "description=" + userId
					+ ",description=queue,description=broker," + "uid=" + userId
					+ ",cn=devices,dc=smartcity";

			Attributes brokerQueue_Name_Entry = new BasicAttributes();
			Attribute queue_Name_read = new BasicAttribute("read", "true");
			Attribute queue_Name_write = new BasicAttribute("write", "true");

			brokerQueue_Name_Entry.put(oc);
			brokerQueue_Name_Entry.put(queue_Name_read);
			brokerQueue_Name_Entry.put(queue_Name_write);

			// Queue Notify (Name or ID) from which User can Read

			brokerQueue_Name_Notify_EntryDN = "description=" + userId + ".notify"
					+ ",description=queue,description=broker," + "uid=" + userId
					+ ",cn=devices,dc=smartcity";

			Attributes brokerQueue_Notify_Name_Entry = new BasicAttributes();
			Attribute queue_Notify_Name_read = new BasicAttribute("read", "true");
			Attribute queue_Notify_Name_write = new BasicAttribute("write", "true");

			brokerQueue_Notify_Name_Entry.put(oc);
			brokerQueue_Notify_Name_Entry.put(queue_Notify_Name_read);
			brokerQueue_Notify_Name_Entry.put(queue_Notify_Name_write);
			
			// Share

			brokerShareEntryDN = "description=share,description=broker," + "uid="
					+ userId + ",cn=devices,dc=smartcity";

			Attributes brokerShareEntry = new BasicAttributes();
			Attribute shareread = new BasicAttribute("read", "true");
			Attribute sharewrite = new BasicAttribute("write", "true");

			brokerShareEntry.put(oc);
			brokerShareEntry.put(shareread);
			brokerShareEntry.put(sharewrite);


			try {
				dirContext.createSubcontext(entryDN, entry);
				dirContext.createSubcontext(brokerEntryDN, brokerEntry);
				dirContext.createSubcontext(brokerExchangeEntryDN,
						brokerExchangeEntry);
				dirContext.createSubcontext(
						brokerExchange_DeviceName_Notify_EntryDN,
						brokerExchange_DeviceName_Notify_Entry);
				dirContext.createSubcontext(brokerQueueEntryDN, brokerQueueEntry);
				dirContext.createSubcontext(brokerQueue_Name_EntryDN,
						brokerQueue_Name_Entry);
				dirContext.createSubcontext(brokerQueue_Name_Notify_EntryDN,
						brokerQueue_Notify_Name_Entry);
				dirContext.createSubcontext(brokerShareEntryDN, brokerShareEntry);
				

				flag = true;

				System.out.println("entryDN : "+entryDN );
				System.out.println("brokerEntryDN : "+brokerEntryDN);
				System.out.println("brokerExchangeEntryDN : "+brokerExchangeEntryDN);
				System.out.println("brokerExchange_DeviceName_EntryDN : "+brokerExchange_DeviceName_EntryDN);
				System.out.println("brokerExchange_DeviceName_Notify_EntryDN : "+brokerExchange_DeviceName_Notify_EntryDN);
				System.out.println("brokerQueueEntryDN : "+brokerQueueEntryDN);
				System.out.println("brokerQueue_Name_EntryDN : "+brokerQueue_Name_EntryDN);
				System.out.println("brokerShareEntryDN : "+brokerShareEntryDN);
				System.out.println("brokerShare_Name_EntryDN : "+brokerShare_Name_EntryDN);

			} catch (Exception e) {
				System.out.println("error: " + e.getMessage());
				return flag;
			}
			return flag;
		}
	
	// Attributes to be set for new entry creation
	public boolean addVideoEntry(String providerId, String userId, String apiKey) 
	{
		boolean flag = false;
		System.out.println(providerId + userId + apiKey);

		Attribute OWNER = new BasicAttribute("owner", providerId);
		Attribute PASSWORD = new BasicAttribute("userPassword", apiKey);
		Attribute BLOCK = new BasicAttribute("block", "false");
		
		// ObjectClass attributes
		Attribute oc = new BasicAttribute("objectClass");
		oc.add("video");
		oc.add("share");

		Attributes entry = new BasicAttributes();

		entry.put(OWNER);
		entry.put(PASSWORD);
		entry.put(BLOCK);
		entry.put(oc);

		String entryDN = "uid=" + userId + ",cn=devices,dc=smartcity";
		System.out.println("entryDN :" + entryDN + " Entry :"
				+ entry.toString());

		// video Object
		// video Entry
		String videoEntryDN = "description=video," + "uid=" + userId
				+ ",cn=devices,dc=smartcity";
		Attributes videoEntry = new BasicAttributes();
		Attribute videoread = new BasicAttribute("read", "true");
		Attribute videowrite = new BasicAttribute("write", "true");

		videoEntry.put(videoread);
		videoEntry.put(videowrite);
		videoEntry.put(oc);

		try {
			dirContext.createSubcontext(entryDN, entry);
			dirContext.createSubcontext(videoEntryDN, videoEntry);
			flag = true;

		} catch (Exception e) {
			System.out.println("error: " + e.getMessage());
			return flag;
		}
		return flag;
	}
	
	// Attributes to be set for new entry creation
	public boolean addShareEntry(String providerId, String userId, String permission, String validity) {
		boolean flag = false;
		//System.out.println(providerId + " : " + userId + " : " + read + " : " + write  + " : " + validity);

		readldappwd();
		
		// ObjectClass attributes
		Attribute oc = new BasicAttribute("objectClass");
		oc.add("broker");
		oc.add("exchange");
		oc.add("queue");
		oc.add("share");
		
		Attributes brokerShareEntry;
		Attribute shareread,sharevalidity,sharewrite;
		
		
		if(permission.equals("read"))
		{
			brokerShareEntryDN = "description="+providerId+".protected,description=read,description=share,description=broker,uid="
					+ userId + ",cn=devices,dc=smartcity";

			brokerShareEntry = new BasicAttributes();
			shareread = new BasicAttribute("read", "true");
			sharevalidity = new BasicAttribute("validity", validity);
		
			brokerShareEntry.put(shareread);
			brokerShareEntry.put(sharevalidity);
			brokerShareEntry.put(oc);
			
			try 
			{
				dirContext.createSubcontext(brokerShareEntryDN,brokerShareEntry);
				flag = true;

			} 
			catch (Exception e) 
			{
				System.out.println("error: " + e.getMessage());
				
				if (e.getMessage().toString().contains("Entry Already Exists"))
				{
					entryexists = true;
				}
				
				return flag;
			}
			
		}
		
		else if(permission.equals("write"))
		{
			brokerShareEntryDN = "description="+providerId+".configure,description=write,description=share,description=broker,uid="
					+ userId + ",cn=devices,dc=smartcity";

			brokerShareEntry = new BasicAttributes();
			sharewrite = new BasicAttribute("write", "true");
			sharevalidity = new BasicAttribute("validity", validity);
		
			brokerShareEntry.put(sharewrite);
			brokerShareEntry.put(sharevalidity);
			brokerShareEntry.put(oc);
			
			try 
			{
				dirContext.createSubcontext(brokerShareEntryDN,brokerShareEntry);
				flag = true;

			} 
			catch (Exception e) 
			{
				System.out.println("error: " + e.getMessage());
				
				if (e.getMessage().toString().contains("Entry Already Exists"))
				{
					entryexists = true;
				}
					
				
				return flag;
			}
		}
		else if(permission.equals("read-write"))
		{
			String brokerReadShareEntryDN = "description="+providerId+".protected,description=read,description=share,description=broker,uid="
					+ userId + ",cn=devices,dc=smartcity";

			Attributes brokerReadShareEntry = new BasicAttributes();
			shareread = new BasicAttribute("read", "true");
			sharevalidity = new BasicAttribute("validity", validity);
		
			brokerReadShareEntry.put(shareread);
			brokerReadShareEntry.put(sharevalidity);
			brokerReadShareEntry.put(oc);
			
			String brokerWriteShareEntryDN = "description="+providerId+".configure,description=write,description=share,description=broker,uid="
					+ userId + ",cn=devices,dc=smartcity";

			Attributes brokerWriteShareEntry = new BasicAttributes();
			sharewrite = new BasicAttribute("write", "true");
			sharevalidity = new BasicAttribute("validity", validity);
		
			brokerWriteShareEntry.put(sharewrite);
			brokerWriteShareEntry.put(sharevalidity);
			brokerWriteShareEntry.put(oc);
			

			try 
			{
				dirContext.createSubcontext(brokerReadShareEntryDN,brokerReadShareEntry);
				dirContext.createSubcontext(brokerWriteShareEntryDN,brokerWriteShareEntry);
				flag = true;

			} 
			catch (Exception e) 
			{
				System.out.println("error: " + e.getMessage());
				if (e.getMessage().toString().contains("Entry Already Exists"))
				{
					entryexists = true;
				}
					
				return flag;
			}
		}
		
		System.out.println("brokerShare_Name_EntryDN : "+brokerShare_Name_EntryDN);
		return flag;
	}
	
	public static int deleteEntry(String providerId, String userId)
	{
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
		NamingEnumeration<SearchResult> namingEnumeration=null;
		
	    ArrayList<String> list =new ArrayList<String>();
	    
	    list.add("description="+userId+",description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".configure,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".follow,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".notify,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".public,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".private,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
	    list.add("description="+userId+".protected,description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        
	    list.add("description="+userId+",description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".follow,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".notify,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".priority,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".configure,description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");


        
        list.add("description="+userId+",description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".configure,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".follow,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".notify,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".priority,description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");

        list.add("description="+userId+".configure,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".public,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".private,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        list.add("description="+userId+".protected,description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
        	
		ArrayList<String> ignoreList = new ArrayList<String>();
		
		ignoreList.add(userId);
		ignoreList.add(userId+".configure");
		ignoreList.add(userId+".follow");
		ignoreList.add(userId+".notify");
		ignoreList.add(userId+".priority");
		
		ignoreList.add(userId+".public");
		ignoreList.add(userId+".private");
		ignoreList.add(userId+".protected");
        
        try 
		{
			namingEnumeration = ctx.search("description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity", "(description=*)", new Object[]{}, searchControls);
			
			while (namingEnumeration.hasMore()) 
			{
				SearchResult sr = namingEnumeration.next();
				
				if((sr.getName().equals(""))||(ignoreList.contains(sr.getName().split("=")[1].trim())))
				{
					continue;
				}
				
				list.add(sr.getName()+",description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			}
			
            namingEnumeration = ctx.search("description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity", "(description=*)", new Object[]{}, searchControls);
			
			while (namingEnumeration.hasMore()) 
			{
				SearchResult sr = namingEnumeration.next();
				
				if((sr.getName().equals(""))||(ignoreList.contains(sr.getName().split("=")[1].trim())))
				{
					continue;
				}
				
				list.add(sr.getName()+",description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			}
			
			list.add("description=exchange,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("description=queue,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("description=read,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("description=write,description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("description=share,description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("description=broker,uid="+userId+",cn=devices,dc=smartcity");
			list.add("uid="+userId+",cn=devices,dc=smartcity");
			
			
			for(String name: list)
			{
				//System.out.println(name);
				ctx.destroySubcontext(name);
			}
			
			System.out.println("Success");
			return 1;
		} 
		catch (Exception e1) 
		{
			e1.printStackTrace();
			System.out.println("Could not delete from ldap");
			return 0;
		}
	}

	public static boolean verifyProvider(String userId,
			String[] decoded_authorization_data) {

		boolean flag = false;
		entryDN = "uid=" + userId + ",cn=devices,dc=smartcity";
		System.out.println("In verifyProvider");

		try {
			readldappwd();
			LDAPEntry = dirContext.getAttributes(entryDN).toString();
			System.out.println(LDAPEntry);

			String[] LDAPEntry_Split = LDAPEntry.split(",");

			String[] providerName = LDAPEntry_Split[0].split("=");
			String[] provider = providerName[1].split(":");
			provider[1] = provider[1].replaceAll("\\s+", "");
			

			String submitted_providerName = decoded_authorization_data[0];

			//System.out.println(provider[1] + "   --  " + decoded_authorization_data[0] + " --  " + decoded_authorization_data[1]);
			
			
			if (provider[1].contains(submitted_providerName)) {
				System.out.println("Valid Device of the User");
				flag = true;
			} else {
				System.out.println("Invalid Device of the User");
				flag = false;
			}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			flag = false;
			return flag;
			// e.printStackTrace();
		}
		return flag;
	}
	
	public static boolean checkEntry(String uid)
	{
		Hashtable<String, Object> env = new Hashtable<String, Object>();
		
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://ldapd:8389/dc=smartcity");
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
		NamingEnumeration<SearchResult> namingEnumeration=null;
		
		try 
        {
        	ctx.search("uid="+uid+",cn=devices", "(*)",new Object[] {},searchControls);
        	return true;
        }
        catch (Exception e) 
        {
			return false;
		}
	}

}