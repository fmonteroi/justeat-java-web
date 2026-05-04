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
import es.unex.cume.tw.dto.OrderItemRequest;
import es.unex.cume.tw.dto.OrderItemResponse;
import es.unex.cume.tw.dto.OrderRequest;
import es.unex.cume.tw.dto.OrderResponse;
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
	 */
	@POST
	public Response createOrder(OrderRequest orderRequest, @Context HttpServletRequest request) {
		User user = Utils.getLoggedUser(request);

		if (orderRequest == null) {
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
			throw new CustomBadRequestException("El pedido debe contener al menos un plato");
		}

		if (orderRequest.getRestaurantId() < 0) {
			throw new CustomBadRequestException("El restaurante es obligatorio");
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

		Restaurant restaurant = restaurantDao.get(orderRequest.getRestaurantId());

		if (restaurant == null) {
			throw new CustomBadRequestException("Restaurante no encontrado");
		}

		if (restaurant.getAvailable() != 1) {
			throw new WebApplicationException("El restaurante no acepta pedidos", Response.Status.CONFLICT);
		}

		int totalPrice = 0;
		List<Dish> dishes = new ArrayList<Dish>();
		List<Long> usedDishIds = new ArrayList<Long>();
		List<Integer> quantities = new ArrayList<Integer>();

		// quantity per dish
		for (OrderItemRequest item : orderRequest.getItems()) {
			if (item == null) {
				throw new CustomBadRequestException("Hay platos nulos en el pedido");
			}

			if (item.getDishId() < 0) {
				throw new CustomBadRequestException("Hay ids de plato no validos");
			}

			if (item.getQuantity() <= 0) {
				throw new CustomBadRequestException("La cantidad debe ser mayor que 0");
			}

			Dish dish = dishDao.get(item.getDishId());

			if (dish == null) {
				throw new CustomBadRequestException("Plato no encontrado: " + item.getDishId());
			}

			if (usedDishIds.contains(dish.getId())) {
				throw new CustomBadRequestException("No se puede repetir el mismo plato");
			}

			usedDishIds.add(dish.getId());

			// same restaurant check
			if (dish.getIdr() != orderRequest.getRestaurantId()) {
				throw new CustomBadRequestException("Todos los platos deben pertenecer al restaurante indicado");
			}

			dishes.add(dish);
			quantities.add(item.getQuantity());
			totalPrice += dish.getPrice() * item.getQuantity();
		}

		Order order = new Order();
		order.setIdu(user.getId());
		order.setTotalPrice(totalPrice);

		long orderId = orderDao.add(order);

		if (orderId < 0) {
			throw new WebApplicationException("No se pudo crear el pedido", Response.Status.INTERNAL_SERVER_ERROR);
		}

		for (int i = 0; i < dishes.size(); i++) {
			Dish dish = dishes.get(i);
			OrderDishes relation = new OrderDishes();
			relation.setIdo(orderId);
			relation.setIddi(dish.getId());
			relation.setQuantity(quantities.get(i));

			if (!orderDishesDao.add(relation)) {
				throw new WebApplicationException("No se pudo guardar el pedido", Response.Status.INTERNAL_SERVER_ERROR);
			}
		}

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(orderId)).build())
				.entity(buildOrderResponse(orderDao.get(orderId), orderDishesDao, dishDao))
				.build();
	}

	/**
	 * GET /api/pedidos/{id}
	 */
	@GET
	@Path("/{id: [0-9]+}")
	public OrderResponse getOrder(@PathParam("id") long orderId, @Context HttpServletRequest request) {
		User loggedUser = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");
		OrderDAO orderDao = new JDBCOrderDAOImpl();
		orderDao.setConnection(conn);

		OrderDishesDAO orderDishesDao = new JDBCOrderDishesDAOImpl();
		orderDishesDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		Order order = orderDao.get(orderId);

		if (order == null) {
			throw new CustomNotFoundException("Pedido no encontrado");
		}

		if (order.getIdu() != loggedUser.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		return buildOrderResponse(order, orderDishesDao, dishDao);
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
