package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;

import java.io.IOException;
import java.sql.Connection;

import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;

/**
 * Servlet implementation class CreateDishServlet
 */
@WebServlet("/CreateDishServlet.do")
public class CreateDishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateDishServlet() {
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

		User user = (User) session.getAttribute("user");

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		// Read and validate restaurant id
		long restaurantId = 0;
		try {
			restaurantId = Long.parseLong(request.getParameter("restaurantId"));
		} catch (NumberFormatException e) {
			response.sendRedirect("ListUserRestaurantServlet.do");
			return;
		}

		Restaurant restaurant = restaurantDao.get(restaurantId);

		// Check restaurant ownership
		if (restaurant == null || restaurant.getIdu() != user.getId()) {
			response.sendRedirect("ListUserRestaurantServlet.do");
			return;
		}

		// Prepare form data
		request.setAttribute("restaurant", restaurant);
		request.setAttribute("formAction", "CreateDishServlet.do");
		request.setAttribute("formTitle", "Añadir plato");

		RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/dish_form.jsp");
		view.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");

		// Prepare database access
		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		// Read form fields
		String restaurantIdParam = request.getParameter("restaurantId");
		String name = request.getParameter("dish-name");
		String description = request.getParameter("dish-description");
		String priceParam = request.getParameter("dish-price");

		long restaurantId = 0;
		String messages = null;
		double price = 0;

		// Validate restaurant id
		try {
			restaurantId = Long.parseLong(restaurantIdParam);
		} catch (NumberFormatException e) {
			response.sendRedirect("ListUserRestaurantServlet.do");
			return;
		}

		Restaurant restaurant = restaurantDao.get(restaurantId);

		// Check restaurant ownership
		if (restaurant == null || restaurant.getIdu() != user.getId()) {
			response.sendRedirect("ListUserRestaurantServlet.do");
			return;
		}

		// Validate required fields
		if (name == null || name.trim().isEmpty() || description == null || description.trim().isEmpty()
				|| priceParam == null || priceParam.trim().isEmpty()) {
			messages = "Todos los campos del plato son obligatorios.";
		}

		// Validate price
		if (messages == null) {
			try {
				price = Double.parseDouble(priceParam);
				if (price < 0) {
					messages = "El precio no puede ser negativo.";
				}
			} catch (NumberFormatException e) {
				messages = "El precio debe ser numérico.";
			}
		}
		
		// Validate name length
		if (messages == null && name != null && name.trim().length() > 80) {
			messages = "El nombre del plato es demasiado largo, debe ser menor o igual a 80 caracteres.";
		}
		
		// Validate description length
		if (messages == null && description != null && description.length() >= 150) {
		    messages = "La descripción del plato es demasiado larga, debe ser menor a 150 caracteres.";
		}

		// Render form again on validation errors
		if (messages != null) {
			request.setAttribute("messages", messages);
			request.setAttribute("restaurant", restaurant);
			request.setAttribute("formAction", "CreateDishServlet.do");
			request.setAttribute("formTitle", "Anadir plato");
			request.setAttribute("dishName", name);
			request.setAttribute("dishDescription", description);
			request.setAttribute("dishPrice", priceParam);

			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/dish_form.jsp");
			view.forward(request, response);
			return;
		}

		// Build the dish object
		Dish dish = new Dish();
		dish.setName(name.trim());
		dish.setDescription(description.trim());
		dish.setPrice(price);
		dish.setIdr(restaurant.getId());

		long dishId = dishDao.add(dish);

		// Handle persistence errors
		if (dishId < 0) {
			request.setAttribute("messages", "No se pudo crear el plato.");
			request.setAttribute("restaurant", restaurant);
			request.setAttribute("formAction", "CreateDishServlet.do");
			request.setAttribute("formTitle", "Añadir plato");
			request.setAttribute("dishName", name);
			request.setAttribute("dishDescription", description);
			request.setAttribute("dishPrice", priceParam);

			RequestDispatcher view = request.getRequestDispatcher("/WEB-INF/dish_form.jsp");
			view.forward(request, response);
			return;
		}

		// Return to the restaurant editor
		response.sendRedirect("EditRestaurantServlet.do?id=" + restaurant.getId());
	}

}
