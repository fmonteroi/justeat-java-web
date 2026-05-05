package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.util.List;

import es.unex.cume.tw.model.UserFavorite;

public interface UserFavoritesDAO {

	public void setConnection(Connection conn);

	public List<UserFavorite> getAll();

	public List<UserFavorite> getAllByUser(long idu);

	public List<UserFavorite> getAllByRestaurant(long idr);

	public UserFavorite get(long idu, long idr);

	public boolean add(UserFavorite favorite);

	public boolean delete(long idu, long idr);
}
