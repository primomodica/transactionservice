package com.nr26.service;
/**
 * API specifications:
 * 
 * GET /transactionservice/transaction/$transaction_id
 * Returns:
 * { "amount":double,"type":string,"parent_id":long }
 *
 * A json list of all transaction ids that share the same type $type.
 * 
 * GET /transactionservice/types/$type
 * Returns:
 * [ long, long, .... ]
 *
 * A sum of all transactions that are transitively linked by their parent_id to $transaction_id.
 * GET /transactionservice/sum/$transaction_id
 * Returns
 * { "sum": double }
 *
 * PUT /transactionservice/transaction/$transaction_id
 * Body:
 * { "amount":double,"type":string,"parent_id":long }
 * where:
 * transaction_id is a long specifying a new transaction
 * amount is a double specifying the amount
 * type is a string specifying a type of the transaction.
 * parent_id is an optional long that may specify the parent transaction of this transaction.
 *
 */

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.nr26.core.Transaction;
import com.nr26.requesthandler.TransactionHandler;
import com.nr26.requesthandler.TypeHandler;

@Path("/transactionservice")
public class Api {
	
	private static final TransactionHandler transactionHandler = TransactionHandler.TrsansacttionHandlerINSTANCE;
	private static final TypeHandler typeHandler = TypeHandler.TypeHandlerINSTANCE;

	@PUT
	@Path("transaction/{transaction_id:\\d+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String storeNewTransaction(@PathParam("transaction_id") String trans_id, String tr) {
		JsonObject creationResult = new JsonObject();
		// hey do not pass floats
		if (trans_id.contains(".") || trans_id.contains(",")) {
			creationResult.addProperty("Error", "The transaction id has to be Int");
		} else {
			try {
				Long transaction_id = Long.parseLong(trans_id);
				creationResult = transactionHandler.newTransaction(transaction_id, tr);
				typeHandler.update(transaction_id, tr);
				Response.Status responseStatus = creationResult.get("status").toString().equals("ok")
						? Response.Status.OK : Response.Status.BAD_REQUEST;
				return creationResult.toString();
			} 
			catch (NumberFormatException nfe) {
				creationResult.addProperty("Error","NumberFormatException");
		      }
			catch (IllegalStateException i) {
				creationResult.addProperty("Error", "No parameters for the transaction");
			}
		}
		return creationResult.toString() + "\n";
	}

	/*
	 * Just for testing localhost and fun
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String get() {
		JsonObject jo = transactionHandler.getAll();
		return jo.toString()+"\n";
	}

	@GET
	@Path("transaction/{transaction_id:\\d+}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTransaction(@PathParam("transaction_id") String transaction_id) {
		JsonObject transaction = transactionHandler.getTransaction(transaction_id);
		return transaction.toString();
	}
	@GET
	@Path("delete")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteAll(){
		JsonObject transaction = transactionHandler.deleteAll();
		return transaction.toString();
	}

	@GET
	@Path("types/{type}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getTypes(@PathParam("type") String type) {
		JsonArray list = typeHandler.getType(type);
		if (list.size() == 0) {
			JsonObject error = new JsonObject();
			error.addProperty("Error", "No transaction of this type");
			return error.toString();
		}
		return list.toString();
	}

	 @GET @Path("sum/{transaction_id:\\d+}")
	 @Produces(MediaType.APPLICATION_JSON)
	 public String getSum(@PathParam("transaction_id") String transaction_id)
	 {
	 JsonObject transaction = transactionHandler.getSum(Long.parseLong(transaction_id));
	 return transaction.toString();

	 }
}