package asr.proyectoFinal.services;

import java.util.Date;

public class Candle {
	private Date fecha;
	private float open,high,low,close;
	private int volume;
	public Candle(Date fecha2, float open, float high, float low, float close, int volume) {
		// TODO Auto-generated constructor stub
		this.setFecha(fecha2);
		this.setOpen(open);
		this.setHigh(high);
		this.setLow(low);
		this.setClose(close);
		this.setVolume(volume);
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha2) {
		this.fecha = fecha2;
	}
	public float getClose() {
		return close;
	}
	public void setClose(float close) {
		this.close = close;
	}
	public float getOpen() {
		return open;
	}
	public void setOpen(float open) {
		this.open = open;
	}
	public float getHigh() {
		return high;
	}
	public void setHigh(float high) {
		this.high = high;
	}
	public int getVolume() {
		return volume;
	}
	public void setVolume(int volume) {
		this.volume = volume;
	}
	public float getLow() {
		return low;
	}
	public void setLow(float low) {
		this.low = low;
	}

}
