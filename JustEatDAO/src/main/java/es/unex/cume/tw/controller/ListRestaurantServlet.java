package es.unex.cume.tw.controller;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.JDBCUserDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.UserDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.ReviewsDAO;
import es.unex.cume.tw.dao.JDBCReviewsDAOImpl;
import es.unex.cume.tw.model.Review;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Triplet;
import java.sql.Connection;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.RequestDispatcher;

/**
 * Servlet implementation class ListRestaurantServlet
 */
@WebServlet("/ListRestaurantServlet.do")
public class ListRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(HttpServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ListRestaurantServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Trace request execution
		logger.info("Atendiendo GET");

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		UserDAO userDAO = new JDBCUserDAOImpl();
		userDAO.setConnection(conn);

		RestaurantDAO RestaurantDAO = new JDBCRestaurantDAOImpl();
		RestaurantDAO.setConnection(conn);

		CategoryDAO categoryDAO = new JDBCCategoryDAOImpl();
		categoryDAO.setConnection(conn);

		RestaurantCategoriesDAO restaurantsCategoriesDAO = new JDBCRestaurantCategoriesDAOImpl();
		restaurantsCategoriesDAO.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		ReviewsDAO reviewsDao = new JDBCReviewsDAOImpl();
		reviewsDao.setConnection(conn);

		// Read filter parameters
		String search = request.getParameter("search");
		String sort = request.getParameter("sort");
		String availability = request.getParameter("availability");
		String category = request.getParameter("category");
		String city = request.getParameter("city");
		String dish = request.getParameter("dish");
		String comments = request.getParameter("comments");
		String minPrice = request.getParameter("minPrice");
		String maxPrice = request.getParameter("maxPrice");

		if (search != null) {
			search = search.trim();
		}

		if (search == null || search.isEmpty()) {
			response.sendRedirect("SearchServlet.do");
			return;
		}

		// Normalize optional filters
		if (city != null) {
			city = city.trim();
		}
		if (dish != null) {
			dish = dish.trim();
		}
		if (comments != null) {
			comments = comments.trim();
		}
		if (minPrice != null) {
			minPrice = minPrice.trim();
		}
		if (maxPrice != null) {
			maxPrice = maxPrice.trim();
		}

		// Run base search
		List<Restaurant> restaurantsList = RestaurantDAO.search(search);

		// Category filter
		// Filter by category
		if (category != null && !category.isEmpty()) {
			try {
				long categoryId = Long.parseLong(category);
				List<Restaurant> filtered = new ArrayList<Restaurant>();

				for (Restaurant restaurant : restaurantsList) {
					List<RestaurantCategories> relations = restaurantsCategoriesDAO
							.getAllByRestaurant(restaurant.getId());
					for (RestaurantCategories rc : relations) {
						if (rc.getIdct() == categoryId) {
							filtered.add(restaurant);
							break;
						}
					}
				}
				restaurantsList = filtered;
			} catch (NumberFormatException e) {
				logger.info("category parameter is not a number");
			}
		}

		// City filter
		// Filter by city
		if (city != null && !city.isEmpty()) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();

			for (Restaurant restaurant : restaurantsList) {
				if (restaurant.getCity() != null && restaurant.getCity().toLowerCase().contains(city.toLowerCase())) {
					filtered.add(restaurant);
				}
			}

			restaurantsList = filtered;
		}

		// Price ranges filter
		// Filter by price range
		if ((minPrice != null && !minPrice.isEmpty()) || (maxPrice != null && !maxPrice.isEmpty())) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();

			int minValue = -1;
			int maxValue = -1;

			try {
				if (minPrice != null && !minPrice.isEmpty()) {
					minValue = Integer.parseInt(minPrice);
				}
				if (maxPrice != null && !maxPrice.isEmpty()) {
					maxValue = Integer.parseInt(maxPrice);
				}
			} catch (NumberFormatException e) {
				minValue = -1;
				maxValue = -1;
			}

			for (Restaurant restaurant : restaurantsList) {
				boolean matches = true;

				if (minValue >= 0 && restaurant.getMinPrice() < minValue) {
					matches = false;
				}

				if (maxValue >= 0 && restaurant.getMaxPrice() > maxValue) {
					matches = false;
				}

				if (matches) {
					filtered.add(restaurant);
				}
			}

			restaurantsList = filtered;
		}

		// Dish filter
		// Filter by dish text
		if (dish != null && !dish.isEmpty()) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();

			for (Restaurant restaurant : restaurantsList) {
				List<Dish> dishes = dishDao.getAllByRestaurant(restaurant.getId());
				boolean matches = false;

				for (Dish currentDish : dishes) {
					if ((currentDish.getName() != null
							&& currentDish.getName().toLowerCase().contains(dish.toLowerCase()))
							|| (currentDish.getDescription() != null
									&& currentDish.getDescription().toLowerCase().contains(dish.toLowerCase()))) {
						matches = true;
						break;
					}
				}

				if (matches) {
					filtered.add(restaurant);
				}
			}

			restaurantsList = filtered;
		}

		// Reviews filter
		// Filter by review text
		if (comments != null && !comments.isEmpty()) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();

			for (Restaurant restaurant : restaurantsList) {
				List<Review> reviews = reviewsDao.getAllByRestaurant(restaurant.getId());
				boolean matches = false;

				for (Review review : reviews) {
					if (review.getReview() != null
							&& review.getReview().toLowerCase().contains(comments.toLowerCase())) {
						matches = true;
						break;
					}
				}

				if (matches) {
					filtered.add(restaurant);
				}
			}

			restaurantsList = filtered;
		}

		// Filter by availability
		if ("open".equals(availability)) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();
			for (Restaurant restaurant : restaurantsList) {
				if (restaurant.getAvailable() == 1) {
					filtered.add(restaurant);
				}
			}
			restaurantsList = filtered;
		} else if ("closed".equals(availability)) {
			List<Restaurant> filtered = new ArrayList<Restaurant>();
			for (Restaurant restaurant : restaurantsList) {
				if (restaurant.getAvailable() == 0) {
					filtered.add(restaurant);
				}
			}
			restaurantsList = filtered;
		}

		Iterator<Restaurant> itRestaurantList = restaurantsList.iterator();

		// Build view models for each restaurant
		List<Triplet<Restaurant, User, List<Category>>> restaurantsUserList = new ArrayList<Triplet<Restaurant, User, List<Category>>>();

		while (itRestaurantList.hasNext()) {
			Restaurant restaurant = (Restaurant) itRestaurantList.next();
			User user = userDAO.get(restaurant.getIdu());
			List<RestaurantCategories> relations = restaurantsCategoriesDAO
					.getAllByRestaurant(restaurant.getId());
			List<Category> restaurantsCategories = new ArrayList<Category>();

			for (RestaurantCategories rc : relations) {
				Category categoryObject = categoryDAO.get(rc.getIdct());
				if (categoryObject != null) {
					restaurantsCategories.add(categoryObject);
				}
			}

			logger.info("User " + user.getName());

			restaurantsUserList
					.add(new Triplet<Restaurant, User, List<Category>>(restaurant, user, restaurantsCategories));
		}

		if ("rating".equals(sort)) {
			// Sort by average rating
			Collections.sort(restaurantsUserList,
					new Comparator<Triplet<Restaurant, User, List<Category>>>() {
						@Override
						public int compare(Triplet<Restaurant, User, List<Category>> o1,
								Triplet<Restaurant, User, List<Category>> o2) {
							return Double.compare(o2.getFirst().getGradesAverage(), o1.getFirst().getGradesAverage());
						}
					});
		}

		List<User> listUser = new ArrayList<User>();
		listUser = userDAO.getAll();
		Iterator<User> itUser = listUser.iterator();
		Map<User, List<Restaurant>> userRestaurantsMap = new HashMap<User, List<Restaurant>>();

		while (itUser.hasNext()) {
			User user = itUser.next();
			restaurantsList = RestaurantDAO.getAllByUser(user.getId());
			userRestaurantsMap.put(user, restaurantsList);
		}

		// Expose view data
		request.setAttribute("restaurantsList", restaurantsUserList);
		request.setAttribute("allCategories", categoryDAO.getAll());
		request.setAttribute("usersMap", userRestaurantsMap);
		request.setAttribute("searchValue", search);
		request.setAttribute("sortValue", sort);
		request.setAttribute("availabilityValue", availability);
		request.setAttribute("categoryValue", category);
		request.setAttribute("cityValue", city);
		request.setAttribute("dishValue", dish);
		request.setAttribute("commentsValue", comments);
		request.setAttribute("minPriceValue", minPrice);
		request.setAttribute("maxPriceValue", maxPrice);

		// Render restaurant list
		RequestDispatcher view = request.getRequestDispatcher("WEB-INF/restaurant_list.jsp");
		view.forward(request, response);

	}

}
