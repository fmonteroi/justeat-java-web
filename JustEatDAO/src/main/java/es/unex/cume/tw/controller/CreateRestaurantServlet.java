package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.RestaurantCategories;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.dao.RestaurantDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

/**
 * Servlet implementation class CreateRestaurantServlet
 */
@WebServlet("/CreateRestaurantServlet.do")
public class CreateRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateRestaurantServlet() {
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

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		// Load form data
		List<Category> categories = categoryDao.getAll();
		request.setAttribute("categories", categories);

		// Render form
		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_create.jsp");
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		
		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);
		
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);
		
		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);
		
		// Get session
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}
		
		// Read form fields
		String name = request.getParameter("restaurant-name");
		String address = request.getParameter("restaurant-address");
		String description = request.getParameter("restaurant-description");
		String telephone = request.getParameter("restaurant-tel");
		String city = request.getParameter("restaurant-city");
		String minPrice = request.getParameter("min-price");
		String maxPrice = request.getParameter("max-price");
		String contactEmail = request.getParameter("restaurant-email");
		String bikeFriendly = request.getParameter("bikefriendly");
		String available = request.getParameter("available");
		String[] categories = request.getParameterValues("restaurant-category");
		String instagramUrl = request.getParameter("instagramUrl");
		String facebookUrl = request.getParameter("facebookUrl");
		String xUrl = request.getParameter("xUrl");
		
		String messages = null;

		// Validate required fields
		if (name == null || name.trim().isEmpty()
		        || address == null || address.trim().isEmpty()
		        || description == null || description.trim().isEmpty()
		        || telephone == null || telephone.trim().isEmpty()
		        || city == null || city.trim().isEmpty()
		        || contactEmail == null || contactEmail.trim().isEmpty()
		        || minPrice == null || minPrice.trim().isEmpty()
		        || maxPrice == null || maxPrice.trim().isEmpty()) {
		    messages = "No puede haber ningún campo vacío (Salvo las redes sociales).";
		}
		
		int min = 0;
		int max = 0;

		// Validate price range
		if (messages == null) {
		    try {
		        min = Integer.parseInt(minPrice);
		        max = Integer.parseInt(maxPrice);

		        if (min < 0 || max < min) {
		            messages = "El rango de precio no es válido.";
		        }
		    } catch (NumberFormatException e) {
		        messages = "Los precios deben ser numéricos.";
		    }
		}
		
		// Validate selected categories
		if (messages == null && (categories == null || categories.length == 0)) {
		    messages = "Debes seleccionar al menos una categoría.";
		}
		
		// Normalize optional social links
		if (instagramUrl == null) {
			instagramUrl = "";
		}
		if (facebookUrl == null) {
			facebookUrl = "";
		}
		if (xUrl == null) {
			xUrl = "";
		}
		
		
		// Render form again on validation errors
		if (messages != null) {
		    List<Category> allCategories = categoryDao.getAll();
		    request.setAttribute("categories", allCategories);

		    request.setAttribute("messages", messages);
		    request.setAttribute("restaurantName", name);
		    request.setAttribute("restaurantAddress", address);
		    request.setAttribute("restaurantDescription", description);
		    request.setAttribute("restaurantTel", telephone);
		    request.setAttribute("restaurantCity", city);
		    request.setAttribute("minPrice", minPrice);
		    request.setAttribute("maxPrice", maxPrice);
		    request.setAttribute("restaurantEmail", contactEmail);
		    request.setAttribute("instagramUrl", instagramUrl);
		    request.setAttribute("facebookUrl", facebookUrl);
		    request.setAttribute("xUrl", xUrl);

		    RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_create.jsp");
		    view.forward(request, response);
		    return;
		}
		
		// Build restaurant data
		User user = (User) session.getAttribute("user");

		Restaurant restaurant = new Restaurant();
		restaurant.setName(name.trim());
		restaurant.setAddress(address.trim());
		restaurant.setDescription(description.trim());
		restaurant.setTelephone(telephone.trim());
		restaurant.setCity(city.trim());
		restaurant.setMinPrice(min);
		restaurant.setMaxPrice(max);
		restaurant.setContactEmail(contactEmail.trim());
		restaurant.setGradesAverage(0.0);
		restaurant.setBikeFriendly(Integer.parseInt(bikeFriendly));
		restaurant.setAvailable(Integer.parseInt(available));
		restaurant.setIdu((int) user.getId());
		restaurant.setInstagramUrl(instagramUrl);
		restaurant.setFacebookUrl(facebookUrl);
		restaurant.setxUrl(xUrl);
		
		// Persist restaurant
		long rid = restaurantDao.add(restaurant);
		
		// Save category relations
		for (int i = 0; i < categories.length; i++) {
		    RestaurantCategories rc = new RestaurantCategories();
		    rc.setIdr(rid);
		    rc.setIdct(Long.parseLong(categories[i]));
		    restaurantCategoriesDao.add(rc);
		}

		// Open the new restaurant detail
		response.sendRedirect("RestaurantDetailServlet.do?id=" + rid);
	}

}
