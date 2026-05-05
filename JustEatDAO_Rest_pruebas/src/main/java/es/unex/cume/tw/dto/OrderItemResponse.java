package es.unex.cume.tw.dto;

import es.unex.cume.tw.model.Dish;

public class OrderItemResponse {

	private Dish dish;
	private int quantity;
	private int subtotal;

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(int subtotal) {
		this.subtotal = subtotal;
	}
}
