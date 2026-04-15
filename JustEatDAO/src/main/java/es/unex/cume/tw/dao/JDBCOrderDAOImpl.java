package es.unex.cume.tw.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import es.unex.cume.tw.model.Order;

public class JDBCOrderDAOImpl implements OrderDAO {

	private Connection conn;
	private static final Logger logger = Logger.getLogger(JDBCOrderDAOImpl.class.getName());

	@Override
	public Order get(long id) {
		if (conn == null)
			return null;

		Order order = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM orders WHERE id =" + id);
			if (!rs.next())
				return null;
			order = new Order();
			order.setId(rs.getInt("id"));
			order.setIdu(rs.getInt("idu"));
			order.setTotalPrice(rs.getDouble("totalPrice"));

			logger.info("fetching Order by id: " + id + " -> " + order.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}

	@Override
	public Order get(long idu, double totalPrice) {
		if (conn == null)
			return null;

		Order order = null;

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt
					.executeQuery("SELECT * FROM orders WHERE idu =" + idu + " AND totalPrice =" + totalPrice);
			if (!rs.next())
				return null;
			order = new Order();
			order.setId(rs.getInt("id"));
			order.setIdu(rs.getInt("idu"));
			order.setTotalPrice(rs.getDouble("totalPrice"));

			logger.info("fetching Order id: " + order.getId());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return order;
	}

	@Override
	public List<Order> getAllByUser(long idu) {
		if (conn == null)
			return null;

		ArrayList<Order> orders = new ArrayList<Order>();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Orders WHERE idu = " + idu + " ORDER BY id DESC");

			while (rs.next()) {
				Order order = new Order();
				order.setId(rs.getInt("id"));
				order.setIdu(rs.getInt("idu"));
				order.setTotalPrice(rs.getDouble("totalPrice"));
				orders.add(order);

				logger.info("fetching Orders by user: " + order.getId());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orders;
	}

	public List<Order> getAll() {

		if (conn == null)
			return null;

		ArrayList<Order> orderes = new ArrayList<Order>();
		try {
			Statement stmt;
			ResultSet rs;
			stmt = conn.createStatement();
			rs = stmt.executeQuery("SELECT * FROM Orders");
			while (rs.next()) {
				Order order = new Order();
				order.setId(rs.getInt("id"));
				order.setIdu(rs.getInt("idu"));
				order.setTotalPrice(rs.getDouble("totalPrice"));
				orderes.add(order);
				logger.info("fetching Orders: " + order.getId());

			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return orderes;
	}

	@Override
	public long add(Order order) {
		long id = -1;
		long lastid = -1;
		if (conn != null) {

			Statement stmt;

			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM sqlite_sequence WHERE name ='orders'");
				if (!rs.next())
					return -1;
				lastid = rs.getInt("seq");

			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO Orders (idu,totalPrice) VALUES(" + order.getIdu() + ", "
						+ order.getTotalPrice() + ")");

			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM sqlite_sequence WHERE name ='orders'");
				if (!rs.next())
					return -1;
				id = rs.getInt("seq");
				if (id <= lastid)
					return -1;

				logger.info("CREATING Order(" + id + "): ");
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return id;
	}

	@Override
	public boolean update(Order order) {
		boolean done = false;
		if (conn != null) {

			Statement stmt;
			try {
				stmt = conn.createStatement();

				stmt.executeUpdate("UPDATE Orders SET id = " + order.getId() + ", idu= " + order.getIdu()
						+ ", totalPrice= " + order.getTotalPrice() + " WHERE id = " + order.getId());

				logger.info("updating Order: " + order.getId());

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

			Statement stmt;
			try {
				stmt = conn.createStatement();
				stmt.executeUpdate("DELETE FROM Orders WHERE id = " + id);

				logger.info("deleting Order: " + id);

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
