package es.unex.cume.tw.resources;

import java.sql.Connection;

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
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/platos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DishesResource {

	@Context
	private ServletContext sc;

	/**
	 * PUT /api/platos/{id}
	 * 
	 * @param dishId
	 * @param dish
	 * @param request
	 * @return
	 */
	@PUT
	@Path("/{id: [0-9]+}")
	public Dish updateDish(@PathParam("id") long dishId, Dish dish, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		// Validate dish input
		validateDish(dish);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Dish currentDish = dishDao.get(dishId);

		if (currentDish == null) {
			throw new WebApplicationException("Plato no encontrado", Response.Status.NOT_FOUND);
		}

		Restaurant restaurant = restaurantDao.get(currentDish.getIdr());

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		// Check restaurant ownership
		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		// Update editable fields
		currentDish.setName(dish.getName().trim());
		currentDish.setDescription(dish.getDescription().trim());
		currentDish.setPrice(dish.getPrice());

		if (!dishDao.update(currentDish)) {
			throw new WebApplicationException("No se pudo actualizar el plato", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return dishDao.get(dishId);
	}

	/**
	 * DELETE /api/platos/{id}
	 * 
	 * @param dishId
	 * @param request
	 * @return
	 */
	@DELETE
	@Path("/{id: [0-9]+}")
	public Response deleteDish(@PathParam("id") long dishId, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Dish dish = dishDao.get(dishId);

		if (dish == null) {
			throw new WebApplicationException("Plato no encontrado", Response.Status.NOT_FOUND);
		}

		Restaurant restaurant = restaurantDao.get(dish.getIdr());

		if (restaurant == null) {
			throw new WebApplicationException("Restaurante no encontrado", Response.Status.NOT_FOUND);
		}

		// Check restaurant ownership
		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

		if (!dishDao.delete(dishId)) {
			throw new WebApplicationException("No se pudo eliminar el plato", Response.Status.INTERNAL_SERVER_ERROR);
		}

		return Response.noContent().build();
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
