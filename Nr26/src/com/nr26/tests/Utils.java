package com.nr26.tests;

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

public class Utils {
	
	public static void  resetTansactions() {
		try {
			URL url = new URL("http://localhost:8080/Nr26/nr26/transactionservice/delete");
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
		} catch (Exception e) {
			System.out.println("\n Error while calling  REST Service");
			System.out.println(e);
		}
	}
}
