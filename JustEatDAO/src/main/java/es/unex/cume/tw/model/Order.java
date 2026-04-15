package es.unex.cume.tw.model;

public class Order {

	private long id;
	private long idu;
	private double totalPrice;

	public double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	public long getIdu() {
		return idu;
	}

	public void setIdu(long idu) {
		this.idu = idu;
	}

	

}
