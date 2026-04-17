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
		String emailPattern = "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$";
		String urlPattern = "^https?://.+$";

		// Validate required fields
		if (name == null || name.trim().isEmpty() || address == null || address.trim().isEmpty() || description == null
				|| description.trim().isEmpty() || telephone == null || telephone.trim().isEmpty() || city == null
				|| city.trim().isEmpty() || contactEmail == null || contactEmail.trim().isEmpty() || minPrice == null
				|| minPrice.trim().isEmpty() || maxPrice == null || maxPrice.trim().isEmpty()) {
			messages = "No puede haber ningún campo vacío (Salvo las redes sociales).";
		}

		// Validate text length
		if (messages == null) {
			if (name.trim().length() > 80) {
				messages = "El nombre del restaurante es demasiado largo, debe ser menor o igual a 80 caracteres.";
			} else if (address.trim().length() > 150) {
				messages = "La dirección del restaurante es demasiado larga, debe ser menor o igual a 150 caracteres.";
			} else if (description.trim().length() > 300) {
				messages = "La descripción del restaurante es demasiado larga, debe ser menor o igual a 300 caracteres.";
			} else if (city.trim().length() > 80) {
				messages = "La ciudad es demasiado larga, debe ser menor o igual a 80 caracteres.";
			} else if (contactEmail.trim().length() > 120) {
				messages = "El correo de contacto es demasiado largo, debe ser menor o igual a 120 caracteres.";
			}
		}

		// Validate contact data
		if (messages == null) {
			if (!telephone.trim().matches("^\\d{9}$")) {
				messages = "El teléfono debe tener 9 digitos numéricos.";
			} else if (!contactEmail.trim().matches(emailPattern)) {
				messages = "El correo de contacto no es válido.";
			}
		}

		// Validate radio values
		if (messages == null) {
			if (bikeFriendly == null || (!bikeFriendly.equals("0") && !bikeFriendly.equals("1"))) {
				messages = "El valor de bike friendly no es válido.";
			} else if (available == null || (!available.equals("0") && !available.equals("1"))) {
				messages = "El estado del restaurante no es válido.";
			}
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

		instagramUrl = instagramUrl.trim();
		facebookUrl = facebookUrl.trim();
		xUrl = xUrl.trim();

		// Validate social links
		if (messages == null) {
			if (instagramUrl.length() > 200 || facebookUrl.length() > 200 || xUrl.length() > 200) {
				messages = "Las redes sociales son demasiado largas, deben ser menores o iguales a 200 caracteres.";
			} else if (!instagramUrl.isEmpty() && !instagramUrl.matches(urlPattern)) {
				messages = "La URL de Instagram no es válida.";
			} else if (!facebookUrl.isEmpty() && !facebookUrl.matches(urlPattern)) {
				messages = "La URL de Facebook no es válida.";
			} else if (!xUrl.isEmpty() && !xUrl.matches(urlPattern)) {
				messages = "La URL de X / Twitter no es válida.";
			}
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

		// Read selected categories
		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				// Skip empty values
				if (categories[i] == null || categories[i].isBlank()) {
					continue;
				}

				RestaurantCategories rc = new RestaurantCategories();
				rc.setIdr(rid);
				rc.setIdct(Long.parseLong(categories[i]));
				restaurantCategoriesDao.add(rc);
			}
		}

		// Open the new restaurant detail
		response.sendRedirect("ListUserRestaurantServlet.do");
	}

}
