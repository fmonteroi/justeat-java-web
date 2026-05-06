package es.unex.cume.tw.dto;

public class Gluten {

	private long id;
	private String name;
	private int ratingAverage;
	private String glutenFree;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRatingAverage() {
		return ratingAverage;
	}
	public void setRatingAverage(int ratingAverage) {
		this.ratingAverage = ratingAverage;
	}
	public String getGlutenFree() {
		return glutenFree;
	}
	public void setGlutenFree(String glutenFree) {
		this.glutenFree = glutenFree;
	}
	
	
}
