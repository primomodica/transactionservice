package com.nr26.core;

import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;


public class Transaction {
	
	private double amount;
	private String type;
	private long transaction_id;
	private Transaction directParent;
	private Set<Transaction> children = new HashSet<Transaction>();
	private Set<Transaction> transitiveChildren = new HashSet<Transaction>();

	private long sum;
	
	public Transaction(String amount, String type){
		this.amount = Double.parseDouble(amount);
		this.type = type;
	}
	
	public void addDirectParent(Transaction parent_id) {
		directParent = parent_id;
	}
	
	public JsonObject toJson() {
		JsonObject trToJson = new JsonObject();
		trToJson.addProperty("amount", this.amount);
		trToJson.addProperty("type", this.type);
		if(directParent != null){trToJson.addProperty("parent_id", this.directParent.getId());}
		return trToJson;
	}
	public void addChild(Transaction p) {
		this.children.add(p);	
	}

	public long getId() 
	{
		return transaction_id;
	}
	public long getDirectParentId() 
	{
		return directParent.getId();
	}

	public double getAmount() 
	{
		return amount;
	}

	public String getType() 
	{
		return type;
	}

	public void setId(Long transaction_id) {
		this.transaction_id = (transaction_id);
	}
	//DFS algorith
	public double sum() {
		double sum = this.amount;
		//exit condition
		if(children.isEmpty()) 
			{
			return sum;
			}
		else{
		for(Transaction t : children){
		//recursion
		sum += t.sum();
		}
		return sum;
	}
	}

}
