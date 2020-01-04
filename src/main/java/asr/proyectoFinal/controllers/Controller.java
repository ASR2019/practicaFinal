package asr.proyectoFinal.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;

import asr.proyectoFinal.models.Candle;
import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.services.AlphaVantageService;
//import asr.proyectoFinal.services.CloudantService;
import asr.proyectoFinal.services.NLUService;
import asr.proyectoFinal.services.YahooService;

/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/news", "/stock", "/data"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		PrintWriter out = response.getWriter();
		
		//CloudantService store = new CloudantService();
		System.out.println(request.getServletPath());

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String symbol = request.getParameter("symbol");

		ArrayList<Candle> symbolStock = new ArrayList<Candle>();
		ArrayList<YahooNew> newsFeed = new ArrayList<YahooNew>();

		switch(request.getServletPath())
		{
			// 
			case "/news":
				newsFeed = this.fetchNews(symbol);				
				
				out.println(gson.toJson(newsFeed));
				
				break;
			
			// 
			case "/stock":
				symbolStock = this.fetchStock(symbol);

				out.println(gson.toJson(symbolStock));

				break;

			// Asynchronously fetch new data from the Internet.
			case "/data":
				
				String id = request.getParameter("idNoticia");
				YahooNew noticiaBuscada = Controller.getNoticia(id);
				YahooNew noticiaFinal = AlphaVantageService.analisisSentimientoNoticia(noticiaBuscada);
				out.println(gson.toJson(noticiaFinal));
				

				break;
			
		}
	}

	private static YahooNew getNoticia(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private ArrayList<Candle> fetchStock(String symbol) {
		try {
			return AlphaVantageService.getStockData(symbol);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ArrayList<YahooNew> fetchNews(String symbol) {
		try {
			ArrayList<YahooNew> newsFeed = YahooService.getNewsFeed(symbol);
			Map<YahooNew, AnalysisResults> analysis = NLUService.sentimentAnalysis(newsFeed);

			List<YahooNew> list = analysis
				.entrySet()
				.stream()
				.map(e -> {
					YahooNew yahooNew = e.getKey();
					yahooNew.setScore(e.getValue().getSentiment().getDocument().getScore());
					return yahooNew;
				})
				.collect(Collectors.toList());

			return new ArrayList<YahooNew>(list);
		} catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}

}
