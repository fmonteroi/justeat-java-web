package es.unex.cume.tw.dto;

// Defensa Actividad 3 - Enunciado 1
public class UserRestaurantCountResponse {

	private long userId;
	private String name;
	private String surname;
	private String email;
	private int numberOfRestaurants;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getNumberOfRestaurants() {
		return numberOfRestaurants;
	}

	public void setNumberOfRestaurants(int numberOfRestaurants) {
		this.numberOfRestaurants = numberOfRestaurants;
	}
}
