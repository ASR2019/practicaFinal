package asr.proyectoFinal.services;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import asr.proyectoFinal.util.JSONHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class YahooService {
    public static void main(String args[]) {
        try {
            String jsonString = YahooService.getJSONNewsFeed("GOOGL");
            JsonElement jsonParse = new JsonParser().parse(jsonString);

            if(jsonParse.isJsonObject()) {
                JsonObject json = jsonParse.getAsJsonObject();
                System.out.println(json);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    // TODO Throw custom exception
    public static String getRawNewsFeed(String symbol) {

        // Build URL
        String urlString = new StringBuilder()
                            .append("https://feeds.finance.yahoo.com/rss/2.0/headline?s=")
                            .append(symbol)
                            .toString();
        StringBuilder response = new StringBuilder();
        String inputLine;

        try {
            // Create connection
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            
            // Response handling
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            
            if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            in.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return response.toString();
    }

    public static String getJSONNewsFeed(String symbol) {
        // Get raw data
        String response = YahooService.getRawNewsFeed(symbol);

        // Transform to JSON
        String jsonString = JSONHelper.xmlToJson(response);

        return jsonString;
    }
}