package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.model.RestaurantSocialLink;

public interface RestaurantSocialLinksDAO {

	public void setConnection(Connection conn);

	public List<RestaurantSocialLink> getAllByRestaurant(long idr);

	public RestaurantSocialLink get(long id);

	public RestaurantSocialLink get(long idr, String platform);

	public long add(RestaurantSocialLink socialLink);

	public boolean update(RestaurantSocialLink socialLink);

	public boolean delete(long id);
}
