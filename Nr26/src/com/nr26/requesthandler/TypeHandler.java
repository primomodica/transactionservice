package com.nr26.requesthandler;

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
	private final HashMap<Long,String> typeHistory;
	
	private TypeHandler()
	{
		 typeHistory = new HashMap<Long,String>();
	}

	public void update(Long  trans_id, String tr) {
		// creation of the transaction
		JsonParser parser = new JsonParser();
		JsonObject tr_json =  parser.parse(tr).getAsJsonObject();
		typeHistory.put(trans_id,tr_json.get("type").getAsString());
	}
	public JsonArray getType(String type){
		JsonArray list = new JsonArray();
		if(typeHistory.containsValue(type)){
		for (Entry<Long, String> entry : typeHistory.entrySet())
		{
		    if(entry.getValue().equals(type)){
		    	list.add(entry.getKey());
		    }
		}
		}
		return list;
	}


}
