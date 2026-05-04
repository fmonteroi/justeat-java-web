package es.unex.cume.tw.dto;

import java.util.List;

import es.unex.cume.tw.model.Order;

public class OrderResponse {

	private Order order;
	private List<OrderItemResponse> items;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public List<OrderItemResponse> getItems() {
		return items;
	}

	public void setItems(List<OrderItemResponse> items) {
		this.items = items;
	}
}
