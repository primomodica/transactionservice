package com.nr26.requesthandler;
/**
 * This is the class that handle the transactions types: storing and retrieving.
 * Using a Map having a string that contains "type" to store the transaction array 
 * it performs the requested operation using just a "get" from a map -> O(1)
 * 
 * Note: in this case we are not taking care about the order of the transactions
 * into the response jsonarray. In that case we should perform an "insertion in order"
 * that raises the time complexity to O(n)
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nr26.core.Transaction;

public class TypeHandler {
	
	public final static TypeHandler TypeHandlerINSTANCE = new TypeHandler();
	private final HashMap<String,JsonArray> typeHistory;
	
	private TypeHandler()
	{
		 typeHistory = new HashMap<String,JsonArray>();
	}

	public void update(Long  trans_id, String tr) {
		// creation of the transaction
		JsonParser parser = new JsonParser();
		String tr_json =  parser.parse(tr).getAsJsonObject().get("type").getAsString();
		if(typeHistory.containsKey(tr_json)){
			JsonArray temp = typeHistory.get(tr_json);
			temp.add(trans_id);
			typeHistory.put(tr_json,temp);
		}
		else{
			JsonArray temp = new JsonArray();
			temp.add(trans_id);
			typeHistory.put(tr_json, temp);
		}
	}
	public JsonArray getType(String type){
		return typeHistory.get(type);
	}


}
