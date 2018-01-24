package rbccps.smartcity.IDEAM.registerapi.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;

public class addEntryToLDAP{

	private DirContext dirContext = null;
	   String url = "ldap://10.156.14.20:389";
	   String conntype = "simple";
  	   String AdminDn  = "cn=admin,dc=smartcity";
	   String password = "secret0";
       Hashtable<String, String> environment = new Hashtable<String, String>();

	public addEntryToLDAP()	{
    System.out.println("constructer to LDAP bind");
		try
        {
    
        environment.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        environment.put(Context.PROVIDER_URL,url);         
        environment.put(Context.SECURITY_AUTHENTICATION,conntype);         
        environment.put(Context.SECURITY_PRINCIPAL,AdminDn);
        environment.put(Context.SECURITY_CREDENTIALS, password);
        dirContext = new InitialDirContext(environment);
        System.out.println("Bind successful");
       
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
      }

	//Attributes to be set for new entry creation
	public boolean addEntry(String providerId, String userId, String apiKey)
	{
	boolean flag = false;
	
	Attribute OWNER = new BasicAttribute("owner", providerId);
	Attribute PASSWORD = new BasicAttribute("userPassword", apiKey);
		
	//ObjectClass attributes
		Attribute oc = new BasicAttribute("objectClass");
		oc.add("broker");
		oc.add("exchange");
		oc.add("queue");
		
	
	Attributes entry = new BasicAttributes();
	
	entry.put(OWNER);
	entry.put(PASSWORD);
	entry.put(oc);
	
	String entryDN = "uid="+ userId +",cn=devices,dc=smartcity";
	System.out.println("entryDN :" + entryDN + " Entry :" + entry.toString());
		
	// Broker Object
	// Broker Entry
	String brokerEntryDN = "description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
	Attributes brokerEntry = new BasicAttributes();
	Attribute brokerread = new BasicAttribute("read", "true");
	Attribute brokerwrite = new BasicAttribute("write", "true");	
	
	brokerEntry.put(brokerread);
	brokerEntry.put(brokerwrite);
	brokerEntry.put(oc);
	
	// Exchange
	
	String brokerExchangeEntryDN = "description=exchange,description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
	
	Attributes brokerExchangeEntry = new BasicAttributes();
	// Attribute exchangeread = new BasicAttribute("read", "true");
	// Attribute exchangewrite = new BasicAttribute("write", "true");
	
	brokerExchangeEntry.put(oc);
	// brokerExchangeEntry.put(exchangeread);
	// brokerExchangeEntry.put(exchangewrite);
		
	// Exchange Name to which User can Read / Write
	
	String brokerExchange_DeviceName_EntryDN = "description="+ userId +",description=exchange,description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
	
	Attributes brokerExchange_DeviceName_Entry = new BasicAttributes();
	Attribute exchange_DeviceName_read = new BasicAttribute("read", "true");
	Attribute exchange_DeviceName_write = new BasicAttribute("write", "true");
	
	brokerExchange_DeviceName_Entry.put(oc);
	brokerExchange_DeviceName_Entry.put(exchange_DeviceName_read);
	brokerExchange_DeviceName_Entry.put(exchange_DeviceName_write);
	
	
	// Exchange Name (Configure) to which User can Read / Write
	
	String brokerExchange_DeviceName_Configure_EntryDN = "description="+ userId +"_configure,description=exchange,description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
	
	Attributes brokerExchange_DeviceName_Configure_Entry = new BasicAttributes();
	Attribute exchange_DeviceName_Configure_read = new BasicAttribute("read", "true");
	Attribute exchange_DeviceName_Configure_write = new BasicAttribute("write", "true");
	
	brokerExchange_DeviceName_Configure_Entry.put(oc);
	brokerExchange_DeviceName_Configure_Entry.put(exchange_DeviceName_Configure_read);
	brokerExchange_DeviceName_Configure_Entry.put(exchange_DeviceName_Configure_write);
	
	
	// Queue
	
	String brokerQueueEntryDN = "description=queue,description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
		
	Attributes brokerQueueEntry = new BasicAttributes();
	Attribute queueread = new BasicAttribute("read", "true");
	Attribute queuewrite = new BasicAttribute("write", "true");
	
	brokerQueueEntry.put(oc);
	brokerQueueEntry.put(queueread);
	brokerQueueEntry.put(queuewrite);
	
	// Queue (Name or ID) from which User can Read
	
		String brokerQueue_Name_EntryDN = "description="+ userId +",description=queue,description=broker,"+"uid="+ userId +",cn=devices,dc=smartcity";
			
		Attributes brokerQueue_Name_Entry = new BasicAttributes();
		Attribute queue_Name_read = new BasicAttribute("read", "true");
		Attribute queue_Name_write = new BasicAttribute("write", "true");
		
		brokerQueue_Name_Entry.put(oc);
		brokerQueue_Name_Entry.put(queue_Name_read);
		brokerQueue_Name_Entry.put(queue_Name_write);
		
	System.out.println("brokerentryDN :" + brokerQueueEntryDN + " Entry :" + brokerQueueEntry.toString());
		
	try{
		dirContext.createSubcontext(entryDN, entry);
		dirContext.createSubcontext(brokerEntryDN, brokerEntry);
	    dirContext.createSubcontext(brokerExchangeEntryDN, brokerExchangeEntry);
	    dirContext.createSubcontext(brokerExchange_DeviceName_EntryDN, brokerExchange_DeviceName_Entry);
	    dirContext.createSubcontext(brokerExchange_DeviceName_Configure_EntryDN, brokerExchange_DeviceName_Configure_Entry);	    
	    dirContext.createSubcontext(brokerQueueEntryDN, brokerQueueEntry);
	    dirContext.createSubcontext(brokerQueue_Name_EntryDN, brokerQueue_Name_Entry);
		flag = true;
		
	}catch(Exception e){
		System.out.println("error: " + e.getMessage());
		return flag;
		}
	return flag	;
	}
}