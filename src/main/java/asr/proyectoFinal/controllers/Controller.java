package asr.proyectoFinal.controllers;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
import java.io.PrintWriter;
//import java.nio.Buffer;
//import java.nio.file.Files;
//import java.util.List;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import asr.proyectoFinal.models.Word;
import asr.proyectoFinal.models.YahooNew;
import asr.proyectoFinal.services.CloudantService;
import asr.proyectoFinal.services.YahooService;

/**
 * Servlet implementation class Controller
 */
@WebServlet(urlPatterns = {"/listar", "/insertar", "/news"})
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		PrintWriter out = response.getWriter();
		
		CloudantService store = new CloudantService();
		System.out.println(request.getServletPath());
		switch(request.getServletPath())
		{
			case "/listar":
				out.println("<html><head><meta charset=\"UTF-8\"></head><body>");
				if(store.getDB() == null)
					  out.println("No hay DB");
				else
					out.println("Palabras en la BD Cloudant:<br />" + store.getAll());

				out.println("</html>");
				break;
				
			case "/insertar":
				out.println("<html><head><meta charset=\"UTF-8\"></head><body>");
				Word palabra = new Word();
				String parametro = request.getParameter("palabra");

				if(parametro==null)
				{
					out.println("usage: /insertar?palabra=palabra_a_traducir");
				}
				else
				{
					if(store.getDB() == null) 
					{
						out.println(String.format("Palabra: %s", palabra));
					}
					else
					{
						palabra.setName(parametro);
						store.persist(palabra);
					    out.println(String.format("Almacenada la palabra: %s", palabra.getName()));			    	  
					}
				}
				out.println("</html>");
				break;
				
			case "/news":
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");

				String symbol = request.getParameter("symbol");
				
				ArrayList<YahooNew> newsFeed = YahooService.getNewsFeed(symbol);
				
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				out.print(gson.toJson(newsFeed));
				
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
