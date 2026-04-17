package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@WebServlet("/RestaurantDetailServlet.do")
public class RestaurantDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Session
		HttpSession session = request.getSession(false);

		// Daos
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		UserDAO userDao = new JDBCUserDAOImpl();
		userDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);

		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
		reviewsDao.setConnection(conn);

		// Reads and validates the restaurant id
		long rid = 0;
		try {
			rid = Long.parseLong(request.getParameter("id"));
		} catch (NumberFormatException e) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

		// Loads the restaurant
		Restaurant restaurant = restaurantDao.get(rid);
		if (restaurant == null) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

		// Loads the restaurant owner.
		User owner = userDao.get(restaurant.getIdu());

		// Loads the categories linked to this restaurant
		List<RestaurantCategories> relations = restaurantCategoriesDao.getAllByRestaurant(rid);
		List<Category> categories = new ArrayList<Category>();

		for (RestaurantCategories rc : relations) {
			Category category = categoryDao.get(rc.getIdct());
			if (category != null) {
				categories.add(category);
			}
		}

		// Loads the dishes
		List<Dish> dishes = dishDao.getAllByRestaurant(rid);

		// Loads restaurant reviews and detect the current user's review
		List<Review> reviews = reviewsDao.getAllByRestaurant(rid);
		List<Triplet<Review, User, Boolean>> reviewsList = new ArrayList<Triplet<Review, User, Boolean>>();

		User sessionUser = null;
		Review myReview = null;

		if (session != null && session.getAttribute("user") != null) {
			sessionUser = (User) session.getAttribute("user");
		}

		for (Review review : reviews) {
			User reviewUser = userDao.get(review.getIdu());
			boolean isMine = false;

			if (sessionUser != null && review.getIdu() == sessionUser.getId()) {
				myReview = review;
				isMine = true;
			}

			reviewsList.add(new Triplet<Review, User, Boolean>(review, reviewUser, isMine));
		}

		// Rebuild cart summary
		List<Triplet<Dish, Integer, Double>> cartDishes = new ArrayList<Triplet<Dish, Integer, Double>>();
		double cartTotal = 0;

		if (session != null) {
			Long cartRestaurantId = (Long) session.getAttribute("cartRestaurantId");

			@SuppressWarnings("unchecked")
			List<Long> cartDishIds = (List<Long>) session.getAttribute("cartDishIds");

			if (cartRestaurantId != null && cartDishIds != null && cartRestaurantId.longValue() == rid) {
				for (Long dishId : cartDishIds) {
					Dish cartDish = dishDao.get(dishId);
					if (cartDish != null) {
						cartTotal += cartDish.getPrice();

						// Group repeated dishes
						Triplet<Dish, Integer, Double> existing = null;
						for (Triplet<Dish, Integer, Double> item : cartDishes) {
							if (item.getFirst().getId() == cartDish.getId()) {
								existing = item;
								break;
							}
						}

						if (existing == null) {
							cartDishes.add(new Triplet<Dish, Integer, Double>(cartDish, 1, cartDish.getPrice()));
						} else {
							existing.setSecond(existing.getSecond() + 1);
							existing.setThird(existing.getFirst().getPrice() * existing.getSecond());
						}
					}
				}
			}
		}

		// Gets related restaurants
		List<Triplet<Restaurant, Integer, List<Category>>> relatedRestaurants = getRelatedRestaurants(restaurant,
				categories, restaurantDao, restaurantCategoriesDao, categoryDao);

		// Expose all data required by the JSP.
		request.setAttribute("restaurant", restaurant);
		request.setAttribute("owner", owner);
		request.setAttribute("categories", categories);
		request.setAttribute("dishes", dishes);
		request.setAttribute("cartDishes", cartDishes);
		request.setAttribute("cartTotal", cartTotal);
		request.setAttribute("reviewsList", reviewsList);
		request.setAttribute("myReview", myReview);
		request.setAttribute("reviewsCount", reviews.size());
		request.setAttribute("relatedRestaurants", relatedRestaurants);

		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_detail.jsp");
		view.forward(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Reuse GET behavior
		doGet(request, response);
	}

	private List<Triplet<Restaurant, Integer, List<Category>>> getRelatedRestaurants(Restaurant restaurant,
			List<Category> categories, RestaurantDAO restaurantDao, RestaurantCategoriesDAO restaurantCategoriesDao,
			CategoryDAO categoryDao) {

		// Loads all restaurants
		List<Restaurant> allRestaurants = restaurantDao.getAll();

		// List of triplets to store similar restaurants
		List<Triplet<Restaurant, Integer, List<Category>>> relatedScored = new ArrayList<Triplet<Restaurant, Integer, List<Category>>>();

		for (Restaurant candidate : allRestaurants) {
			// Skip the current restaurant
			if (candidate.getId() == restaurant.getId()) {
				continue;
			}

			int score = 0;

			// Same city gives 5 points
			if (candidate.getCity() != null && restaurant.getCity() != null
					&& candidate.getCity().equalsIgnoreCase(restaurant.getCity())) {
				score = score + 5;
			}

			// Loads the categories of the candidate restaurant.
			List<RestaurantCategories> candidateRelations = restaurantCategoriesDao.getAllByRestaurant(candidate.getId());
			List<Category> candidateCategories = new ArrayList<Category>();

			for (RestaurantCategories rc : candidateRelations) {
				Category category = categoryDao.get(rc.getIdct());
				if (category != null) {
					candidateCategories.add(category);
				}
			}

			// Adds 2 points for each shared category.
			for (Category currentCategory : categories) {
				for (Category candidateCategory : candidateCategories) {
					if (currentCategory.getId() == candidateCategory.getId()) {
						score = score + 2;
					}
				}
			}

			// Adds 3 points if the price is similar or 5 if it's the same
			int minDiff = Math.abs(candidate.getMinPrice() - restaurant.getMinPrice());
			int maxDiff = Math.abs(candidate.getMaxPrice() - restaurant.getMaxPrice());
			int diff = minDiff + maxDiff;
			
			if (diff < 5) {
				if(diff==0) {
					score = score + 5;
				} else {
					score = score + 3;
				}
				
			}

			// Keeps only restaurants with at least 1 point
			if (score > 0) {
				relatedScored.add(new Triplet<Restaurant, Integer, List<Category>>(candidate, score, candidateCategories));
			}
		}

		// Sort candidates by score
		Collections.sort(relatedScored, new Comparator<Triplet<Restaurant, Integer, List<Category>>>() {
			@Override
			public int compare(Triplet<Restaurant, Integer, List<Category>> o1,
					Triplet<Restaurant, Integer, List<Category>> o2) {
				return Integer.compare(o2.getSecond(), o1.getSecond());
			}
		});

		// Return only the top related restaurants
		List<Triplet<Restaurant, Integer, List<Category>>> relatedRestaurants = new ArrayList<Triplet<Restaurant, Integer, List<Category>>>();

		int maxRelatedRestaurants = relatedScored.size();
		if(maxRelatedRestaurants > 4) {
			maxRelatedRestaurants = 4;
		}
		for (int i = 0; i < maxRelatedRestaurants; i++) {
			
			relatedRestaurants.add(relatedScored.get(i));
		}

		return relatedRestaurants;
	}
}
