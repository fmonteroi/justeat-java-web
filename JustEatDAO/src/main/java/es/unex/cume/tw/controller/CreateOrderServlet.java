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
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.User;

/**
 * Servlet implementation class CreateOrderServlet
 */
@WebServlet("/CreateOrderServlet.do")
public class CreateOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateOrderServlet() {
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

	    OrderDAO orderDao = new JDBCOrderDAOImpl();
	    orderDao.setConnection(conn);

	    OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
	    orderDishesDao.setConnection(conn);

	    User user = (User) session.getAttribute("user");

	    Long cartRestaurantId = (Long) session.getAttribute("cartRestaurantId");

	    @SuppressWarnings("unchecked")
	    List<Long> cartDishIds = (List<Long>) session.getAttribute("cartDishIds");

	    if (cartRestaurantId == null || cartDishIds == null || cartDishIds.isEmpty()) {
	        response.sendRedirect("ListRestaurantServlet.do");
	        return;
	    }
	    
	    List<Dish> dishes = new ArrayList<Dish>();
	    int totalPrice = 0;

	    for (Long dishId : cartDishIds) {
	        Dish dish = dishDao.get(dishId);
	        if (dish != null && dish.getIdr() == cartRestaurantId.longValue()) {
	            dishes.add(dish);
	            totalPrice += dish.getPrice();
	        }
	    }

	    if (dishes.isEmpty()) {
	        response.sendRedirect("RestaurantDetailServlet.do?id=" + cartRestaurantId);
	        return;
	    }
	    
	    Order order = new Order();
	    order.setIdu(user.getId());
	    order.setTotalPrice(totalPrice);

	    long orderId = orderDao.add(order);

	    if (orderId < 0) {
	        response.sendRedirect("RestaurantDetailServlet.do?id=" + cartRestaurantId);
	        return;
	    }
	    
	    for (Dish dish : dishes) {
	        OrderDishes orderDish = new OrderDishes();
	        orderDish.setIdo(orderId);
	        orderDish.setIddi(dish.getId());
	        orderDishesDao.add(orderDish);
	    }
	    
	    session.removeAttribute("cartRestaurantId");
	    session.removeAttribute("cartDishIds");

	    response.sendRedirect("ListUserOrderServlet.do");
	    
	    
	}

}
