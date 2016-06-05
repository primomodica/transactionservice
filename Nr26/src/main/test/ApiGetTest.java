package main.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ApiGetTest {
	HashMap<Integer, String> existingTransaction = new HashMap<Integer, String>();
	@BeforeClass
	public static void setUp(){
		Utils.resetTansactions();
	}
	@Test
	public void test() {
		int i = 0;
		while (i < 10) {
			i++;
			String newJson = generateTransaction(i);
		}
		for (Entry<Integer, String> entry : existingTransaction.entrySet()) {
			JSONObject transaction = new JSONObject(getTransaction(entry.getKey()));
			JSONObject value = new JSONObject(entry.getValue());
			System.out.println("The expected response is: " + value.toString() + " and from server we had: " + transaction.toString());
			assertTrue(transaction.toString().equals(value.toString()));
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
				j = randomGenerator.nextInt(existingTransaction.size())+1;
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
			String attended = "{\"status\":\"ok\"}";
			assertTrue(attended.equals(resp));
			in.close();
		} catch (Exception e) {
			System.out.println("\n Error while calling  REST Service");
			System.out.println(e);
		}
		return transaction;
	}

	private String getTransaction(int id) {
		try {
			URL url = new URL("http://localhost:8080/transactionservice/transaction/" + id);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			System.out.println(connection.getURL());
			System.out.println();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String resp = in.readLine();
			in.close();
			return resp;
		} catch (Exception e) {
			System.out.println("\n Error while calling  REST Service");
			System.out.println(e);
		}
		return "";
	}
}
