package org.keybase.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class KeybaseMerkleTreeApp {

	public static void main(String[] args) throws IOException {

		Scanner input = new Scanner(System.in);
		System.out.print("Enter Keybase Username: ");
		String username = input.nextLine();
		String hash = "cb3a16b209a78c0fd145f271c2bd2b2993fae4d222a5cf1650ddb731e1250311e45f40be9d38e58d64d53c165ff7c7abf0bfe5167ac85cfe4f60d742222c3360";
		// compute uid (also found here:
		// https://keybase.io/_/api/1.0/user/lookup.json?)
		String uid = org.apache.commons.codec.digest.DigestUtils.sha256Hex(username).substring(0, 30) + "19";
		System.out.println("uid: " + uid);

		boolean leafnode = false;
		int indexLength = 1;
		while (!leafnode) {
			// merkle/block get request
			String url = "https://keybase.io/_/api/1.0/merkle/block.json?hash=" + hash;
			HttpClient client = HttpClients.custom()
			        .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
			        .build();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			// parse jSON result
			JsonObject resultObj = new JsonParser().parse(result.toString()).getAsJsonObject();
			// verify node's hash
			if (!resultObj.get("hash").getAsString().equals(org.apache.commons.codec.digest.DigestUtils
			        .sha512Hex(resultObj.get("value_string").getAsString()))) {
				System.out.println("**Could not verify node's hash**");
				System.exit(0);
			}
			// check if leaf node
			if (resultObj.get("type").getAsInt() == 2) {
				leafnode = true;
				System.out.println(toPrettyFormat(resultObj.toString()));
			} else {
				// get correct child hash according to uid
				hash = resultObj.get("value").getAsJsonObject().get("tab").getAsJsonObject()
				        .get(uid.substring(0, indexLength)).getAsString();
				System.out.println(uid.substring(0, indexLength) + ": " + hash);
				indexLength++;
			}

		}
	
		

	}

	public static String toPrettyFormat(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String prettyJson = gson.toJson(json);
		return prettyJson;
	}

}
