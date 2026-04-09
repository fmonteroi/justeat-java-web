package es.unex.cume.tw.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.model.Dish;
/**
 * Servlet implementation class AddDishToCartServlet
 */
@WebServlet("/AddDishToCartServlet.do")
public class AddDishToCartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddDishToCartServlet() {
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
	    
	    Connection conn = (Connection) getServletContext().getAttribute("dbConn");

	    DishDAO dishDao = new JDBCDishDAOImpl();
	    dishDao.setConnection(conn);

	    long dishId = 0;
	    try {
	        dishId = Long.parseLong(request.getParameter("dishId"));
	    } catch (NumberFormatException e) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }

	    Dish dish = dishDao.get(dishId);
	    if (dish == null) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }
	    
	    Long cartRestaurantId = (Long) session.getAttribute("cartRestaurantId");

	    @SuppressWarnings("unchecked")
	    List<Long> cartDishIds = (List<Long>) session.getAttribute("cartDishIds");

	    if (cartDishIds == null) {
	        cartDishIds = new ArrayList<Long>();
	    }
	    
	    if (cartRestaurantId == null || cartRestaurantId.longValue() != dish.getIdr()) {
	        cartRestaurantId = dish.getIdr();
	        cartDishIds.clear();
	    }

	    if (!cartDishIds.contains(dish.getId())) {
	        cartDishIds.add(dish.getId());
	    }

	    session.setAttribute("cartRestaurantId", cartRestaurantId);
	    session.setAttribute("cartDishIds", cartDishIds);

	    response.sendRedirect("RestaurantDetailServlet.do?id=" + dish.getIdr());
	      
	}

}
