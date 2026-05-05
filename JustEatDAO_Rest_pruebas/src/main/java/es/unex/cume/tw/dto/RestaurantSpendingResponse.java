package es.unex.cume.tw.dto;

// Defensa Actividad 3 - Enunciado 3
public class RestaurantSpendingResponse {

	private String name;
	private int averageSpending;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAverageSpending() {
		return averageSpending;
	}

	public void setAverageSpending(int averageSpending) {
		this.averageSpending = averageSpending;
	}
}
