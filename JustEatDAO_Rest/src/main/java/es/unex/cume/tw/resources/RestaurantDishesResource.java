package es.unex.cume.tw.resources;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import es.unex.cume.tw.dao.DishDAO;
import es.unex.cume.tw.dao.JDBCDishDAOImpl;
import es.unex.cume.tw.dao.JDBCRestaurantDAOImpl;
import es.unex.cume.tw.dao.RestaurantDAO;
import es.unex.cume.tw.model.Dish;
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

@Path("/restaurantes/{id: [0-9]+}/platos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RestaurantDishesResource {

	@Context
	private ServletContext sc;

	@Context
	private UriInfo uriInfo;

	/**
	 * GET /api/restaurantes/{id}/platos
	 * 
	 * @param restaurantId
	 * @param request
	 * @return
	 */
	@GET
	public List<Dish> getRestaurantDishes(@PathParam("id") long restaurantId, @Context HttpServletRequest request) {

		Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		List<Dish> result = new ArrayList<Dish>();

		// Filter dishes by restaurant
		for (Dish dish : dishDao.getAll()) {
			if (dish.getIdr() == restaurantId) {
				result.add(dish);
			}
		}

		return result;
	}

	/**
	 * POST /api/restaurantes/{id}/platos
	 * 
	 * @param restaurantId
	 * @param dish
	 * @param request
	 * @return
	 */
	@POST
	public Response createDish(@PathParam("id") long restaurantId, Dish dish, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		// Validate dish input
		validateDish(dish);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		Restaurant restaurant = restaurantDao.get(restaurantId);

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		// Check restaurant ownership
		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		// Force server-controlled fields
		dish.setId(0);
		dish.setIdr(restaurantId);

		long id = dishDao.add(dish);

		if (id < 0) {
			throw new WebApplicationException("No se pudo crear el plato", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(id)).build())
				.entity(dishDao.get(id)).build();
	}

	private void validateDish(Dish dish) {
		if (dish == null) {
			throw new WebApplicationException("El cuerpo es obligatorio", Response.Status.BAD_REQUEST);
		}

		// Check required dish fields
		if (dish.getName() == null || dish.getName().trim().isEmpty() || dish.getDescription() == null
				|| dish.getDescription().trim().isEmpty()) {
			throw new WebApplicationException("Nombre y descripcion son obligatorios", Response.Status.BAD_REQUEST);
		}

		// Check valid price
		if (dish.getPrice() < 0) {
			throw new WebApplicationException("El precio no puede ser negativo", Response.Status.BAD_REQUEST);
		}
	}
}
