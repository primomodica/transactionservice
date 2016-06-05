package main.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonObject;

public class ApiSumTest {
	HashMap<Integer, String> existingTransaction = new HashMap<Integer, String>();
	private final static int NUMBER_OF_TRANSACTION = 10;
	private final static double TRANSACTION_AMOUNT = 10.10;

	@BeforeClass
	public static void setUp() {
		Utils.resetTansactions();
	}

	@Test
	public void testAllToOne() {
		int i = 0;
		while (i < NUMBER_OF_TRANSACTION) {
			i++;
			String newJson = generateTransaction(i, "all");
		}
		// all linked

		JSONObject resp = new JSONObject(getSum(1));
		double sum = Math.round(resp.getDouble("sum"));
		double expectedSum = Math.round(i * TRANSACTION_AMOUNT);
		assertTrue(sum == expectedSum);
		System.out.println("The expected response is: " + expectedSum + " and from server we had: " + sum);

	}

	@Test
	public void testNoOne() {
		Utils.resetTansactions();
		int i = 0;
		while (i < NUMBER_OF_TRANSACTION) {
			i++;
			String newJson = generateTransaction(i, "none");
		}
		// all linked
		for (int j = 1; j <= NUMBER_OF_TRANSACTION; j++) {
			JSONObject resp = new JSONObject(getSum(j));
			double sum = Math.round(resp.getDouble("sum"));
			double expectedSum = Math.round(TRANSACTION_AMOUNT);
			assertTrue(sum == expectedSum);
			System.out.println("The expected response is: " + expectedSum + " and from server we had: " + sum);
		}
	}

	@Test
	public void someLinked() {
		Utils.resetTansactions();

		generateSpecificTransaction();
		// all linked

		// the "node" 1 has as child 2
		// node 2 has as child 4 and 7
		// 4 has as child 8 and 7 is alone
		JSONObject resp = new JSONObject(getSum(1));
		double sum = Math.round(resp.getDouble("sum"));
		double expectedSum = Math.round(26);
		assertTrue(sum == expectedSum);
		System.out.println("The expected response is: " + expectedSum + " and from server we had: " + sum);
		//the node 3 has as child 5 and transitively has as child 6
		resp = new JSONObject(getSum(3));
		sum = Math.round(resp.getDouble("sum"));
		expectedSum = Math.round(10668.8111);
		assertTrue(sum == expectedSum);
		System.out.println("The expected response is: " + expectedSum + " and from server we had: " + sum);
		// 9 has no child
		resp = new JSONObject(getSum(9));
		sum = Math.round(resp.getDouble("sum"));
		expectedSum = Math.round(9.99);
		assertTrue(sum == expectedSum);
		System.out.println("The expected response is: " + expectedSum + " and from server we had: " + sum);

	}

	public void generateSpecificTransaction() {
		String[] types = { "shopping", "services", "taxes", "food", "amusement", "sport", "travel" };
		int typePicker = (int) (Math.random() * 6);
		ArrayList<String> transactions = new ArrayList<String>();
		// 1
		transactions.add(new String("{ \"amount\": " + 10.1 + ", \"type\":shopping }"));
		// 2
		transactions.add(new String("{ \"amount\": " + 3.45 + ", \"type\":shopping, \"parent_id\":" + 1 + " }"));
		// 3
		transactions.add(new String("{ \"amount\": " + 1 + ", \"type\":shopping }"));
		// 4
		transactions.add(new String("{ \"amount\": " + 2.45 + ", \"type\":shopping, \"parent_id\":" + 2 + " }"));
		// 5
		transactions.add(new String("{ \"amount\": " + 10000.1111 + ", \"type\":shopping, \"parent_id\":" + 3 + " }"));
		// 6
		transactions.add(new String("{ \"amount\": " + 667.7 + ", \"type\":shopping, \"parent_id\":" + 5 + " }"));
		// 7
		transactions.add(new String("{ \"amount\": " + 4.4 + ", \"type\":shopping, \"parent_id\":" + 2 + " }"));
		// 8
		transactions.add(new String("{ \"amount\": " + 5.5 + ", \"type\":shopping, \"parent_id\":" + 4 + " }"));
		// 9
		transactions.add(new String("{ \"amount\": " + 9.99 + ", \"type\":shopping }"));

		// links 1 <- 2 <- (4 <- 8,7)
		// links 3 <- 5 <- 6 
		// 9 alone -> sum = 9
		int i = 0;
		for (String t : transactions) {
			i++;
			JSONObject jsonObject = new JSONObject(t);

			try {
				URL url = new URL("http://localhost:8080/transactionservice/transaction/" + i);
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
			} catch (Exception e) {
				System.out.println("\n Error while calling  REST Service");
				System.out.println(e);
			}
		}

	}

	public String generateTransaction(int trans_id, String mode) {
		String[] types = { "shopping", "services", "taxes", "food", "amusement", "sport", "travel" };
		int typePicker = (int) (Math.random() * 6);
		String transaction = "";
		if (trans_id < 2 || mode.equals("none")) {

			transaction = "{ \"amount\": " + TRANSACTION_AMOUNT + ", \"type\":" + types[typePicker] + " }";
		} else {
			// let's link all to 1
			transaction = "{ \"amount\": " + TRANSACTION_AMOUNT + ", \"type\":" + types[typePicker] + ", \"parent_id\":"
					+ 1 + " }";
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
		return "";
	}

	private String getSum(int id) {
		try {
			URL url = new URL("http://localhost:8080/transactionservice/sum/" + id);
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
			System.out.println(resp);
			return resp;
		} catch (Exception e) {
			System.out.println("\n Error while calling  REST Service");
			System.out.println(e);
		}
		return "";
	}
}
