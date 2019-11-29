package asr.proyectoFinal.services;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import asr.proyectoFinal.util.VCAPHelper;

public class AlphaVantageService {

    public static String API_KEY = null;

    public static void main(String args[]) {
        try {
            String src = AlphaVantageService.getJSONStockData("GOOGL");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            BufferedWriter writer = new BufferedWriter(new FileWriter("alpha.json"));
            writer.write(gson.toJson(gson.fromJson(src,JsonObject.class)));
     
            writer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void setApiKey() {

        if (System.getenv("VCAP_SERVICES") != null) {
            // When running in Bluemix, the VCAP_SERVICES env var will have the credentials
            // for all bound/connected services
            // Parse the VCAP JSON structure looking for cloudant.
            JsonObject alphaVantageCredentials = VCAPHelper.getCloudCredentials("alpha_vantage");
            if (alphaVantageCredentials == null) {
                System.out.println("No Alpha Vantage service bound to this application");
                //return null;
            }
            API_KEY = alphaVantageCredentials.get("url").getAsString();
        } else {
            System.out.println("Running locally. Looking for credentials in alpha-vantage.properties");
            API_KEY = VCAPHelper.getLocalProperties("alpha-vantage.properties").getProperty("alphavantage_api");
            if (API_KEY == null || API_KEY.length() == 0) {
                System.out.println(
                        "To use a database, set the Cloudant url in src/main/resources/alpha-vantage.properties");
                //return null;
            }
        }
    }

    public static String getJSONStockData(String symbol) throws IOException {
        if(API_KEY == null)
            AlphaVantageService.setApiKey();

        // Form parameters
        Map<String, String> parametersMap = new HashMap<>();
        parametersMap.put("function", "TIME_SERIES_INTRADAY");
        parametersMap.put("symbol", URLEncoder.encode(symbol,StandardCharsets.UTF_8.toString()));
        parametersMap.put("interval","1min");
        parametersMap.put("apikey", API_KEY);

        // Form string
        StringBuilder paramString = new StringBuilder();
        parametersMap
            .entrySet()
            .stream()
            .map(AlphaVantageService::encodeParam)
            .map("&"::concat)
            .forEach(paramString::append);

        // String params
        String params = paramString.substring(1).toString();

        // Set URL
        String apiUrl = new StringBuilder("https://www.alphavantage.co/query?").append(params).toString();
        
        // Connect to Alpha Vantage
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        // Get result
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        in.close();

        return response.toString();
    }

    private static String encodeParam(Entry<String, String> entry) {
        StringBuilder result = new StringBuilder();
        try {
            return result
                    .append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()))
                    .toString();
        } catch (UnsupportedEncodingException e) {
            return entry.toString();
        }
    }
}