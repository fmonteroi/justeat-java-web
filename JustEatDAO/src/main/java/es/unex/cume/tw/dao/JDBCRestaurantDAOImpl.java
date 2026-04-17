package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.unex.cume.tw.model.Restaurant;

public class JDBCRestaurantDAOImpl implements RestaurantDAO {

	private Connection conn;
	private static final Logger logger = Logger.getLogger(JDBCRestaurantDAOImpl.class.getName());

	@Override
	public Restaurant get(long id) {
		if (conn == null)
			return null;

		Restaurant restaurant = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurant WHERE id =" + id);
			if (!rs.next())
				return null;
			restaurant = new Restaurant();
			restaurant.setId(rs.getInt("id"));
			restaurant.setName(rs.getString("name"));
			restaurant.setAddress(rs.getString("address"));
			restaurant.setDescription(rs.getString("description"));
			restaurant.setTelephone(rs.getString("telephone"));
			restaurant.setCity(rs.getString("city"));
			restaurant.setIdu(rs.getInt("idu"));
			restaurant.setGradesAverage(rs.getDouble("gradesAverage"));
			restaurant.setMinPrice(rs.getInt("minPrice"));
			restaurant.setContactEmail(rs.getString("contactemail"));
			restaurant.setMaxPrice(rs.getInt("maxPrice"));
			restaurant.setBikeFriendly(rs.getInt("bikeFriendly"));
			restaurant.setAvailable(rs.getInt("available"));
			restaurant.setInstagramUrl(rs.getString("instagramUrl"));
			restaurant.setFacebookUrl(rs.getString("facebookUrl"));
			restaurant.setxUrl(rs.getString("xUrl"));

			logger.info("fetching restaurant: " + restaurant.getId());

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return restaurant;
	}

	public List<Restaurant> getAll() {

		if (conn == null)
			return null;

		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		try {
			Statement stmt;
			ResultSet rs;
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM restaurant");
			while (rs.next()) {
				Restaurant restaurant = new Restaurant();
				restaurant = new Restaurant();
				restaurant.setId(rs.getInt("id"));
				restaurant.setName(rs.getString("name"));
				restaurant.setAddress(rs.getString("address"));
				restaurant.setDescription(rs.getString("description"));
				restaurant.setTelephone(rs.getString("telephone"));
				restaurant.setCity(rs.getString("city"));
				restaurant.setIdu(rs.getInt("idu"));
				restaurant.setGradesAverage(rs.getDouble("gradesAverage"));
				restaurant.setMinPrice(rs.getInt("minPrice"));
				restaurant.setContactEmail(rs.getString("contactemail"));
				restaurant.setMaxPrice(rs.getInt("maxPrice"));
				restaurant.setBikeFriendly(rs.getInt("bikeFriendly"));
				restaurant.setAvailable(rs.getInt("available"));
				restaurant.setInstagramUrl(rs.getString("instagramUrl"));
				restaurant.setFacebookUrl(rs.getString("facebookUrl"));
				restaurant.setxUrl(rs.getString("xUrl"));

				restaurants.add(restaurant);
				logger.info("fetching restaurant: " + restaurant.getId());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return restaurants;
	}

	public List<Restaurant> getAllBySearchName(String search) {
		search = search.toUpperCase();
		if (conn == null)
			return null;

		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurant WHERE UPPER(name) LIKE '%" + search + "%'");

			while (rs.next()) {
				Restaurant restaurant = new Restaurant();

				restaurant = new Restaurant();
				restaurant.setId(rs.getInt("id"));
				restaurant.setName(rs.getString("name"));
				restaurant.setAddress(rs.getString("address"));
				restaurant.setDescription(rs.getString("description"));
				restaurant.setTelephone(rs.getString("telephone"));
				restaurant.setCity(rs.getString("city"));
				restaurant.setIdu(rs.getInt("idu"));
				restaurant.setGradesAverage(rs.getDouble("gradesAverage"));
				restaurant.setMinPrice(rs.getInt("minPrice"));
				restaurant.setContactEmail(rs.getString("contactemail"));
				restaurant.setMaxPrice(rs.getInt("maxPrice"));
				restaurant.setBikeFriendly(rs.getInt("bikeFriendly"));
				restaurant.setAvailable(rs.getInt("available"));
				restaurant.setInstagramUrl(rs.getString("instagramUrl"));
				restaurant.setFacebookUrl(rs.getString("facebookUrl"));
				restaurant.setxUrl(rs.getString("xUrl"));

				restaurants.add(restaurant);

				logger.info("fetching restaurant: " + restaurant.getId());

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return restaurants;
	}

	public List<Restaurant> getAllByUser(long idu) {

		if (conn == null)
			return null;

		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurant WHERE idu = " + idu);

			while (rs.next()) {
				Restaurant restaurant = new Restaurant();
				restaurant = new Restaurant();
				restaurant.setId(rs.getInt("id"));
				restaurant.setName(rs.getString("name"));
				restaurant.setAddress(rs.getString("address"));
				restaurant.setDescription(rs.getString("description"));
				restaurant.setTelephone(rs.getString("telephone"));
				restaurant.setCity(rs.getString("city"));
				restaurant.setIdu(rs.getInt("idu"));
				restaurant.setGradesAverage(rs.getDouble("gradesAverage"));
				restaurant.setMinPrice(rs.getInt("minPrice"));
				restaurant.setContactEmail(rs.getString("contactemail"));
				restaurant.setMaxPrice(rs.getInt("maxPrice"));
				restaurant.setBikeFriendly(rs.getInt("bikeFriendly"));
				restaurant.setAvailable(rs.getInt("available"));
				restaurant.setInstagramUrl(rs.getString("instagramUrl"));
				restaurant.setFacebookUrl(rs.getString("facebookUrl"));
				restaurant.setxUrl(rs.getString("xUrl"));
				restaurants.add(restaurant);

				logger.info("fetching restaurants: " + restaurant.getId());
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return restaurants;
	}

	@Override
	public List<Restaurant> search(String search) {
		if (conn == null) {
			return null;
		}

		search = search.toUpperCase();

		ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM restaurant WHERE " + "UPPER(name) LIKE '%" + search
					+ "%' OR " + "UPPER(city) LIKE '%" + search + "%' OR " + "UPPER(description) LIKE '%" + search
					+ "%'");

			while (rs.next()) {
				Restaurant restaurant = new Restaurant();
				restaurant.setId(rs.getInt("id"));
				restaurant.setName(rs.getString("name"));
				restaurant.setAddress(rs.getString("address"));
				restaurant.setDescription(rs.getString("description"));
				restaurant.setTelephone(rs.getString("telephone"));
				restaurant.setCity(rs.getString("city"));
				restaurant.setIdu(rs.getInt("idu"));
				restaurant.setGradesAverage(rs.getDouble("gradesAverage"));
				restaurant.setMinPrice(rs.getInt("minPrice"));
				restaurant.setContactEmail(rs.getString("contactemail"));
				restaurant.setMaxPrice(rs.getInt("maxPrice"));
				restaurant.setBikeFriendly(rs.getInt("bikeFriendly"));
				restaurant.setAvailable(rs.getInt("available"));
				restaurant.setInstagramUrl(rs.getString("instagramUrl"));
				restaurant.setFacebookUrl(rs.getString("facebookUrl"));
				restaurant.setxUrl(rs.getString("xUrl"));

				restaurants.add(restaurant);
				logger.info("fetching restaurant by search: " + restaurant.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return restaurants;
	}

	@Override
	public long add(Restaurant restaurant) {
		long id = -1;
		long lastid = -1;
		if (conn != null) {

			Statement stmt;

			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM sqlite_sequence WHERE name ='restaurant'");
				if (!rs.next())
					return -1;
				lastid = rs.getInt("seq");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				stmt = conn.createStatement();
				stmt.executeUpdate(
						"INSERT INTO restaurant (name,address,description,telephone,idu,gradesAverage,city,minPrice,contactemail,maxPrice,bikeFriendly,available,instagramUrl,facebookUrl,xUrl) VALUES('"
								+ restaurant.getName() + "','" + restaurant.getAddress() + "','" + restaurant.getDescription() + "','"
								+ restaurant.getTelephone() + "'," + restaurant.getIdu() + ","
								+ restaurant.getGradesAverage() + ",'" + restaurant.getCity() + "',"
								+ restaurant.getMinPrice() + ",'" + restaurant.getContactEmail() + "',"
								+ restaurant.getMaxPrice() + "," + restaurant.getBikeFriendly() + ","
								+ restaurant.getAvailable() + ",'" + restaurant.getInstagramUrl() + "','"
								+ restaurant.getFacebookUrl() + "','" + restaurant.getxUrl() + "')");

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM sqlite_sequence WHERE name ='restaurant'");
				if (!rs.next())
					return -1;
				id = rs.getInt("seq");
				if (id <= lastid)
					return -1;

				logger.info("CREATING restaurant(" + id + "): " + restaurant.getName());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return id;
	}

	@Override
	public boolean update(Restaurant restaurant) {
		boolean done = false;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE restaurant SET name='" + restaurant.getName() + "', address='"
						+ restaurant.getAddress() + "', description='" + restaurant.getDescription() + "', telephone='" + restaurant.getTelephone() + "', idu="
						+ restaurant.getIdu() + ", gradesAverage=" + restaurant.getGradesAverage() + ", city='"
						+ restaurant.getCity() + "', minPrice=" + restaurant.getMinPrice() + ", contactemail='"
						+ restaurant.getContactEmail() + "', maxPrice=" + restaurant.getMaxPrice() + ", bikeFriendly="
						+ restaurant.getBikeFriendly() + ", available=" + restaurant.getAvailable() + ", instagramUrl='"
						+ restaurant.getInstagramUrl() + "'" + ", facebookUrl='" + restaurant.getFacebookUrl() + "'"
						+ ", xUrl='" + restaurant.getxUrl() + "'" + " WHERE id = " + restaurant.getId());
				logger.info("updating restaurant: " + restaurant.getId());

				done = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return done;

	}

	@Override
	public boolean delete(long id) {
		boolean done = false;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM restaurant WHERE id =" + id);
				logger.info("deleting restaurant: " + id);
				done = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return done;
	}

	@Override
	public void setConnection(Connection conn) {
		// TODO Auto-generated method stub
		this.conn = conn;
	}

}
