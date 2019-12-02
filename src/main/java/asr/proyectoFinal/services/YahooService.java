package asr.proyectoFinal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.google.gson.reflect.TypeToken;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.util.JSONHelper;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

public class YahooService {

    private static final String API_ENDPOINT = "https://feeds.finance.yahoo.com/rss/2.0/headline";
    // public static void main(String args[]) {
    //     try {
    //         ArrayList<YahooNew> list = YahooService.getNewsFeed("GOOG");
    //         //System.out.printl(new Gson.toJson(list));
    //         Iterator it = list.iterator();
    //         YahooNew aux = new YahooNew();
    //         ArrayList news = new ArrayList();
    //         while (it.hasNext())	{
    //         	aux = (YahooNew) it.next();
    //         	news.add(aux);
    //         	//System.out.println(aux.getLink());
    //         }
            
    //         FileWriter csvWriter = new FileWriter("news.csv");
    //         AnalysisResults analisis;
    //         YahooNew yahooNew;
    //         Double score;
    //         Date date;
    //         for (Object rowData : news) {
    //         	yahooNew = (YahooNew) rowData;
    //         	date = yahooNew.getPubDate();
            	
    //         	analisis = NLUService.analisisSentimientoURL(yahooNew.getLink());
    //         	score = analisis.getSentiment().getDocument().getScore();
    //         	//System.out.println(score);
            	
    //             csvWriter.append(""+date);
    //             csvWriter.append(",");
    //             csvWriter.append(""+score);
    //             csvWriter.append("\n");
    //         }

    //         csvWriter.flush();
    //         csvWriter.close();
    //     } catch(Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // TODO Throw custom exception
    public static String getRawNewsFeed(String symbol) throws IOException {

        // Build URL
        String urlString = new StringBuilder(YahooService.API_ENDPOINT)
                            .append("?s=")
                            .append(symbol)
                            .toString();
        
        // Set up response string
        StringBuilder response = new StringBuilder();
        String inputLine;

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
        
        return response.toString();
    }

    public static String getJSONNewsFeed(String symbol) throws IOException {
        // GSON to parse and unparse
        Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

        // Get raw data
        String response = YahooService.getRawNewsFeed(symbol);

        // Transform to JSON
        String jsonString = JSONHelper.xmlToJson(response);
        
        // Get items
        JsonObject json = gson.fromJson(jsonString, JsonObject.class);
        JsonArray items = json
                            .get("rss")
                            .getAsJsonObject()
                            .get("channel")
                            .getAsJsonObject()
                            .get("item")
                            .getAsJsonArray();

        return gson.toJson(items);
    }

    public static ArrayList<YahooNew> getNewsFeed(String symbol) throws IOException {
        // GSON parse to Java Object
        Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .setDateFormat("EEE, dd MMM yyyy HH:mm:ss Z")
                        .create();
        
        // Get JSON string of chars
        String jsonString = YahooService.getJSONNewsFeed(symbol);
        
        // Set the type of the resulting data
        Type listType = new TypeToken<ArrayList<YahooNew>>() {}.getType();
        
        // Parse JSON String to JsonObject from GSON
        ArrayList<YahooNew> list = gson.fromJson(jsonString, listType);

        // Return generated list
        return list;
    }
}