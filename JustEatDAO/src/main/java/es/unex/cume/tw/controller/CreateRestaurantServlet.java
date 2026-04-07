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
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);

		List<Category> categories = categoryDao.getAll();
		request.setAttribute("categories", categories);

		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_create.jsp");
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		
		CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
		categoryDao.setConnection(conn);
		
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);
		
		RestaurantCategoriesDAO restaurantCategoriesDao = new JDBCRestaurantCategoriesDAOImpl();
		restaurantCategoriesDao.setConnection(conn);
		
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}
		
		String name = request.getParameter("restaurant-name");
		String address = request.getParameter("restaurant-address");
		String telephone = request.getParameter("restaurant-tel");
		String city = request.getParameter("restaurant-city");
		String minPrice = request.getParameter("min-price");
		String maxPrice = request.getParameter("max-price");
		String contactEmail = request.getParameter("restaurant-email");
		String bikeFriendly = request.getParameter("bikefriendly");
		String available = request.getParameter("available");
		String[] categories = request.getParameterValues("restaurant-category");
		
		String messages = null;

		if (name == null || name.trim().isEmpty()
		        || address == null || address.trim().isEmpty()
		        || telephone == null || telephone.trim().isEmpty()
		        || city == null || city.trim().isEmpty()
		        || contactEmail == null || contactEmail.trim().isEmpty()
		        || minPrice == null || minPrice.trim().isEmpty()
		        || maxPrice == null || maxPrice.trim().isEmpty()) {
		    messages = "No puede haber ningún campo vacío.";
		}
		
		int min = 0;
		int max = 0;

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
		
		if (messages == null && (categories == null || categories.length == 0)) {
		    messages = "Debes seleccionar al menos una categoría.";
		}
		
		
		if (messages != null) {
		    List<Category> allCategories = categoryDao.getAll();
		    request.setAttribute("categories", allCategories);

		    request.setAttribute("messages", messages);
		    request.setAttribute("restaurantName", name);
		    request.setAttribute("restaurantAddress", address);
		    request.setAttribute("restaurantTel", telephone);
		    request.setAttribute("restaurantCity", city);
		    request.setAttribute("minPrice", minPrice);
		    request.setAttribute("maxPrice", maxPrice);
		    request.setAttribute("restaurantEmail", contactEmail);

		    RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_create.jsp");
		    view.forward(request, response);
		    return;
		}
		
		User user = (User) session.getAttribute("user");

		Restaurant restaurant = new Restaurant();
		restaurant.setName(name.trim());
		restaurant.setAddress(address.trim());
		restaurant.setTelephone(telephone.trim());
		restaurant.setCity(city.trim());
		restaurant.setMinPrice(min);
		restaurant.setMaxPrice(max);
		restaurant.setContactEmail(contactEmail.trim());
		restaurant.setGradesAverage(0);
		restaurant.setBikeFriendly(Integer.parseInt(bikeFriendly));
		restaurant.setAvailable(Integer.parseInt(available));
		restaurant.setIdu((int) user.getId());
		
		long rid = restaurantDao.add(restaurant);
		
		for (int i = 0; i < categories.length; i++) {
		    RestaurantCategories rc = new RestaurantCategories();
		    rc.setIdr(rid);
		    rc.setIdct(Long.parseLong(categories[i]));
		    restaurantCategoriesDao.add(rc);
		}

		response.sendRedirect("RestaurantDetailServlet.do?id=" + rid);
	}

}
