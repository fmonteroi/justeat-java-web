package es.unex.cume.tw.dto;

import java.util.List;

public class OrderRequest {

	private long restaurantId;
	private List<OrderItemRequest> items;

	public long getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(long restaurantId) {
		this.restaurantId = restaurantId;
	}

	public List<OrderItemRequest> getItems() {
		return items;
	}

	public void setItems(List<OrderItemRequest> items) {
		this.items = items;
	}
}
