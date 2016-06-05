package com.nr26.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.json.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ApiTypeTest {
	private String[] types = { "shopping", "services", "taxes", "food", "amusement", "sport", "travel" };
	private ArrayList<Integer> shopping_ids = new ArrayList<Integer>();
	private ArrayList<Integer> taxes_ids = new ArrayList<Integer>();
	private ArrayList<Integer> amusement_ids = new ArrayList<Integer>();
	private final static int shop = 10;
	private final static int taxes = 15;
	private final static int amus = 22;
	@BeforeClass
	public static void setUp(){
		Utils.resetTansactions();
	}
	@Test
	public void test() {
		int i = 0;
		while (i < shop) {
			i++;
			// type Shopping
			shopping_ids.add(i);
			generateTransactionType(i, 0);
		}
		while (i < taxes) {
			i++;
			// type Taxes
			taxes_ids.add(i);
			generateTransactionType(i, 2);
		}
		while (i < amus) {
			i++;
			// type Amusement
			amusement_ids.add(i);
			generateTransactionType(i, 4);
		}
		
		String ids = getTypeArray("shopping");
		assertTrue(ids.contains("1"));
		assertTrue(ids.contains("2"));
		assertTrue(ids.contains("3"));
		assertTrue(ids.contains("4"));
		assertTrue(ids.contains("5"));
		assertTrue(ids.contains("6"));
		assertTrue(ids.contains("7"));
		assertTrue(ids.contains("8"));
		assertTrue(ids.contains("9"));
		assertTrue(ids.contains("10"));

		

		ids = getTypeArray("taxes");
		assertTrue(ids.contains("11"));
		assertTrue(ids.contains("12"));
		assertTrue(ids.contains("13"));
		assertTrue(ids.contains("14"));
		assertTrue(ids.contains("15"));


		ids = getTypeArray("amusement");
		assertTrue(ids.contains("16"));
		assertTrue(ids.contains("17"));
		assertTrue(ids.contains("18"));
		assertTrue(ids.contains("19"));
		assertTrue(ids.contains("20"));
		assertTrue(ids.contains("21"));
		assertTrue(ids.contains("22"));

	}

	private void generateTransactionType(int id, int picker) {

		String transaction = "{ \"amount\": " + (Math.random() * 10) + ", \"type\":" + types[picker] + " }";
		JSONObject jsonObject = new JSONObject(transaction);
		try {
			URL url = new URL("http://localhost:8080/Nr26/nr26/transactionservice/transaction/" + id);
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
	}

	private String getTypeArray(String type) {
		try {
			URL url = new URL("http://localhost:8080/Nr26/nr26/transactionservice/types/" + type);
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
