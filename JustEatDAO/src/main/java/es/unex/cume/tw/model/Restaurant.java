package es.unex.cume.tw.model;

public class Restaurant {

	private long id;
	private String name;
	private String address;
	private String description;
	private String telephone;
	private String city;
	private int minPrice;
	private int maxPrice;
	private String contactEmail;
	private double gradesAverage;
	private int bikeFriendly;
	private int available;
	private int gluten;
	private int idu;
	private String instagramUrl;
	private String facebookUrl;
	private String xUrl;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getGluten() {
		return gluten;
	}
	public void setGluten(int gluten) {
		this.gluten = gluten;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(int minPrice) {
		this.minPrice = minPrice;
	}
	public int getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(int maxPrice) {
		this.maxPrice = maxPrice;
	}
	public double getGradesAverage() {
		return gradesAverage;
	}
	public void setGradesAverage(double gradesAverage) {
		this.gradesAverage = gradesAverage;
	}
	public int getBikeFriendly() {
		return bikeFriendly;
	}
	public void setBikeFriendly(int bikeFriendly) {
		this.bikeFriendly = bikeFriendly;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public int getAvailable() {
		return available;
	}
	public void setAvailable(int available) {
		this.available = available;
	}
	public int getIdu() {
		return idu;
	}
	public void setIdu(int idu) {
		this.idu = idu;
	}
	public String getInstagramUrl() {
		return instagramUrl;
	}
	public void setInstagramUrl(String instagramUrl) {
		this.instagramUrl = instagramUrl;
	}
	public String getFacebookUrl() {
		return facebookUrl;
	}
	public void setFacebookUrl(String facebookUrl) {
		this.facebookUrl = facebookUrl;
	}
	public String getxUrl() {
		return xUrl;
	}
	public void setxUrl(String xUrl) {
		this.xUrl = xUrl;
	}
	
}
