package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.List;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.model.Dish;
/**
 * Servlet implementation class RemoveDishFromCartServlet
 */
@WebServlet("/RemoveDishFromCartServlet.do")
public class RemoveDishFromCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveDishFromCartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("SearchServlet.do");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get session
		HttpSession session = request.getSession(false);
	    if (session == null || session.getAttribute("user") == null) {
	        response.sendRedirect("LoginServlet.do");
	        return;
	    }

	    // Prepare database access
	    Connection conn = (Connection) getServletContext().getAttribute("dbConn");

	    DishDAO dishDao = new JDBCDishDAOImpl();
	    dishDao.setConnection(conn);

	    // Read and validate dish id
	    long dishId = 0;
	    try {
	        dishId = Long.parseLong(request.getParameter("dishId"));
	    } catch (NumberFormatException e) {
	        response.sendRedirect("SearchServlet.do");
	        return;
	    }

	    Dish dish = dishDao.get(dishId);
	    if (dish == null) {
	        response.sendRedirect("SearchServlet.do");
	        return;
	    }

	    // Load current cart state
	    @SuppressWarnings("unchecked")
	    List<Long> cartDishIds = (List<Long>) session.getAttribute("cartDishIds");

	    Long cartRestaurantId = (Long) session.getAttribute("cartRestaurantId");

	    if (cartDishIds == null || cartRestaurantId == null) {
	        response.sendRedirect("RestaurantDetailServlet.do?id=" + dish.getIdr());
	        return;
	    }

	    // Read removal mode
	    String removeAll = request.getParameter("removeAll");

	    // Remove one or all dish occurrences
	    if ("1".equals(removeAll)) {
	        while (cartDishIds.remove(Long.valueOf(dishId))) {
	            // Remove all occurrences
	        }
	    } else {
	        cartDishIds.remove(Long.valueOf(dishId));
	    }

	    // Update cart state
	    if (cartDishIds.isEmpty()) {
	        session.removeAttribute("cartDishIds");
	        session.removeAttribute("cartRestaurantId");
	    } else {
	        session.setAttribute("cartDishIds", cartDishIds);
	    }

	    // Return to the restaurant detail
	    response.sendRedirect("RestaurantDetailServlet.do?id=" + dish.getIdr());
	}

}
