package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDAOImpl;
import es.unex.cume.tw.dao.JDBCOrderDishesDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.OrderDAO;
import es.unex.cume.tw.dao.OrderDishesDAO;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Dish;
import es.unex.cume.tw.model.Order;
import es.unex.cume.tw.model.OrderDishes;
import es.unex.cume.tw.model.Restaurant;
import es.unex.cume.tw.model.User;
import es.unex.cume.tw.util.Utils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrdersResource {

	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * POST /api/pedidos
	 * 
	 * @param dishIds
	 * @param request
	 * @return
	 */
	@POST
	public Response createOrder(List<Long> dishIds, @Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		// Check order has at least one dish
		if (dishIds == null || dishIds.isEmpty()) {
			throw new WebApplicationException("El pedido debe contener al menos un plato", Response.Status.BAD_REQUEST);
		}

		Connection conn = (Connection) sc.getAttribute("dbConn");

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);

		Integer restaurantId = null;
		int totalPrice = 0;
		List<Dish> dishes = new ArrayList<Dish>();
		List<Long> usedDishIds = new ArrayList<Long>();

		for (Long dishId : dishIds) {
			if (dishId == null) {
				throw new WebApplicationException("Hay ids de plato nulos", Response.Status.BAD_REQUEST);
			}

			Dish dish = dishDao.get(dishId);

			if (dish == null) {
				throw new WebApplicationException("Plato no encontrado: " + dishId, Response.Status.BAD_REQUEST);
			}

			// Check duplicated dish in same order
			if (usedDishIds.contains(dish.getId())) {
				throw new WebApplicationException("No se puede repetir el mismo plato", Response.Status.BAD_REQUEST);
			}

			usedDishIds.add(dish.getId());

			// Check all dishes belong to same restaurant
			if (restaurantId == null) {
				restaurantId = (int) dish.getIdr();
			} else if (restaurantId.intValue() != dish.getIdr()) {
				throw new WebApplicationException("Todos los platos deben ser del mismo restaurante",
						Response.Status.BAD_REQUEST);
			}

			dishes.add(dish);
			totalPrice += dish.getPrice();
		}

		Restaurant restaurant = restaurantDao.get(restaurantId.longValue());

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.BAD_REQUEST);
		}

		// Check restaurant accepts orders
		if (restaurant.getAvailable() != 1) {
			throw new WebApplicationException("El restaurante no acepta pedidos", Response.Status.CONFLICT);
		}

		Order order = new Order();
		order.setIdu(user.getId());
		order.setTotalPrice(totalPrice);

		long orderId = orderDao.add(order);

		if (orderId < 0) {
			throw new WebApplicationException("No se pudo crear el pedido", Response.Status.INTERNAL_SERVER_ERROR);
		}

		// Save order dish relations
		for (Dish dish : dishes) {
			OrderDishes relation = new OrderDishes();
			relation.setIdo(orderId);
			relation.setIddi(dish.getId());

			if (!orderDishesDao.add(relation)) {
				throw new WebApplicationException("No se pudo guardar el pedido", Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(orderId)).build())
				.entity(orderDao.get(orderId))
				.build();
	}

	/**
	 * GET /api/pedidos/{id}
	 * 
	 * @param orderId
	 * @param request
	 * @return
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public Order getOrder(@PathParam("id") long orderId, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		Order order = orderDao.get(orderId);

		if (order == null) {
			throw new WebApplicationException("Pedido no encontrado", Response.Status.NOT_FOUND);
		}

		// Check user reads only his own order
		if (order.getIdu() != loggedUser.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		return order;
	}
}
