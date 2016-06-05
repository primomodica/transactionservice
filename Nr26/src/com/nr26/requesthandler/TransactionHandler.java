package com.nr26.requesthandler;
/**
 * This is the class that handle the transactions creating, checking them and store in memory.
 * 
 * Insertion Operation:
 * This operation consist into insert an element in a TreeMap -> O(1)
 *
 * Transaction Retrieval:
 * This operation consists into get an element from a map -> O(1)
 *
 * Get sum of "transitively linked transaction":
 * In this case the handler calls a DFS that searches and sum the transactions in a 
 * recursive way. In the worst case we have a "transaction chain" like
 * 1 <- 2 <- 3 <- 4 <- [...]
 * In this case the algorithm "traverse all nodes" time complexity -> O(n)
 *
 */
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONObject;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nr26.core.Transaction;

public class TransactionHandler {
	
	public final static TransactionHandler TrsansacttionHandlerINSTANCE = new TransactionHandler();
	private final JsonObject response; 
	//A TreeMap that implements SortedMap and orders using the ids
	//suits to our case in which for some extent order of transactions may be important
	private final TreeMap<Long, Transaction> transactionHistory;
	
	private TransactionHandler(){
		response = new JsonObject();
		transactionHistory  = new TreeMap<Long, Transaction>();
	}

	public JsonObject newTransaction(Long trans_id, String tr) {
		// creation of the transaction
		Transaction transaction;
		JsonParser parser = new JsonParser();
		JsonObject tr_json = parser.parse(tr).getAsJsonObject();
		//let's check if a parent exists
		Long parent_id = null;
		if (tr_json.has("parent_id"))
			parent_id = tr_json.get("parent_id").getAsLong();
		try {
			transaction = new Transaction(tr_json.get("amount").getAsString(), tr_json.get("type").getAsString());
			transaction.setId(trans_id);
		} catch (NumberFormatException e) {
			response.addProperty("status", "You should take care of writing numbers properly! THIS IS A PAYMENT");
			return response;
		}
		/*
		 * Here i start to take care of different cases about transaction
		 * insert:
		 */
		if (checkPaymet(transaction.getId())) return response;
		if (parent_id != null) {

			if (!checkIfParentExists(parent_id)) {
				return response;
			} else {
				transaction.addDirectParent(transactionHistory.get(parent_id));
			}
			if (checkIdsEquality(transaction.getId(), transaction.getDirectParentId()))
				return response;
			if (checkCycles(transactionHistory.get(transaction.getDirectParentId()).getId(), transaction.getId()))
				return response;
		}
		/*
		 * the controls are done, it seems that the Transaction is fine -> let's
		 * insert it
		 */
		transactionHistory.put(transaction.getId(), transaction);
		
//		// let's handle parents
		if (tr_json.has("parent_id")) {
//			// Now if this transaction has a partent, transitively, all the
//			// parent parents' are his parent, so we just need to add them all
//			transaction.addAllParents(transactionHistory.get(transaction.getDirectParentId()).getParents());
//			for(Transaction t : transactionHistory.get(transaction.getDirectParentId()).getParents()){
//				t.addChild(transaction);
//			}
//			// It is a parent of the new transaction as well
//			transaction.addChild(transactionHistory.get(transaction.getDirectParentId()));
//			// And of course the new transaction is now a parent
			transactionHistory.get(transaction.getDirectParentId()).addChild(transaction);
		}
		response.addProperty("status", "ok");
		
		return response;
	}
	
	
	/*
	 * Simple get of the transaction from the map
	 */
	public JsonObject getTransaction(String transaction_id) {
		if (transactionHistory.containsKey(Long.parseLong(transaction_id))) {
			return transactionHistory.get(Long.parseLong(transaction_id)).toJson();
		} else {
			JsonObject error = new JsonObject();
			error.addProperty("Error", "No transaction with this id");
			return error;
		}

	}
	/*
	 * Get the sum of the children transaction's 
	 * I'm not sure if i get properly the request but i'm making the assumption 
	 * that basically to get the sum is needed to "search" in a tree because a transaction
	 * has one or more children and all the children may have children.
	 * Hope that the request is this (hope that in any case "showing" DFS with recursion is fine)
	 */
	public JsonObject getSum(Long transaction) {
		JsonObject sum = new JsonObject();
		double transactionSum = transactionHistory.get(transaction).sum();
		sum.addProperty("sum", transactionSum);
		return sum;
		
	}
	public JsonObject getAll(){
		JsonObject response = new JsonObject();
		for (Entry<Long, Transaction> entry : transactionHistory.entrySet())
		{
			response.addProperty(entry.getValue().toJson().toString(), entry.getKey().toString());
		}
		return response;
	}
	public JsonObject deleteAll() {
		JsonObject response = new JsonObject();
		this.transactionHistory.clear();
		response.addProperty("OK","Transactions deleted");
		return response;
	}
	/*
	 * The payment id already exists
	 */
	private boolean checkPaymet(Long id) {
		if (transactionHistory.containsKey(id)) {
			response.addProperty("status", "Do not try to overwrite payment, thank you!");
			return true;
		}
		return false;
	}

	/*
	 * The sent parent_id does not exists
	 */
	private boolean checkIfParentExists(Long id) {
		if (!transactionHistory.containsKey(id)) {
			response.addProperty("status", "forever alone, no parent_id");
			return false;
		}
		return true;
	}

	/*
	 * Insert consistency check on id and parent_id not have to be equal
	 */
	private boolean checkIdsEquality(Long id, Long parentId) {
		if (id.equals(parentId)) {
			response.addProperty("status", "We are cool, but this is a bank, no self references");
			return true;
		}
		return false;
	}

	/*
	 * Cyclic reference are to avoid in order to properly calulate 
	 * sum of tansactions
	 */
	private boolean checkCycles(Long id, Long parentId) {
		if (id.equals(parentId)) {
			response.addProperty("status", "We already know that you are parent of your parent");
			return true;
		}
		return false;
	}

}
