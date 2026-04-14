package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet implementation class DeleteRestaurantServlet
 */
@WebServlet("/DeleteRestaurantServlet.do")
public class DeleteRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteRestaurantServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("ListRestaurantServlet.do");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
		    response.sendRedirect("LoginServlet.do");
		    return;
		}
		
		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		// Load current user and restaurant
		User user = (User) session.getAttribute("user");
		long rid = Long.parseLong(request.getParameter("id"));

		Restaurant restaurant = restaurantDao.get(rid);

		// Check restaurant ownership
		if (restaurant == null || restaurant.getIdu() != user.getId()) {
		    response.sendRedirect("ListRestaurantServlet.do");
		    return;
		}
		
		
		// Remove category relations
		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(rid);
		for (RestaurantCategories rc : relations) {
		    restaurantCategoriesDao.delete(rc.getIdr(), rc.getIdct());
		}
		
		// Delete the restaurant
		restaurantDao.delete(rid);
		// Return to the restaurant list
		response.sendRedirect("ListRestaurantServlet.do");
	}

}
