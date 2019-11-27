package asr.proyectoFinal.services;

import java.util.List;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class YahooService {
    public static void main(String args[]) {
        try {
            System.out.println(YahooService.getRawNewsFeed("GOOGL"));
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

        try{
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
}