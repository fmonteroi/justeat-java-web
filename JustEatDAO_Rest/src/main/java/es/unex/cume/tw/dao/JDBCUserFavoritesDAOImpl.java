package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.unex.cume.tw.model.UserFavorite;

public class JDBCUserFavoritesDAOImpl implements UserFavoritesDAO {

	private Connection conn;
	private static final Logger logger = Logger.getLogger(JDBCUserFavoritesDAOImpl.class.getName());

	@Override
	public List<UserFavorite> getAll() {
		if (conn == null) {
			return null;
		}

		List<UserFavorite> favorites = new ArrayList<UserFavorite>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM UserFavorites");

			while (rs.next()) {
				UserFavorite favorite = new UserFavorite();
				favorite.setIdu(rs.getLong("idu"));
				favorite.setIdr(rs.getLong("idr"));
				favorites.add(favorite);
				logger.info("fetching UserFavorite: " + favorite.getIdu() + " " + favorite.getIdr());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favorites;
	}

	@Override
	public List<UserFavorite> getAllByUser(long idu) {
		if (conn == null) {
			return null;
		}

		List<UserFavorite> favorites = new ArrayList<UserFavorite>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM UserFavorites WHERE idu = " + idu);

			while (rs.next()) {
				UserFavorite favorite = new UserFavorite();
				favorite.setIdu(rs.getLong("idu"));
				favorite.setIdr(rs.getLong("idr"));
				favorites.add(favorite);
				logger.info("fetching UserFavorite by user: " + favorite.getIdu() + " " + favorite.getIdr());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favorites;
	}

	@Override
	public List<UserFavorite> getAllByRestaurant(long idr) {
		if (conn == null) {
			return null;
		}

		List<UserFavorite> favorites = new ArrayList<UserFavorite>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM UserFavorites WHERE idr = " + idr);

			while (rs.next()) {
				UserFavorite favorite = new UserFavorite();
				favorite.setIdu(rs.getLong("idu"));
				favorite.setIdr(rs.getLong("idr"));
				favorites.add(favorite);
				logger.info("fetching UserFavorite by restaurant: " + favorite.getIdu() + " " + favorite.getIdr());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favorites;
	}

	@Override
	public UserFavorite get(long idu, long idr) {
		if (conn == null) {
			return null;
		}

		UserFavorite favorite = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM UserFavorites WHERE idu = " + idu + " AND idr = " + idr);

			if (!rs.next()) {
				return null;
			}

			favorite = new UserFavorite();
			favorite.setIdu(rs.getLong("idu"));
			favorite.setIdr(rs.getLong("idr"));
			logger.info("fetching UserFavorite: " + favorite.getIdu() + " " + favorite.getIdr());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return favorite;
	}

	@Override
	public boolean add(UserFavorite favorite) {
		boolean done = false;

		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO UserFavorites (idu, idr) VALUES (" + favorite.getIdu() + ", "
						+ favorite.getIdr() + ")");
				logger.info("creating UserFavorite: " + favorite.getIdu() + " " + favorite.getIdr());
				done = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return done;
	}

	@Override
	public boolean delete(long idu, long idr) {
		boolean done = false;

		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM UserFavorites WHERE idu = " + idu + " AND idr = " + idr);
				logger.info("deleting UserFavorite: " + idu + " " + idr);
				done = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return done;
	}

	@Override
	public void setConnection(Connection conn) {
		this.conn = conn;
	}
}
