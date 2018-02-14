package rbccps.smartcity.IDEAM.registerapi.parser;

public class entity {
	
	static String entityID;
	static String entitySchemaObject;
	static String entityapikey;
	static String entitysubscriptionEndPoint;
	static String entityaccessEndPoint;
	static String entitypublicationEndPoint;
	static String entityresourceAPIInfo;
		
	public static String getEntityapikey() {
		return entityapikey;
	}
	public static void setEntityapikey(String entityapikey) {
		entity.entityapikey = entityapikey;
	}
	public static String getEntityID() {
		return entityID;
	}
	public static void setEntityID(String entityID) {
		entity.entityID = entityID;
		entity.entityID = entity.entityID.replaceAll("^\"|\"$", "");
	}
	public static String getEntitySchemaObject() {
		return entitySchemaObject;
	}
	public static void setEntitySchemaObject(String entitySchemaObject) {
		entity.entitySchemaObject = entitySchemaObject;
	}
}
