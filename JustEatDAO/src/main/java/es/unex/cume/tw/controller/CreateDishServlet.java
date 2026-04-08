package es.unex.cume.tw.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;

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
		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");

		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		long restaurantId = 0;
		try {
			restaurantId = Long.parseLong(request.getParameter("restaurantId"));
		} catch (NumberFormatException e) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null || restaurant.getIdu() != user.getId()) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

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
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("LoginServlet.do");
			return;
		}

		User user = (User) session.getAttribute("user");

		Connection conn = (Connection) getServletContext().getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		String restaurantIdParam = request.getParameter("restaurantId");
		String name = request.getParameter("dish-name");
		String description = request.getParameter("dish-description");
		String priceParam = request.getParameter("dish-price");

		long restaurantId = 0;
		String messages = null;
		int price = 0;

		try {
			restaurantId = Long.parseLong(restaurantIdParam);
		} catch (NumberFormatException e) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null || restaurant.getIdu() != user.getId()) {
			response.sendRedirect("ListRestaurantServlet.do");
			return;
		}

		if (name == null || name.trim().isEmpty() || description == null || description.trim().isEmpty()
				|| priceParam == null || priceParam.trim().isEmpty()) {
			messages = "Todos los campos del plato son obligatorios.";
		}

		if (messages == null) {
			try {
				price = Integer.parseInt(priceParam);
				if (price < 0) {
					messages = "El precio no puede ser negativo.";
				}
			} catch (NumberFormatException e) {
				messages = "El precio debe ser numerico.";
			}
		}
		
		if (messages == null && description != null && description.length() >= 150) {
		    messages = "La descripcion del plato es demasiado larga, debe ser menor a 150 caracteres.";
		}

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

		Dish dish = new Dish();
		dish.setName(name.trim());
		dish.setDescription(description.trim());
		dish.setPrice(price);
		dish.setIdr(restaurant.getId());

		long dishId = dishDao.add(dish);

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

		response.sendRedirect("EditRestaurantServlet.do?id=" + restaurant.getId());
	}

}
