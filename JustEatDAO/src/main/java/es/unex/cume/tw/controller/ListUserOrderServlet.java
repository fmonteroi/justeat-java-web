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
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");

		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		List<Order> orders = orderDao.getAllByUser(user.getId());

		List<Triplet<Order, Restaurant, List<Dish>>> ordersList = new ArrayList<Triplet<Order, Restaurant, List<Dish>>>();

		for (Order order : orders) {
			List<OrderDishes> orderDishes = orderDishesDao.getAllByOrder(order.getId());
			List<Dish> dishes = new ArrayList<Dish>();
			Restaurant restaurant = null;

			for (OrderDishes relation : orderDishes) {
				Dish dish = dishDao.get(relation.getIddi());
				if (dish != null) {
					dishes.add(dish);

					if (restaurant == null) {
						restaurant = restaurantDao.get(dish.getIdr());
					}
				}
			}

			ordersList.add(new Triplet<Order, Restaurant, List<Dish>>(order, restaurant, dishes));
		}
		
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
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
