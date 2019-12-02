package asr.proyectoFinal.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import asr.proyectoFinal.models.Candle;
import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.services.AlphaVantageService;
import asr.proyectoFinal.services.CloudantService;
import asr.proyectoFinal.services.YahooService;

/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/news", "/stock"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		PrintWriter out = response.getWriter();
		
		CloudantService store = new CloudantService();
		System.out.println(request.getServletPath());

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		String symbol = request.getParameter("symbol");

		switch(request.getServletPath())
		{
			case "/news":
				ArrayList<YahooNew> newsFeed = YahooService.getNewsFeed(symbol);
				
				out.println(gson.toJson(newsFeed));
				
				break;

			case "/stock":
				ArrayList<Candle> symbolStock = AlphaVantageService.getStockData(symbol);

				out.println(gson.toJson(symbolStock));

				break;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
