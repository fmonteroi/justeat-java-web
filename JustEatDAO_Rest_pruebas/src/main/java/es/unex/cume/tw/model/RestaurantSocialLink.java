package es.unex.cume.tw.model;

public class RestaurantSocialLink {

	private long id;
	private long idr;
	private String platform;
	private String url;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdr() {
		return idr;
	}

	public void setIdr(long idr) {
		this.idr = idr;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
