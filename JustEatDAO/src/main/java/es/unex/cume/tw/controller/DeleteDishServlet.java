package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;

/**
 * Servlet implementation class DeleteDishServlet
 */
@WebServlet("/DeleteDishServlet.do")
public class DeleteDishServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteDishServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("ListRestaurantServlet.do");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("user") == null) {
	        response.sendRedirect("LoginServlet.do");
	        return;
	    }

	    User user = (User) session.getAttribute("user");

	    Connection conn = (Connection) getServletContext().getAttribute("dbConn");

	    DishDAO dishDao = new JDBCDishDAOImpl();
	    dishDao.setConnection(conn);

	    RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
	    restaurantDao.setConnection(conn);
	    
	    long dishId = 0;
	    try {
	        dishId = Long.parseLong(request.getParameter("id"));
	    } catch (NumberFormatException e) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }

	    Dish dish = dishDao.get(dishId);
	    if (dish == null) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }
	    
	    Restaurant restaurant = restaurantDao.get(dish.getIdr());

	    if (restaurant == null || restaurant.getIdu() != user.getId()) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }
	    
	    dishDao.delete(dish.getId());

	    response.sendRedirect("EditRestaurantServlet.do?id=" + restaurant.getId());
	}

}
