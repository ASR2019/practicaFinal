package asr.proyectoFinal.models;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import asr.proyectoFinal.models.YahooNew.Guid;
import asr.proyectoFinal.services.AlphaVantageService;
import asr.proyectoFinal.services.YahooService;

public class Symbol {
	//2ª tarea: asegurarme la funcionalidad
	private String jsonData;
	private String newsJson;
	private String symbolId;
	private ArrayList<YahooNew> news;
	
	public Symbol(String symbolId) throws IOException	{
		jsonData = AlphaVantageService.getJSONStockData(symbolId);
		newsJson = YahooService.getJSONNewsFeed(symbolId);
		ArrayList<YahooNew> list = YahooService.getNewsFeed(symbolId);
		
		YahooNew aux = new YahooNew();
		news = new ArrayList();
		Iterator it = list.iterator();
		while(it.hasNext())	{
			aux = AlphaVantageService.analisisSentimientoNoticia((YahooNew) it.next());
			news.add(aux);
		}
	}
	public static void main() throws Exception	{
		ArrayList<YahooNew> list = YahooService.getNewsFeed("GOOGL");
		
		YahooNew aux = new YahooNew();
		aux = list.get(1);
		String id = aux.getGuid().getContent();
		System.out.println(id);
		YahooNew noticiaFinal = AlphaVantageService.analisisSentimientoNoticia(aux);
		System.out.println(noticiaFinal);
	}
}
