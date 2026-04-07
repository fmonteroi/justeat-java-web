package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class RestaurantDetailServlet
 */
@WebServlet("/RestaurantDetailServlet.do")
public class RestaurantDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RestaurantDetailServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		Restaurant restaurant = null;
		long rid = 0;

		try {
		    rid = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e) {
		    response.sendRedirect("ListRestaurantServlet.do");
		    return;
		}

		restaurant = restaurantDao.get(rid);

		if (restaurant == null) {
		    response.sendRedirect("ListRestaurantServlet.do");
		    return;
		}

		User owner = userDao.get(restaurant.getIdu());

		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(rid);
		List<Category> categories = new ArrayList<Category>();

		for (RestaurantCategories rc : relations) {
			Category category = categoryDao.get(rc.getIdct());
			if (category != null) {
				categories.add(category);
			}
		}

		request.setAttribute("restaurant", restaurant);
		request.setAttribute("owner", owner);
		request.setAttribute("categories", categories);
		
		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_detail.jsp");
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
