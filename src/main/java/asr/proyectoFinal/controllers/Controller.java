package asr.proyectoFinal.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;
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

/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/news", "/stock", "/score", "/data"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ArrayList<Symbol> symbols = new ArrayList<Symbol>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		CloudantService store = new CloudantService();

		// Response parameters
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		// Gson for JSON formatting
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		// Get request parameters
		String symbolString = request.getParameter("symbol");
		String newId = request.getParameter("id");

		// Find symbol if exists
		Symbol targetSymbol = symbols
								.stream()
								.filter(symbol -> symbol.getSymbolId().equals(symbolString))
								.findFirst()
								.orElse(new Symbol(symbolString));

		// Paths
		try {
			System.out.println(request.getServletPath());
			switch(request.getServletPath()) {
				// Return news from a given symbol
				case "/news":
					ArrayList<YahooNew> newsFeed = targetSymbol.updateNews();				
					
					out.println(gson.toJson(newsFeed));
					
					break;
				
				// Return the historic stock from a certain symbol
				case "/stock":
					ArrayList<Candle> symbolStock = targetSymbol.updateStock();

					out.println(gson.toJson(symbolStock));

					break;

				// Get score for a Yahoo new
				case "/score":
					YahooNew targetNew = targetSymbol.getNews()
													   .stream()
													   .filter(n -> n.getGuid().getContent().equals(newId))
													   .findFirst()
													   .get();
										
					AnalysisResults analysis = NLUService.sentimentAnalysis(targetNew);
					
					targetNew.setScore(analysis.getSentiment().getDocument().getScore());

					out.println(gson.toJson(analysis));
					
					break;
				
				// Get stored data from a symbol
				case "/data":
					out.println(gson.toJson(targetSymbol));

					break;
			}

			// Store modified symbol
			Symbol storedSymbol;
			if(targetSymbol.get_id() != null)
				storedSymbol = store.update(targetSymbol.get_id(), targetSymbol);
			else
				storedSymbol = store.persist(targetSymbol);

			// Remove original symbol
			symbols = symbols
						.stream()
						.filter(symbol -> !symbol.getSymbolId().equals(symbolString))
						.collect(Collectors.toCollection(ArrayList::new));

			// Add modified symbol
			symbols.add(storedSymbol);


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
