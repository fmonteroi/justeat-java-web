package es.unex.cume.tw.controller;

import java.io.IOException;
import java.sql.Connection;

import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/DeleteUserServlet.do")
public class DeleteUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("EditUserServlet.do");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

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

		User sessionUser = (User) session.getAttribute("user");
		User freshUser = userDao.get(sessionUser.getId());

		if (freshUser == null) {
			session.invalidate();
			response.sendRedirect("LoginServlet.do");
			return;
		}

		boolean deleted = true;
		List<Restaurant> restaurants = restaurantDao.getAllByUser(freshUser.getId());
		
		// Remove owned restaurants
		for (int i = 0; i < restaurants.size(); i++) {
			long restaurantId = restaurants.get(i).getId();
			List<Long> orderIds = new ArrayList<Long>();
			List<Dish> dishes = dishDao.getAllByRestaurant(restaurantId);
			
			// Collect related orders
			for (int j = 0; j < dishes.size(); j++) {
				List<OrderDishes> relations = orderDishesDao.getAllByDish(dishes.get(j).getId());
				
				for (int k = 0; k < relations.size(); k++) {
					OrderDishes relation = relations.get(k);
					boolean exists = false;
					
					for (int l = 0; l < orderIds.size(); l++) {
						if (orderIds.get(l).longValue() == relation.getIdo()) {
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
			for (int j = 0; j < orderIds.size(); j++) {
				List<OrderDishes> orderRelations = orderDishesDao.getAllByOrder(orderIds.get(j));
				
				for (int k = 0; k < orderRelations.size(); k++) {
					OrderDishes relation = orderRelations.get(k);
					if (!orderDishesDao.delete(relation.getIdo(), relation.getIddi())) {
						deleted = false;
					}
				}
				
				if (!orderDao.delete(orderIds.get(j))) {
					deleted = false;
				}
			}
			
			// Remove restaurant reviews
			List<Review> restaurantReviews = reviewsDao.getAllByRestaurant(restaurantId);
			for (int j = 0; j < restaurantReviews.size(); j++) {
				Review review = restaurantReviews.get(j);
				if (!reviewsDao.delete(review.getIdr(), review.getIdu())) {
					deleted = false;
				}
			}
			
			// Remove category relations
			List<RestaurantCategories> categoryRelations = restaurantCategoriesDao.getAllByRestaurant(restaurantId);
			for (int j = 0; j < categoryRelations.size(); j++) {
				RestaurantCategories relation = categoryRelations.get(j);
				if (!restaurantCategoriesDao.delete(relation.getIdr(), relation.getIdct())) {
					deleted = false;
				}
			}
			
			// Remove dishes
			for (int j = 0; j < dishes.size(); j++) {
				if (!dishDao.delete(dishes.get(j).getId())) {
					deleted = false;
				}
			}
			
			// Remove restaurant
			if (!restaurantDao.delete(restaurantId)) {
				deleted = false;
			}
		}
		
		// Remove remaining reviews by user
		List<Review> userReviews = reviewsDao.getAllByUser(freshUser.getId());
		for (int i = 0; i < userReviews.size(); i++) {
			Review review = userReviews.get(i);
			if (!reviewsDao.delete(review.getIdr(), review.getIdu())) {
				deleted = false;
			}
		}
		
		// Remove remaining orders by user
		List<Order> orders = orderDao.getAllByUser(freshUser.getId());
		for (int i = 0; i < orders.size(); i++) {
			List<OrderDishes> orderRelations = orderDishesDao.getAllByOrder(orders.get(i).getId());
			
			for (int j = 0; j < orderRelations.size(); j++) {
				OrderDishes relation = orderRelations.get(j);
				if (!orderDishesDao.delete(relation.getIdo(), relation.getIddi())) {
					deleted = false;
				}
			}
			
			if (!orderDao.delete(orders.get(i).getId())) {
				deleted = false;
			}
		}
		
		// Remove user
		if (!userDao.delete(freshUser.getId())) {
			deleted = false;
		}

		if (deleted) {
			session.invalidate();
			response.sendRedirect("LoginServlet.do");
		} else {
			request.setAttribute("messages", "No se pudo eliminar la cuenta.");
			request.setAttribute("profileUser", freshUser);

			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/user_edit.jsp");
			view.forward(request, response);
		}
	}
}
