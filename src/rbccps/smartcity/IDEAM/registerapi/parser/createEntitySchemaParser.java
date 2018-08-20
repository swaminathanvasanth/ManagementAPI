package rbccps.smartcity.IDEAM.registerapi.parser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class createEntitySchemaParser {
	
	JsonElement entitySchema;
	String _entitySchema;

	JsonObject entitySchemaObject;
	JsonElement id_element;

	JsonElement ID;
	String _ID;

	
	
	public String parse(JsonObject jsonObject, JsonElement access_jsonTree){
		
		try {
			entity entityInfo = new entity();			
			entitySchema = jsonObject.get("entitySchema");
			_entitySchema = entitySchema.toString();
			entitySchemaObject = entitySchema.getAsJsonObject();
/*			System.out.println("\n-------entitySchema--------\n"
					+ _entitySchema + "\n-------entitySchema--------\n");
*/			entitySchemaObject.remove("accessMechanism");
			//entitySchemaObject.remove("id");

/*			System.out.println("\n-------REMOVED--------\n");
			System.out.println("\n-------entitySchemaObject--------\n"
					+ entitySchemaObject.toString()
					+ "\n-------entitySchemaObject--------\n");
			System.out.println("\n-------REMOVED--------\n");
*/
			entitySchemaObject.add("accessMechanism", access_jsonTree);
			//id_element = (JsonElement) gson.fromJson(id, JsonElement.class);
			//entitySchemaObject.add("id", id_element);
			entityInfo.setEntityID(entitySchemaObject.get("id").toString());
			System.out.println(entitySchemaObject.get("id").toString());
			
/*
			System.out.println("\n-------ADDED--------\n");
			System.out.println("\n-------entitySchemaObject--------\n"
					+ entitySchemaObject.toString()
					+ "\n-------entitySchemaObject--------\n");
			System.out.println("\n-------ADDED--------\n");
*/
		} catch (Exception e) {
			System.out.println("Error : entitySchema not found");
			System.out.println("Its an application registration !!!");
		}
		
		
		try {
			ID = jsonObject.get("id");
			_ID = ID.toString().replaceAll("^\"|\"$", "");
			System.out.println(_ID);
			createEntityJSONParser.isSubscriber = true;
			return _ID;
			
		} catch (Exception e) {
			
		}
		
		
		return entitySchemaObject.toString();
	}
}
