package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.dto.OrderItemResponse;
import es.unex.cume.tw.dto.OrderResponse;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/usuarios/{id: [0-9]+}/pedidos")
@Produces(MediaType.APPLICATION_JSON)
public class UserOrdersResource {

	@Context
	private ServletContext sc;

	/**
	 * GET /api/usuarios/{id}/pedidos
	 */
	@GET
	public List<OrderResponse> getUserOrders(@PathParam("id") long userId, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		if (loggedUser.getId() != userId) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");
		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		List<OrderResponse> result = new ArrayList<OrderResponse>();

		for (Order order : orderDao.getAll()) {
			if (order.getIdu() == userId) {
				result.add(buildOrderResponse(order, orderDishesDao, dishDao));
			}
		}

		return result;
	}

	private OrderResponse buildOrderResponse(Order order, OrderDishesDAO orderDishesDao, DishDAO dishDao) {
		OrderResponse response = new OrderResponse();
		response.setOrder(order);

		List<OrderItemResponse> items = new ArrayList<OrderItemResponse>();
		List<OrderDishes> relations = orderDishesDao.getAllByOrder(order.getId());

		if (relations != null) {
			for (OrderDishes relation : relations) {
				Dish dish = dishDao.get(relation.getIddi());

				if (dish != null) {
					OrderItemResponse item = new OrderItemResponse();
					item.setDish(dish);
					item.setQuantity(relation.getQuantity());
					// subtotal for the response
					item.setSubtotal(dish.getPrice() * relation.getQuantity());
					items.add(item);
				}
			}
		}

		response.setItems(items);

		return response;
	}
}
