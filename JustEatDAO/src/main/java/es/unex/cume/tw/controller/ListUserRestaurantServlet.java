package es.unex.cume.tw.controller;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class ListUserRestaurantServlet
 */
@WebServlet("/ListUserRestaurantServlet.do")
public class ListUserRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListUserRestaurantServlet() {
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

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantsCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantsCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		// Load user restaurants
		List<Restaurant> restaurants = restaurantDao.getAllByUser(user.getId());

		Iterator<Restaurant> itRestaurants = restaurants.iterator();

		// Build restaurant view models
		List<Triplet<Restaurant, User, List<RestaurantCategories>>> restaurantsUserList = new ArrayList<Triplet<Restaurant, User, List<RestaurantCategories>>>();

		while (itRestaurants.hasNext()) {
			Restaurant restaurant = itRestaurants.next();
			User owner = userDao.get(restaurant.getIdu());
			List<RestaurantCategories> restaurantsCategories = restaurantsCategoriesDao
					.getAllByRestaurant(restaurant.getId());

			restaurantsUserList.add(new Triplet<Restaurant, User, List<RestaurantCategories>>(restaurant, owner,
					restaurantsCategories));
		}

		// Render restaurant list
		request.setAttribute("restaurantsList", restaurantsUserList);

		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_restaurant_list.jsp");
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
