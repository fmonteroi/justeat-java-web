package es.unex.cume.tw.controller;

import es.unex.cume.tw.dao.CategoryDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCCategoryDAOImpl;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantCategoriesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantCategoriesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Category;
import es.unex.cume.tw.model.Dish;
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

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Servlet implementation class EditRestaurantServlet
 */
@WebServlet(urlPatterns = { "/EditRestaurantServlet.do" })
public class EditRestaurantServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(HttpServlet.class.getName());

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EditRestaurantServlet() {
		super();
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
		try {
			// Prepare database access
			Connection conn = (Connection) getServletContext().getAttribute("dbConn");
			RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
			restaurantDao.setConnection(conn);

			CategoryDAO categoryDao = new JDBCCategoryDAOImpl();
			categoryDao.setConnection(conn);

			RestaurantCategoriesDAO restaurantsCategoriesDAO = new JDBCRestaurantCategoriesDAOImpl();
			restaurantsCategoriesDAO.setConnection(conn);

			DishDAO dishDao = new JDBCDishDAOImpl();
			dishDao.setConnection(conn);

			// Read and validate restaurant id
			logger.info(request.getParameter("id"));

			long rid = Long.parseLong(request.getParameter("id"));
			Restaurant restaurant = restaurantDao.get(rid);

			// Load restaurant dishes and categories
			List<Dish> dishes = dishDao.getAllByRestaurant(rid);

			List<RestaurantCategories> restaurantCategories = restaurantsCategoriesDAO.getAll();

			List<Triplet<Category, Boolean, User>> categoriesChecked = new ArrayList<Triplet<Category, Boolean, User>>();

			List<Category> categories = categoryDao.getAll();
			Iterator<Category> itCategories = categories.iterator();

			while (itCategories.hasNext()) {
				Category category = itCategories.next();

				int len = restaurantCategories.size();
				Boolean exist = false;
				for (int i = 0; i < len; i++) {
					RestaurantCategories restaurantCategory = restaurantCategories.get(i);
					if (restaurant.getId() == restaurantCategory.getIdr()
							&& category.getId() == restaurantCategory.getIdct()) {
						exist = true;
						break;
					}
				}
				categoriesChecked.add(new Triplet<Category, Boolean, User>(category, exist, user));
			}

			// Check restaurant ownership and render form
			if (restaurant != null && restaurant.getIdu() == user.getId()) {
				session.setAttribute("restaurant", restaurant);
				session.setAttribute("categories", categoriesChecked);
				request.setAttribute("dishes", dishes);

				// Show success message after a successful POST-redirect-GET
				if ("1".equals(request.getParameter("saved"))) {
					request.setAttribute("successMessage", "Los cambios se han guardado correctamente.");
				}

				RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_edit.jsp");
				view.forward(request, response);
			} else {
				response.sendRedirect("ListRestaurantServlet.do");
			}

		} catch (NumberFormatException e) {
			logger.info("parameter id is not a number");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession();

		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");
		Restaurant restaurant = (Restaurant) session.getAttribute("restaurant");

		if (restaurant == null) {
			response.sendRedirect("ListRestaurantServlet.do");
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
		String gluten = request.getParameter("gluten");
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
			messages = "No puede haber ningún campo vacío (Salvo las redes sociales y categorías).";
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
			} else if (gluten == null || (!gluten.equals("0") && !gluten.equals("1"))) {
				messages = "El Gluten Free no es válido.";
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

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");
		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		RestaurantCategoriesDAO restaurantsCategoriesDAO = new JDBCRestaurantCategoriesDAOImpl();
		restaurantsCategoriesDAO.setConnection(conn);

		// Render form again on validation errors
		if (messages != null) {
			request.setAttribute("messages", messages);
			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_edit.jsp");
			view.forward(request, response);
			return;
		}

		// Apply form changes
		restaurant.setName(name.trim());
		restaurant.setAddress(address.trim());
		restaurant.setDescription(description.trim());
		restaurant.setTelephone(telephone.trim());
		restaurant.setCity(city.trim());
		restaurant.setContactEmail(contactEmail.trim());
		restaurant.setInstagramUrl(instagramUrl);
		restaurant.setFacebookUrl(facebookUrl);
		restaurant.setxUrl(xUrl);

		// Apply numeric changes
		restaurant.setMinPrice(min);
		restaurant.setMaxPrice(max);
		restaurant.setBikeFriendly(Integer.parseInt(bikeFriendly));
		restaurant.setAvailable(Integer.parseInt(available));
		restaurant.setGluten(Integer.parseInt(gluten));

		List<RestaurantCategories> restaurantCategories = restaurantsCategoriesDAO
				.getAllByRestaurant(restaurant.getId());

		// Clear old category relations
		int len = restaurantCategories.size();
		for (int i = 0; i < len; i++) {
			RestaurantCategories rc = restaurantCategories.get(i);
			restaurantsCategoriesDAO.delete(rc.getIdr(), rc.getIdct());
		}

		// Read selected categories
		String[] categories = request.getParameterValues("restaurant-category");

		// No selection is valid
		if (categories != null) {
			for (int i = 0; i < categories.length; i++) {
				// Skip empty values
				if (categories[i] == null || categories[i].isBlank()) {
					continue;
				}

				try {
					RestaurantCategories rc = new RestaurantCategories();
					rc.setIdct(Long.parseLong(categories[i]));
					rc.setIdr(restaurant.getId());
					restaurantsCategoriesDAO.add(rc);
				} catch (Exception e) {
					logger.info("id not a number");
				}
			}
		}

		// Persist restaurant changes
		if (restaurant.getIdu() == user.getId() && restaurantDao.update(restaurant)) {
			response.sendRedirect("EditRestaurantServlet.do?id=" + restaurant.getId() + "&saved=1");
		} else {
			// Render form again on persistence errors
			request.setAttribute("messages", "Hubo un fallo al guardar los datos del restaurante");
			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/restaurant_edit.jsp");
			view.forward(request, response);
		}
	}

}
