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
	 */
	@PUT
	@Path("/{id: [0-9]+}")
	public Dish updateDish(@PathParam("id") long dishId, Dish dish, @Context HttpServletRequest request) {

		User user = Utils.getLoggedUser(request);

		validateDish(dish);

		Connection conn = (Connection) sc.getAttribute("dbConn");

		DishDAO dishDao = new JDBCDishDAOImpl();
		dishDao.setConnection(conn);

		RestaurantDAO restaurantDao = new JDBCRestaurantDAOImpl();
		restaurantDao.setConnection(conn);

		Dish currentDish = dishDao.get(dishId);

		if (currentDish == null) {
			throw new CustomNotFoundException("Plato no encontrado");
		}

		Restaurant restaurant = restaurantDao.get(currentDish.getIdr());

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

		if (restaurant.getIdu() != user.getId()) {
			throw new WebApplicationException("No autorizado", Response.Status.FORBIDDEN);
		}

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
			throw new CustomNotFoundException("Plato no encontrado");
		}

		Restaurant restaurant = restaurantDao.get(dish.getIdr());

		if (restaurant == null) {
			throw new CustomNotFoundException("Restaurante no encontrado");
		}

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
			throw new CustomBadRequestException("El cuerpo es obligatorio");
		}

		if (dish.getName() == null || dish.getName().trim().isEmpty() || dish.getDescription() == null
				|| dish.getDescription().trim().isEmpty()) {
			throw new CustomBadRequestException("Nombre y descripcion son obligatorios");
		}

		if (dish.getPrice() < 0) {
			throw new CustomBadRequestException("El precio no puede ser negativo");
		}
	}
}
