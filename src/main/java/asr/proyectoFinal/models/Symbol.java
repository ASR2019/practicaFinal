package asr.proyectoFinal.models;

import java.io.IOException;
import java.util.ArrayList;

import asr.proyectoFinal.services.AlphaVantageService;
import asr.proyectoFinal.services.YahooService;

public class Symbol {
	private String _id = null;
	private String _rev = null;
	private String symbolId;
	private ArrayList<Candle> stock;
	private ArrayList<YahooNew> news;

	public Symbol(String symbolId) {
		this.setSymbolId(symbolId);
	}

	public Symbol(String symbolId, ArrayList<Candle> stock, ArrayList<YahooNew> news) {
		this.setSymbolId(symbolId);
		this.setStock(stock);
		this.setNews(news);
	}

	/**
	 * Gets the ID.
	 * 
	 * @return The ID.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Sets the ID
	 * 
	 * @param _id
	 *            The ID to set.
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * Gets the revision of the document.
	 * 
	 * @return The revision of the document.
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * Sets the revision.
	 * 
	 * @param _rev
	 *            The revision to set.
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}

	public String getSymbolId() {
		return this.symbolId;
	}

	public void setSymbolId(String symbolId) {
		this.symbolId = symbolId;
	}

	public ArrayList<YahooNew> getNews() {
		return this.news;
	}

	public void setNews(ArrayList<YahooNew> news) {
		this.news = news;
	}

	public ArrayList<Candle> getStock() {
		return this.stock;
	}

	public void setStock(ArrayList<Candle> stock) {
		this.stock = stock;
	}

	public ArrayList<Candle> updateStock() throws IOException {
		this.stock = AlphaVantageService.getStockData(this.symbolId);
		return this.stock;
	}
	
	public ArrayList<YahooNew> updateNews() throws IOException {
		this.news = YahooService.getNewsFeed(symbolId);
		return this.news;
	}

	// YahooNew aux = new YahooNew();
	// 	news = new ArrayList();
	// 	Iterator it = news.iterator();
	// 	while(it.hasNext())	{
	// 		aux = AlphaVantageService.analisisSentimientoNoticia((YahooNew) it.next());
	// 		news.add(aux);
	// 	}
}
