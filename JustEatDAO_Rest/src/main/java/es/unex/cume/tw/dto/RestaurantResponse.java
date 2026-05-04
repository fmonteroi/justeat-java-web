package es.unex.cume.tw.dto;

import java.util.List;

import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;

public class RestaurantResponse {

	private Restaurant restaurant;
	private List<Category> categories;

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
