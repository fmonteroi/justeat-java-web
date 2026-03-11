package es.unex.cume.tw.model;

import java.util.List;

public class Restaurant {
	private String nombre;
	private String direccion;
	private String phone;
	private int minprice; // Preguntar si integer
	private int maxprice; // Preguntar si integer
	private String email;
	private List<String> categorias;
	private boolean bikeFriendly;
	private float rating;
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getMinprice() {
		return minprice;
	}
	public void setMinprice(int minprice) {
		this.minprice = minprice;
	}
	public int getMaxprice() {
		return maxprice;
	}
	public void setMaxprice(int maxprice) {
		this.maxprice = maxprice;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public List<String> getCategorias() {
		return categorias;
	}
	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}
	public boolean isBikeFriendly() {
		return bikeFriendly;
	}
	public void setBikeFriendly(boolean bikeFriendly) {
		this.bikeFriendly = bikeFriendly;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}
	
	
	
}
