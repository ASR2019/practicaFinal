package asr.proyectoFinal.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
// import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults;

import asr.proyectoFinal.models.Candle;
import asr.proyectoFinal.models.Symbol;
import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.services.CloudantService;
import asr.proyectoFinal.services.NLUService;
import asr.proyectoFinal.services.TranslatorService;

/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/api/news", "/api/stock", "/api/score", "/api/data", "/api/translate"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static ArrayList<Symbol> symbols = new ArrayList<Symbol>();
	private static CloudantService store;
	private static Map<String,String> dbRefs;

	public void init() throws ServletException {
		// Create database client
		store = new CloudantService();

		// Get references for database
		dbRefs = store.getReferences();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{		
		// Response parameters
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// TODO: Remove
		response.setHeader("Access-Control-Allow-Origin", "*");

		// Get request parameters
		String symbolString = request.getParameter("symbol");
		String newId = request.getParameter("id");
		String targetLanguage = request.getParameter("lang");
		String angularClient = request.getParameter("ng");

		// Gson for JSON formatting
		Gson gson;
		if(angularClient != null)
			gson = new GsonBuilder().create();
		else
			gson = new GsonBuilder().setPrettyPrinting().create();

		Symbol targetSymbol = null;

		if(symbolString != null) {
			// Find symbol if exists on memory
			targetSymbol = symbols
								.stream()
								.filter(symbol -> symbol.getSymbolId().equals(symbolString))
								.findFirst()
								.orElse(null);
			
			// Find symbol on database through references
			if(targetSymbol == null) {
				System.out.println(dbRefs.keySet().contains(symbolString));
				System.out.println(symbolString);
				targetSymbol = dbRefs.keySet().contains(symbolString) ? store.get(dbRefs.get(symbolString)) : new Symbol(symbolString);
			}
		}

		// Path management
		try {
			System.out.println(request.getServletPath());
			switch(request.getServletPath()) {
				// Return news for a given symbol
				case "/api/news":
					ArrayList<YahooNew> newsFeed = targetSymbol.updateNews();			
					
					out.println(gson.toJson(newsFeed));
					
					break;
				
				// Return the historic stock from a certain symbol
				case "/api/stock":
					ArrayList<Candle> symbolStock = targetSymbol.updateStock();

					out.println(gson.toJson(symbolStock));

					break;

				// Get score for a Yahoo new
				case "/api/score":
					YahooNew targetNew = targetSymbol.getNews()
													   .stream()
													   .filter(n -> n.getGuid().getContent().equals(newId))
													   .findFirst()
													   .get();

					System.out.println("New found");
										
					AnalysisResults analysis = NLUService.sentimentAnalysis(targetNew);
					
					System.out.println("Analysis done");

					targetNew.setScore(analysis.getSentiment().getDocument().getScore());

					System.out.println("Score set");

					out.println(gson.toJson(analysis));
					
					break;
				
				// Get stored data from a symbol
				case "/api/data":
					out.println(gson.toJson(targetSymbol));

					break;
				
				case "/api/translate":
					String content = request.getParameter("q");
					String result = TranslatorService.translate(content, targetLanguage);

					out.println(gson.toJson(result));
			}

			if(targetSymbol != null) {
				// Store modified symbol on DB
				Symbol storedSymbol;
				if(targetSymbol.get_id() != null)
					storedSymbol = store.update(targetSymbol.get_id(), targetSymbol);
				else {
					storedSymbol = store.persist(targetSymbol);
					dbRefs.put(storedSymbol.getSymbolId(), storedSymbol.get_id());
				}

				// Remove original symbol from memory
				symbols = symbols
							.stream()
							.filter(symbol -> !symbol.getSymbolId().equals(symbolString))
							.collect(Collectors.toCollection(ArrayList::new));

				// Add modified symbol to memory
				symbols.add(storedSymbol);
			}
		} catch(Exception e) {
			out.println(e.toString());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
