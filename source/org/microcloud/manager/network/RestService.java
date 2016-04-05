package org.microcloud.manager.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestService {
	
	public String getStringFromAddress(String address) throws IOException {
		URL url;
		try {
			url = new URL(address);

			HttpURLConnection conn;
			conn = (HttpURLConnection) url.openConnection();
			
			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}
			
			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(
			    new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			
			conn.disconnect();
			return sb.toString();			
			
		} catch (MalformedURLException e) {
			System.err.println("GIVEN URL ADDRESS " + address + " IS INCORRECT!");
			System.err.println(e.getMessage());
			System.exit(-1);
			return null;
		}


	}
}
