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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;

import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.util.VCAPHelper;

public class AlphaVantageService {

    public static String API_KEY = null;

    public static void main(String args[]) {
        try {
        	String symbolId = "GOOGL";
            String src = AlphaVantageService.getJSONStockData(symbolId);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            //System.out.println(gson);
            BufferedWriter writer = new BufferedWriter(new FileWriter("alpha.json"));
            writer.write(gson.toJson(gson.fromJson(src,JsonObject.class)));
            //System.out.println(gson.toJson(gson.fromJson(src,JsonObject.class)));
            String listaCompleta = gson.toJson(gson.fromJson(src,JsonObject.class));
            
            JSONObject obj = new JSONObject(listaCompleta);
            //System.out.println(obj);
            //JSONObject l = obj.getJSONObject("Time Series (1min)").getJSONObject("2019-11-29 13:01:00");
            JSONArray fechas = obj.getJSONObject("Time Series (1min)").names();
            Iterator it = fechas.iterator();
            Date fecha = new Date();
            String texto;
            JSONObject momento;
            float low,high,open,close;
            int volume;
            ArrayList listaProcesada = new ArrayList();
            //Get financial data into an ArrayList listaProcesada
            while(it.hasNext())	{
            	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");	//y-M-d t
            	texto = it.next().toString();//.replace("CET", "");
            	fecha = sdf.parse(texto);
            	momento = obj.getJSONObject("Time Series (1min)").getJSONObject(texto);
            	
            	open = momento.getFloat("1. open");
            	high = momento.getFloat("2. high");
            	low = momento.getFloat("3. low");
            	close = momento.getFloat("4. close");
            	volume = momento.getInt("5. volume");
            	
            	//Candle aux = new Candle(fecha, open, high, low, close, volume);
            	
            	listaProcesada.add(new Candle(fecha, open, high, low, close, volume));
            	System.out.println(fecha+"\n"+texto+"\n"+momento);
            	System.out.println(open+"\n"+high+"\n"+low+"\n"+close+"\n"+volume);
            	
            }
            
            //get news data into an ArrayList news
            ArrayList<YahooNew> list = YahooService.getNewsFeed(symbolId);
            //System.out.printl(new Gson.toJson(list));
            it = list.iterator();
            YahooNew aux = new YahooNew();
            ArrayList news = new ArrayList();
            AnalysisResults analisis;
            Double score;
            while (it.hasNext())	{
            	aux = (YahooNew) it.next();
            	analisis = NLUService.analisisSentimientoURL(aux.getLink());
            	score = analisis.getSentiment().getDocument().getScore();
            	aux.setScore(score);
            	news.add(aux);
            	System.out.println(aux.getLink());
            	System.out.println(score);
            }
            
            int maxNoticias = 5;
            
            FileWriter csvWriter = new FileWriter("prices.csv");
            csvWriter.append("date");
            csvWriter.append(",");
            csvWriter.append("Close");
            csvWriter.append(",");
            csvWriter.append("Open");
            csvWriter.append(",");
            csvWriter.append("High");
            csvWriter.append(",");
            csvWriter.append("Low");
            csvWriter.append(",");
            csvWriter.append("Volume");
            for(int j = 0; j<maxNoticias; j++)	{
            	csvWriter.append(",");
            	csvWriter.append("Score"+j);
            	csvWriter.append(",");
            	csvWriter.append("Distance"+j);
            }
            	
            
            
            csvWriter.append("\n");
            Date dateNew;
            ArrayList newsValidDates;
            Candle candle;
            
            
            long distancia;
            int cont;
            for (Object rowData : listaProcesada) {
            	candle = (Candle) rowData;
            	fecha = candle.getFecha();
            	newsValidDates = new ArrayList();
            	cont = 0;
            	//Se toman las noticias válidas empezando por la última
            	for (int j = 0; j < news.size(); j++)	{
            		aux = (YahooNew) news.get(j);
            		dateNew = aux.getPubDate();
            		System.out.println("Fecha de la vela: "+fecha);
            		System.out.println("Fecha a analizar: "+dateNew);
            		
            		if(dateNew.before(fecha) && cont < maxNoticias)	{
            			System.out.println("Fecha anterior: "+dateNew);
            			System.out.println("Fecha posterior: "+fecha);
            			System.out.println("Score añadido: "+aux.getScore());
            			newsValidDates.add(aux);
            			cont++;
            		}
            	}
            	
            	
            	
            	csvWriter.append(""+candle.getFecha());
                csvWriter.append(",");
            	csvWriter.append(""+candle.getClose());
                csvWriter.append(",");
                csvWriter.append(""+candle.getOpen());
                csvWriter.append(",");
                csvWriter.append(""+candle.getHigh());
                csvWriter.append(",");
                csvWriter.append(""+candle.getLow());
                csvWriter.append(",");
                csvWriter.append(""+candle.getVolume());
                
                for(int j = 0; j<maxNoticias; j++)	{
                	score = 0.0;
                	distancia = 999999999;
                	try {
	                	//analisis = NLUService.analisisSentimientoURL(((YahooNew) newsValidDates.get(j)).getLink());
	                	aux = (YahooNew) newsValidDates.get(j);
                		score = aux.getScore();
	                	distancia = fecha.getTime() - aux.getPubDate().getTime();
	                	System.out.println(score);
                	}catch(Exception e)	{
                		
                	}finally{
                		csvWriter.append(",");
                    	csvWriter.append(""+score);
                    	csvWriter.append(",");
                    	csvWriter.append(""+distancia);
                	}
                }
                
                csvWriter.append("\n");
                
            }

            csvWriter.flush();
            csvWriter.close();
            	
            //JSONArray geodata = obj.getJSONArray("geodata");
            /*int n = obj.length();
            for (int i = 0; i < n; ++i) {
              JSONObject person = obj
              System.out.println(person);
            }*/
            
            //System.out.println(gson.toJson(gson.fromJson(src,JsonObject.class)).getClass());
            
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