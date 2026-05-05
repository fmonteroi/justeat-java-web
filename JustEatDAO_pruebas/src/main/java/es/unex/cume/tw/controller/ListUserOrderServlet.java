package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;

/**
 * Servlet implementation class ListUserOrderServlet
 */
@WebServlet("/ListUserOrderServlet.do")
public class ListUserOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListUserOrderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		// Load user orders
		List<Order> orders = orderDao.getAllByUser(user.getId());

		// Build order view models
		List<Triplet<Order, Restaurant, List<Triplet<Dish, Integer, Double>>>> ordersList =
				new ArrayList<Triplet<Order, Restaurant, List<Triplet<Dish, Integer, Double>>>>();

		for (Order order : orders) {
			List<OrderDishes> orderDishes = orderDishesDao.getAllByOrder(order.getId());
			List<Triplet<Dish, Integer, Double>> dishes = new ArrayList<Triplet<Dish, Integer, Double>>();
			Restaurant restaurant = null;

			for (OrderDishes relation : orderDishes) {
				Dish dish = dishDao.get(relation.getIddi());
				if (dish != null) {
					// Map stored quantity
					dishes.add(new Triplet<Dish, Integer, Double>(dish, relation.getQuantity(),
							dish.getPrice() * relation.getQuantity()));

					if (restaurant == null) {
						restaurant = restaurantDao.get(dish.getIdr());
					}
				}
			}

			ordersList.add(
					new Triplet<Order, Restaurant, List<Triplet<Dish, Integer, Double>>>(order, restaurant, dishes));
		}
		
		// Render order history
		request.setAttribute("ordersList", ordersList);

	    RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_order_list.jsp");
	    view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Reuse GET behavior
		doGet(request, response);
	}

}
