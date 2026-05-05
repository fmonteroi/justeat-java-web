package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
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
		response.sendRedirect("ListUserRestaurantServlet.do");
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
		
		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);
		
		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);
		
		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);
		
		ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
		reviewsDao.setConnection(conn);

		// Load current user and restaurant
		User user = (User) session.getAttribute("user");
		long rid = Long.parseLong(request.getParameter("id"));

		Restaurant restaurant = restaurantDao.get(rid);

		// Check restaurant ownership
		if (restaurant == null || restaurant.getIdu() != user.getId()) {
		    response.sendRedirect("ListUserRestaurantServlet.do");
		    return;
		}
		
		// Clear cart when it belongs to the deleted restaurant
		Long cartRestaurantId = (Long) session.getAttribute("cartRestaurantId");
		if (cartRestaurantId != null && cartRestaurantId.longValue() == rid) {
			session.removeAttribute("cartRestaurantId");
			session.removeAttribute("cartDishIds");
		}
		
		boolean deleted = true;
		List<Long> orderIds = new ArrayList<Long>();
		List<Dish> dishes = dishDao.getAllByRestaurant(rid);
		
		// Collect related orders
		for (int i = 0; i < dishes.size(); i++) {
			List<OrderDishes> relations = orderDishesDao.getAllByDish(dishes.get(i).getId());
			
			for (int j = 0; j < relations.size(); j++) {
				OrderDishes relation = relations.get(j);
				boolean exists = false;
				
				for (int k = 0; k < orderIds.size(); k++) {
					if (orderIds.get(k).longValue() == relation.getIdo()) {
						exists = true;
						break;
					}
				}
				
				if (!exists) {
					orderIds.add(relation.getIdo());
				}
			}
		}
		
		// Remove related orders
		for (int i = 0; i < orderIds.size(); i++) {
			List<OrderDishes> orderRelations = orderDishesDao.getAllByOrder(orderIds.get(i));
			
			for (int j = 0; j < orderRelations.size(); j++) {
				OrderDishes relation = orderRelations.get(j);
				if (!orderDishesDao.delete(relation.getIdo(), relation.getIddi())) {
					deleted = false;
				}
			}
			
			if (!orderDao.delete(orderIds.get(i))) {
				deleted = false;
			}
		}
		
		// Remove reviews
		List<Review> reviews = reviewsDao.getAllByRestaurant(rid);
		for (int i = 0; i < reviews.size(); i++) {
			Review review = reviews.get(i);
			if (!reviewsDao.delete(review.getIdr(), review.getIdu())) {
				deleted = false;
			}
		}
		
		// Remove category relations
		List<RestaurantCategories> categoryRelations = restaurantCategoriesDao.getAllByRestaurant(rid);
		for (int i = 0; i < categoryRelations.size(); i++) {
			RestaurantCategories relation = categoryRelations.get(i);
			if (!restaurantCategoriesDao.delete(relation.getIdr(), relation.getIdct())) {
				deleted = false;
			}
		}
		
		// Remove dishes
		for (int i = 0; i < dishes.size(); i++) {
			if (!dishDao.delete(dishes.get(i).getId())) {
				deleted = false;
			}
		}
		
		// Delete the restaurant
		if (!restaurantDao.delete(rid)) {
			deleted = false;
		}

		// Return to the restaurant list
		if (deleted) {
			response.sendRedirect("ListUserRestaurantServlet.do");
		} else {
			response.sendRedirect("EditRestaurantServlet.do?id=" + rid);
		}
	}

}
