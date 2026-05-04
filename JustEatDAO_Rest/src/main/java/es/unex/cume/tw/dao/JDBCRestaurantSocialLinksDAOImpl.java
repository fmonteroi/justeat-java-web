package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.unex.cume.tw.model.RestaurantSocialLink;

public class JDBCRestaurantSocialLinksDAOImpl implements RestaurantSocialLinksDAO {

	private Connection conn;
	private static final Logger logger = Logger.getLogger(JDBCRestaurantSocialLinksDAOImpl.class.getName());

	@Override
	public List<RestaurantSocialLink> getAllByRestaurant(long idr) {
		if (conn == null) {
			return null;
		}

		List<RestaurantSocialLink> socialLinks = new ArrayList<RestaurantSocialLink>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurantSocialLinks WHERE idr = " + idr);

			while (rs.next()) {
				RestaurantSocialLink socialLink = buildSocialLink(rs);
				socialLinks.add(socialLink);
				logger.info("fetching RestaurantSocialLink by restaurant: " + socialLink.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return socialLinks;
	}

	@Override
	public RestaurantSocialLink get(long id) {
		if (conn == null) {
			return null;
		}

		RestaurantSocialLink socialLink = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurantSocialLinks WHERE id = " + id);

			if (!rs.next()) {
				return null;
			}

			socialLink = buildSocialLink(rs);
			logger.info("fetching RestaurantSocialLink: " + socialLink.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return socialLink;
	}

	@Override
	public RestaurantSocialLink get(long idr, String platform) {
		if (conn == null) {
			return null;
		}

		RestaurantSocialLink socialLink = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurantSocialLinks WHERE idr = " + idr
					+ " AND platform = '" + platform + "'");

			if (!rs.next()) {
				return null;
			}

			socialLink = buildSocialLink(rs);
			logger.info("fetching RestaurantSocialLink by platform: " + socialLink.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return socialLink;
	}

	@Override
	public long add(RestaurantSocialLink socialLink) {
		long id = -1;

		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO restaurantSocialLinks (idr, platform, url) VALUES ("
						+ socialLink.getIdr() + ", '" + socialLink.getPlatform() + "', '" + socialLink.getUrl() + "')");

				ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() AS id");
				if (rs.next()) {
					id = rs.getLong("id");
				}

				logger.info("creating RestaurantSocialLink: " + id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return id;
	}

	@Override
	public boolean update(RestaurantSocialLink socialLink) {
		boolean done = false;

		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE restaurantSocialLinks SET platform = '" + socialLink.getPlatform()
						+ "', url = '" + socialLink.getUrl() + "' WHERE id = " + socialLink.getId());
				logger.info("updating RestaurantSocialLink: " + socialLink.getId());
				done = true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return done;
	}

	@Override
	public boolean delete(long id) {
		boolean done = false;

		if (conn != null) {
			try {
				Statement stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM restaurantSocialLinks WHERE id = " + id);
				logger.info("deleting RestaurantSocialLink: " + id);
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

	private RestaurantSocialLink buildSocialLink(ResultSet rs) throws SQLException {
		RestaurantSocialLink socialLink = new RestaurantSocialLink();
		socialLink.setId(rs.getLong("id"));
		socialLink.setIdr(rs.getLong("idr"));
		socialLink.setPlatform(rs.getString("platform"));
		socialLink.setUrl(rs.getString("url"));
		return socialLink;
	}
}
