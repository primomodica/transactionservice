package com.nr26.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApiPutTest {
	HashMap<Integer, String> existingTransaction = new HashMap<Integer, String>();
	private final static int NUMBER_OF_TRANSACTION = 10;
	 @BeforeClass
	 public static void setUp(){
	 Utils.resetTansactions();
	 }
	
	@Test
	public void testPut() {
		int i = 0;
		while (i < NUMBER_OF_TRANSACTION) {
 
			i++;
			JSONObject newJson = new JSONObject(generateTransaction(i));
			String attended = "{\"status\":\"ok\"}";
			System.out.println("The expected response is: " + attended + " and from server we had: " + newJson.toString());
			assertTrue(attended.equals(newJson.toString()));

		}
	}

	public String generateTransaction(int trans_id) {
		String[] types = { "shopping", "services", "taxes", "food", "amusement", "sport", "travel" };
		int typePicker = (int) (Math.random() * 6);

		String transaction = "{ \"amount\": " + (Math.random() * 10) + ", \"type\":" + types[typePicker];
		if (Math.random() > 0.5 && !existingTransaction.isEmpty()) {
			int j;
			Random randomGenerator = new Random();
			do {
				j = randomGenerator.nextInt(existingTransaction.size()) + 1;
			} while (j == trans_id && existingTransaction.containsKey(j));
			transaction = new String(transaction + ", \"parent_id\":" + j + " }");
		} else {
			transaction = new String(transaction + " }");
		}
		JSONObject jsonObject = new JSONObject(transaction);
		existingTransaction.put(trans_id, jsonObject.toString());

		try {
			URL url = new URL("http://localhost:8080/transactionservice/transaction/" + trans_id);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
			osw.write(jsonObject.toString());
			osw.flush();
			osw.close();
			System.out.println(connection.getURL());
			System.out.println(jsonObject);
			System.out.println();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String resp = in.readLine();
			in.close();
			return resp;
		} catch (Exception e) {
			System.out.println("\n Error while calling  REST Service");
			System.out.println(e);
		}
		return null;
	}
}
