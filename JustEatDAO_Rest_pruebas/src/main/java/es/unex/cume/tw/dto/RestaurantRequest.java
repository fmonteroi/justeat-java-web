package es.unex.cume.tw.dto;

import java.util.List;

import es.unex.cume.tw.model.Restaurant;

public class RestaurantRequest {

	private Restaurant restaurant;
	private List<Long> categoryIds;

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
	}

	public List<Long> getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(List<Long> categoryIds) {
		this.categoryIds = categoryIds;
	}
}
